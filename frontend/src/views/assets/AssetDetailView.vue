<template>
  <section class="detail-page">
    <header class="page-heading">
      <div><h1>{{ isNew ? 'Register New Asset' : asset.serialNumber }}</h1><p class="subtitle">Fields marked * are required (FR-AST-02)</p></div>
      <span v-if="!isNew" class="status" :class="statusClass(asset.status)">{{ asset.status }}</span>
    </header>
  <form class="record-card" novalidate @submit.prevent="saveRecord">
      <h2>Asset Details</h2>
      <div class="form-grid">
        <label>Asset Type *<input v-model.trim="record.type" :class="{ invalid: errors.type }" required /><small v-if="errors.type">{{ errors.type }}</small></label>
        <label>Serial Number *<input v-model.trim="record.serialNumber" :class="{ invalid: errors.serialNumber }" required /><small v-if="errors.serialNumber">{{ errors.serialNumber }}</small></label>
        <label>MAC Address *<input v-model.trim="record.macAddress" placeholder="AA:BB:CC:11:22:33" :class="{ invalid: errors.macAddress }" required /><small v-if="errors.macAddress">{{ errors.macAddress }}</small></label>
        <label>Brand *<input v-model.trim="record.brand" :class="{ invalid: errors.brand }" required /><small v-if="errors.brand">{{ errors.brand }}</small></label>
        <label>Model<input v-model.trim="record.model" /></label>
        <label>Operating System<input v-model.trim="record.operatingSystem" /></label>
        <label>Warranty Start Date<input v-model="record.warrantyStartDate" type="date" /></label>
        <label>Warranty End Date<input v-model="record.warrantyEndDate" type="date" :class="{ invalid: errors.warrantyEndDate }" /><small v-if="errors.warrantyEndDate">{{ errors.warrantyEndDate }}</small></label>
        <label>Department *<select v-model="record.department" :class="{ invalid: errors.department }" required><option value="">Select department</option><option v-for="department in departments" :key="department">{{ department }}</option></select><small v-if="errors.department">{{ errors.department }}</small></label>
        <label>Bandari Station *<select v-model="record.station" :class="{ invalid: errors.station }" required><option value="">Select station</option><option v-for="station in stations" :key="station">{{ station }}</option></select><small v-if="errors.station">{{ errors.station }}</small></label>
      </div>
      <div v-if="serverError" class="server-error" role="alert">{{ serverError }}</div>
      <div class="form-actions"><RouterLink class="cancel-button" :to="{ name: 'Assets' }">Cancel</RouterLink><button class="primary-button" type="submit">Save Asset</button></div>
      <p class="save-note">The system checks the serial number against existing records and rejects duplicates before saving. Every successful registration is written to the audit log automatically.</p>
    </form>
  </section>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { assets, departments, saveAssetRecord, stations, validateAssetRecord } from '@/data/assets';

const router = useRouter();
const route = useRoute();
const isNew = computed(() => route.name === 'AssetRegister' || route.name === 'AssetRegisterNew' || route.params.id === 'new');
const source = assets.find((item) => item.id === route.params.id) || { id: '', serialNumber: '', type: '', macAddress: '', brand: '', model: '', operatingSystem: '', department: '', station: '', warrantyStartDate: '', warrantyEndDate: '', status: 'Available' };
const asset = source;
const record = reactive({ ...source });
const errors = ref({});
const serverError = ref('');
function statusClass(status) { return status.toLowerCase().replaceAll(' ', '-'); }
function saveRecord() {
  errors.value = validateAssetRecord(record);
  serverError.value = '';
  if (Object.keys(errors.value).length) return;
  const result = saveAssetRecord(record);
  if (!result.success) {
    errors.value = result.errors;
    serverError.value = 'The server rejected this asset. Please correct the highlighted fields.';
    return;
  }
  router.push({ name: 'Assets' });
}
</script>

<style scoped>
.detail-page { max-width: 1280px; margin: 0 auto; }
.page-heading { align-items: end; display: flex; justify-content: space-between; margin-bottom: 18px; }
h1 { color: #1D2939; font-size: 24px; margin: 0; }
.subtitle { color: #7A8699; font-size: 13px; margin-top: 5px; }
.status { border-radius: 999px; font-size: 11px; font-weight: 700; padding: 4px 11px; }
.assigned, .available { background: #D9F5E5; color: #16834D; }
.pending-disposal { background: #FFF0C7; color: #B66A00; }
.disposed { background: #FDE0DE; color: #C9362B; }
.under-maintenance { background: #DCEEFE; color: #2674A8; }
.record-card { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 24px; }
.record-card h2 { color: #1D2939; font-size: 15px; margin-bottom: 16px; }
.form-grid { display: grid; gap: 20px 50px; grid-template-columns: 1fr 1fr; }
.form-grid label { color: #5D6C80; display: flex; flex-direction: column; font-size: 14px; gap: 5px; }
.form-grid input, .form-grid select { border: 2px solid #CBD3DE; border-radius: 7px; color: #344054; font: inherit; padding: 9px 12px; }
.form-grid input:focus, .form-grid select:focus { border-color: #123F73; box-shadow: 0 0 0 3px rgba(18,63,115,.1); outline: none; }
.form-grid .invalid { border-color: #BA2A25; }
.form-grid small { color: #BA2A25; font-size: 12px; }
.form-actions { border-top: 1px solid #E9EDF2; display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px; padding-top: 18px; }
.primary-button, .cancel-button { border-radius: 5px; font-size: 13px; padding: 9px 16px; text-decoration: none; }
.primary-button { background: #DFA30B; border: 0; color: #102F55; cursor: pointer; font-weight: 700; text-transform: uppercase; }
.cancel-button { border: 1px solid #CDD5DF; color: #475467; }
.server-error { margin: 20px 0 0; padding: 11px 14px; background: #FDEDEC; border: 1px solid #E7A8A4; border-radius: 5px; color: #8E211D; font-size: 13px; }
.save-note { margin: 34px 0 0; padding: 14px 18px; background: #E8F0F8; border: 1px solid #AFC3D8; border-radius: 7px; color: #344054; font-size: 13px; line-height: 1.5; }
@media (max-width: 600px) { .page-heading { align-items: flex-start; flex-direction: column; gap: 12px; } .form-grid { grid-template-columns: 1fr; } }
</style>