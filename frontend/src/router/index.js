import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue')
      },
      {
        path: 'assets',
        name: 'Assets',
        component: () => import('@/views/assets/AssetsView.vue')
      },
      {
        path: 'assets/register',
        alias: ['/assets/new'],
        name: 'AssetRegister',
        component: () => import('@/views/assets/AssetDetailView.vue')
      },
      {
        path: 'assets/:id',
        name: 'AssetDetail',
        component: () => import('@/views/assets/AssetDetailView.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/users/UsersView.vue'),
        meta: { requiresAuth: true, role: 'ADMINISTRATOR' }
      },
      {
        path: 'assignments',
        name: 'Assignments',
        component: () => import('@/views/assignments/AssignmentsView.vue')
      },
      {
        path: 'audit',
        name: 'AuditLogs',
        component: () => import('@/views/audit/AuditLogsView.vue'),
        meta: { requiresAuth: true, role: 'ADMINISTRATOR' }
      },
      {
        path: 'disposal',
        name: 'DisposalConfirmation',
        component: () => import('@/views/disposal/DisposalConfirmationView.vue')
      },
      {
        path: 'maintenance',
        alias: ['/settings'],
        name: 'Maintenance',
        component: () => import('@/views/maintenance/MaintenanceView.vue')
      },
      {
        path: 'reports',
        name: 'Reports',
        component: () => import('@/views/reports/ReportsView.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue')
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true' && !!token;
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
    return;
  }
  
  if (to.meta.requiresGuest && isAuthenticated) {
    next({ name: 'Dashboard' });
    return;
  }

  if (to.meta.role) {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const roles = user.roles || [];
    if (!roles.includes(to.meta.role)) {
      next({ name: 'Dashboard' });
      return;
    }
  }
  
  next();
});

export default router;
