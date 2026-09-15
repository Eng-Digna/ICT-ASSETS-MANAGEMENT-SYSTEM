<template>
  <section class="detail-page">
    <RouterLink class="back-link" :to="{ name: 'Assets' }">← Back to Asset Directory</RouterLink>
    <header class="page-heading">
      <div><p class="eyebrow">Asset record</p><h1>{{ isNew ? 'Register Asset' : asset.serialNumber }}</h1><p class="subtitle">Update the registration and assignment details for this ICT asset.</p></div>
      <span v-if="!isNew" class="status" :class="statusClass(asset.status)">{{ asset.status }}</span>
    </header>
    <form class="record-card" @submit.prevent="saveRecord">
      <h2>Asset Details</h2>
      <div class="form-grid">
        <label>Serial Number<input v-model="record.serialNumber" required /></label>
        <label>Asset Type<input v-model="record.type" required /></label>
        <label>Brand / Model<input v-model="record.brand" required /></label>
        <label>Assigned User<input v-model="record.assignedUser" /></label>
        <label>Department<input v-model="record.department" required /></label>
        <label>Station<input v-model="record.station" required /></label>
        <label>Status<select v-model="record.status"><option>Available</option><option>Assigned</option><option>Under Maintenance</option><option>Pending Disposal</option><option>Disposed</option></select></label>
        <label>Purchase Date<input v-model="record.purchaseDate" type="date" /></label>
        <label class="full-width">Notes<textarea v-model="record.notes" rows="4"></textarea></label>
      </div>
      <p v-if="duplicateSerial" class="validation-alert">Validation: Serial number "{{ record.serialNumber }}" already exists in the register.</p>
      <div class="form-actions"><RouterLink class="cancel-button" :to="{ name: 'Assets' }">Cancel</RouterLink><button class="primary-button" type="submit">Save Asset</button></div>
    </form>
  </section>
</template>

<script setup>
import { computed, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { assets, assetTypes, departments, stations } from '@/data/assets';

const route = useRoute();
const router = useRouter();
const isNew = computed(() => route.params.id === 'new');
const source = assets.find((item) => item.id === route.params.id) || { id: 'new', serialNumber: '', type: '', macAddress: '', brand: '', model: '', operatingSystem: '', assignedUser: '', department: '', station: '', status: 'Available', purchaseDate: '', warrantyStartDate: '', warrantyEndDate: '', notes: '' };
const asset = source;
const record = reactive({ ...source });
const duplicateSerial = computed(() => {
  const serialNumber = record.serialNumber.trim().toLowerCase();
  return serialNumber && assets.some((item) => item.id !== asset.id && item.serialNumber.trim().toLowerCase() === serialNumber);
});
function statusClass(status) { return status.toLowerCase().replaceAll(' ', '-'); }
function saveRecord() {
  record.serialNumber = record.serialNumber.trim();
  if (duplicateSerial.value) return;
  if (isNew.value) {
    assets.push({ ...record, id: record.serialNumber });
  } else {
    Object.assign(asset, record);
  }
  router.push({ name: 'Assets' });
}
</script>

<style scoped>
.detail-page { max-width: 900px; margin: 0 auto; }
.back-link { color: #5D6C80; display: inline-block; font-size: 13px; margin-bottom: 20px; text-decoration: none; }
.back-link:hover { color: #2E90C8; }
.page-heading { align-items: end; display: flex; justify-content: space-between; margin-bottom: 18px; }
.eyebrow { color: #2E90FA; font-size: 11px; font-weight: 700; letter-spacing: .08em; margin-bottom: 5px; text-transform: uppercase; }
h1 { color: #1D2939; font-size: 24px; margin: 0; }
.subtitle { color: #7A8699; font-size: 13px; margin-top: 5px; }
.status { border-radius: 999px; font-size: 11px; font-weight: 700; padding: 4px 11px; }
.assigned, .available { background: #D9F5E5; color: #16834D; }
.pending-disposal { background: #FFF0C7; color: #B66A00; }
.disposed { background: #FDE0DE; color: #C9362B; }
.under-maintenance { background: #DCEEFE; color: #2674A8; }
.record-card { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 24px; }
.record-card h2 { color: #1D2939; font-size: 15px; margin-bottom: 16px; }
.form-grid { display: grid; gap: 18px 20px; grid-template-columns: 1fr 1fr; }
.form-grid label { color: #475467; display: flex; flex-direction: column; font-size: 12px; font-weight: 700; gap: 7px; }
.form-grid input, .form-grid select, .form-grid textarea { border: 1px solid #CDD5DF; border-radius: 5px; color: #344054; font: inherit; font-weight: 400; padding: 9px 10px; }
.form-grid textarea { resize: vertical; }
.field-error { color: #B42318; font-size: 11px; font-weight: 600; }
.validation-alert { background: #FEE4E2; border-radius: 5px; color: #B42318; font-size: 13px; margin-top: 20px; padding: 10px 12px; }
.full-width { grid-column: 1 / -1; }
.form-actions { border-top: 1px solid #E9EDF2; display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px; padding-top: 18px; }
.primary-button, .cancel-button { border-radius: 5px; font-size: 13px; padding: 9px 16px; text-decoration: none; }
.primary-button { background: #2E90C8; border: 0; color: white; cursor: pointer; font-weight: 700; }
.cancel-button { border: 1px solid #CDD5DF; color: #475467; }
@media (max-width: 600px) { .page-heading { align-items: flex-start; flex-direction: column; gap: 12px; } .form-grid { grid-template-columns: 1fr; } .full-width { grid-column: auto; } }
</style>