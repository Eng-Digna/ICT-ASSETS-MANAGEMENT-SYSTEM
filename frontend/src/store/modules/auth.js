import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token'),
    isAuthenticated: false,
    loading: false,
    error: null
  }),

  getters: {
    isLoggedIn: (state) => state.isAuthenticated && !!state.token,
    userName: (state) => state.user?.name || 'Guest',
    userRole: (state) => state.user?.role || 'user'
  },

  actions: {
    async login(credentials) {
      this.loading = true;
      this.error = null;
      try {
        // Simulate API call
        await new Promise(resolve => setTimeout(resolve, 500));
        
        const user = {
          id: 1,
          name: 'Sarah Collins',
          email: credentials.email,
          role: 'admin',
          department: 'ICT'
        };
        const token = 'mock-jwt-token';
        
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
