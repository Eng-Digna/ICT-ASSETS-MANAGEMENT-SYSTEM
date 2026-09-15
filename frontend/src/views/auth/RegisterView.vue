<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-brand">
        <img :src="tpaLogo" alt="Tanzania Ports Authority logo" class="brand-logo" />
        <h1 class="brand-title">TPA</h1>
        <p class="brand-subtitle">ICT ASSETS MANAGEMENT</p>
      </div>
      
      <div class="register-card">
        <h2 class="register-title">Create Account</h2>
        <p class="register-subtitle">Request access to the system</p>
        
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>Full Name</label>
            <input type="text" v-model="name" placeholder="Enter your full name" required />
          </div>
          
          <div class="form-group">
            <label>Email Address</label>
            <input type="email" v-model="email" placeholder="Enter your email" required />
          </div>
          
          <div class="form-group">
            <label>Station</label>
            <select v-model="Station" required>
              <option value="">Select Station</option>
              <option>DAR-ES-SALAAM</option>
              <option>TANGA</option>
              <option>KIGOMA</option>
              <option>MWANZA</option>
              <option>MTWARA</option>
              <option>LINDI</option>

            </select>
          </div>
          
          <button type="submit" class="btn-primary btn-full">Request Access</button>
          
          <p class="register-footer">
            Already have an account? <router-link to="/login">Sign In</router-link>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import tpaLogo from '../../assets/tpa-logo.png';

const router = useRouter();
const name = ref('');
const email = ref('');
const Station = ref('');

const handleRegister = () => {
  alert('Registration request submitted! You will receive an email once approved.');
  router.push('/login');
};
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0B2265 0%, #071741 100%);
  padding: 20px;
}

.register-container {
  display: flex;
  max-width: 900px;
  width: 100%;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 16px rgba(0,0,0,0.12);
}

.register-brand {
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

.brand-logo { width: 150px; height: 92px; object-fit: contain; margin-bottom: 16px; }
.brand-title { font-size: 32px; font-weight: 700; margin: 0; }
.brand-subtitle { font-size: 11px; opacity: 0.7; margin-top: 8px; letter-spacing: 3px; }

.register-card {
  flex: 1;
  padding: 40px;
  max-width: 420px;
}

.register-title { font-size: 24px; font-weight: 600; margin: 0 0 4px 0; }
.register-subtitle { font-size: 14px; color: #667085; margin: 0 0 24px 0; }

.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; font-weight: 500; margin-bottom: 6px; }
.form-group input, .form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #E4E7EC;
  border-radius: 6px;
  font-size: 14px;
}
.form-group input:focus, .form-group select:focus {
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

.register-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 13px;
  color: #667085;
}
.register-footer a { color: #00A3DD; text-decoration: none; }

@media (max-width: 768px) {
  .register-container { flex-direction: column; }
  .register-brand { padding: 32px 24px; }
  .register-card { max-width: 100%; padding: 24px; }
}
</style>
