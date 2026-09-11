<template>
  <section class="assets-page">
    <header class="page-heading">
      <div><p class="eyebrow">ICT asset register</p><h1>Asset Directory</h1><p class="subtitle">Search and filter all registered ICT assets across TPA.</p></div>
      <button class="primary-button" type="button" @click="registerAsset">+ Register Asset</button>
    </header>
    <div class="directory-card">
      <div class="filter-bar">
        <label class="search-field"><span class="sr-only">Search assets</span><input v-model="searchQuery" type="search" placeholder="Search by serial number, user, or asset type" /></label>
        <label><span class="sr-only">Filter by station</span><select v-model="filters.station"><option value="">All Stations</option><option v-for="station in stations" :key="station" :value="station">{{ station }}</option></select></label>
        <label><span class="sr-only">Filter by department</span><select v-model="filters.department"><option value="">All Departments</option><option v-for="department in departments" :key="department" :value="department">{{ department }}</option></select></label>
        <label><span class="sr-only">Filter by asset type</span><select v-model="filters.type"><option value="">All Asset Types</option><option v-for="type in assetTypes" :key="type" :value="type">{{ type }}</option></select></label>
        <button v-if="hasFilters" class="clear-button" type="button" @click="clearFilters">Clear</button>
      </div>
      <div class="table-wrap">
        <table class="asset-table">
          <thead><tr><th>Serial No.</th><th>Type</th><th>Brand / Model</th><th>Assigned User</th><th>Department</th><th>Station</th><th>Status</th><th><span class="sr-only">Actions</span></th></tr></thead>
          <tbody>
            <tr v-for="asset in filteredAssets" :key="asset.id"><td><strong>{{ asset.serialNumber }}</strong></td><td>{{ asset.type }}</td><td>{{ asset.brand }}</td><td>{{ asset.assignedUser }}</td><td>{{ asset.department }}</td><td>{{ asset.station }}</td><td><span class="status" :class="statusClass(asset.status)">{{ asset.status }}</span></td><td><RouterLink class="view-link" :to="{ name: 'AssetDetail', params: { id: asset.id } }">View</RouterLink></td></tr>
            <tr v-if="filteredAssets.length === 0"><td colspan="8" class="empty-state">No assets match the current search and filters.</td></tr>
          </tbody>
        </table>
      </div>
      <footer class="table-footer"><span>Showing {{ filteredAssets.length }} of {{ assets.length }} assets</span><span class="directory-note">Select View to open the full asset record.</span></footer>
    </div>
  </section>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { assetTypes, assets, departments, stations } from '@/data/assets';

const router = useRouter();
const searchQuery = ref('');
const filters = reactive({ station: '', department: '', type: '' });
const filteredAssets = computed(() => {
  const query = searchQuery.value.trim().toLowerCase();
  return assets.filter((asset) => {
    const matchesQuery = !query || [asset.serialNumber, asset.assignedUser, asset.department, asset.station, asset.type, asset.brand].some((value) => value.toLowerCase().includes(query));
    return matchesQuery && (!filters.station || asset.station === filters.station) && (!filters.department || asset.department === filters.department) && (!filters.type || asset.type === filters.type);
  });
});
const hasFilters = computed(() => Boolean(searchQuery.value || filters.station || filters.department || filters.type));
function clearFilters() { searchQuery.value = ''; filters.station = ''; filters.department = ''; filters.type = ''; }
function statusClass(status) { return status.toLowerCase().replaceAll(' ', '-'); }
function registerAsset() { router.push({ name: 'AssetDetail', params: { id: 'new' } }); }
</script>

<style scoped>
.assets-page { max-width: 1180px; margin: 0 auto; }
.page-heading { display: flex; justify-content: space-between; align-items: end; margin-bottom: 18px; }
.eyebrow { color: #2E90FA; font-size: 11px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; margin-bottom: 5px; }
h1 { color: #1D2939; font-size: 24px; margin: 0; }
.subtitle { color: #7A8699; font-size: 13px; margin-top: 5px; }
.primary-button { background: #2E90C8; border: 0; border-radius: 5px; color: white; cursor: pointer; font-size: 13px; font-weight: 700; padding: 9px 16px; }
.primary-button:hover { background: #247CAE; }
.directory-card { background: white; border: 1px solid #DDE3EA; border-radius: 6px; overflow: hidden; }
.filter-bar { align-items: center; display: flex; gap: 0; padding: 18px 18px 14px; }
.filter-bar label { display: block; }
.filter-bar input, .filter-bar select { background: white; border: 1px solid #CDD5DF; color: #344054; font-size: 13px; height: 33px; padding: 0 10px; }
.filter-bar input { border-radius: 5px 0 0 5px; min-width: 280px; }
.filter-bar select { border-left: 0; min-width: 145px; }
.filter-bar label:last-of-type select { border-radius: 0 5px 5px 0; }
.clear-button { background: none; border: 0; color: #2E90FA; cursor: pointer; font-size: 12px; margin-left: 10px; }
.table-wrap { overflow-x: auto; padding: 0 18px; }
.asset-table { border-collapse: collapse; min-width: 920px; width: 100%; }
.asset-table th { background: #F2F4F7; color: #5D6C80; font-size: 12px; font-weight: 700; padding: 9px 10px; text-align: left; white-space: nowrap; }
.asset-table td { border-bottom: 1px solid #E9EDF2; color: #3F4854; font-size: 13px; padding: 10px; white-space: nowrap; }
.asset-table td strong { color: #344054; font-weight: 500; }
.asset-table tr:last-child td { border-bottom: 0; }
.status { border-radius: 999px; display: inline-block; font-size: 11px; font-weight: 700; padding: 3px 10px; }
.assigned, .available { background: #D9F5E5; color: #16834D; }
.pending-disposal { background: #FFF0C7; color: #B66A00; }
.disposed { background: #FDE0DE; color: #C9362B; }
.under-maintenance { background: #DCEEFE; color: #2674A8; }
.view-link { color: #344054; text-decoration: none; }
.view-link:hover { color: #2E90C8; text-decoration: underline; }
.empty-state { color: #7A8699 !important; padding: 38px 10px !important; text-align: center; }
.table-footer { color: #7A8699; display: flex; font-size: 12px; justify-content: space-between; padding: 14px 18px 17px; }
.directory-note { color: #98A2B3; }
.sr-only { height: 1px; margin: -1px; overflow: hidden; position: absolute; width: 1px; clip: rect(0, 0, 0, 0); }
@media (max-width: 800px) { .page-heading { align-items: flex-start; flex-direction: column; gap: 14px; } .filter-bar { align-items: stretch; flex-direction: column; gap: 8px; } .filter-bar input, .filter-bar select { border: 1px solid #CDD5DF; border-radius: 5px !important; min-width: 0; width: 100%; } .clear-button { margin: 0; text-align: left; } .table-footer { align-items: flex-start; flex-direction: column; gap: 6px; } }
</style>
