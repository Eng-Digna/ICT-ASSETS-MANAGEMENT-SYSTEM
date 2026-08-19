<template>
  <header class="header">
    <div class="header-left">
      <h1 class="page-title">{{ currentPage }}</h1>
    </div>
    <div class="header-right">
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input type="text" placeholder="Search..." />
      </div>
      <div class="user-profile">
        <span class="avatar">{{ userInitials }}</span>
        <span class="user-name">{{ userName }}</span>
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
  background: white; padding: 0 24px; height: 56px;
  display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid #E4E7EC;
}
.page-title { font-size: 16px; font-weight: 600; color: #0B2265; margin: 0; }
.header-right { display: flex; align-items: center; gap: 16px; }
.search-box {
  display: flex; align-items: center; background: #F5F7FA;
  border-radius: 6px; padding: 6px 12px; border: 1px solid #E4E7EC;
}
.search-box input { border: none; background: transparent; outline: none; font-size: 13px; width: 200px; }
.user-profile { display: flex; align-items: center; gap: 8px; }
.avatar {
  width: 32px; height: 32px; border-radius: 50%; background: #00A3DD;
  color: white; display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600;
}
.user-name { font-size: 13px; font-weight: 500; color: #344054; }
</style>
