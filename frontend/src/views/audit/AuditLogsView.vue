<template>
  <div class="audit-page">

    <!-- PAGE TITLE -->
    <div class="page-title">
      <h1>Audit Log</h1>
      <p>
        Read-only history of every change made in the system.
        Entries cannot be edited or deleted.
      </p>
    </div>

    <!-- AUDIT LOG CARD -->
    <div class="audit-card">

      <!-- FILTERS -->
      <div class="filters">
        <input
          v-model="search"
          type="text"
          placeholder="Search by user, record, or action..."
          class="search"
        />

        <select v-model="selectedUser">
          <option value="">All Users</option>
          <option v-for="user in users" :key="user" :value="user">
            {{ user }}
          </option>
        </select>

        <select v-model="selectedAction">
          <option value="">All Actions</option>
          <option v-for="action in actions" :key="action" :value="action">
            {{ action }}
          </option>
        </select>

        <input v-model="fromDate" type="date" />
        <input v-model="toDate" type="date" />
      </div>

      <!-- TABLE -->
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>User</th>
              <th>Action</th>
              <th>Record</th>
              <th>Previous Value</th>
              <th>New Value</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="log in filteredLogs" :key="log.id">
              <td>
                <div>{{ log.date }}</div>
                <div class="time">{{ log.time }}</div>
              </td>
              <td>{{ log.user }}</td>
              <td>
                <span class="action-badge" :class="getActionClass(log.action)">
                  {{ log.action }}
                </span>
              </td>
              <td>{{ log.record }}</td>
              <td class="muted">{{ log.previousValue }}</td>
              <td>{{ log.newValue }}</td>
            </tr>

            <tr v-if="filteredLogs.length === 0">
              <td colspan="6" class="no-results">No audit logs found.</td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: "AuditLogsView",

  data() {
    return {
      search: "",
      selectedUser: "",
      selectedAction: "",
      fromDate: "",
      toDate: "",

      users: ["J. Mushi", "A. Komba", "D. Andrea", "F. Ngoma"],

      actions: [
        "CREATE",
        "DISPOSAL_REQUESTED",
        "DISPOSAL_APPROVED",
        "TRANSFER",
        "UPDATE"
      ],

      logs: [
        {
          id: 1,
          date: "02/09/2026",
          time: "14:12:03",
          user: "J. Mushi",
          action: "CREATE",
          record: "Asset SN-TPA-00231",
          previousValue: "—",
          newValue: "New record created"
        },
        {
          id: 2,
          date: "02/09/2026",
          time: "11:47:29",
          user: "A. Komba",
          action: "DISPOSAL_REQUESTED",
          record: "Asset SN-TPA-00119",
          previousValue: "status: Assigned",
          newValue: "status: Disposal Requested"
        },
        {
          id: 3,
          date: "01/09/2026",
          time: "16:03:55",
          user: "D. Andrea",
          action: "DISPOSAL_APPROVED",
          record: "Asset SN-TPA-00098",
          previousValue: "status: Disposal Requested",
          newValue: "status: Disposed"
        },
        {
          id: 4,
          date: "01/09/2026",
          time: "09:22:10",
          user: "F. Ngoma",
          action: "TRANSFER",
          record: "Asset SN-TPA-00204",
          previousValue: "station: Mwanza",
          newValue: "station: Kigoma"
        },
        {
          id: 5,
          date: "31/08/2026",
          time: "15:40:02",
          user: "D. Andrea",
          action: "UPDATE",
          record: "User check_no. 4521",
          previousValue: "role: Viewer",
          newValue: "role: Registrar"
        }
      ]
    };
  },

  computed: {
    filteredLogs() {
      const searchText = this.search.toLowerCase().trim();

      return this.logs.filter((log) => {
        const matchesSearch =
          !searchText ||
          log.user.toLowerCase().includes(searchText) ||
          log.action.toLowerCase().includes(searchText) ||
          log.record.toLowerCase().includes(searchText) ||
          log.previousValue.toLowerCase().includes(searchText) ||
          log.newValue.toLowerCase().includes(searchText);

        const matchesUser =
          !this.selectedUser || log.user === this.selectedUser;

        const matchesAction =
          !this.selectedAction || log.action === this.selectedAction;

        return matchesSearch && matchesUser && matchesAction;
      });
    }
  },

  methods: {
    getActionClass(action) {
      if (action === "CREATE") return "create";
      if (action === "DISPOSAL_REQUESTED") return "disposal-requested";
      if (action === "DISPOSAL_APPROVED") return "disposal-approved";
      if (action === "TRANSFER") return "transfer";
      if (action === "UPDATE") return "update";
      return "";
    }
  }
};
</script>

<style scoped>
.audit-page {
  max-width: 1280px;
  margin: 0 auto;
}

/* =========================
   PAGE TITLE
========================= */
.page-title h1 {
  margin: 0;
  font-size: 22px;
  color: #263542;
}

.page-title p {
  margin: 4px 0 16px;
  font-size: 13px;
  color: #8a99a6;
}

/* =========================
   CARD
========================= */
.audit-card {
  background: #ffffff;
  border: 1px solid #dfe4e8;
  border-radius: 6px;
  padding: 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

/* =========================
   FILTERS
========================= */
.filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.filters input,
.filters select {
  height: 34px;
  border: 1px solid #d7dde2;
  background: #ffffff;
  border-radius: 4px;
  padding: 0 10px;
  font-size: 13px;
  color: #444;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.filters input:focus,
.filters select:focus {
  border-color: #20a4df;
  box-shadow: 0 0 0 2px rgba(32, 164, 223, 0.15);
}

.filters .search {
  flex: 1;
  min-width: 240px;
}

.filters select {
  min-width: 120px;
}

.filters input[type="date"] {
  width: 150px;
}

/* =========================
   TABLE
========================= */
.table-wrapper {
  overflow-x: auto;
  border: 1px solid #e8ecef;
  border-radius: 4px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #f0f2f3;
}

th {
  text-align: left;
  font-size: 12px;
  font-weight: 700;
  color: #68798a;
  padding: 10px 12px;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  border-bottom: 1px solid #e0e5ea;
}

td {
  padding: 12px;
  font-size: 13px;
  color: #4b535a;
  border-bottom: 1px solid #eef1f3;
  vertical-align: middle;
}

tbody tr:last-child td {
  border-bottom: none;
}

tbody tr:hover {
  background: #fafbfc;
}

.time {
  font-size: 11px;
  color: #8a99a6;
  margin-top: 2px;
}

.muted {
  color: #99a5b0;
}

/* =========================
   BADGES
========================= */
.action-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
  letter-spacing: 0.3px;
}

.create,
.transfer,
.update {
  background: #d9edf9;
  color: #2877a8;
}

.disposal-requested {
  background: #fff0c9;
  color: #bd7a00;
}

.disposal-approved {
  background: #d7f2df;
  color: #26934d;
}

/* =========================
   NO RESULTS
========================= */
.no-results {
  text-align: center;
  padding: 30px;
  color: #8b969f;
  font-size: 13px;
}
</style>