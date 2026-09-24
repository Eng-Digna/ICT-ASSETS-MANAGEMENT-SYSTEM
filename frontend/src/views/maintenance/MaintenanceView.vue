<template>
  <section class="page">
    <header class="page-heading">
      <div>
        <p class="eyebrow">Asset lifecycle</p>
        <h1>Maintenance</h1>
        <p class="subtitle">Monitor repairs, service schedules, and assets currently under maintenance.</p>
      </div>
      <button class="primary-button" type="button" @click="showForm = !showForm">
        {{ showForm ? 'Close Form' : '+ Log Maintenance' }}
      </button>
    </header>

    <!-- FORM -->
    <section v-if="showForm" class="panel form-panel">
      <h2>Log Maintenance Activity</h2>
      <div class="form-grid">
        <label>
          Asset *
          <select v-model="form.assetId" required>
            <option value="">Select asset to service</option>
            <option v-for="asset in serviceableAssets" :key="asset.id" :value="asset.id">
              {{ asset.serialNumber }} ({{ asset.brand }} - {{ asset.type || asset.assetType }})
            </option>
          </select>
        </label>
        <label>
          Issue Description *
          <input v-model="form.issue" placeholder="Describe the fault or service needed" required />
        </label>
        <label>
          Service Date *
          <input v-model="form.serviceDate" type="date" required />
        </label>
      </div>

      <p v-if="error" class="error-text">{{ error }}</p>
      <p v-if="successMsg" class="success-text">{{ successMsg }}</p>

      <button class="primary-button" type="button" :disabled="isSubmitting" @click="addRecord">
        {{ isSubmitting ? 'Saving...' : 'Save Record' }}
      </button>
    </section>

    <!-- STATS -->
    <section class="stats">
      <article>
        <span>Under maintenance</span>
        <strong>{{ assetsUnderMaintenanceCount }}</strong>
      </article>
      <article>
        <span>Total service records</span>
        <strong>{{ records.length }}</strong>
      </article>
      <article>
        <span>Healthy / Available assets</span>
        <strong>{{ availableAssetsCount }}</strong>
      </article>
    </section>

    <!-- TABLE -->
    <section class="panel table-panel">
      <div class="panel-heading">
        <h2>Maintenance Work Orders & History</h2>
        <input v-model="search" placeholder="Search by serial, issue, or technician..." />
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Asset Serial</th>
              <th>Brand / Model</th>
              <th>Description / Issue</th>
              <th>Logged By</th>
              <th>Service Date</th>
              <th>Asset Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="record in filteredRecords" :key="record.id">
              <td><strong>{{ record.assetSerialNumber || ('Asset #' + record.assetId) }}</strong></td>
              <td>{{ record.brand ? record.brand + (record.model ? ' - ' + record.model : '') : '—' }}</td>
              <td>{{ record.description }}</td>
              <td>{{ record.loggedByName || ('User #' + record.loggedBy) }}</td>
              <td>{{ record.serviceDate }}</td>
              <td>
                <span class="status" :class="statusClass(record.assetStatus)">
                  {{ formatStatus(record.assetStatus) }}
                </span>
              </td>
              <td>
                <button
                  v-if="record.assetStatus === 'UNDER_MAINTENANCE'"
                  class="action-btn complete-btn"
                  type="button"
                  @click="completeRecord(record.id)"
                >
                  Complete Service
                </button>
                <span v-else class="muted-text">—</span>
              </td>
            </tr>
            <tr v-if="!filteredRecords.length">
              <td colspan="7" class="empty">No maintenance records found.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { api } from '../../services/api';
import { useAuthStore } from '../../store/modules/auth';

const authStore = useAuthStore();
const isAdmin = computed(() => authStore.userRole === 'ADMINISTRATOR');
const userStationId = computed(() => authStore.user?.stationId || '');

const showForm = ref(false);
const search = ref('');
const isSubmitting = ref(false);
const error = ref('');
const successMsg = ref('');

const form = reactive({
  assetId: '',
  issue: '',
  serviceDate: new Date().toISOString().slice(0, 10)
});

const records = ref([]);
const assets = ref([]);

const filteredAssets = computed(() => {
  if (isAdmin.value || !userStationId.value) return assets.value;
  return assets.value.filter(a => a.stationId === userStationId.value);
});

const serviceableAssets = computed(() => {
  return filteredAssets.value.filter(a => a.status === 'REGISTERED' || a.status === 'AVAILABLE' || a.status === 'ASSIGNED');
});

const assetsUnderMaintenanceCount = computed(() => {
  return filteredAssets.value.filter(a => a.status === 'UNDER_MAINTENANCE').length;
});

const availableAssetsCount = computed(() => {
  return filteredAssets.value.filter(a => a.status === 'REGISTERED' || a.status === 'AVAILABLE').length;
});

const filteredRecords = computed(() => {
  let list = records.value;
  if (!isAdmin.value && userStationId.value) {
    // If maintenance record does not have stationId directly, we filter based on if the asset is in our filteredAssets
    const validAssetIds = new Set(filteredAssets.value.map(a => a.id));
    list = list.filter(r => validAssetIds.has(r.assetId));
  }

  const q = search.value.trim().toLowerCase();
  if (!q) return list;
  return list.filter(r => {
    return [
      r.assetSerialNumber,
      r.brand,
      r.model,
      r.description,
      r.loggedByName,
      r.assetStatus
    ].some(val => val && String(val).toLowerCase().includes(q));
  });
});

function statusClass(status) {
  return (status || '').toLowerCase().replaceAll('_', '-');
}

function formatStatus(status) {
  if (!status) return 'Completed';
  return status.replaceAll('_', ' ');
}

async function load() {
  try {
    const [recordPage, assetPage] = await Promise.all([
      api('/maintenance-records').catch(() => ({ content: [] })),
      api('/assets').catch(() => ({ content: [] }))
    ]);
    records.value = Array.isArray(recordPage) ? recordPage : (recordPage.content || []);
    assets.value = Array.isArray(assetPage) ? assetPage : (assetPage.content || []);
  } catch (err) {
    error.value = 'Failed to load maintenance records: ' + err.message;
  }
}

async function addRecord() {
  error.value = '';
  successMsg.value = '';
  if (!form.assetId || !form.issue.trim()) {
    error.value = 'Please select an asset and describe the issue.';
    return;
  }
  isSubmitting.value = true;
  try {
    await api('/maintenance-records', {
      method: 'POST',
      body: JSON.stringify({
        assetId: Number(form.assetId),
        description: form.issue.trim(),
        serviceDate: form.serviceDate || new Date().toISOString().slice(0, 10)
      })
    });
    successMsg.value = 'Maintenance activity logged successfully.';
    Object.assign(form, { assetId: '', issue: '', serviceDate: new Date().toISOString().slice(0, 10) });
    showForm.value = false;
    await load();
  } catch (err) {
    error.value = err.message || 'Failed to log maintenance record.';
  } finally {
    isSubmitting.value = false;
  }
}

async function completeRecord(recordId) {
  if (!confirm('Mark maintenance as complete and restore asset to inventory?')) return;
  try {
    await api(`/maintenance-records/${recordId}/complete`, {
      method: 'PATCH'
    });
    await load();
  } catch (err) {
    alert('Failed to complete maintenance: ' + err.message);
  }
}

onMounted(() => load());
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; }
.page-heading,.panel-heading { align-items: center; display: flex; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
.eyebrow { color: #2E90C8; font-size: 11px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
.page h1 { color: #1D2939; font-size: 24px; margin: 4px 0; }
.subtitle { color: #7A8699; font-size: 13px; }
.primary-button { background: #2E90C8; border: 0; border-radius: 5px; color: white; cursor: pointer; font-weight: 700; padding: 10px 14px; font-size: 13px; }
.primary-button:hover { background: #247CAE; }
.primary-button:disabled { opacity: 0.6; cursor: not-allowed; }
.panel { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 18px; }
.form-panel { margin-bottom: 18px; }
.panel h2 { color: #1D2939; font-size: 16px; margin: 0 0 16px; }
.form-grid { display: grid; gap: 14px; grid-template-columns: 1fr 2fr 1fr; margin-bottom: 16px; }
.form-grid label { color: #475467; display: grid; font-size: 12px; font-weight: 700; gap: 6px; }
.form-grid input,.form-grid select,.panel-heading input { border: 1px solid #CDD5DF; border-radius: 5px; color: #344054; padding: 9px; font: inherit; }
.stats { display: grid; gap: 14px; grid-template-columns: repeat(3, 1fr); margin-bottom: 18px; }
.stats article { background: #F2F4F7; border-left: 3px solid #2E90C8; padding: 14px 16px; border-radius: 4px; }
.stats span { color: #667085; display: block; font-size: 12px; }
.stats strong { color: #1D2939; display: block; font-size: 24px; margin-top: 5px; }
.table-wrap { overflow-x: auto; }
.table-panel table { border-collapse: collapse; min-width: 800px; width: 100%; }
.table-panel th { background: #F2F4F7; color: #5D6C80; font-size: 12px; padding: 10px; text-align: left; }
.table-panel td { border-bottom: 1px solid #E9EDF2; color: #3F4854; font-size: 13px; padding: 11px 10px; }
.status { border-radius: 999px; font-size: 11px; font-weight: 700; padding: 4px 10px; text-transform: capitalize; }
.under-maintenance { background: #DCEEFE; color: #2674A8; }
.registered, .available { background: #D9F5E5; color: #16834D; }
.action-btn { border: 0; border-radius: 4px; cursor: pointer; font-size: 12px; font-weight: 600; padding: 5px 10px; }
.complete-btn { background: #D1FADF; color: #027A48; }
.complete-btn:hover { background: #A6F4C5; }
.muted-text { color: #98A2B3; font-size: 12px; }
.empty { color: #7A8699; text-align: center; }
.error-text { color: #B42318; font-size: 13px; margin: 8px 0; }
.success-text { color: #16834D; font-size: 13px; margin: 8px 0; }
.panel-heading input { min-width: 260px; }
@media (max-width: 700px) { .page-heading,.panel-heading { align-items: flex-start; flex-direction: column; }.form-grid,.stats { grid-template-columns: 1fr; }.panel-heading input { min-width: 0; width: 100%; } }
</style>
