<template>
  <div class="app-layout">
    <Sidebar :is-open="isSidebarOpen" @close="isSidebarOpen = false" />
    <div class="main-content">
      <Header @toggle-sidebar="isSidebarOpen = !isSidebarOpen" />
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Sidebar from './Sidebar.vue';
import Header from './Header.vue';

const isSidebarOpen = ref(false);
</script>

<style scoped>
.app-layout { display: flex; min-height: 100vh; background: #F5F7FA; }
.main-content { flex: 1; display: flex; flex-direction: column; margin-left: 220px; min-height: 100vh; }
.content-area { flex: 1; min-width: 0; padding: 12px 30px 28px; overflow-y: auto; }
@media (max-width: 768px) {
  .main-content { margin-left: 0; }
  .content-area { padding: 16px; }
}
@media (max-width: 480px) { .content-area { padding: 12px; } }
</style>
