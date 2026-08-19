<template>
  <div class="app-layout">
    <Sidebar />
    <div class="main-content">
      <Header />
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import Sidebar from './Sidebar.vue';
import Header from './Header.vue';
</script>

<style scoped>
.app-layout { display: flex; min-height: 100vh; background: #F5F7FA; }
.main-content { flex: 1; display: flex; flex-direction: column; margin-left: 220px; min-height: 100vh; }
.content-area { flex: 1; padding: 24px; overflow-y: auto; }
@media (max-width: 768px) { .main-content { margin-left: 0; } }
</style>
