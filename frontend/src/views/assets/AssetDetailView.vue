<template>
  <section class="detail-page">
    <header class="page-heading">
      <div>
        <h1>{{ isNew ? 'Register New Asset' : (record.serialNumber || 'Asset Details') }}</h1>
        <p class="subtitle">{{ isNew ? 'Fields marked * are required (FR-AST-02)' : 'Review and update asset details' }}</p>
      </div>
      <span v-if="!isNew && record.status" class="status" :class="statusClass(record.status)">
        {{ record.status }}
      </span>
    </header>

    <form class="record-card" novalidate @submit.prevent="saveRecord">
      <h2>Asset Details</h2>
      <div class="form-grid">
        <label>
          Asset Type *
          <select v-if="isNew" v-model="record.assetType" :class="{ invalid: errors.assetType }" required>
            <option value="">Select type</option>
            <option value="DESKTOP">Desktop</option>
            <option value="LAPTOP">Laptop</option>
            <option value="PRINTER">Printer</option>
            <option value="SCANNER">Scanner</option>
          </select>
          <input v-else :value="record.assetType" disabled />
          <small v-if="errors.assetType">{{ errors.assetType }}</small>
        </label>

        <label>
          Serial Number *
          <input v-model.trim="record.serialNumber" :disabled="!isNew" :class="{ invalid: errors.serialNumber }" required />
          <small v-if="errors.serialNumber">{{ errors.serialNumber }}</small>
        </label>

        <label>
          MAC Address *
          <input v-model.trim="record.macAddress" :disabled="!isNew" placeholder="AA:BB:CC:11:22:33" :class="{ invalid: errors.macAddress }" required />
          <small v-if="errors.macAddress">{{ errors.macAddress }}</small>
        </label>

        <label>
          Brand *
          <input v-model.trim="record.brand" :class="{ invalid: errors.brand }" required />
          <small v-if="errors.brand">{{ errors.brand }}</small>
        </label>

        <label>
          Model
          <input v-model.trim="record.model" />
        </label>

        <label>
          Operating System
          <input v-model.trim="record.operatingSystem" />
        </label>

        <label>
          Warranty Start Date
          <input v-model="record.warrantyStartDate" :disabled="!isNew" type="date" />
        </label>

        <label>
          Warranty End Date
          <input v-model="record.warrantyEndDate" type="date" :class="{ invalid: errors.warrantyEndDate }" />
          <small v-if="errors.warrantyEndDate">{{ errors.warrantyEndDate }}</small>
        </label>

        <label>
          Department *
          <select v-if="isNew" v-model="record.departmentId" :class="{ invalid: errors.departmentId }" required>
            <option value="">Select department</option>
            <option v-for="dept in departments" :key="dept.id" :value="dept.id">{{ dept.name }}</option>
          </select>
          <input v-else :value="record.department || '—'" disabled />
          <small v-if="errors.departmentId">{{ errors.departmentId }}</small>
        </label>

        <label>
          Bandari Station *
          <select v-if="isNew" v-model="record.stationId" :class="{ invalid: errors.stationId }" required>
            <option value="">Select station</option>
            <option v-for="stn in stations" :key="stn.id" :value="stn.id">{{ stn.name }}</option>
          </select>
          <input v-else :value="record.station || '—'" disabled />
          <small v-if="errors.stationId">{{ errors.stationId }}</small>
        </label>
      </div>

      <div v-if="serverError" class="server-error" role="alert">{{ serverError }}</div>
      <div v-if="serverSuccess" class="server-success" role="alert">{{ serverSuccess }}</div>

      <div class="form-actions">
        <RouterLink class="cancel-button" :to="{ name: 'Assets' }">Cancel</RouterLink>
        <button class="primary-button" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? 'Saving...' : (isNew ? 'Save Asset' : 'Update Asset') }}
        </button>
      </div>
      <p class="save-note">
        The system checks the serial number against existing database records and rejects duplicates. Every successful registration and update is written to the audit log automatically.
      </p>
    </form>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { api } from '../../services/api';

const route = useRoute();
const router = useRouter();

const isNew = computed(() => route.name === 'AssetRegister' || route.params.id === 'new' || !route.params.id);

const record = reactive({
  id: null,
  assetType: '',
  serialNumber: '',
  macAddress: '',
  brand: '',
  model: '',
  operatingSystem: '',
  warrantyStartDate: '',
  warrantyEndDate: '',
  departmentId: '',
  stationId: '',
  department: '',
  station: '',
  status: 'REGISTERED'
});

const departments = ref([]);
const stations = ref([]);
const errors = ref({});
const serverError = ref('');
const serverSuccess = ref('');
const isSubmitting = ref(false);

function statusClass(status) {
  return (status || '').toLowerCase().replaceAll('_', '-').replaceAll(' ', '-');
}

async function loadReferenceData() {
  try {
    const [deptRes, stnRes] = await Promise.all([
      api('/departments').catch(() => []),
      api('/stations').catch(() => [])
    ]);
    departments.value = Array.isArray(deptRes) ? deptRes : (Array.isArray(deptRes) ? deptRes : (deptRes.content || []));
    stations.value = Array.isArray(stnRes) ? stnRes : (Array.isArray(stnRes) ? stnRes : (stnRes.content || []));
  } catch (e) {
    console.error('Failed to load reference data', e);
  }
}

async function loadAsset() {
  if (isNew.value) return;
  try {
    const data = await api(`/assets/${route.params.id}`);
    if (data) {
      record.id = data.id;
      record.assetType = data.assetType || data.type || '';
      record.serialNumber = data.serialNumber || '';
      record.macAddress = data.macAddress || '';
      record.brand = data.brand || '';
      record.model = data.model || '';
      record.operatingSystem = data.operatingSystem || '';
      record.warrantyStartDate = data.warrantyStartDate || '';
      record.warrantyEndDate = data.warrantyEndDate || '';
      record.departmentId = data.departmentId || '';
      record.stationId = data.stationId || '';
      record.department = data.department || '';
      record.station = data.station || '';
      record.status = data.status || 'REGISTERED';
    }
  } catch (err) {
    serverError.value = 'Failed to load asset details: ' + (err.message || 'Unknown error');
  }
}

function validate() {
  const errs = {};
  if (isNew.value) {
    if (!record.assetType) errs.assetType = 'Asset type is required.';
    if (!record.serialNumber.trim()) errs.serialNumber = 'Serial number is required.';
    if (!record.macAddress.trim()) errs.macAddress = 'MAC address is required.';
    if (!record.departmentId) errs.departmentId = 'Department is required.';
    if (!record.stationId) errs.stationId = 'Station is required.';
  }
  if (!record.brand.trim()) errs.brand = 'Brand is required.';
  if (record.warrantyStartDate && record.warrantyEndDate && record.warrantyEndDate < record.warrantyStartDate) {
    errs.warrantyEndDate = 'Warranty end date must be after start date.';
  }
  errors.value = errs;
  return Object.keys(errs).length === 0;
}

async function saveRecord() {
  serverError.value = '';
  serverSuccess.value = '';
  if (!validate()) return;

  isSubmitting.value = true;
  try {
    if (isNew.value) {
      await api('/assets', {
        method: 'POST',
        body: JSON.stringify({
          assetType: record.assetType,
          serialNumber: record.serialNumber.trim(),
          macAddress: record.macAddress.trim(),
          brand: record.brand.trim(),
          model: record.model.trim() || null,
          operatingSystem: record.operatingSystem.trim() || null,
          warrantyStartDate: record.warrantyStartDate || null,
          warrantyEndDate: record.warrantyEndDate || null,
          departmentId: Number(record.departmentId),
          stationId: Number(record.stationId)
        })
      });
      router.push({ name: 'Assets' });
    } else {
      await api(`/assets/${record.id}`, {
        method: 'PATCH',
        body: JSON.stringify({
          brand: record.brand.trim(),
          model: record.model.trim() || null,
          operatingSystem: record.operatingSystem.trim() || null,
          warrantyEndDate: record.warrantyEndDate || null
        })
      });
      serverSuccess.value = 'Asset updated successfully!';
      setTimeout(() => {
        router.push({ name: 'Assets' });
      }, 1000);
    }
  } catch (err) {
    serverError.value = err.message || 'Failed to save asset record.';
  } finally {
    isSubmitting.value = false;
  }
}

onMounted(async () => {
  await loadReferenceData();
  await loadAsset();
});
</script>

<style scoped>
.detail-page { max-width: 1280px; margin: 0 auto; }
.page-heading { align-items: end; display: flex; justify-content: space-between; margin-bottom: 18px; }
h1 { color: #1D2939; font-size: 24px; margin: 0; }
.subtitle { color: #7A8699; font-size: 13px; margin-top: 5px; }
.status { border-radius: 999px; font-size: 11px; font-weight: 700; padding: 4px 11px; text-transform: capitalize; }
.registered, .available { background: #D9F5E5; color: #16834D; }
.assigned { background: #E6F4FB; color: #2674A8; }
.disposal-requested, .pending-disposal { background: #FFF0C7; color: #B66A00; }
.disposed { background: #FDE0DE; color: #C9362B; }
.under-maintenance { background: #DCEEFE; color: #2674A8; }
.record-card { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 24px; }
.record-card h2 { color: #1D2939; font-size: 15px; margin-bottom: 16px; }
.form-grid { display: grid; gap: 20px 50px; grid-template-columns: 1fr 1fr; }
.form-grid label { color: #5D6C80; display: flex; flex-direction: column; font-size: 14px; gap: 5px; }
.form-grid input, .form-grid select { border: 2px solid #CBD3DE; border-radius: 7px; color: #344054; font: inherit; padding: 9px 12px; }
.form-grid input:focus, .form-grid select:focus { border-color: #123F73; box-shadow: 0 0 0 3px rgba(18,63,115,.1); outline: none; }
.form-grid input:disabled { background: #F2F4F7; cursor: not-allowed; border-color: #DDE3EA; color: #667085; }
.form-grid .invalid { border-color: #BA2A25; }
.form-grid small { color: #BA2A25; font-size: 12px; }
.form-actions { border-top: 1px solid #E9EDF2; display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px; padding-top: 18px; }
.primary-button, .cancel-button { border-radius: 5px; font-size: 13px; padding: 9px 16px; text-decoration: none; }
.primary-button { background: #DFA30B; border: 0; color: #102F55; cursor: pointer; font-weight: 700; text-transform: uppercase; }
.primary-button:disabled { opacity: 0.6; cursor: not-allowed; }
.cancel-button { border: 1px solid #CDD5DF; color: #475467; }
.server-error { margin: 20px 0 0; padding: 11px 14px; background: #FDEDEC; border: 1px solid #E7A8A4; border-radius: 5px; color: #8E211D; font-size: 13px; }
.server-success { margin: 20px 0 0; padding: 11px 14px; background: #EDFDF5; border: 1px solid #A4E7C4; border-radius: 5px; color: #16834D; font-size: 13px; }
.save-note { margin: 34px 0 0; padding: 14px 18px; background: #E8F0F8; border: 1px solid #AFC3D8; border-radius: 7px; color: #344054; font-size: 13px; line-height: 1.5; }
@media (max-width: 600px) { .page-heading { align-items: flex-start; flex-direction: column; gap: 12px; } .form-grid { grid-template-columns: 1fr; } }
</style>
