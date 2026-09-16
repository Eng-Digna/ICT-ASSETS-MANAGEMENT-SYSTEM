<template>
  <section class="disposal-page">
    <header class="page-heading"><div><p class="eyebrow">Asset lifecycle</p><h1>Disposal Requests</h1><p class="subtitle">Pending disposal requests requiring Administrator approval</p></div></header>
    <section class="panel request-panel"><div class="table-wrap"><table><thead><tr><th>Serial No.</th><th>Type</th><th>Station</th><th>Requested By</th><th>Reason</th><th>Date</th><th>Status</th></tr></thead><tbody><tr v-for="request in requests" :key="request.serial" :class="{ selected: request.serial === selectedRequest.serial }" @click="selectedRequest = request"><td><strong>{{ request.serial }}</strong></td><td>{{ request.type }}</td><td>{{ request.station }}</td><td>{{ request.requestedBy }}</td><td>{{ request.reason }}</td><td>{{ request.date }}</td><td><span class="status" :class="request.status.toLowerCase()">{{ request.status }}</span></td></tr><tr v-if="!requests.length"><td colspan="7" class="empty">No disposal requests found.</td></tr></tbody></table></div></section>
    <section v-if="selectedRequest" class="panel confirmation-panel"><h2>Confirm Disposal — {{ selectedRequest.serial }}</h2><div class="detail-list"><div><span>Asset</span><strong>{{ selectedRequest.asset }} ({{ selectedRequest.type }})</strong></div><div><span>Station / Department</span><strong>{{ selectedRequest.station }} — {{ selectedRequest.department }}</strong></div><div><span>Requested By</span><strong>{{ selectedRequest.requestedBy }}, on {{ selectedRequest.date }}</strong></div><div><span>Reason</span><strong>{{ selectedRequest.reason }}</strong></div></div><div class="warning">⚠ This action is final. Once approved, the asset status will be set to "Disposed" and the record retained for audit purposes.</div><p v-if="message" class="result" :class="messageType">{{ message }}</p><div class="button-row"><button class="reject-button" type="button" :disabled="selectedRequest.status !== 'Pending'" @click="updateRequest('Rejected')">Reject Request</button><button class="approve-button" type="button" :disabled="selectedRequest.status !== 'Pending'" @click="updateRequest('Approved')">Approve Disposal</button></div></section>
  </section>
</template>

<script setup>
import { ref } from 'vue';

const requests = ref([
  { serial: 'SN-TPA-00119', asset: 'HP LaserJet M404', type: 'Printer', station: 'Tanga', department: 'ICT', requestedBy: 'A. Komba', reason: 'Beyond economical repair — parts unavailable', date: '02/09/2026', status: 'Pending' },
  { serial: 'SN-TPA-00341', asset: 'Dell OptiPlex 3090', type: 'Desktop', station: 'Mwanza', department: 'ICT', requestedBy: 'F. Ngoma', reason: 'End of life / obsolete', date: '01/09/2026', status: 'Pending' }
]);
const selectedRequest = ref(requests.value[0]);
const message = ref('');
const messageType = ref('success');
function updateRequest(status) { selectedRequest.value.status = status; messageType.value = status === 'Approved' ? 'success' : 'rejected'; message.value = `Disposal request ${status.toLowerCase()} successfully.`; }
</script>

<style scoped>
.disposal-page { max-width: 1100px; margin: 0 auto; }
.page-heading { margin-bottom: 18px; }
.eyebrow { color: #2E90C8; font-size: 11px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
.page-heading h1 { color: #1D2939; font-size: 24px; margin: 4px 0; }
.subtitle { color: #7A8699; font-size: 13px; }
.panel { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 18px; }
.request-panel { margin-bottom: 16px; }
.table-wrap { overflow-x: auto; }
.request-panel table { border-collapse: collapse; min-width: 850px; width: 100%; }
.request-panel th { background: #F2F4F7; color: #5D6C80; font-size: 12px; padding: 10px; text-align: left; }
.request-panel td { border-bottom: 1px solid #E9EDF2; color: #3F4854; font-size: 13px; padding: 11px 10px; white-space: nowrap; }
.request-panel tbody tr { cursor: pointer; }
.request-panel tbody tr:hover,.request-panel tr.selected { background: #F8FBFD; }
.status { border-radius: 999px; display: inline-block; font-size: 11px; font-weight: 700; padding: 4px 10px; }
.pending { background: #FFF0C7; color: #B66A00; }
.approved { background: #D9F5E5; color: #16834D; }
.rejected { background: #FDE0DE; color: #C9362B; }
.empty { color: #7A8699; text-align: center; }
.confirmation-panel { border-color: #F3A7A1; }
.confirmation-panel h2 { color: #1D2939; font-size: 16px; margin: 0 0 12px; }
.detail-list > div { border-bottom: 1px solid #E9EDF2; display: grid; gap: 20px; grid-template-columns: 140px 1fr; padding: 10px 0; }
.detail-list span { color: #7A8699; font-size: 13px; }
.detail-list strong { color: #3F4854; font-size: 13px; font-weight: 500; }
.warning { background: #FFF9E8; color: #967000; font-size: 13px; margin-top: 14px; padding: 11px 14px; }
.result { font-size: 13px; margin: 12px 0 0; }
.result.success { color: #16834D; }
.result.rejected { color: #C9362B; }
.button-row { display: flex; gap: 0; margin-top: 16px; }
.reject-button,.approve-button { border: 0; border-radius: 5px; color: white; cursor: pointer; font-size: 13px; font-weight: 700; padding: 9px 16px; }
.reject-button { background: #C9362B; }
.approve-button { background: #2E90C8; }
.reject-button:disabled,.approve-button:disabled { cursor: not-allowed; opacity: .55; }
@media (max-width: 650px) { .detail-list > div { gap: 8px; grid-template-columns: 1fr; } }
</style>
