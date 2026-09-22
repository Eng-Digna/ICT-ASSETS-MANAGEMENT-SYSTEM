<template>
  <section class="page">
    <header class="page-heading"><div><p class="eyebrow">Asset operations</p><h1>Assignments</h1><p class="subtitle">Track ICT assets assigned to staff and departments.</p></div><button class="primary-button" type="button" @click="showForm = !showForm">{{ showForm ? 'Close Form' : 'New Assignment' }}</button></header>
    <section v-if="showForm" class="panel assignment-form"><h2>Assign an asset</h2><div class="form-grid"><label>Asset<select v-model="form.asset"><option value="">Select asset</option><option v-for="asset in assets" :key="asset.id">{{ asset.serialNumber }}</option></select></label><label>Assigned To<input v-model="form.assignee" placeholder="Staff name" /></label><label>Department<input v-model="form.department" placeholder="Department" /></label><label>Assignment Date<input v-model="form.date" type="date" /></label></div><button class="primary-button" type="button" @click="addAssignment">Save Assignment</button></section>
    <section class="stats"><article><span>Active assignments</span><strong>{{ assignments.length }}</strong></article><article><span>Available assets</span><strong>{{ availableCount }}</strong></article><article><span>Due for review</span><strong>3</strong></article></section>
    <section class="panel table-panel"><div class="panel-heading"><h2>Current assignments</h2><input v-model="search" placeholder="Search assignments" /></div><div class="table-wrap"><table><thead><tr><th>Asset</th><th>Type</th><th>Assigned To</th><th>Department</th><th>Station</th><th>Date Assigned</th><th>Status</th></tr></thead><tbody><tr v-for="assignment in filteredAssignments" :key="assignment.asset"><td><strong>{{ assignment.asset }}</strong></td><td>{{ assignment.type }}</td><td>{{ assignment.assignee }}</td><td>{{ assignment.department }}</td><td>{{ assignment.station }}</td><td>{{ assignment.date }}</td><td><span class="status assigned">Assigned</span></td></tr><tr v-if="!filteredAssignments.length"><td colspan="7" class="empty">No assignments found.</td></tr></tbody></table></div></section>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { createAssignment, listAssignments, listAssets } from '@/services/api';

const search = ref('');
const showForm = ref(false);
const form = reactive({ asset: '', assignee: '', department: '', date: '' });
const assets = ref([]);
const assignments = ref([]);
const availableCount = computed(() => assets.value.filter((asset) => asset.status === 'AVAILABLE').length);
const filteredAssignments = computed(() => assignments.value.filter((item) => Object.values(item).some((value) => value.toLowerCase().includes(search.value.toLowerCase()))));
onMounted(async () => {
  const [assetPage, assignmentPage] = await Promise.all([listAssets(), listAssignments()]);
  assets.value = assetPage.data;
  assignments.value = assignmentPage.data.map((item) => ({ ...item, asset: `Asset ${item.assetId}`, type: 'ICT Asset', assignee: `User ${item.userId}`, department: '', station: `Station ${item.stationId}`, date: item.assignedDate || '', status: item.status }));
});
async function addAssignment() { const asset = assets.value.find((item) => item.serialNumber === form.asset); if (!asset || !Number(form.assignee)) return; await createAssignment({ assetId: asset.id, userId: Number(form.assignee), stationId: Number(asset.stationId) }); const response = await listAssignments(); assignments.value = response.data.map((item) => ({ ...item, asset: `Asset ${item.assetId}`, type: 'ICT Asset', assignee: `User ${item.userId}`, department: '', station: `Station ${item.stationId}`, date: item.assignedDate || '', status: item.status })); Object.assign(form, { asset: '', assignee: '', department: '', date: '' }); showForm.value = false; }
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; }.page-heading,.panel-heading { align-items: center; display: flex; justify-content: space-between; gap: 16px; margin-bottom: 18px; }.eyebrow { color: #2E90C8; font-size: 11px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }.page h1 { color: #1D2939; font-size: 24px; margin: 4px 0; }.subtitle { color: #7A8699; font-size: 13px; }.primary-button { background: #2E90C8; border: 0; border-radius: 5px; color: white; cursor: pointer; font-weight: 700; padding: 10px 14px; }.panel { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 18px; }.assignment-form { margin-bottom: 18px; }.assignment-form h2,.panel h2 { color: #1D2939; font-size: 16px; margin: 0 0 16px; }.form-grid { display: grid; gap: 14px; grid-template-columns: repeat(4, 1fr); margin-bottom: 16px; }.form-grid label { color: #475467; display: grid; font-size: 12px; font-weight: 700; gap: 6px; }.form-grid input,.form-grid select,.panel-heading input { border: 1px solid #CDD5DF; border-radius: 5px; color: #344054; padding: 9px; }.stats { display: grid; gap: 14px; grid-template-columns: repeat(3, 1fr); margin-bottom: 18px; }.stats article { background: #F2F4F7; border-left: 3px solid #2E90C8; padding: 14px 16px; }.stats span { color: #667085; display: block; font-size: 12px; }.stats strong { color: #1D2939; display: block; font-size: 24px; margin-top: 5px; }.table-wrap { overflow-x: auto; }.table-panel table { border-collapse: collapse; min-width: 800px; width: 100%; }.table-panel th { background: #F2F4F7; color: #5D6C80; font-size: 12px; padding: 10px; text-align: left; }.table-panel td { border-bottom: 1px solid #E9EDF2; color: #3F4854; font-size: 13px; padding: 11px 10px; }.status { border-radius: 999px; display: inline-block; font-size: 11px; font-weight: 700; padding: 4px 10px; }.assigned { background: #D9F5E5; color: #16834D; }.empty { color: #7A8699; text-align: center; }.panel-heading input { min-width: 220px; }
@media (max-width: 700px) { .page-heading,.panel-heading { align-items: flex-start; flex-direction: column; }.form-grid,.stats { grid-template-columns: 1fr; }.panel-heading input { min-width: 0; width: 100%; } }
</style>
