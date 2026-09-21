<template>
  <div class="dashboard">
    <div class="page-heading">
      <h1>Dashboard</h1>
      <p>Overview across TPA Headquarters and all Bandari stations</p>
    </div>

    <section class="kpi-grid" aria-label="Management KPIs">
      <article v-for="kpi in kpis" :key="kpi.label" class="kpi-card" :class="`accent-${kpi.accent}`">
        <p>{{ kpi.label }}</p>
        <strong>{{ kpi.value }}</strong>
      </article>
    </section>

    <section class="overview-grid">
      <article class="panel station-panel">
        <h2>Asset Distribution by Station</h2>
        <div class="station-chart" aria-label="Asset distribution by station">
          <div v-for="station in stationDistribution" :key="station.name" class="station-bar-group">
            <strong>{{ station.value }}</strong>
            <div class="station-bar-track">
              <div class="station-bar" :class="station.color" :style="{ height: `${(station.value / maxStationValue) * 100}%` }"></div>
            </div>
            <span>{{ station.name }}</span>
          </div>
        </div>
      </article>

      <article class="panel audit-panel">
        <h2>Recent Audit Activity</h2>
        <ul class="audit-list">
          <li v-for="activity in auditActivity" :key="activity">{{ activity }}</li>
        </ul>
      </article>
    </section>

    <section class="watchlist">
      <h2>Warranty Expiring Soon</h2>
      <div class="table-wrap">
        <table>
          <thead>
            <tr><th>Serial No.</th><th>Type</th><th>Station</th><th>Warranty End</th></tr>
          </thead>
          <tbody>
            <tr v-for="item in warrantyItems" :key="item.serial">
              <td>{{ item.serial }}</td><td>{{ item.type }}</td><td>{{ item.station }}</td><td>{{ item.warrantyEnd }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { getDashboardOverview, listAssets } from '@/services/api';

const kpis = ref([]);

const stationDistribution = ref([]);

const maxStationValue = computed(() => Math.max(...stationDistribution.value.map((station) => station.value)));

const auditActivity = ref([]);

const warrantyItems = ref([]);

onMounted(async () => {
  const [overview, assetPage] = await Promise.all([getDashboardOverview(), listAssets()]);
  const assets = assetPage.data;
  kpis.value = [
    { label: 'Total Assets', value: overview.totalAssets, accent: 'navy' },
    { label: 'Assigned', value: assets.filter((asset) => asset.status === 'ASSIGNED').length, accent: 'gold' },
    { label: 'Pending Disposal', value: assets.filter((asset) => asset.status === 'PENDING_DISPOSAL').length, accent: 'red' },
    { label: 'Active Users', value: overview.activeUsers, accent: 'green' }
  ];
  const counts = assets.reduce((result, asset) => { result[asset.station] = (result[asset.station] || 0) + 1; return result; }, {});
  stationDistribution.value = Object.entries(counts).map(([name, value], index) => ({ name, value, color: ['navy', 'gold', 'green'][index % 3] }));
  warrantyItems.value = assets.filter((asset) => asset.warrantyEndDate).slice(0, 5).map((asset) => ({ serial: asset.serialNumber, type: asset.type, station: asset.station, warrantyEnd: asset.warrantyEndDate }));
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
.accent-navy { border-left-color: #123f73; }.accent-gold { border-left-color: #dfa30b; }.accent-red { border-left-color: #ba2a25; }.accent-green { border-left-color: #2f8436; }

.overview-grid { display: grid; grid-template-columns: 1.35fr 1fr; gap: 20px; margin-bottom: 10px; }
.panel { min-height: 302px; padding: 18px 20px; background: #fff; border: 1px solid #d7dee8; border-radius: 9px; }
.panel h2, .watchlist h2 { margin: 0; color: #202020; font-size: 17px; }
.station-chart { height: 230px; display: flex; align-items: end; justify-content: space-around; gap: 14px; padding: 12px 10px 0; }
.station-bar-group { display: flex; flex: 1; height: 100%; min-width: 58px; flex-direction: column; align-items: center; justify-content: end; }
.station-bar-group strong { margin-bottom: 4px; color: #202020; font-size: 13px; }
.station-bar-track { width: min(100%, 100px); height: 170px; display: flex; align-items: end; }
.station-bar { width: 100%; min-height: 16px; border-radius: 5px 5px 2px 2px; }
.station-bar.navy { background: #123f73; }.station-bar.gold { background: #dfa30b; }.station-bar.green { background: #2f8436; }
.station-bar-group span { margin-top: 8px; color: #5d6878; font-size: 12px; white-space: nowrap; }
.audit-list { margin: 12px 0 0; padding: 0; list-style: none; }
.audit-list li { position: relative; padding: 11px 0 11px 21px; color: #303030; font-size: 13px; }
.audit-list li::before { content: ''; position: absolute; top: 17px; left: 1px; width: 8px; height: 8px; border-radius: 50%; background: #dfa30b; }

.watchlist h2 { margin-bottom: 10px; }
.table-wrap { overflow-x: auto; border: 1px solid #d7dee8; }
table { width: 100%; border-collapse: collapse; background: #fff; }
th, td { padding: 11px 13px; text-align: left; font-size: 13px; }
th { color: #123f73; background: #e8eef5; font-weight: 700; }
tbody tr:nth-child(even) { background: #f7f8fa; }

@media (max-width: 900px) { .kpi-grid { grid-template-columns: repeat(2, 1fr); }.overview-grid { grid-template-columns: 1fr; } }
@media (max-width: 560px) { .kpi-grid { grid-template-columns: 1fr; }.station-chart { gap: 4px; padding-left: 0; padding-right: 0; }.station-bar-group span { font-size: 10px; transform: rotate(-25deg); transform-origin: top center; } }
</style>
