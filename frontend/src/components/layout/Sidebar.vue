<template>
  <aside class="sidebar">
    <div class="sidebar-header">
      <div class="brand">
        <span class="brand-icon">⚓</span>
        <div>
          <div class="brand-name">TPA</div>
          <div class="brand-subtitle">ICT ASSETS</div>
        </div>
      </div>
    </div>
    <nav class="nav-menu">
      <router-link to="/" class="nav-item">
        <span class="nav-icon">📊</span><span>Dashboard</span>
      </router-link>
      <router-link to="/assets" class="nav-item">
        <span class="nav-icon">💻</span><span>Assets Directory</span>
      </router-link>
      <router-link to="/assets/new" class="nav-item">
        <span class="nav-icon">➕</span><span>Register Assets</span>
      </router-link>
      <router-link to="/users" class="nav-item">
        <span class="nav-icon">👥</span><span>Users & Roles</span>
      </router-link>
      <router-link to="/assignments" class="nav-item">
        <span class="nav-icon">📋</span><span>Assignments</span>
      </router-link>
      <router-link to="/audit" class="nav-item">
        <span class="nav-icon">📜</span><span>Audit Logs</span>
      </router-link>
      <router-link to="/disposal" class="nav-item">
        <span class="nav-icon">🗑️</span><span>Disposal Request</span>
      </router-link>
      <router-link to="/settings" class="nav-item">
        <span class="nav-icon">⚙️</span><span>Maintenance</span>
      </router-link>
      <router-link to="/reports" class="nav-item">
        <span class="nav-icon">📊</span><span>Reports</span>
      </router-link>

    </nav>
    <div class="sidebar-footer">
      <button @click="handleLogout" class="logout-btn">
        <span class="nav-icon">🚪</span><span>Logout</span>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();
const handleLogout = () => {
  localStorage.removeItem('isAuthenticated');
  localStorage.removeItem('user');
  localStorage.removeItem('token');
  router.push('/login');
};
</script>

<style scoped>
.sidebar {
  width: 220px; height: 100vh; background: #082d4b; color: white;
  display: flex; flex-direction: column; position: fixed; left: 0; top: 0;
  overflow-y: auto; z-index: 100;
}
.sidebar-header { padding: 20px 16px; border-bottom: 1px solid rgba(255,255,255,0.08); }
.brand { display: flex; align-items: center; gap: 12px; }
.brand-icon { font-size: 28px; }
.brand-name { font-size: 18px; font-weight: 700; letter-spacing: 1px; }
.brand-subtitle { font-size: 8px; opacity: 0.6; letter-spacing: 2px; text-transform: uppercase; }
.nav-menu { flex: 1; padding: 16px 12px; }
.nav-item {
  display: flex; align-items: center; gap: 12px; padding: 10px 12px;
  color: rgba(255,255,255,0.9); text-decoration: none; border-radius: 0;
  margin-bottom: 4px; font-size: 13px; transition: all 0.2s;
}
.nav-item:hover { background: rgba(255,255,255,0.08); color: white; }
.nav-item.router-link-active { background: #dfa30b; color: #102f55; font-weight: 700; }
.nav-icon { font-size: 18px; width: 24px; }
.sidebar-footer { padding: 16px 12px; border-top: 1px solid rgba(255,255,255,0.08); }
.logout-btn {
  display: flex; align-items: center; gap: 12px; width: 100%;
  padding: 10px 12px; background: none; border: none;
  color: rgba(255,255,255,0.5); font-size: 13px; cursor: pointer;
  border-radius: 6px; transition: all 0.2s;
}
.logout-btn:hover { background: rgba(211,47,47,0.15); color: white; }
</style>
