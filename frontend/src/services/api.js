import axios from 'axios';

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('isAuthenticated');
    }
    return Promise.reject(error);
  }
);

function unwrap(response) {
  return response.data?.data ?? response.data;
}

export function formatAsset(asset) {
  return {
    ...asset,
    id: asset.id,
    type: asset.assetType ?? asset.type ?? '',
    department: asset.department ?? (asset.departmentId == null ? '' : `Department ${asset.departmentId}`),
    station: asset.station ?? (asset.stationId == null ? '' : `Station ${asset.stationId}`),
    assignedUser: asset.assignedUser ?? 'Unassigned'
  };
}

export async function login(credentials) {
  return unwrap(await api.post('/auth/login', {
    username: credentials.username ?? credentials.email,
    password: credentials.password
  }));
}

export async function changePassword(userId, request) {
  return unwrap(await api.put(`/users/${userId}/change-password`, request));
}

export async function listAssets(params = {}) {
  const page = await api.get('/assets', { params: { page: 0, pageSize: 1000, ...params } });
  const payload = unwrap(page);
  return { ...payload, data: (payload.data ?? []).map(formatAsset) };
}

export async function getAsset(id) {
  return formatAsset(unwrap(await api.get(`/assets/${id}`)));
}

export async function createAsset(record) {
  return formatAsset(unwrap(await api.post('/assets', {
    assetType: record.type,
    serialNumber: record.serialNumber,
    macAddress: record.macAddress,
    brand: record.brand,
    model: record.model,
    operatingSystem: record.operatingSystem,
    warrantyStartDate: record.warrantyStartDate || null,
    warrantyEndDate: record.warrantyEndDate || null,
    departmentId: record.departmentId || null,
    stationId: record.stationId || null
  })));
}

export async function updateAsset(id, record) {
  return formatAsset(unwrap(await api.patch(`/assets/${id}`, {
    brand: record.brand,
    model: record.model,
    operatingSystem: record.operatingSystem,
    warrantyEndDate: record.warrantyEndDate || null
  })));
}

export async function getDashboardOverview() {
  return unwrap(await api.get('/dashboard/overview'));
}

export async function listAssignments() {
  return unwrap(await api.get('/assignments', { params: { page: 0, pageSize: 1000 } }));
}

export async function listMaintenanceRecords() {
  return unwrap(await api.get('/maintenance-records', { params: { page: 0, pageSize: 1000 } }));
}

export async function listDisposalRequests() {
  return unwrap(await api.get('/disposal-requests', { params: { page: 0, pageSize: 1000 } }));
}

export async function listUsers() {
  const payload = unwrap(await api.get('/users', { params: { page: 0, size: 1000 } }));
  return payload.content ?? [];
}

export async function createUser(request) {
  return unwrap(await api.post('/users', request));
}

export async function listAudits() {
  const payload = unwrap(await api.get('/audits', { params: { page: 0, size: 1000 } }));
  return payload.content ?? [];
}

export async function createAssignment(request) {
  return unwrap(await api.post('/assignments', request));
}

export async function createMaintenanceRecord(request) {
  return unwrap(await api.post('/maintenance-records', request));
}

export default api;
