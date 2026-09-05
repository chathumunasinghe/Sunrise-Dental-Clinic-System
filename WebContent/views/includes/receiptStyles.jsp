<style>
    .receipt-doc {
        max-width: 640px;
        background: #fff;
        border-radius: 18px;
        box-shadow: 0 20px 50px rgba(15, 155, 142, 0.16);
        overflow: hidden;
        margin: 8px auto 0;
        border: 1px solid #eef2f2;
    }
    .receipt-head {
        position: relative;
        background: linear-gradient(135deg, var(--primary), var(--primary-dark));
        color: #fff;
        padding: 30px 32px 60px;
        overflow: hidden;
    }
    .receipt-head::before {
        content: "";
        position: absolute;
        top: -60px; right: -60px;
        width: 200px; height: 200px;
        border-radius: 50%;
        background: rgba(255,255,255,0.08);
    }
    .receipt-head::after {
        content: "";
        position: absolute;
        bottom: -80px; left: -40px;
        width: 180px; height: 180px;
        border-radius: 50%;
        background: rgba(255,255,255,0.06);
    }
    .receipt-head-row { display: flex; align-items: center; justify-content: space-between; position: relative; z-index: 1; }
    .receipt-head .clinic-mark { display: flex; align-items: center; gap: 12px; }
    .receipt-head .clinic-mark .tooth { font-size: 30px; }
    .receipt-head .clinic-name { font-size: 20px; font-weight: 700; letter-spacing: .01em; }
    .receipt-head .clinic-sub { font-size: 12px; opacity: .85; margin-top: 3px; }
    .receipt-status {
        font-size: 11px; font-weight: 700; letter-spacing: .05em;
        padding: 6px 14px; border-radius: 999px; text-transform: uppercase;
        position: relative; z-index: 1;
    }
    .receipt-status.paid { background: #ffffff; color: #15803d; }
    .receipt-status.unpaid { background: #fff7e6; color: #b45309; }

    .receipt-id-strip {
        position: relative; z-index: 1;
        margin-top: 22px;
        background: rgba(255,255,255,0.14);
        border-radius: 12px;
        padding: 12px 16px;
        display: flex; justify-content: space-between;
        font-size: 12px;
    }
    .receipt-id-strip strong { display: block; font-size: 15px; margin-top: 2px; letter-spacing: .02em; }

    .receipt-body { padding: 0 32px 8px; margin-top: -34px; position: relative; z-index: 2; }
    .receipt-panel {
        background: #fff;
        border-radius: 14px;
        box-shadow: 0 8px 24px rgba(0,0,0,0.06);
        padding: 22px 24px;
        margin-bottom: 18px;
    }
    .receipt-section-title {
        font-size: 11px; font-weight: 700; text-transform: uppercase;
        letter-spacing: .06em; color: var(--primary-dark);
        margin-bottom: 12px; display: flex; align-items: center; gap: 6px;
    }
    .receipt-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px 20px; font-size: 13.5px; }
    .receipt-grid div span { display: block; color: var(--text-muted); font-size: 11px; margin-bottom: 2px; }
    .receipt-grid div strong { font-size: 14px; }

    .receipt-line { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px dashed #e2e8f0; font-size: 14px; }
    .receipt-line small { display: block; color: var(--text-muted); font-weight: 400; margin-top: 2px; }
    .receipt-total-row { display: flex; justify-content: space-between; align-items: center; padding-top: 18px; }
    .receipt-total-row .label { font-size: 13px; color: var(--text-muted); text-transform: uppercase; letter-spacing: .04em; }
    .receipt-total-row .amount { font-size: 26px; font-weight: 800; color: var(--primary-dark); }

    .receipt-footer {
        text-align: center; font-size: 11.5px; color: var(--text-muted);
        padding: 18px 30px 26px; border-top: 1px dashed #e2e8f0; margin-top: 4px;
    }
    .receipt-footer .thanks { font-weight: 600; color: var(--text-dark); margin-bottom: 4px; }

    @media print {
        .topbar, form, .back-link, .no-print, .page-title, .page-subtitle { display: none !important; }
        body { background: #fff !important; }
        .patient-page-wrapper, .page-wrapper { margin: 0; padding: 0; }
        .receipt-doc { box-shadow: none; max-width: 100%; border: none; }
    }
</style>
