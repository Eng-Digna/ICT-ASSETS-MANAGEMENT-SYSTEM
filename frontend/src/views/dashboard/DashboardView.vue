<template>
  <div class="dashboard">
    <div class="page-header">
      <div>
        <h1>Dashboard</h1>
        <p class="text-muted">Welcome to the TPA ICT Assets Management System</p>
      </div>
      <RouterLink class="btn-primary" :to="{ name: 'Assets' }">View Asset Directory</RouterLink>
    </div>
    
    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.title" class="stat-card">
        <div class="stat-icon">{{ stat.icon }}</div>
        <div class="stat-content">
          <h3>{{ stat.value }}</h3>
          <p>{{ stat.title }}</p>
          <span class="stat-change" :class="stat.change.startsWith('+') ? 'positive' : 'negative'">
            {{ stat.change }}
          </span>
        </div>
      </div>
    </div>
    
    <div class="charts-row">
      <div class="chart-card">
        <h4>Assets by Status</h4>
        <div v-for="item in statusBreakdown" :key="item.label" class="status-item">
          <span class="status-label">{{ item.label }}</span>
          <div class="status-bar">
            <div class="status-bar-fill" :style="{ width: item.percentage + '%', background: item.color }"></div>
          </div>
          <span class="status-count">{{ item.count }}</span>
        </div>
      </div>
      
      <div class="chart-card">
        <h4>Recent Activity</h4>
        <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
          <span class="activity-icon">{{ activity.icon }}</span>
          <div>
            <div class="activity-description">{{ activity.description }}</div>
            <div class="activity-time">{{ activity.time }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="card">
      <div class="card-header">
        <h4>Recent Assignments</h4>
        <a href="#" class="view-all">View All →</a>
      </div>
      <table class="data-table">
        <thead>
          <tr>
            <th>Asset</th>
            <th>Assignee</th>
            <th>Department</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="assignment in recentAssignments" :key="assignment.id">
            <td><strong>{{ assignment.asset }}</strong></td>
            <td>{{ assignment.assignee }}</td>
            <td>{{ assignment.department }}</td>
            <td><span class="status-badge" :class="assignment.status.toLowerCase()">{{ assignment.status }}</span></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const stats = ref([
  { title: 'Total Assets', value: '1,450', icon: '💻', change: '+12%' },
  { title: 'Available', value: '320', icon: '✅', change: '+8%' },
  { title: 'In Use', value: '1,050', icon: '📝', change: '+5%' },
  { title: 'Maintenance', value: '80', icon: '🔧', change: '-3%' }
]);

const statusBreakdown = ref([
  { label: 'Active', count: 850, percentage: 58, color: '#12B76A' },
  { label: 'Assigned', count: 350, percentage: 24, color: '#2E90FA' },
  { label: 'Maintenance', count: 180, percentage: 12, color: '#F79009' },
  { label: 'Disposed', count: 70, percentage: 6, color: '#98A2B3' }
]);

const recentActivities = ref([
  { id: 1, icon: '📦', description: 'Dell XPS 13 assigned to John Doe', time: '2 min ago' },
  { id: 2, icon: '🔧', description: 'HP ProBook maintenance completed', time: '15 min ago' },
  { id: 3, icon: '➕', description: 'iPhone 14 Pro registered', time: '1 hour ago' },
  { id: 4, icon: '📋', description: 'Audit log review completed', time: '2 hours ago' }
]);

const recentAssignments = ref([
  { id: 1, asset: 'Dell XPS 13', assignee: 'John Doe', department: 'ICT', status: 'Active' },
  { id: 2, asset: 'HP ProBook', assignee: 'Jane Smith', department: 'Finance', status: 'Active' },
  { id: 3, asset: 'iPhone 14 Pro', assignee: 'Mike Johnson', department: 'HR', status: 'Pending' },
  { id: 4, asset: 'MacBook Pro', assignee: 'Sarah Wilson', department: 'Operations', status: 'Active' }
]);
</script>

<style scoped>
.dashboard { max-width: 1400px; margin: 0 auto; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-header h1 { font-size: 24px; font-weight: 600; color: #101828; margin: 0; }
.text-muted { color: #667085; margin: 4px 0 0 0; }

.btn-primary {
  padding: 10px 20px;
  background: #00A3DD;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}
.btn-primary:hover { background: #008C95; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.stat-icon { font-size: 28px; }
.stat-content h3 { font-size: 20px; font-weight: 700; margin: 0; color: #101828; }
.stat-content p { font-size: 13px; color: #667085; margin: 4px 0 0 0; }
.stat-change { font-size: 12px; font-weight: 500; }
.stat-change.positive { color: #12B76A; }
.stat-change.negative { color: #D32F2F; }

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.chart-card h4 { font-size: 14px; font-weight: 600; margin: 0 0 16px 0; }

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.status-label { font-size: 13px; color: #475467; width: 80px; }
.status-bar { flex: 1; height: 6px; background: #F2F4F7; border-radius: 999px; overflow: hidden; }
.status-bar-fill { height: 100%; border-radius: 999px; transition: width 0.6s ease; }
.status-count { font-size: 13px; font-weight: 500; color: #344054; }

.activity-item {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #F2F4F7;
}
.activity-item:last-child { border-bottom: none; }
.activity-description { font-size: 13px; color: #344054; }
.activity-time { font-size: 11px; color: #98A2B3; margin-top: 2px; }

.card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.card-header h4 { font-size: 14px; font-weight: 600; margin: 0; }
.view-all { font-size: 13px; color: #00A3DD; text-decoration: none; }

.data-table { width: 100%; border-collapse: collapse; }
.data-table thead th {
  text-align: left;
  padding: 8px 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  color: #667085;
  border-bottom: 1px solid #E4E7EC;
}
.data-table tbody td {
  padding: 10px 12px;
  font-size: 13px;
  border-bottom: 1px solid #F2F4F7;
}

.status-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 500;
}
.status-badge.active { background: rgba(18,183,106,0.12); color: #12B76A; }
.status-badge.pending { background: rgba(247,144,9,0.12); color: #F79009; }

@media (max-width: 1024px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .charts-row { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .stats-grid { grid-template-columns: 1fr; }
  .page-header { flex-direction: column; gap: 12px; align-items: flex-start; }
}
</style>
