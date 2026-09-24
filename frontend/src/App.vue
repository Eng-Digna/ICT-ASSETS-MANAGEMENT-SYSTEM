<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from './store/modules/auth';

const router = useRouter();
const authStore = useAuthStore();

const IDLE_TIMEOUT_MS = 15 * 60 * 1000; // 15 minutes
let idleTimer = null;

function resetTimer() {
  clearTimeout(idleTimer);
  if (authStore.isAuthenticated) {
    idleTimer = setTimeout(() => {
      authStore.logout();
      router.push('/login');
    }, IDLE_TIMEOUT_MS);
  }
}

const EVENTS = ['mousemove', 'mousedown', 'keydown', 'touchstart', 'scroll', 'click'];

onMounted(() => {
  EVENTS.forEach(e => window.addEventListener(e, resetTimer, { passive: true }));
  resetTimer();
});

onUnmounted(() => {
  EVENTS.forEach(e => window.removeEventListener(e, resetTimer));
  clearTimeout(idleTimer);
});
</script>

<style>
#app {
  min-height: 100vh;
}
</style>
