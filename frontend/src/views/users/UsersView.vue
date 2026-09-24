<template>
  <section class="page">
    <header class="page-heading">
      <div>
        <p class="eyebrow">Access management</p>
        <h1>Users &amp; Roles</h1>
        <p class="subtitle">Administrators create, update, and remove user accounts.</p>
      </div>
      <button class="primary-button" type="button" @click="openCreateForm">
        {{ showForm ? 'Close Form' : 'Add User' }}
      </button>
    </header>

    <!-- Create / Edit Form -->
    <section v-if="showForm" class="panel user-form">
      <h2>{{ isEditing ? 'Edit User' : 'Create User' }}</h2>
      <div class="form-grid">
        <label>Username<input v-model="form.username" :disabled="isEditing" required /></label>
        <label>First name<input v-model="form.firstName" required /></label>
        <label>Last name<input v-model="form.lastName" required /></label>
        <label>Email<input v-model="form.email" type="email" required /></label>
        <label v-if="!isEditing">Temporary password<input v-model="form.password" type="password" minlength="8" required /></label>
        <label>Role
          <select v-model="form.role">
            <option value="REGISTRAR">Registrar</option>
            <option value="ADMINISTRATOR">Administrator</option>
          </select>
        </label>
        <label v-if="form.role === 'REGISTRAR'">Station (Required for Registrar)
          <select v-model="form.stationId" required>
            <option value="">Select station</option>
            <option v-for="stn in stations" :key="stn.id" :value="stn.id">{{ stn.name }}</option>
          </select>
        </label>
      </div>
      <p v-if="error" class="form-error">{{ error }}</p>
      <p v-if="success" class="form-success">{{ success }}</p>
      <div class="form-actions">
        <button class="secondary-button" type="button" @click="cancelForm">Cancel</button>
        <button class="primary-button" type="button" @click="saveUser" :disabled="saving">
          {{ saving ? 'Saving...' : (isEditing ? 'Update User' : 'Save User') }}
        </button>
      </div>
    </section>

    <!-- Stats -->
    <section class="stats">
      <article><span>Total users</span><strong>{{ users.length }}</strong></article>
      <article><span>Administrators</span><strong>{{ users.filter(u => u.roles?.includes('ADMINISTRATOR')).length }}</strong></article>
      <article><span>Active users</span><strong>{{ users.filter(u => u.enabled).length }}</strong></article>
    </section>

    <!-- Users Table -->
    <section class="panel table-panel">
      <div class="panel-heading">
        <h2>Registered users</h2>
        <input v-model="search" placeholder="Search users" />
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Station</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in filteredUsers" :key="user.id">
              <td><strong>{{ user.fullName }}</strong></td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td><span class="role">{{ user.roles?.join(', ') }}</span></td>
              <td>{{ user.stationName || '—' }}</td>
              <td>
                <span class="status" :class="user.enabled ? 'active' : 'disabled'">
                  {{ user.enabled ? 'Active' : 'Disabled' }}
                </span>
              </td>
              <td class="actions-cell">
                <button class="action-btn edit" title="Edit user" @click="editUser(user)">✏️</button>
                <button class="action-btn toggle" :title="user.enabled ? 'Disable user' : 'Enable user'" @click="toggleStatus(user)">
                  {{ user.enabled ? '🔒' : '🔓' }}
                </button>
                <button class="action-btn delete" title="Delete user" @click="confirmDelete(user)">🗑️</button>
              </td>
            </tr>
            <tr v-if="!filteredUsers.length">
              <td colspan="6" class="empty">No users found.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- Delete Confirmation Dialog -->
    <div v-if="deleteTarget" class="dialog-backdrop" @click.self="deleteTarget = null">
      <section class="confirm-dialog" role="alertdialog" aria-labelledby="delete-title">
        <h2 id="delete-title">Delete User</h2>
        <p>Are you sure you want to delete <strong>{{ deleteTarget.fullName }}</strong> ({{ deleteTarget.username }})?</p>
        <p class="delete-warning">This action cannot be undone.</p>
        <div class="dialog-actions">
          <button class="secondary-button" type="button" @click="deleteTarget = null">Cancel</button>
          <button class="danger-button" type="button" @click="deleteUser" :disabled="saving">
            {{ saving ? 'Deleting...' : 'Delete' }}
          </button>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { api } from '../../services/api';

const search = ref('');
const showForm = ref(false);
const isEditing = ref(false);
const editingId = ref(null);
const error = ref('');
const success = ref('');
const saving = ref(false);
const deleteTarget = ref(null);
const users = ref([]);
const stations = ref([]);

const form = reactive({
  username: '', firstName: '', lastName: '', email: '', password: '', role: 'REGISTRAR', stationId: ''
});

const filteredUsers = computed(() =>
  users.value.filter(user =>
    [user.fullName, user.username, user.email, ...(user.roles || [])]
      .join(' ').toLowerCase().includes(search.value.toLowerCase())
  )
);

function resetForm() {
  Object.assign(form, { username: '', firstName: '', lastName: '', email: '', password: '', role: 'REGISTRAR', stationId: '' });
  isEditing.value = false;
  editingId.value = null;
  error.value = '';
  success.value = '';
}

function openCreateForm() {
  if (showForm.value && !isEditing.value) {
    showForm.value = false;
    return;
  }
  resetForm();
  showForm.value = true;
}

function cancelForm() {
  showForm.value = false;
  resetForm();
}

function editUser(user) {
  resetForm();
  isEditing.value = true;
  editingId.value = user.id;
  form.username = user.username;
  form.firstName = user.firstName || user.fullName?.split(' ')[0] || '';
  form.lastName = user.lastName || user.fullName?.split(' ').slice(1).join(' ') || '';
  form.email = user.email;
  form.role = user.roles?.[0] || 'REGISTRAR';
  showForm.value = true;
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function loadUsers() {
  const page = await api('/users');
  users.value = Array.isArray(page) ? page : (page.content || []);
}

async function loadStations() {
  const data = await api('/stations');
  stations.value = Array.isArray(data) ? data : (data.data || data.content || []);
}

async function saveUser() {
  try {
    saving.value = true;
    error.value = '';
    success.value = '';

    if (isEditing.value) {
      await api(`/users/${editingId.value}`, {
        method: 'PUT',
        body: JSON.stringify({
          firstName: form.firstName,
          lastName: form.lastName,
          email: form.email,
          role: form.role
        })
      });
      success.value = 'User updated successfully.';
    } else {
      await api('/users', {
        method: 'POST',
        body: JSON.stringify(form)
      });
      success.value = 'User created successfully.';
    }

    await loadUsers();
    setTimeout(() => {
      showForm.value = false;
      resetForm();
    }, 1200);
  } catch (e) {
    error.value = e.message;
  } finally {
    saving.value = false;
  }
}

async function toggleStatus(user) {
  try {
    await api(`/users/${user.id}/status`, {
      method: 'PUT',
      body: JSON.stringify(!user.enabled)
    });
    await loadUsers();
  } catch (e) {
    error.value = e.message;
  }
}

function confirmDelete(user) {
  deleteTarget.value = user;
}

async function deleteUser() {
  try {
    saving.value = true;
    await api(`/users/${deleteTarget.value.id}`, { method: 'DELETE' });
    deleteTarget.value = null;
    await loadUsers();
  } catch (e) {
    error.value = e.message;
  } finally {
    saving.value = false;
  }
}

onMounted(() => {
  loadUsers().catch(e => (error.value = e.message));
  loadStations().catch(e => (error.value = e.message));
});
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; }
.page-heading, .panel-heading {
  align-items: center; display: flex; justify-content: space-between; gap: 16px; margin-bottom: 18px;
}
.eyebrow { color: #2E90C8; font-size: 11px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; }
.page h1 { color: #1D2939; font-size: 24px; margin: 4px 0; }
.subtitle { color: #7A8699; font-size: 13px; }

.primary-button {
  background: #2E90C8; border: 0; border-radius: 5px; color: white;
  cursor: pointer; font-weight: 700; padding: 10px 14px;
}
.primary-button:disabled { opacity: 0.6; cursor: not-allowed; }
.secondary-button {
  background: white; border: 1px solid #CDD5DF; border-radius: 5px;
  color: #475467; cursor: pointer; font-weight: 700; padding: 10px 14px;
}
.danger-button {
  background: #C9362B; border: 0; border-radius: 5px; color: white;
  cursor: pointer; font-weight: 700; padding: 10px 14px;
}
.danger-button:disabled { opacity: 0.6; cursor: not-allowed; }

.panel { background: white; border: 1px solid #DDE3EA; border-radius: 6px; padding: 18px; }
.user-form { margin-bottom: 18px; }
.panel h2 { color: #1D2939; font-size: 16px; margin: 0 0 16px; }

.form-grid {
  display: grid; gap: 14px; grid-template-columns: repeat(3, 1fr); margin-bottom: 16px;
}
.form-grid label { color: #475467; display: grid; font-size: 12px; font-weight: 700; gap: 6px; }
.form-grid input, .form-grid select, .panel-heading input {
  border: 1px solid #CDD5DF; border-radius: 5px; color: #344054; padding: 9px;
}
.form-grid input:disabled { background: #F2F4F7; cursor: not-allowed; }
.form-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 8px; }
.form-error { color: #B42318; font-size: 13px; margin: 6px 0; }
.form-success { color: #16834D; font-size: 13px; margin: 6px 0; }

.stats { display: grid; gap: 14px; grid-template-columns: repeat(3, 1fr); margin-bottom: 18px; }
.stats article { background: #F2F4F7; border-left: 3px solid #2E90C8; padding: 14px 16px; }
.stats span { color: #667085; display: block; font-size: 12px; }
.stats strong { color: #1D2939; display: block; font-size: 24px; margin-top: 5px; }

.table-wrap { overflow-x: auto; }
.table-panel table { border-collapse: collapse; min-width: 900px; width: 100%; }
.table-panel th { background: #F2F4F7; color: #5D6C80; font-size: 12px; padding: 10px; text-align: left; }
.table-panel td { border-bottom: 1px solid #E9EDF2; color: #3F4854; font-size: 13px; padding: 11px 10px; }
.role { background: #E6F4FB; border-radius: 999px; color: #2674A8; font-size: 11px; font-weight: 700; padding: 4px 9px; }
.status { border-radius: 999px; font-size: 11px; font-weight: 700; padding: 4px 10px; }
.active { background: #D9F5E5; color: #16834D; }
.disabled { background: #FEE4E2; color: #B42318; }

.actions-cell { display: flex; gap: 6px; align-items: center; }
.action-btn {
  background: transparent; border: 1px solid #E9EDF2; border-radius: 4px;
  cursor: pointer; font-size: 14px; padding: 4px 8px; transition: background 0.2s;
}
.action-btn:hover { background: #F2F4F7; }
.action-btn.delete:hover { background: #FEE4E2; }

.panel-heading input { min-width: 220px; }

/* Delete confirmation dialog */
.dialog-backdrop {
  align-items: center; background: rgba(16, 24, 40, .45); display: flex;
  inset: 0; justify-content: center; padding: 20px; position: fixed; z-index: 50;
}
.confirm-dialog {
  background: white; border-radius: 8px; box-shadow: 0 16px 36px rgba(16, 24, 40, .22);
  max-width: 420px; padding: 22px; width: 100%;
}
.confirm-dialog h2 { color: #B42318; font-size: 18px; margin: 0 0 12px; }
.confirm-dialog p { color: #475467; font-size: 14px; margin: 0 0 8px; }
.delete-warning { color: #B42318; font-size: 12px; font-weight: 600; }
.dialog-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 18px; }

.empty { color: #667085; font-size: 14px; padding: 32px; text-align: center; }

@media (max-width: 700px) {
  .page-heading, .panel-heading { align-items: flex-start; flex-direction: column; }
  .form-grid, .stats { grid-template-columns: 1fr; }
  .panel-heading input { min-width: 0; width: 100%; }
}
</style>