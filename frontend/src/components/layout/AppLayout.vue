<template>
  <div class="app-layout">
    <!-- Mobile Backdrop -->
    <div v-if="isSidebarOpen" class="sidebar-backdrop" @click="isSidebarOpen = false"></div>
    
    <Sidebar :is-open="isSidebarOpen" />
    
    <div class="main-content">
      <Header @toggle-sidebar="isSidebarOpen = !isSidebarOpen" />
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import Sidebar from './Sidebar.vue';
import Header from './Header.vue';

const isSidebarOpen = ref(false);
const route = useRoute();

// Auto-close sidebar on mobile when navigating
watch(() => route.path, () => {
  isSidebarOpen.value = false;
});
</script>

<style scoped>
.app-layout { display: flex; min-height: 100vh; background: #F5F7FA; }
.main-content { flex: 1; display: flex; flex-direction: column; margin-left: 220px; min-height: 100vh; width: calc(100% - 220px); }
.content-area { flex: 1; padding: 12px 30px 28px; overflow-y: auto; }
.sidebar-backdrop { display: none; }

@media (max-width: 900px) { 
  .main-content { margin-left: 0; width: 100%; }
  .content-area { padding: 12px 16px 28px; }
  .sidebar-backdrop {
    display: block; position: fixed; inset: 0;
    background: rgba(16, 24, 40, 0.6); z-index: 90;
  }
}
</style>
