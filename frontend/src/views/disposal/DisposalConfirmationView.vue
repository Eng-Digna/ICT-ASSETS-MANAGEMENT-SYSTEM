<template>
  <div class="disposal-page">
    <header class="page-header">
      <div>
        <p class="eyebrow">Asset lifecycle</p>
        <h1>Disposal Confirmation</h1>
      </div>
      <button class="secondary-btn" type="button">Download Report</button>
    </header>

    <div v-if="isSubmitted" class="success-banner" role="status" aria-live="polite">
      <span class="success-icon">✓</span>
      <div>
        <strong>Disposal confirmed</strong>
        <p>Asset record has been logged and the disposal request is now marked as completed.</p>
      </div>
    </div>

    <div class="summary-grid">
      <article class="panel primary-panel">
        <div class="panel-header">
          <div>
            <p class="panel-label">Asset under review</p>
            <h2>{{ asset.tag }}</h2>
          </div>
          <span class="status-chip warning">Pending disposal</span>
        </div>

        <div class="asset-hero">
          <div class="asset-icon">💻</div>
          <div class="asset-meta">
            <h3>{{ asset.name }}</h3>
            <p>{{ asset.category }} • {{ asset.serial }}</p>
          </div>
        </div>

        <div class="info-grid">
          <div>
            <label>Location</label>
            <strong>{{ asset.location }}</strong>
          </div>
          <div>
            <label>Assigned to</label>
            <strong>{{ asset.assignee }}</strong>
          </div>
          <div>
            <label>Purchase date</label>
            <strong>{{ asset.purchaseDate }}</strong>
          </div>
          <div>
            <label>Residual value</label>
            <strong>{{ asset.residualValue }}</strong>
          </div>
        </div>
      </article>

      <aside class="panel side-panel">
        <div class="panel-header compact">
          <p class="panel-label">Approval summary</p>
        </div>

        <div class="approval-metrics">
          <div>
            <span>Request ID</span>
            <strong>{{ approval.requestId }}</strong>
          </div>
          <div>
            <span>Requested by</span>
            <strong>{{ approval.requestedBy }}</strong>
          </div>
          <div>
            <span>Reviewed by</span>
            <strong>{{ approval.reviewedBy }}</strong>
          </div>
          <div>
            <span>Approval status</span>
            <strong class="success-text">Approved</strong>
          </div>
        </div>
      </aside>
    </div>

    <div class="content-grid">
      <section class="panel details-panel">
        <div class="panel-header compact">
          <p class="panel-label">Disposal details</p>
        </div>

        <div class="detail-stack">
          <div class="detail-row">
            <span>Disposal reason</span>
            <strong>{{ disposal.reason }}</strong>
          </div>
          <div class="detail-row">
            <span>Disposal method</span>
            <strong>{{ disposal.method }}</strong>
          </div>
          <div class="detail-row">
            <span>Collection partner</span>
            <strong>{{ disposal.partner }}</strong>
          </div>
          <div class="detail-row">
            <span>Scheduled date</span>
            <strong>{{ disposal.date }}</strong>
          </div>
          <div class="detail-row">
            <span>Estimated scrap value</span>
            <strong>{{ disposal.scrapValue }}</strong>
          </div>
        </div>
      </section>

      <section class="panel checklist-panel">
        <div class="panel-header compact">
          <p class="panel-label">Compliance checklist</p>
        </div>

        <ul class="checklist">
          <li v-for="item in checklist" :key="item.text">
            <span class="checkmark">✓</span>
            <span>{{ item.text }}</span>
          </li>
        </ul>
      </section>
    </div>

    <section class="panel confirmation-panel">
      <div class="confirmation-header">
        <div>
          <p class="panel-label">Final confirmation</p>
          <h3>Confirm disposal documentation and asset handoff</h3>
        </div>
      </div>

      <div class="confirmation-body">
        <label class="checkbox-row">
          <input v-model="isConfirmed" type="checkbox" />
          <span>
            I confirm that the asset has been reviewed, approved for disposal, and that the designated
            disposal process is compliant with IT asset policy.
          </span>
        </label>

        <div class="button-row">
          <button class="ghost-btn" type="button" @click="resetForm">Cancel</button>
          <button
            class="primary-btn"
            type="button"
            :disabled="!isConfirmed || isSubmitted"
            @click="confirmDisposal"
          >
            {{ isSubmitted ? 'Confirmed' : 'Confirm Disposal' }}
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';

const asset = ref({
  tag: 'TPA-IT-2048',
  name: 'Dell Latitude 7420',
  category: 'Laptop',
  serial: 'DL7420-2048-91',
  location: 'Nairobi HQ',
  assignee: 'Grace Wanjiku',
  purchaseDate: '12 Mar 2021',
  residualValue: 'KSh 84,000'
});

const approval = ref({
  requestId: 'DISP-2026-048',
  requestedBy: 'Alex Njeri',
  reviewedBy: 'Samuel Otieno'
});

const disposal = ref({
  reason: 'End of useful life and failed battery replacement cost threshold',
  method: 'Certified electronic recycling and asset wipe',
  partner: 'GreenCycle Recycling Ltd.',
  date: '26 Sep 2026',
  scrapValue: 'KSh 28,500'
});

const checklist = ref([
  { text: 'Data sanitization certificate attached' },
  { text: 'Asset condition assessment completed' },
  { text: 'Approvals captured from ICT and finance' },
  { text: 'Vendor disposal certificate verified' }
]);

const isConfirmed = ref(false);
const isSubmitted = ref(false);

const confirmDisposal = () => {
  if (!isConfirmed.value || isSubmitted.value) return;

  isSubmitted.value = true;

  const existingRecords = JSON.parse(localStorage.getItem('disposalRecords') || '[]');
  existingRecords.unshift({
    id: approval.value.requestId,
    asset: asset.value.tag,
    reason: disposal.value.reason,
    method: disposal.value.method,
    date: disposal.value.date,
    status: 'Confirmed'
  });

  localStorage.setItem('disposalRecords', JSON.stringify(existingRecords.slice(0, 20)));
};

const resetForm = () => {
  isConfirmed.value = false;
  isSubmitted.value = false;
};

const statusText = computed(() => (isSubmitted.value ? 'Confirmed' : 'Pending disposal'));
</script>

<style scoped>
.disposal-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #00A3DD;
}

h1 {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  color: #101828;
}

.secondary-btn,
.primary-btn,
.ghost-btn {
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.secondary-btn {
  padding: 10px 16px;
  background: #F2F4F7;
  color: #344054;
}

.primary-btn {
  padding: 12px 20px;
  background: linear-gradient(135deg, #00A3DD, #008C95);
  color: white;
  box-shadow: 0 10px 20px rgba(0, 163, 221, 0.22);
}

.primary-btn:disabled {
  background: #B2D8E8;
  box-shadow: none;
  cursor: not-allowed;
}

.ghost-btn {
  padding: 12px 18px;
  background: transparent;
  color: #475467;
  border: 1px solid #D0D5DD;
}

.summary-grid,
.content-grid {
  display: grid;
  grid-template-columns: 1.7fr 0.9fr;
  gap: 20px;
  margin-bottom: 20px;
}

.panel {
  background: #FFFFFF;
  border: 1px solid #E4E7EC;
  border-radius: 18px;
  box-shadow: 0 8px 24px rgba(16, 24, 40, 0.04);
  padding: 22px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.panel-header.compact {
  margin-bottom: 12px;
}

.panel-label {
  margin: 0;
  color: #667085;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.09em;
  text-transform: uppercase;
}

.panel h2,
.panel h3 {
  margin: 6px 0 0;
  color: #101828;
}

.status-chip {
  display: inline-flex;
  align-items: center;
  padding: 7px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.status-chip.warning {
  background: rgba(247, 144, 9, 0.12);
  color: #B54708;
}

.asset-hero {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 18px 0 14px;
  border-bottom: 1px solid #F2F4F7;
}

.asset-icon {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  font-size: 28px;
  background: linear-gradient(135deg, rgba(0, 163, 221, 0.13), rgba(0, 140, 149, 0.12));
}

.asset-meta h3 {
  font-size: 24px;
  margin: 0;
}

.asset-meta p {
  margin: 4px 0 0;
  color: #667085;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.info-grid label,
.detail-row span,
.approval-metrics span {
  display: block;
  font-size: 12px;
  color: #667085;
  margin-bottom: 6px;
}

.info-grid strong,
.detail-row strong,
.approval-metrics strong {
  font-size: 14px;
  color: #101828;
}

.side-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.approval-metrics {
  display: grid;
  gap: 18px;
}

.success-text {
  color: #12B76A;
}

.detail-stack {
  display: grid;
  gap: 16px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px solid #F2F4F7;
}

.detail-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.checklist {
  list-style: none;
  display: grid;
  gap: 12px;
  margin: 14px 0 0;
}

.checklist li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  background: #F9FAFB;
  border: 1px solid #E4E7EC;
  border-radius: 12px;
  color: #344054;
}

.checkmark {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: rgba(18, 183, 106, 0.12);
  color: #12B76A;
  font-weight: 700;
}

.confirmation-panel {
  margin-top: 20px;
}

.confirmation-body {
  padding-top: 8px;
}

.checkbox-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  font-size: 15px;
  color: #344054;
  line-height: 1.6;
}

.checkbox-row input {
  width: 18px;
  height: 18px;
  margin-top: 4px;
  accent-color: #00A3DD;
}

.button-row {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 22px;
}

.success-banner {
  display: flex;
  align-items: center;
  gap: 14px;
  background: rgba(18, 183, 106, 0.12);
  border: 1px solid rgba(18, 183, 106, 0.24);
  border-radius: 14px;
  color: #075E3C;
  padding: 16px 18px;
  margin-bottom: 20px;
}

.success-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #12B76A;
  color: white;
  font-weight: 700;
}

.success-banner p {
  margin: 4px 0 0;
  font-size: 13px;
}

@media (max-width: 900px) {
  .summary-grid,
  .content-grid {
    grid-template-columns: 1fr;
  }

  .page-header,
  .button-row,
  .detail-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .button-row {
    width: 100%;
  }

  .button-row > * {
    width: 100%;
  }
}
</style>
