import { defineStore } from 'pinia';
import { api } from '../../services/api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token'),
    isAuthenticated: localStorage.getItem('isAuthenticated') === 'true',
    loading: false,
    error: null
  }),

  getters: {
    isLoggedIn: (state) => state.isAuthenticated && !!state.token,
    userName: (state) => state.user?.username || 'Guest',
    userRole: (state) => state.user?.roles?.[0] || ''
  },

  actions: {
    async login(credentials) {
      this.loading = true;
      this.error = null;
      try {
        const result = await api('/auth/login', { method: 'POST', body: JSON.stringify({ username: credentials.username, password: credentials.password }) });
        const user = { username: result.username, roles: result.roles || [] };
        const token = result.token;
        
        this.user = user;
        this.token = token;
        this.isAuthenticated = true;
        
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));
        localStorage.setItem('isAuthenticated', 'true');
        
        return { success: true };
      } catch (error) {
        this.error = error.message || 'Login failed';
        return { success: false, error: this.error };
      } finally {
        this.loading = false;
      }
    },

    logout() {
      this.user = null;
      this.token = null;
      this.isAuthenticated = false;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('isAuthenticated');
    },

    checkAuth() {
      const token = localStorage.getItem('token');
      const user = localStorage.getItem('user');
      const isAuth = localStorage.getItem('isAuthenticated') === 'true';
      
      if (token && user && isAuth) {
        this.token = token;
        this.user = JSON.parse(user);
        this.isAuthenticated = true;
        return true;
      }
      return false;
    }
  }
});
