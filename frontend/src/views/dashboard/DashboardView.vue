<template>
  <div class="dashboard">
    <div class="page-heading">
      <h1>Executive Dashboard</h1>
      <p>Operational overview across TPA Headquarters and all Bandari port stations</p>
    </div>

    <section class="kpi-grid" aria-label="Management KPIs">
      <article v-for="kpi in kpis" :key="kpi.label" class="kpi-card" :class="`accent-${kpi.accent}`">
        <p>{{ kpi.label }}</p>
        <strong>{{ kpi.value }}</strong>
      </article>
    </section>

    <section class="overview-grid">
      <!-- STATION DISTRIBUTION -->
      <article class="panel station-panel">
        <h2>Asset Distribution by Station</h2>
        <div v-if="stationDistribution.length" class="station-chart" aria-label="Asset distribution by station">
          <div v-for="station in stationDistribution" :key="station.name" class="station-bar-group">
            <strong>{{ station.value }}</strong>
            <div class="station-bar-track">
              <div
                class="station-bar"
                :class="station.color"
                :style="{ height: `${maxStationValue ? Math.max((station.value / maxStationValue) * 100, 10) : 10}%` }"
              ></div>
            </div>
            <span>{{ station.name }}</span>
          </div>
        </div>
        <p v-else class="empty-msg">No station asset distribution available yet.</p>
      </article>

      <!-- AUDIT ACTIVITY -->
      <article class="panel audit-panel">
        <h2>Recent System Activity</h2>
        <ul v-if="auditActivity.length" class="audit-list">
          <li v-for="(act, idx) in auditActivity" :key="idx">
            {{ act }}
          </li>
        </ul>
        <p v-else class="empty-msg">No recent activity logged yet.</p>
      </article>
    </section>


  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { api } from '../../services/api';
import { useAuthStore } from '../../store/modules/auth';

const authStore = useAuthStore();
const isAdmin = computed(() => authStore.userRole === 'ADMINISTRATOR');
const userStationId = computed(() => authStore.user?.stationId || '');

const overview = ref(null);
const assets = ref([]);
const recentAudits = ref([]);
const error = ref('');

const kpis = computed(() => {
  if (overview.value) {
    return [
      { label: 'Total ICT Assets', value: overview.value.totalAssets, accent: 'navy' },
      { label: 'Total System Users', value: overview.value.totalUsers, accent: 'gold' },
      { label: 'Active Users', value: overview.value.activeUsers, accent: 'green' },
      { label: 'Inactive / Disabled', value: overview.value.inactiveUsers, accent: 'red' }
    ];
  }
  return [
    { label: 'Total ICT Assets', value: assets.value.length, accent: 'navy' },
    { label: 'Total System Users', value: '—', accent: 'gold' },
    { label: 'Active Users', value: '—', accent: 'green' },
    { label: 'System Health', value: 'ONLINE', accent: 'green' }
  ];
});

const colors = ['navy', 'gold', 'green'];
const stationDistribution = computed(() => {
  const map = {};
  assets.value
    .filter(a => a.status !== 'DISPOSED')
    .forEach(a => {
      const stn = a.station || 'Unspecified';
      map[stn] = (map[stn] || 0) + 1;
    });
  return Object.entries(map).map(([name, value], idx) => ({
    name,
    value,
    color: colors[idx % colors.length]
  }));
});

const maxStationValue = computed(() => {
  if (!stationDistribution.value.length) return 1;
  return Math.max(...stationDistribution.value.map(s => s.value), 1);
});

const auditActivity = computed(() => {
  if (recentAudits.value.length) {
    return recentAudits.value.slice(0, 5).map(a => {
      const user = a.username || 'System';
      const action = a.action || 'Activity';
      const res = a.resourceType ? `${a.resourceType} #${a.resourceId || ''}` : (a.endpoint || '');
      const timeStr = a.timestamp ? new Date(a.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '';
      return `${timeStr ? '[' + timeStr + '] ' : ''}${user}: ${action} on ${res}`;
    });
  }
  if (overview.value?.systemStatus) {
    return [`System status: ${overview.value.systemStatus}`];
  }
  return ['System operational'];
});


async function loadDashboard() {
  try {
    const [ovData, assetData, auditData] = await Promise.all([
      api('/dashboard/overview').catch(() => null),
      api('/assets').catch(() => ({ content: [] })),
      api('/audits').catch(() => ({ content: [] }))
    ]);
    if (ovData) overview.value = ovData;
    
    let loadedAssets = Array.isArray(assetData) ? assetData : (assetData.content || []);
    if (!isAdmin.value && userStationId.value) {
      loadedAssets = loadedAssets.filter(a => a.stationId === userStationId.value);
    }
    assets.value = loadedAssets;
    
    recentAudits.value = Array.isArray(auditData) ? auditData : (auditData.content || []);
  } catch (e) {
    error.value = e.message;
  }
}

onMounted(() => {
  loadDashboard();
});
</script>

<style scoped>
.dashboard { max-width: 1280px; margin: 0 auto; }
.page-heading { margin-bottom: 16px; }
.page-heading h1 { margin: 0; color: #202020; font-size: 24px; line-height: 1.2; }
.page-heading p { margin: 4px 0 0; color: #5d6878; font-size: 14px; }
.kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 28px; }
.kpi-card { min-height: 102px; padding: 18px 20px; background: #fff; border: 1px solid #d7dee8; border-left: 8px solid; border-radius: 9px; }
.kpi-card p { margin: 0 0 4px; color: #5d6878; font-size: 14px; }
.kpi-card strong { color: #202020; font-size: 29px; line-height: 1; }
.accent-navy { border-left-color: #123f73; }
.accent-gold { border-left-color: #dfa30b; }
.accent-red { border-left-color: #ba2a25; }
.accent-green { border-left-color: #2f8436; }
.overview-grid { display: grid; grid-template-columns: 1.35fr 1fr; gap: 20px; margin-bottom: 20px; }
.panel { min-height: 280px; padding: 18px 20px; background: #fff; border: 1px solid #d7dee8; border-radius: 9px; }
.panel h2, .watchlist h2 { margin: 0; color: #202020; font-size: 17px; }
.station-chart { height: 210px; display: flex; align-items: end; justify-content: space-around; gap: 14px; padding: 12px 10px 0; }
.station-bar-group { display: flex; flex: 1; height: 100%; min-width: 58px; flex-direction: column; align-items: center; justify-content: end; }
.station-bar-group strong { margin-bottom: 4px; color: #202020; font-size: 13px; }
.station-bar-track { width: min(100%, 80px); height: 150px; display: flex; align-items: end; }
.station-bar { width: 100%; min-height: 12px; border-radius: 5px 5px 2px 2px; transition: height 0.4s ease; }
.station-bar.navy { background: #123f73; }
.station-bar.gold { background: #dfa30b; }
.station-bar.green { background: #2f8436; }
.station-bar-group span { margin-top: 8px; color: #5d6878; font-size: 12px; white-space: nowrap; }
.audit-list { margin: 12px 0 0; padding: 0; list-style: none; }
.audit-list li { position: relative; padding: 9px 0 9px 20px; color: #303030; font-size: 13px; border-bottom: 1px solid #f0f2f5; }
.audit-list li::before { content: ''; position: absolute; top: 15px; left: 1px; width: 7px; height: 7px; border-radius: 50%; background: #dfa30b; }
.watchlist h2 { margin-bottom: 10px; }
.table-wrap { overflow-x: auto; border: 1px solid #d7dee8; border-radius: 6px; }
table { width: 100%; border-collapse: collapse; background: #fff; min-width: 700px; }
th, td { padding: 11px 13px; text-align: left; font-size: 13px; }
th { color: #123f73; background: #e8eef5; font-weight: 700; }
tbody tr:nth-child(even) { background: #f7f8fa; }
.status-badge { border-radius: 999px; display: inline-block; font-size: 11px; font-weight: 700; padding: 3px 9px; text-transform: capitalize; }
.registered, .available { background: #D9F5E5; color: #16834D; }
.assigned { background: #E6F4FB; color: #2674A8; }
.disposal-requested { background: #FFF0C7; color: #B66A00; }
.disposed { background: #FDE0DE; color: #C9362B; }
.under-maintenance { background: #DCEEFE; color: #2674A8; }
.empty-msg { text-align: center; color: #7a8699; padding: 30px; font-size: 13px; }
@media (max-width: 900px) { .kpi-grid { grid-template-columns: repeat(2, 1fr); } .overview-grid { grid-template-columns: 1fr; } }
@media (max-width: 560px) { .kpi-grid { grid-template-columns: 1fr; } .station-chart { gap: 4px; padding-left: 0; padding-right: 0; } .station-bar-group span { font-size: 10px; transform: rotate(-25deg); transform-origin: top center; } }
</style>
