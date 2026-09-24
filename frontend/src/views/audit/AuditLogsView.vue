<template>
  <div class="audit-page">
    <!-- PAGE TITLE -->
    <div class="page-title">
      <p class="eyebrow">Governance & Compliance</p>
      <h1>Audit Logs</h1>
      <p class="subtitle">
        Read-only history of every change made in the ICT Assets Management System. Entries cannot be altered or deleted.
      </p>
    </div>

    <!-- AUDIT LOG CARD -->
    <div class="audit-card">
      <!-- FILTERS -->
      <div class="filters">
        <input
          v-model="search"
          type="text"
          placeholder="Search by user, action, or record..."
          class="search"
        />

        <select v-model="selectedAction">
          <option value="">All Actions</option>
          <option v-for="action in availableActions" :key="action" :value="action">
            {{ action }}
          </option>
        </select>

        <button v-if="search || selectedAction" class="clear-btn" type="button" @click="clearFilters">
          Clear
        </button>
      </div>

      <!-- TABLE -->
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>User</th>
              <th>Action</th>
              <th>Resource / Record</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="log in filteredLogs" :key="log.id">
              <td>
                <div>{{ formatDate(log.timestamp) }}</div>
                <div class="time">{{ formatTime(log.timestamp) }}</div>
              </td>
              <td><strong>{{ log.username || ('User #' + log.userId) }}</strong></td>
              <td>
                <span class="action-badge" :class="getActionClass(log.action)">
                  {{ log.action }}
                </span>
              </td>
              <td>{{ log.resourceType ? log.resourceType + (log.resourceId ? ' #' + log.resourceId : '') : (log.endpoint || '—') }}</td>
              <td>
                <span class="status-indicator" :class="log.success ? 'success' : 'failed'">
                  {{ log.success ? 'Success' : 'Failed' }}
                </span>
              </td>
            </tr>

            <tr v-if="filteredLogs.length === 0">
              <td colspan="7" class="no-results">
                {{ loading ? 'Loading audit records...' : 'No audit records match the selected criteria.' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <footer class="table-footer">
        <span>Total audit logs: {{ logs.length }}</span>
        <span>Export and inspection is restricted to Administrators</span>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { api } from '../../services/api';

const search = ref('');
const selectedAction = ref('');
const loading = ref(false);
const logs = ref([]);

const availableActions = computed(() => {
  const actions = logs.value.map(l => l.action).filter(Boolean);
  const defaults = ['CREATE', 'UPDATE', 'DELETE', 'ASSIGN', 'RETURN', 'DISPOSAL_REQUESTED', 'DISPOSAL_APPROVED', 'MAINTENANCE_LOGGED'];
  return [...new Set([...defaults, ...actions])];
});

const filteredLogs = computed(() => {
  const q = search.value.trim().toLowerCase();
  return logs.value.filter(log => {
    const matchesAction = !selectedAction.value || log.action === selectedAction.value;
    const matchesSearch = !q || [
      log.username,
      log.action,
      log.resourceType,
      log.resourceId,
      log.endpoint,
      log.previousValue,
      log.newValue
    ].some(val => val && String(val).toLowerCase().includes(q));

    return matchesAction && matchesSearch;
  });
});

function clearFilters() {
  search.value = '';
  selectedAction.value = '';
}

function formatDate(timestamp) {
  if (!timestamp) return '—';
  try {
    return new Date(timestamp).toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' });
  } catch {
    return timestamp;
  }
}

function formatTime(timestamp) {
  if (!timestamp) return '';
  try {
    return new Date(timestamp).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  } catch {
    return '';
  }
}

function getActionClass(action) {
  const a = (action || '').toLowerCase();
  if (a.includes('create') || a.includes('assign')) return 'create';
  if (a.includes('disposal_req') || a.includes('pending')) return 'disposal-requested';
  if (a.includes('disposal_app') || a.includes('approved')) return 'disposal-approved';
  if (a.includes('update') || a.includes('complete')) return 'update';
  if (a.includes('delete') || a.includes('reject')) return 'delete';
  return 'default-badge';
}

async function loadAuditLogs() {
  loading.value = true;
  try {
    const page = await api('/audits');
    logs.value = Array.isArray(page) ? page : (page.content || []);
  } catch (err) {
    console.error('Failed to load audit logs:', err);
    logs.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadAuditLogs();
});
</script>

<style scoped>
.audit-page { max-width: 1280px; margin: 0 auto; }
.eyebrow { color: #2E90C8; font-size: 11px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; margin: 0 0 4px; }
.page-title h1 { margin: 0; font-size: 24px; color: #263542; }
.subtitle { margin: 4px 0 16px; font-size: 13px; color: #8a99a6; }
.audit-card { background: #ffffff; border: 1px solid #dfe4e8; border-radius: 6px; padding: 16px; box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03); }
.filters { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 12px; align-items: center; }
.filters input, .filters select { height: 34px; border: 1px solid #d7dde2; background: #ffffff; border-radius: 4px; padding: 0 10px; font-size: 13px; color: #444; outline: none; }
.filters .search { flex: 1; min-width: 240px; }
.clear-btn { background: none; border: 0; color: #2E90C8; cursor: pointer; font-size: 13px; font-weight: 600; padding: 0 6px; }
.table-wrapper { overflow-x: auto; border: 1px solid #e8ecef; border-radius: 4px; }
table { width: 100%; border-collapse: collapse; min-width: 850px; }
thead { background: #f0f2f3; }
th { text-align: left; font-size: 12px; font-weight: 700; color: #68798a; padding: 10px 12px; text-transform: uppercase; letter-spacing: 0.4px; border-bottom: 1px solid #e0e5ea; }
td { padding: 12px; font-size: 13px; color: #4b535a; border-bottom: 1px solid #eef1f3; vertical-align: middle; }
tbody tr:last-child td { border-bottom: none; }
tbody tr:hover { background: #fafbfc; }
.time { font-size: 11px; color: #8a99a6; margin-top: 2px; }
.muted { color: #99a5b0; font-family: monospace; font-size: 12px; }
.action-badge { display: inline-block; padding: 4px 10px; border-radius: 12px; font-size: 11px; font-weight: 700; white-space: nowrap; letter-spacing: 0.3px; }
.create, .update { background: #d9edf9; color: #2877a8; }
.disposal-requested { background: #fff0c9; color: #bd7a00; }
.disposal-approved { background: #d7f2df; color: #26934d; }
.delete { background: #fde0de; color: #c9362b; }
.default-badge { background: #edf0f2; color: #475467; }
.status-indicator { font-size: 12px; font-weight: 600; }
.status-indicator.success { color: #16834d; }
.status-indicator.failed { color: #b42318; }
.no-results { text-align: center; padding: 30px; color: #8b969f; font-size: 13px; }
.table-footer { display: flex; justify-content: space-between; font-size: 12px; color: #8a99a6; margin-top: 12px; padding: 0 4px; }
</style>
