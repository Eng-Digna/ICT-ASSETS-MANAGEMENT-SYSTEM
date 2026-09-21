<template>
  <header class="header">
    <div class="header-left">
      <button class="menu-button" type="button" aria-label="Open navigation menu" @click="$emit('toggle-sidebar')">☰</button>
      <div class="header-brand"><span class="brand-mark">T</span><div><strong>ICT-AMS</strong><small>Tanzania Ports Authority</small></div></div>
      <h1 class="page-title">{{ currentPage }}</h1>
    </div>
    <div class="header-right">
      <div class="profile-menu">
        <button class="profile-button" type="button" aria-haspopup="menu" :aria-expanded="isMenuOpen" @click="isMenuOpen = !isMenuOpen">
          <span class="profile-icon" aria-hidden="true">👤</span><span class="profile-chevron">▾</span>
        </button>
        <div v-if="isMenuOpen" class="profile-dropdown" role="menu">
          <div class="profile-summary">
            <strong>{{ userName }}</strong>
            <span>{{ userStation }}</span>
          </div>
          <button type="button" role="menuitem" @click="openPasswordDialog">Change Password</button>
          <button type="button" role="menuitem" class="logout-item" @click="handleLogout">Logout</button>
        </div>
      </div>
    </div>
  </header>
  <div v-if="isPasswordDialogOpen" class="dialog-backdrop" @click.self="closePasswordDialog">
    <section class="password-dialog" role="dialog" aria-modal="true" aria-labelledby="password-title">
      <div class="dialog-header"><div><p class="dialog-eyebrow">Account security</p><h2 id="password-title">Change Password</h2></div><button class="close-button" type="button" aria-label="Close change password dialog" @click="closePasswordDialog">×</button></div>
      <form @submit.prevent="changePassword">
        <label>Current Password<input v-model="passwordForm.current" type="password" required autocomplete="current-password" /></label>
        <label>New Password<input v-model="passwordForm.next" type="password" minlength="8" required autocomplete="new-password" /></label>
        <label>Confirm New Password<input v-model="passwordForm.confirm" type="password" minlength="8" required autocomplete="new-password" /></label>
        <p v-if="passwordError" class="password-error">{{ passwordError }}</p><p v-if="passwordSuccess" class="password-success">{{ passwordSuccess }}</p>
        <div class="dialog-actions"><button class="cancel-button" type="button" @click="closePasswordDialog">Cancel</button><button class="save-button" type="submit">Update Password</button></div>
      </form>
    </section>
  </div>
</template>

<script setup>
defineEmits(['toggle-sidebar']);
import { computed, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/store/modules/auth';
import { changePassword as changePasswordRequest } from '@/services/api';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const user = JSON.parse(localStorage.getItem('user') || '{"name": "Admin"}');
const isMenuOpen = ref(false);
const isPasswordDialogOpen = ref(false);
const passwordError = ref('');
const passwordSuccess = ref('');
const passwordForm = reactive({ current: '', next: '', confirm: '' });

const currentPage = computed(() => {
  const pages = {
    '/': 'Dashboard',
    '/assets': 'Assets',
    '/users': 'Users',
    '/assignments': 'Assignments',
    '/audit': 'Audit Logs'
  };
  return pages[route.path] || 'Dashboard';
});

const userName = user?.name || user?.username || 'Admin';
const userStation = user?.station || user?.Station || 'Dar es Salaam HQ';
const userInitials = computed(() => {
  return userName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
});
function openPasswordDialog() { isMenuOpen.value = false; passwordError.value = ''; passwordSuccess.value = ''; isPasswordDialogOpen.value = true; }
function closePasswordDialog() { isPasswordDialogOpen.value = false; Object.assign(passwordForm, { current: '', next: '', confirm: '' }); }
async function changePassword() {
  passwordError.value = '';
  passwordSuccess.value = '';
  if (passwordForm.next !== passwordForm.confirm) { passwordError.value = 'New password and confirmation do not match.'; return; }
  if (!user?.id) { passwordError.value = 'Your session does not contain a user ID. Please log in again.'; return; }
  try {
    await changePasswordRequest(user.id, { currentPassword: passwordForm.current, newPassword: passwordForm.next });
    passwordSuccess.value = 'Password updated successfully.';
    Object.assign(passwordForm, { current: '', next: '', confirm: '' });
  } catch (error) {
    passwordError.value = error.response?.data?.message || 'Unable to update password.';
  }
}
function handleLogout() { auth.logout(); isMenuOpen.value = false; router.replace({ name: 'Login' }); }
</script>

<style scoped>
.header {
  background: #123f73; padding: 0 26px; height: 72px;
  display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid #123f73;
}
.page-title { display: none; }
.menu-button { background: transparent; border: 0; color: white; cursor: pointer; display: none; font-size: 22px; padding: 4px; }
.header-brand { display: flex; align-items: center; gap: 12px; color: #fff; }
.header-brand strong { display: block; font-size: 18px; }
.header-brand small { display: block; margin-top: 3px; color: #f4cf55; font-size: 12px; }
.brand-mark { width: 38px; height: 38px; display: grid; place-items: center; border-radius: 50%; background: #dfa30b; color: #123f73; font-weight: 700; }
.header-right { display: flex; align-items: center; gap: 16px; }
.profile-menu { position: relative; }
.profile-button { align-items: center; background: transparent; border: 0; color: white; cursor: pointer; display: flex; font: inherit; gap: 8px; padding: 5px 0; }
.profile-icon { align-items: center; background: #dfa30b; border-radius: 50%; color: #123f73; display: flex; font-size: 17px; height: 34px; justify-content: center; width: 34px; }
.profile-chevron { color: #f4cf55; font-size: 14px; }
.profile-dropdown { background: white; border: 1px solid #DDE3EA; border-radius: 5px; box-shadow: 0 8px 20px rgba(16, 24, 40, .16); min-width: 220px; padding: 5px; position: absolute; right: 0; top: calc(100% + 4px); z-index: 20; }
.profile-summary { border-bottom: 1px solid #E9EDF2; padding: 10px; }.profile-summary strong,.profile-summary span { display: block; }.profile-summary strong { color: #1D2939; font-size: 13px; }.profile-summary span { color: #667085; font-size: 12px; margin-top: 4px; }
.profile-dropdown button { background: transparent; border: 0; border-radius: 4px; color: #344054; cursor: pointer; display: block; font-size: 13px; padding: 9px 10px; text-align: left; width: 100%; }
.profile-dropdown button:hover { background: #F2F4F7; }.profile-dropdown .logout-item { color: #C9362B; }
.dialog-backdrop { align-items: center; background: rgba(16, 24, 40, .45); display: flex; inset: 0; justify-content: center; padding: 20px; position: fixed; z-index: 50; }
.password-dialog { background: white; border-radius: 8px; box-shadow: 0 16px 36px rgba(16, 24, 40, .22); max-width: 430px; padding: 22px; width: 100%; }
.dialog-header { align-items: flex-start; display: flex; justify-content: space-between; margin-bottom: 18px; }.dialog-eyebrow { color: #2E90C8; font-size: 11px; font-weight: 700; letter-spacing: .08em; margin: 0 0 5px; text-transform: uppercase; }.password-dialog h2 { color: #1D2939; font-size: 20px; margin: 0; }.close-button { background: transparent; border: 0; color: #667085; cursor: pointer; font-size: 24px; line-height: 1; }.password-dialog label { color: #475467; display: grid; font-size: 12px; font-weight: 700; gap: 6px; margin-bottom: 13px; }.password-dialog input { border: 1px solid #CDD5DF; border-radius: 5px; color: #344054; padding: 10px; }.password-error,.password-success { font-size: 12px; margin: 3px 0 13px; }.password-error { color: #B42318; }.password-success { color: #16834D; }.dialog-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 18px; }.cancel-button,.save-button { border-radius: 5px; cursor: pointer; font-size: 13px; font-weight: 700; padding: 9px 13px; }.cancel-button { background: white; border: 1px solid #CDD5DF; color: #475467; }.save-button { background: #2E90C8; border: 0; color: white; }
@media (max-width: 768px) {
  .header { height: 64px; padding: 0 16px; }
  .menu-button { display: block; }
  .header-left { align-items: center; display: flex; gap: 10px; }
  .header-brand { gap: 8px; }.brand-mark { height: 32px; width: 32px; }.header-brand strong { font-size: 16px; }.header-brand small { font-size: 10px; }
}
@media (max-width: 420px) { .header { padding: 0 12px; }.header-brand small { display: none; }.profile-dropdown { position: fixed; right: 12px; top: 58px; } }
</style>
