<template>
  <header class="header">
    <div class="header-left">
      <div class="header-brand"><span class="brand-mark">T</span><div><strong>ICT-AMS</strong><small>Tanzania Ports Authority</small></div></div>
      <h1 class="page-title">{{ currentPage }}</h1>
    </div>
    <div class="header-right">
      <div class="user-profile">
        <span class="user-name">{{ userName }} — ICT Manager ▾</span>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const user = JSON.parse(localStorage.getItem('user') || '{"name": "Admin"}');

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

const userName = user?.name || 'Admin';
const userInitials = computed(() => {
  return userName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
});
</script>

<style scoped>
.header {
  background: #123f73; padding: 0 26px; height: 72px;
  display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid #123f73;
}
.page-title { display: none; }
.header-brand { display: flex; align-items: center; gap: 12px; color: #fff; }
.header-brand strong { display: block; font-size: 18px; }
.header-brand small { display: block; margin-top: 3px; color: #f4cf55; font-size: 12px; }
.brand-mark { width: 38px; height: 38px; display: grid; place-items: center; border-radius: 50%; background: #dfa30b; color: #123f73; font-weight: 700; }
.header-right { display: flex; align-items: center; gap: 16px; }
.user-profile { display: flex; align-items: center; gap: 8px; }
.avatar {
  width: 32px; height: 32px; border-radius: 50%; background: #00A3DD;
  color: white; display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600;
}
.user-name { font-size: 14px; color: #fff; }
</style>
