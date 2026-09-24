<template>
  <section class="disposal-page">
    <header class="page-heading">
      <div>
        <p class="eyebrow">Asset lifecycle</p>
        <h1>Disposal Requests</h1>
        <p class="subtitle">Manage and review asset decommissioning and disposal requests.</p>
      </div>
      <button class="primary-button" type="button" @click="showNewRequest = !showNewRequest">
        {{ showNewRequest ? 'Close Form' : '+ Submit Disposal Request' }}
      </button>
    </header>

    <!-- NEW DISPOSAL REQUEST FORM -->
    <section v-if="showNewRequest" class="panel request-form-panel">
      <h2>Submit Asset for Disposal</h2>
      <form @submit.prevent="submitNewRequest">
        <div class="form-grid">
          <label>
            Asset to Dispose *
            <select v-model="newRequestForm.assetId" required>
              <option value="">Select registered asset</option>
              <option v-for="asset in eligibleAssets" :key="asset.id" :value="asset.id">
                {{ asset.serialNumber }} ({{ asset.brand }} - {{ asset.type || asset.assetType }})
              </option>
            </select>
          </label>
          <label class="full-width">
            Disposal Reason *
            <input v-model="newRequestForm.reason" placeholder="e.g. Beyond economical repair, obsolete hardware..." required />
          </label>
        </div>
        <p v-if="submitError" class="result rejected">{{ submitError }}</p>
        <button class="primary-button" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? 'Submitting...' : 'Submit Request' }}
        </button>
      </form>
    </section>

    <!-- REQUESTS TABLE -->
    <section class="panel request-panel">
      <div class="panel-heading">
        <h2>Disposal Registry</h2>
        <select v-model="filterStatus">
          <option value="">All Statuses</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
        </select>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Serial No.</th>
              <th>Type</th>
              <th>Station</th>
              <th>Department</th>
              <th>Requested By</th>
              <th>Reason</th>
              <th>Date</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="request in filteredRequests"
              :key="request.id"
              :class="{ selected: selectedRequest && request.id === selectedRequest.id }"
              @click="selectedRequest = request"
            >
              <td><strong>{{ request.assetSerialNumber || ('Asset #' + request.assetId) }}</strong></td>
              <td>{{ request.assetType || '—' }}</td>
              <td>{{ request.stationName || '—' }}</td>
              <td>{{ request.departmentName || '—' }}</td>
              <td>{{ request.requestedByName || ('User #' + request.requestedBy) }}</td>
              <td>{{ request.reason }}</td>
              <td>{{ request.requestDate }}</td>
              <td>
                <span class="status" :class="request.status?.toLowerCase()">
                  {{ request.status }}
                </span>
              </td>
            </tr>
            <tr v-if="!filteredRequests.length">
              <td colspan="8" class="empty">No disposal requests found.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- CONFIRMATION / DECISION PANEL (ADMINISTRATOR) -->
    <section v-if="selectedRequest" class="panel confirmation-panel">
      <h2>Review Request — {{ selectedRequest.assetSerialNumber || ('Asset #' + selectedRequest.assetId) }}</h2>
      <div class="detail-list">
        <div>
          <span>Asset</span>
          <strong>{{ selectedRequest.assetSerialNumber || ('Asset #' + selectedRequest.assetId) }} ({{ selectedRequest.assetType || 'ICT Asset' }})</strong>
        </div>
        <div>
          <span>Station / Department</span>
          <strong>{{ selectedRequest.stationName || '—' }} — {{ selectedRequest.departmentName || '—' }}</strong>
        </div>
        <div>
          <span>Requested By</span>
          <strong>{{ selectedRequest.requestedByName || ('User #' + selectedRequest.requestedBy) }} on {{ selectedRequest.requestDate }}</strong>
        </div>
        <div>
          <span>Reason</span>
          <strong>{{ selectedRequest.reason }}</strong>
        </div>
        <div v-if="selectedRequest.status !== 'PENDING'">
          <span>Decision</span>
          <strong>{{ selectedRequest.status }} on {{ selectedRequest.approvalDate || '—' }}</strong>
        </div>
      </div>

      <div v-if="selectedRequest.status === 'PENDING'">
        <div class="decision-comment">
          <label>
            Decision Comments (optional)
            <input v-model="decisionComments" placeholder="Enter comments or justification..." />
          </label>
        </div>
        <div class="warning">
          ⚠ Approval will set the asset status to "Disposed" permanently. The asset record and history are retained for audit inspection.
        </div>
        <p v-if="message" class="result" :class="messageType">{{ message }}</p>
        <div v-if="isAdmin" class="button-row">
          <button
            class="reject-button"
            type="button"
            :disabled="isDeciding"
            @click="decideRequest('reject')"
          >
            Reject Request
          </button>
          <button
            class="approve-button"
            type="button"
            :disabled="isDeciding"
            @click="decideRequest('approve')"
          >
            Approve Disposal
          </button>
        </div>
        <p v-else class="admin-only-notice">
          Only Administrators can approve or reject disposal requests.
        </p>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { api } from '../../services/api';
import { useAuthStore } from '../../store/modules/auth';

const authStore = useAuthStore();
const isAdmin = computed(() => {
  const user = authStore.user || JSON.parse(localStorage.getItem('user') || '{}');
  return user.roles?.includes('ADMINISTRATOR');
});

const requests = ref([]);
const assets = ref([]);
const selectedRequest = ref(null);
const filterStatus = ref('');
const showNewRequest = ref(false);
const isSubmitting = ref(false);
const isDeciding = ref(false);
const submitError = ref('');
const message = ref('');
const messageType = ref('success');
const decisionComments = ref('');

const newRequestForm = reactive({
  assetId: '',
  reason: ''
});

const userStationId = computed(() => authStore.user?.stationId || '');

const filteredAssets = computed(() => {
  if (isAdmin.value || !userStationId.value) return assets.value;
  return assets.value.filter(a => a.stationId === userStationId.value);
});

const eligibleAssets = computed(() => {
  return filteredAssets.value.filter(a => a.status === 'REGISTERED');
});

const filteredRequests = computed(() => {
  let list = requests.value;
  if (!isAdmin.value && userStationId.value) {
    const validAssetIds = new Set(filteredAssets.value.map(a => a.id));
    list = list.filter(r => validAssetIds.has(r.assetId));
  }
  if (!filterStatus.value) return list;
  return list.filter(r => r.status === filterStatus.value);
});

async function load() {
  try {
    const [reqPage, assetPage] = await Promise.all([
      api('/disposal-requests').catch(() => ({ content: [] })),
      api('/assets').catch(() => ({ content: [] }))
    ]);
    requests.value = Array.isArray(reqPage) ? reqPage : (reqPage.content || []);
    assets.value = Array.isArray(assetPage) ? assetPage : (assetPage.content || []);
    if (requests.value.length && !selectedRequest.value) {
      selectedRequest.value = requests.value[0];
    } else if (selectedRequest.value) {
      selectedRequest.value = requests.value.find(r => r.id === selectedRequest.value.id) || requests.value[0] || null;
    }
  } catch (err) {
    console.error('Failed to load disposal requests', err);
  }
}

async function submitNewRequest() {
  submitError.value = '';
  if (!newRequestForm.assetId || !newRequestForm.reason.trim()) {
    submitError.value = 'Please select an asset and state the disposal reason.';
    return;
  }
  isSubmitting.value = true;
  try {
    await api('/disposal-requests', {
      method: 'POST',
      body: JSON.stringify({
        assetId: Number(newRequestForm.assetId),
        reason: newRequestForm.reason.trim()
      })
    });
    Object.assign(newRequestForm, { assetId: '', reason: '' });
    showNewRequest.value = false;
    await load();
  } catch (err) {
    submitError.value = err.message || 'Failed to submit disposal request.';
  } finally {
    isSubmitting.value = false;
  }
}

async function decideRequest(decision) {
  if (!selectedRequest.value) return;
  isDeciding.value = true;
  message.value = '';
  try {
    await api(`/disposal-requests/${selectedRequest.value.id}`, {
      method: 'PATCH',
      body: JSON.stringify({
        decision: decision,
        comments: decisionComments.value.trim() || null
      })
    });
    messageType.value = decision === 'approve' ? 'success' : 'rejected';
    message.value = `Disposal request ${decision}d successfully.`;
    decisionComments.value = '';
    await load();
  } catch (err) {
    messageType.value = 'rejected';
    message.value = err.message || 'Failed to decide disposal request.';
  } finally {
    isDeciding.value = false;
  }
}

onMounted(() => load());
</script>

<style scoped>
.disposal-page { max-width: 1100px; margin: 0 auto; }
.page-heading,.panel-heading { align-items: center; display: flex; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
.eyebrow { color: #2E90C8; font-size: 11px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
.page-heading h1 { color: #1D2939; font-size: 24px; margin: 4px 0; }
.subtitle { color: #7A8699; font-size: 13px; }
.primary-button { background: #2E90C8; border: 0; border-radius: 5px; color: white; cursor: pointer; font-size: 13px; font-weight: 700; padding: 10px 14px; }
.primary-button:hover { background: #247CAE; }
.primary-button:disabled { opacity: 0.6; cursor: not-allowed; }
.panel { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 18px; }
.request-form-panel { margin-bottom: 18px; }
.request-form-panel h2, .panel h2 { color: #1D2939; font-size: 16px; margin: 0 0 16px; }
.form-grid { display: grid; gap: 14px; grid-template-columns: 1fr 2fr; margin-bottom: 14px; }
.form-grid label { color: #475467; display: grid; font-size: 12px; font-weight: 700; gap: 6px; }
.form-grid input, .form-grid select, .panel-heading select, .decision-comment input { border: 1px solid #CDD5DF; border-radius: 5px; color: #344054; padding: 9px; font: inherit; }
.request-panel { margin-bottom: 16px; }
.table-wrap { overflow-x: auto; }
.request-panel table { border-collapse: collapse; min-width: 850px; width: 100%; }
.request-panel th { background: #F2F4F7; color: #5D6C80; font-size: 12px; padding: 10px; text-align: left; }
.request-panel td { border-bottom: 1px solid #E9EDF2; color: #3F4854; font-size: 13px; padding: 11px 10px; white-space: nowrap; }
.request-panel tbody tr { cursor: pointer; }
.request-panel tbody tr:hover, .request-panel tr.selected { background: #F8FBFD; }
.status { border-radius: 999px; display: inline-block; font-size: 11px; font-weight: 700; padding: 4px 10px; text-transform: capitalize; }
.pending { background: #FFF0C7; color: #B66A00; }
.approved { background: #D9F5E5; color: #16834D; }
.rejected { background: #FDE0DE; color: #C9362B; }
.empty { color: #7A8699; text-align: center; }
.confirmation-panel { border-color: #F3A7A1; }
.detail-list > div { border-bottom: 1px solid #E9EDF2; display: grid; gap: 20px; grid-template-columns: 140px 1fr; padding: 10px 0; }
.detail-list span { color: #7A8699; font-size: 13px; }
.detail-list strong { color: #3F4854; font-size: 13px; font-weight: 500; }
.decision-comment { margin-top: 14px; }
.decision-comment label { color: #475467; display: grid; font-size: 12px; font-weight: 700; gap: 6px; }
.warning { background: #FFF9E8; color: #967000; font-size: 13px; margin-top: 14px; padding: 11px 14px; border-radius: 4px; }
.result { font-size: 13px; margin: 12px 0 0; }
.result.success { color: #16834D; }
.result.rejected { color: #C9362B; }
.button-row { display: flex; gap: 10px; margin-top: 16px; }
.reject-button, .approve-button { border: 0; border-radius: 5px; color: white; cursor: pointer; font-size: 13px; font-weight: 700; padding: 9px 16px; }
.reject-button { background: #C9362B; }
.reject-button:hover { background: #A8251C; }
.approve-button { background: #2E90C8; }
.approve-button:hover { background: #247CAE; }
.admin-only-notice { color: #7A8699; font-size: 12px; font-style: italic; margin-top: 12px; }
@media (max-width: 650px) { .form-grid { grid-template-columns: 1fr; } .detail-list > div { gap: 8px; grid-template-columns: 1fr; } }
</style>
