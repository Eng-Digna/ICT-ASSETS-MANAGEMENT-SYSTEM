<template>
  <div class="login-page">
    <div class="login-card">
      <img :src="tpaLogo" alt="Tanzania Ports Authority logo" class="brand-logo" />
      <h1 class="login-title">ICT Asset Management System</h1>
      <p class="login-subtitle">Tanzania Ports Authority</p>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Username</label>
          <input id="username" v-model="username" type="text" placeholder="e.g. jmwkalinga" autocomplete="username" required />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input id="password" v-model="password" type="password" placeholder="Enter your password" autocomplete="current-password" required />
        </div>

        <button type="submit" class="btn-primary">Log In</button>
      </form>

      <p class="login-help">Forgot your password? Contact your ICT Administrator</p>
      <div class="login-divider"></div>
      <p class="login-notice">Access restricted to authorised TPA ICT users</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../store/modules/auth';
import tpaLogo from '../../assets/tpa-logo.png';

const router = useRouter();
const authStore = useAuthStore();
const username = ref('');
const password = ref('');
const loginError = ref('');

const handleLogin = async () => {
  loginError.value = '';
  const result = await authStore.login({ username: username.value, password: password.value });
  if (result.success) {
    router.push('/');
  } else {
    loginError.value = result.error || 'Invalid credentials. Please try again.';
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 20px;
  background: #f5f6f8;
}

.login-card {
  width: min(100%, 526px);
  padding: 32px 48px 18px;
  background: #fff;
  border: 1px solid #d9e0e8;
  border-radius: 14px;
  box-shadow: 0 2px 5px rgba(16, 24, 40, 0.04);
  text-align: center;
}

.brand-logo {
  width: 100px;
  height: 62px;
  margin: 0 auto 14px;
  object-fit: contain;
}

.login-title {
  color: #123f73;
  font-size: 21px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0;
}

.login-subtitle {
  color: #5d6878;
  font-size: 14px;
  margin: 5px 0 22px;
}

.form-group { margin-bottom: 22px; text-align: left; }
.form-group label { display: block; color: #596576; font-size: 14px; margin-bottom: 4px; }
.form-group input {
  width: 100%;
  min-height: 40px;
  padding: 9px 12px;
  border: 2px solid #cbd3de;
  border-radius: 8px;
  font-size: 14px;
  color: #1d2939;
}
.form-group input:focus {
  outline: none;
  border-color: #123f73;
  box-shadow: 0 0 0 3px rgba(18, 63, 115, 0.1);
}

.btn-primary {
  width: 100%;
  min-height: 52px;
  padding: 12px 24px;
  background: #dfa30b;
  color: #102f55;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.2s ease;
}
.btn-primary:hover { background: #c99008; }

.login-help {
  margin: 17px 0 45px;
  text-align: center;
  font-size: 13px;
  color: #5d6878;
}

.login-divider {
  height: 1px;
  background: #e7eaee;
}

.login-notice {
  margin: 15px 0 0;
  color: #5d6878;
  font-size: 12px;
}

@media (max-width: 768px) {
  .login-card { padding: 28px 24px 18px; }
}
</style>
