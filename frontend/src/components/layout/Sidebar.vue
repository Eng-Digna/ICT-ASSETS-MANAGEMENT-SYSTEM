<template>
  <aside class="sidebar" :class="{ 'is-open': isOpen }">
    <div class="sidebar-header">
      <div class="brand">
        <img :src="tpaLogo" alt="Tanzania Ports Authority logo" class="brand-logo" />
        <div>
          <div class="brand-name">TPA</div>
          <div class="brand-subtitle">ICT ASSETS</div>
        </div>
      </div>
    </div>
    <nav class="nav-menu">
      <router-link to="/" exact-active-class="active" class="nav-item">
        <span class="nav-icon">📊</span><span>Dashboard</span>
      </router-link>
      <router-link to="/assets" active-class="active" class="nav-item">
        <span class="nav-icon">💻</span><span>Assets Directory</span>
      </router-link>
      <router-link to="/assets/register" active-class="active" class="nav-item">
        <span class="nav-icon">➕</span><span>Register Asset</span>
      </router-link>
      <router-link v-if="isAdmin" to="/users" active-class="active" class="nav-item">
        <span class="nav-icon">👥</span><span>Users & Roles</span>
      </router-link>
      <router-link to="/assignments" active-class="active" class="nav-item">
        <span class="nav-icon">📋</span><span>Assignments</span>
      </router-link>
      <router-link v-if="isAdmin" to="/audit" active-class="active" class="nav-item">
        <span class="nav-icon">📜</span><span>Audit Logs</span>
      </router-link>
      <router-link to="/disposal" active-class="active" class="nav-item">
        <span class="nav-icon">🗑️</span><span>Disposal Requests</span>
      </router-link>
      <router-link to="/maintenance" active-class="active" class="nav-item">
        <span class="nav-icon">⚙️</span><span>Maintenance</span>
      </router-link>
      <router-link to="/reports" active-class="active" class="nav-item">
        <span class="nav-icon">📈</span><span>Reports</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '../../store/modules/auth';
import tpaLogo from '../../assets/tpa-logo.png';

defineProps({ isOpen: Boolean });

const authStore = useAuthStore();
const isAdmin = computed(() => {
  const user = authStore.user || JSON.parse(localStorage.getItem('user') || '{}');
  return user.roles?.includes('ADMINISTRATOR');
});
</script>

<style scoped>
.sidebar {
  width: 220px; height: 100vh; background: #123f73; color: white;
  display: flex; flex-direction: column; position: fixed; left: 0; top: 0;
  overflow-y: auto; z-index: 100;
}
.sidebar-header { padding: 20px 16px; border-bottom: 1px solid rgba(255,255,255,0.08); }
.brand { display: flex; align-items: center; gap: 12px; }
.brand-logo { width: 50px; height: 42px; object-fit: contain; flex: 0 0 auto; }
.brand-name { font-size: 18px; font-weight: 700; letter-spacing: 1px; }
.brand-subtitle { font-size: 8px; opacity: 0.6; letter-spacing: 2px; text-transform: uppercase; }
.nav-menu { flex: 1; padding: 16px 12px; }
.nav-item {
  display: flex; align-items: center; gap: 12px; padding: 10px 12px;
  color: rgba(255,255,255,0.9); text-decoration: none; border-radius: 4px;
  margin-bottom: 4px; font-size: 13px; transition: all 0.2s;
}
.nav-item:hover { background: rgba(255,255,255,0.08); color: white; }
.nav-item.active { background: #dfa30b; color: #102f55; font-weight: 700; }
.nav-icon { font-size: 18px; width: 24px; }

@media (max-width: 900px) {
  .sidebar { transform: translateX(-100%); transition: transform 0.3s ease; }
  .sidebar.is-open { transform: translateX(0); }
}
</style>
