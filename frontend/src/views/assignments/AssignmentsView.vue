<template>
  <section class="page">
    <header class="page-heading">
      <div>
        <p class="eyebrow">Asset operations</p>
        <h1>Asset Assignments</h1>
        <p class="subtitle">Assign ICT assets to staff and track custody across TPA stations.</p>
      </div>
      <button class="primary-button" type="button" @click="showForm = !showForm">
        {{ showForm ? 'Close Form' : '+ New Assignment' }}
      </button>
    </header>

    <!-- ASSIGNMENT FORM -->
    <section v-if="showForm" class="panel assignment-form">
      <h2>Assign an ICT Asset</h2>
      <div class="form-grid">
        <label>
          Available Asset *
          <select v-model="form.assetId" required>
            <option value="">Select available asset</option>
            <option v-for="asset in availableAssets" :key="asset.id" :value="asset.id">
              {{ asset.serialNumber }} ({{ asset.brand }} - {{ asset.type || asset.assetType }})
            </option>
          </select>
        </label>

        <label>
          Assigned Staff Name *
          <input v-model="form.assigneeName" type="text" placeholder="Type full name of the staff" required />
        </label>

        <label>
          Station *
          <select v-model="form.stationId" required>
            <option value="">Select station</option>
            <option v-for="stn in stations" :key="stn.id" :value="stn.id">
              {{ stn.name }}
            </option>
          </select>
        </label>
      </div>

      <p v-if="error" class="error-text">{{ error }}</p>
      <p v-if="successMsg" class="success-text">{{ successMsg }}</p>

      <button class="primary-button" type="button" :disabled="isSubmitting" @click="addAssignment">
        {{ isSubmitting ? 'Saving...' : 'Confirm Assignment' }}
      </button>
    </section>

    <!-- STATS -->
    <section class="stats">
      <article>
        <span>Active assignments</span>
        <strong>{{ activeAssignmentsCount }}</strong>
      </article>
      <article>
        <span>Available for assignment</span>
        <strong>{{ availableAssets.length }}</strong>
      </article>
      <article>
        <span>Total assignment records</span>
        <strong>{{ assignments.length }}</strong>
      </article>
    </section>

    <!-- TABLE -->
    <section class="panel table-panel">
      <div class="panel-heading">
        <h2>Assignment History & Current Custody</h2>
        <input v-model="search" placeholder="Search by asset, user, or station..." />
      </div>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Asset Serial</th>
              <th>Asset Type</th>
              <th>Assigned To</th>
              <th>Station</th>
              <th>Assigned Date</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredAssignments" :key="item.id">
              <td><strong>{{ item.assetSerialNumber || ('Asset #' + item.assetId) }}</strong></td>
              <td>{{ item.assetType || '—' }}</td>
              <td>{{ item.assigneeName || ('User #' + item.userId) }}</td>
              <td>{{ item.stationName || ('Station #' + item.stationId) }}</td>
              <td>{{ item.assignedDate || '—' }}</td>
              <td>
                <span class="status" :class="item.status?.toLowerCase()">
                  {{ item.status }}
                </span>
              </td>
              <td>
                <button
                  v-if="item.status === 'ACTIVE'"
                  class="action-btn return-btn"
                  type="button"
                  @click="returnAsset(item.id)"
                >
                  Return Asset
                </button>
                <span v-else class="muted-text">Returned</span>
              </td>
            </tr>
            <tr v-if="!filteredAssignments.length">
              <td colspan="7" class="empty">No assignments found.</td>
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

const search = ref('');
const showForm = ref(false);
const isSubmitting = ref(false);
const error = ref('');
const successMsg = ref('');

const form = reactive({
  assetId: '',
  assigneeName: '',
  stationId: ''
});

const assignments = ref([]);
const allAssets = ref([]);

const stations = ref([]);

const availableAssets = computed(() => {
  return allAssets.value.filter(a => a.status === 'REGISTERED' || a.status === 'AVAILABLE');
});

const activeAssignmentsCount = computed(() => {
  return assignments.value.filter(a => a.status === 'ACTIVE').length;
});

const filteredAssignments = computed(() => {
  const q = search.value.trim().toLowerCase();
  if (!q) return assignments.value;
  return assignments.value.filter(item => {
    return [
      item.assetSerialNumber,
      item.assetType,
      item.assigneeName,
      item.stationName,
      item.status
    ].some(val => val && String(val).toLowerCase().includes(q));
  });
});

async function loadData() {
  try {
    const [assignmentRes, assetRes, stationRes] = await Promise.all([
      api('/assignments').catch(() => ({ content: [] })),
      api('/assets').catch(() => ({ content: [] })),
      
      api('/stations').catch(() => [])
    ]);

    assignments.value = Array.isArray(assignmentRes) ? assignmentRes : (assignmentRes.content || []);
    allAssets.value = Array.isArray(assetRes) ? assetRes : (assetRes.content || []);
    
    stations.value = Array.isArray(stationRes) ? stationRes : (Array.isArray(stationRes) ? stationRes : (stationRes.content || []));
  } catch (err) {
    error.value = 'Failed to load assignment data: ' + err.message;
  }
}

async function addAssignment() {
  error.value = '';
  successMsg.value = '';

  if (!form.assetId || !form.assigneeName || !form.stationId) {
    error.value = 'Please select asset, user, and station.';
    return;
  }

  isSubmitting.value = true;
  try {
    await api('/assignments', {
      method: 'POST',
      body: JSON.stringify({
        assetId: Number(form.assetId),
        assigneeName: form.assigneeName.trim(),
        stationId: Number(form.stationId)
      })
    });
    successMsg.value = 'Asset assigned successfully!';
    Object.assign(form, { assetId: '', assigneeName: '', stationId: '' });
    showForm.value = false;
    await loadData();
  } catch (err) {
    error.value = err.message || 'Failed to assign asset.';
  } finally {
    isSubmitting.value = false;
  }
}

async function returnAsset(assignmentId) {
  if (!confirm('Are you sure you want to return this asset to inventory?')) return;
  try {
    await api(`/assignments/${assignmentId}`, {
      method: 'PATCH',
      body: JSON.stringify({ action: 'return' })
    });
    await loadData();
  } catch (err) {
    alert('Failed to return asset: ' + err.message);
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
.primary-button:disabled { opacity: 0.6; cursor: not-allowed; }
.panel { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 18px; }
.assignment-form { margin-bottom: 18px; }
.assignment-form h2,.panel h2 { color: #1D2939; font-size: 16px; margin: 0 0 16px; }
.form-grid { display: grid; gap: 14px; grid-template-columns: repeat(3, 1fr); margin-bottom: 16px; }
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
.status { border-radius: 999px; display: inline-block; font-size: 11px; font-weight: 700; padding: 4px 10px; }
.active { background: #D9F5E5; color: #16834D; }
.returned { background: #F2F4F7; color: #667085; }
.action-btn { border: 0; border-radius: 4px; cursor: pointer; font-size: 12px; font-weight: 600; padding: 5px 10px; }
.return-btn { background: #FEE4E2; color: #B42318; }
.return-btn:hover { background: #FECDCA; }
.muted-text { color: #98A2B3; font-size: 12px; }
.empty { color: #7A8699; text-align: center; }
.error-text { color: #B42318; font-size: 13px; margin: 8px 0; }
.success-text { color: #16834D; font-size: 13px; margin: 8px 0; }
.panel-heading input { min-width: 260px; }
@media (max-width: 700px) { .page-heading,.panel-heading { align-items: flex-start; flex-direction: column; }.form-grid,.stats { grid-template-columns: 1fr; }.panel-heading input { min-width: 0; width: 100%; } }
</style>
