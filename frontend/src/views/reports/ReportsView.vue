<template>
  <section class="page">
    <header class="page-heading">
      <div>
        <p class="eyebrow">Management information</p>
        <h1>Executive & Operational Reports</h1>
        <p class="subtitle">Review live asset inventory, assignment status, maintenance volume, and disposal activity.</p>
      </div>
      <button class="primary-button" type="button" @click="downloadReport">Print / Export Report</button>
    </header>

    <!-- STATS -->
    <section class="stats">
      <article>
        <span>Total ICT Assets</span>
        <strong>{{ totalAssets }}</strong>
        <small>Across all TPA ports and HQ</small>
      </article>
      <article>
        <span>Assigned to Staff</span>
        <strong>{{ assignedCount }}</strong>
        <small>{{ totalAssets ? Math.round((assignedCount / totalAssets) * 100) : 0 }}% of inventory</small>
      </article>
      <article>
        <span>Pending Disposal</span>
        <strong>{{ pendingDisposalCount }}</strong>
        <small>Awaiting review</small>
      </article>
      <article>
        <span>Available in Stock</span>
        <strong>{{ availableCount }}</strong>
        <small>Ready for deployment</small>
      </article>
    </section>

    <!-- GRAPHS / BREAKDOWNS -->
    <div class="report-grid">
      <section class="panel">
        <div class="panel-heading">
          <h2>Assets by Status</h2>
          <span>Live inventory breakdown</span>
        </div>
        <div v-for="item in statusSummary" :key="item.label" class="bar-row">
          <div>
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
          <div class="bar">
            <i :style="{ width: `${totalAssets ? (item.value / totalAssets) * 100 : 0}%` }"></i>
          </div>
        </div>
        <p v-if="!statusSummary.length" class="empty">No assets recorded yet.</p>
      </section>

      <section class="panel">
        <div class="panel-heading">
          <h2>Assets by Station</h2>
          <span>Geographical distribution</span>
        </div>
        <div v-for="item in stationSummary" :key="item.label" class="station-row">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }} assets</strong>
        </div>
        <p v-if="!stationSummary.length" class="empty">No station data available.</p>
      </section>
    </div>

    <!-- RECENT ACTIVITY OVERVIEW -->
    <section class="panel table-panel">
      <div class="panel-heading">
        <h2>Subsystem Summary</h2>
        <span>Database record aggregates</span>
      </div>
      <table>
        <thead>
          <tr>
            <th>Module</th>
            <th>Primary Status</th>
            <th>Total Records</th>
            <th>Action Link</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><strong>Asset Directory</strong></td>
            <td>{{ availableCount }} Available / {{ assignedCount }} Assigned</td>
            <td>{{ totalAssets }} assets</td>
            <td><RouterLink to="/assets" class="table-link">View Directory</RouterLink></td>
          </tr>
          <tr>
            <td><strong>Staff Custody & Assignments</strong></td>
            <td>{{ activeAssignmentsCount }} Active Custody</td>
            <td>{{ totalAssignments }} records</td>
            <td><RouterLink to="/assignments" class="table-link">View Assignments</RouterLink></td>
          </tr>
          <tr>
            <td><strong>Hardware Maintenance</strong></td>
            <td>{{ underMaintenanceCount }} Under Maintenance</td>
            <td>{{ totalMaintenanceRecords }} records</td>
            <td><RouterLink to="/maintenance" class="table-link">View Maintenance</RouterLink></td>
          </tr>
          <tr>
            <td><strong>Decommissioning & Disposal</strong></td>
            <td>{{ pendingDisposalCount }} Pending Approval</td>
            <td>{{ totalDisposalRequests }} requests</td>
            <td><RouterLink to="/disposal" class="table-link">View Disposal</RouterLink></td>
          </tr>
        </tbody>
      </table>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { api } from '../../services/api';

const assets = ref([]);
const assignments = ref([]);
const maintenanceRecords = ref([]);
const disposalRequests = ref([]);

const totalAssets = computed(() => assets.value.length);
const assignedCount = computed(() => assets.value.filter(a => a.status === 'ASSIGNED').length);
const availableCount = computed(() => assets.value.filter(a => a.status === 'REGISTERED' || a.status === 'AVAILABLE').length);
const underMaintenanceCount = computed(() => assets.value.filter(a => a.status === 'UNDER_MAINTENANCE').length);
const pendingDisposalCount = computed(() => disposalRequests.value.filter(d => d.status === 'PENDING').length);

const totalAssignments = computed(() => assignments.value.length);
const activeAssignmentsCount = computed(() => assignments.value.filter(a => a.status === 'ACTIVE').length);

const totalMaintenanceRecords = computed(() => maintenanceRecords.value.length);
const totalDisposalRequests = computed(() => disposalRequests.value.length);

const statusSummary = computed(() => {
  const statuses = [
    { label: 'Available / Registered', key: 'REGISTERED' },
    { label: 'Assigned to Staff', key: 'ASSIGNED' },
    { label: 'Under Maintenance', key: 'UNDER_MAINTENANCE' },
    { label: 'Pending Disposal', key: 'DISPOSAL_REQUESTED' },
    { label: 'Disposed', key: 'DISPOSED' }
  ];
  return statuses.map(s => {
    let count = 0;
    if (s.key === 'REGISTERED') {
      count = assets.value.filter(a => a.status === 'REGISTERED' || a.status === 'AVAILABLE').length;
    } else {
      count = assets.value.filter(a => a.status === s.key).length;
    }
    return { label: s.label, value: count };
  });
});

const stationSummary = computed(() => {
  const map = {};
  assets.value.forEach(a => {
    const stn = a.station || 'Unspecified';
    map[stn] = (map[stn] || 0) + 1;
  });
  return Object.entries(map)
    .map(([label, value]) => ({ label, value }))
    .sort((a, b) => b.value - a.value);
});

function downloadReport() {
  window.print();
}

async function loadData() {
  try {
    const [assetPage, assignPage, maintPage, dispPage] = await Promise.all([
      api('/assets').catch(() => ({ content: [] })),
      api('/assignments').catch(() => ({ content: [] })),
      api('/maintenance-records').catch(() => ({ content: [] })),
      api('/disposal-requests').catch(() => ({ content: [] }))
    ]);
    assets.value = Array.isArray(assetPage) ? assetPage : (assetPage.content || []);
    assignments.value = Array.isArray(assignPage) ? assignPage : (assignPage.content || []);
    maintenanceRecords.value = Array.isArray(maintPage) ? maintPage : (maintPage.content || []);
    disposalRequests.value = Array.isArray(dispPage) ? dispPage : (dispPage.content || []);
  } catch (err) {
    console.error('Failed to load report data', err);
  }
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; }
.page-heading,.panel-heading { align-items: center; display: flex; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
.eyebrow { color: #2E90C8; font-size: 11px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
.page h1 { color: #1D2939; font-size: 24px; margin: 4px 0; }
.subtitle { color: #7A8699; font-size: 13px; }
.primary-button { background: #2E90C8; border: 0; border-radius: 5px; color: white; cursor: pointer; font-weight: 700; padding: 10px 14px; font-size: 13px; }
.primary-button:hover { background: #247CAE; }
.panel { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 18px; }
.stats { display: grid; gap: 14px; grid-template-columns: repeat(4, 1fr); margin-bottom: 18px; }
.stats article { background: #F2F4F7; border-top: 3px solid #2E90C8; padding: 14px 16px; border-radius: 4px; }
.stats span, .stats small, .panel-heading span { color: #667085; display: block; font-size: 12px; }
.stats strong { color: #1D2939; display: block; font-size: 24px; margin: 5px 0; }
.report-grid { display: grid; gap: 18px; grid-template-columns: 1fr 1fr; margin-bottom: 18px; }
.panel h2 { color: #1D2939; font-size: 16px; margin: 0; }
.bar-row { margin: 18px 0; }
.bar-row > div:first-child { display: flex; justify-content: space-between; color: #475467; font-size: 13px; }
.bar { background: #E9EDF2; height: 8px; margin-top: 8px; border-radius: 4px; overflow: hidden; }
.bar i { background: #2E90C8; display: block; height: 100%; border-radius: 4px; transition: width 0.3s ease; }
.station-row { border-bottom: 1px solid #E9EDF2; color: #475467; display: flex; font-size: 13px; justify-content: space-between; padding: 12px 0; }
.table-panel { overflow-x: auto; }
.table-panel table { border-collapse: collapse; min-width: 700px; width: 100%; }
.table-panel th { background: #F2F4F7; color: #5D6C80; font-size: 12px; padding: 10px; text-align: left; }
.table-panel td { border-bottom: 1px solid #E9EDF2; color: #3F4854; font-size: 13px; padding: 11px 10px; }
.table-link { color: #2E90C8; font-weight: 600; text-decoration: none; font-size: 12px; }
.table-link:hover { text-decoration: underline; }
.empty { color: #7A8699; text-align: center; padding: 12px 0; }
@media (max-width: 700px) { .page-heading,.panel-heading { align-items: flex-start; flex-direction: column; }.stats,.report-grid { grid-template-columns: 1fr; } }
</style>
