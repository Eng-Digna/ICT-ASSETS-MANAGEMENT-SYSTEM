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
  padding: 32px 20px;
  background: #f5f6f8;
}

.register-container {
  width: min(100%, 526px);
  padding: 32px 48px 18px;
  background: #fff;
  border: 1px solid #d9e0e8;
  border-radius: 14px;
  box-shadow: 0 2px 5px rgba(16, 24, 40, 0.04);
  text-align: center;
}

.register-brand {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.brand-logo { width: 100px; height: 62px; object-fit: contain; margin: 0 auto 14px; }
.brand-title { color: #123f73; font-size: 21px; font-weight: 700; line-height: 1.3; margin: 0; }
.brand-subtitle { color: #5d6878; font-size: 14px; margin: 5px 0 22px; letter-spacing: 0; }

.register-card {
  width: 100%;
}

.register-title { color: #123f73; font-size: 21px; font-weight: 700; line-height: 1.3; margin: 0 0 4px; }
.register-subtitle { color: #5d6878; font-size: 14px; margin: 0 0 22px; }

.form-group { margin-bottom: 22px; text-align: left; }
.form-group label { display: block; color: #596576; font-size: 14px; margin-bottom: 4px; }
.form-group input, .form-group select {
  width: 100%;
  min-height: 40px;
  padding: 9px 12px;
  border: 2px solid #cbd3de;
  border-radius: 8px;
  font-size: 14px;
  color: #1d2939;
}
.form-group input:focus, .form-group select:focus {
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

.register-footer {
  margin: 17px 0 45px;
  text-align: center;
  font-size: 13px;
  color: #5d6878;
}
.register-footer a { color: #123f73; text-decoration: none; }

@media (max-width: 768px) {
  .register-container { padding: 28px 24px 18px; }
}
</style>
