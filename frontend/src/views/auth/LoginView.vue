<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-brand">
        <div class="brand-icon">⚓</div>
        <h1 class="brand-title">TPA</h1>
        <p class="brand-subtitle">ICT ASSETS MANAGEMENT</p>
      </div>
      
      <div class="login-card">
        <h2 class="login-title">Welcome Back</h2>
        <p class="login-subtitle">Sign in to access the system</p>
        
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label>Email Address</label>
            <input type="email" v-model="email" placeholder="Enter your email" required />
          </div>
          
          <div class="form-group">
            <label>Password</label>
            <input type="password" v-model="password" placeholder="Enter your password" required />
          </div>
          
          <button type="submit" class="btn-primary btn-full">Sign In</button>
          
          <p class="login-footer">
            Don't have an account? <router-link to="/register">Register</router-link>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const email = ref('');
const password = ref('');

const handleLogin = () => {
  localStorage.setItem('isAuthenticated', 'true');
  localStorage.setItem('user', JSON.stringify({ name: 'Sarah Collins', email: email.value }));
  router.push('/');
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0B2265 0%, #071741 100%);
  padding: 20px;
}

.login-container {
  display: flex;
  max-width: 900px;
  width: 100%;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 16px rgba(0,0,0,0.12);
}

.login-brand {
  flex: 1;
  padding: 48px 40px;
  background: #0B2265;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.brand-icon { font-size: 48px; margin-bottom: 16px; }
.brand-title { font-size: 32px; font-weight: 700; margin: 0; }
.brand-subtitle { font-size: 11px; opacity: 0.7; margin-top: 8px; letter-spacing: 3px; }

.login-card {
  flex: 1;
  padding: 40px;
  max-width: 420px;
}

.login-title { font-size: 24px; font-weight: 600; margin: 0 0 4px 0; }
.login-subtitle { font-size: 14px; color: #667085; margin: 0 0 24px 0; }

.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; font-weight: 500; margin-bottom: 6px; }
.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #E4E7EC;
  border-radius: 6px;
  font-size: 14px;
}
.form-group input:focus {
  outline: none;
  border-color: #00A3DD;
  box-shadow: 0 0 0 3px rgba(0,163,221,0.1);
}

.btn-primary {
  padding: 12px 24px;
  background: #00A3DD;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-primary:hover { background: #008C95; }
.btn-full { width: 100%; }

.login-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 13px;
  color: #667085;
}
.login-footer a { color: #00A3DD; text-decoration: none; }

@media (max-width: 768px) {
  .login-container { flex-direction: column; }
  .login-brand { padding: 32px 24px; }
  .login-card { max-width: 100%; padding: 24px; }
}
</style>
