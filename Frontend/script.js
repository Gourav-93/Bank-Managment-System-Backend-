const API_BASE_URL = 'http://localhost:8080';

// Session Management
let currentUser = null;

function checkSession() {
    const sessionData = localStorage.getItem('bank_user');
    if (!sessionData) {
        window.location.href = 'login.html';
        return;
    }
    currentUser = JSON.parse(sessionData);
    document.querySelector('.user-name').textContent = currentUser.accountName;
    autoFillAccountNumbers();
}

function logout() {
    localStorage.removeItem('bank_user');
    window.location.href = 'login.html';
}

function autoFillAccountNumbers() {
    if (!currentUser) return;
    const fields = ['cdAccountNumber', 'sourceAccount', 'balAccountNumber', 'stmtAccountNumber'];
    fields.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = currentUser.accountNumber;
    });
}

// Initialize Layout and Navigation
document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', (e) => {
        const targetId = e.currentTarget.getAttribute('data-target');
        
        // Handle Logout specially if needed, but it's an <a> tag now.
        // If it's a normal nav item:
        if (!targetId) return;

        // Navigation visual
        document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
        e.currentTarget.classList.add('active');
        
        // Switch section
        document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
        const targetSection = document.getElementById(targetId);
        if (targetSection) targetSection.classList.add('active');
        
        // Update Title
        const titleMap = {
            'create-account': 'Open Account',
            'credit-debit': 'Move Money',
            'transfer': 'Send Funds',
            'balance': 'Check Balance',
            'statement': 'Statements'
        };
        document.getElementById('current-page-title').textContent = titleMap[targetId] || 'Dashboard';
        
        // Mobile sidebar close
        if(window.innerWidth <= 900) {
            document.getElementById('sidebar').classList.remove('open');
        }

        // Clear forms visually but keep account numbers if auto-filled
        clearForms();
        autoFillAccountNumbers();
    });
});

document.getElementById('mobile-menu-toggle').addEventListener('click', () => {
    document.getElementById('sidebar').classList.toggle('open');
});

function clearForms() {
    document.querySelectorAll('form').forEach(form => form.reset());
    document.getElementById('statementTable')?.classList.add('hidden');
    document.getElementById('emptyState')?.classList.remove('hidden');
    document.getElementById('balanceDisplay')?.classList.add('hidden');
}

// Toast System
function showToast(message, isSuccess = true) {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${isSuccess ? 'success' : 'error'}`;
    
    const icon = isSuccess ? 'fa-circle-check' : 'fa-circle-exclamation';
    
    toast.innerHTML = `
        <div class="toast-icon"><i class="fa-solid ${icon}"></i></div>
        <div class="toast-content">${message}</div>
    `;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'toastSlideOut 0.4s cubic-bezier(0.36, 0, 0.66, -0.56) forwards';
        setTimeout(() => toast.remove(), 400);
    }, 4000);
}

// Button Loading State Logic
function setBtnLoading(btn, isLoading) {
    if(isLoading) {
        btn.classList.add('loading');
        btn.disabled = true;
    } else {
        btn.classList.remove('loading');
        btn.disabled = false;
    }
}

// 1. Create Account
document.getElementById('createAccountForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    setBtnLoading(btn, true);
    
    const payload = {
        firstName: document.getElementById('firstName').value.trim(),
        lastName: document.getElementById('lastName').value.trim(),
        gender: document.getElementById('gender').value,
        address: document.getElementById('address').value.trim(),
        email: document.getElementById('email').value.trim(),
        phoneNumber: document.getElementById('phone').value.trim()
    };

    try {
        const response = await fetch(`${API_BASE_URL}/api/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        
        const data = await response.json();

        if (response.ok && (data.responcecode === "002" || data.responcecode === "200")) {
            showToast(`Account created! No: ${data.accountInfo.accountNumber}`, true);
            e.target.reset();
        } else {
            showToast(data.responcemessage || 'Failed to create account.', false);
        }
    } catch (error) {
        showToast('Network error: Unable to connect to server.', false);
    } finally {
        setBtnLoading(btn, false);
    }
});

// 2. Credit/Debit
document.getElementById('creditDebitForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    setBtnLoading(btn, true);
    
    const type = document.getElementById('transactionType').value;
    const endpoint = type === 'credit' ? '/api/users/credit' : '/api/users/debit';
    
    const payload = {
        accountNumber: document.getElementById('cdAccountNumber').value.trim(),
        amount: parseFloat(document.getElementById('cdAmount').value)
    };

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        
        const data = await response.json();

        if (response.ok && (data.responcecode === "005" || data.responcecode === "007" || data.responcecode === "200")) {
            showToast(`Success! New Balance: $${data.accountInfo.accountBalance}`, true);
            e.target.reset();
            autoFillAccountNumbers();
        } else {
            showToast(data.responcemessage || 'Error processing transaction.', false);
        }
    } catch (error) {
        showToast('Network error.', false);
    } finally {
        setBtnLoading(btn, false);
    }
});

// 3. Transfer
document.getElementById('transferForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    setBtnLoading(btn, true);
    
    const payload = {
        sourceAccountNumber: document.getElementById('sourceAccount').value.trim(),
        destinationAccountNumber: document.getElementById('destAccount').value.trim(),
        amount: parseFloat(document.getElementById('transferAmount').value)
    };

    try {
        const response = await fetch(`${API_BASE_URL}/api/users/transfer`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        
        const data = await response.json();

        if (response.ok && (data.responcecode === "008" || data.responcecode === "200")) {
            showToast('Transfer completed successfully.', true);
            e.target.reset();
            autoFillAccountNumbers();
        } else {
            showToast(data.responcemessage || 'Error processing transfer.', false);
        }
    } catch (error) {
        showToast('Network error.', false);
    } finally {
        setBtnLoading(btn, false);
    }
});

// 4. Balance Enquiry
document.getElementById('balanceForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    const balanceDisplay = document.getElementById('balanceDisplay');
    const balAmountTxt = document.getElementById('balAmountTxt');
    
    setBtnLoading(btn, true);
    balanceDisplay.classList.add('hidden');
    
    const payload = {
        accountNumber: document.getElementById('balAccountNumber').value.trim()
    };

    try {
        const response = await fetch(`${API_BASE_URL}/api/users/balanceEnquiry`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        
        const data = await response.json();

        if (response.ok && (data.responcecode === "004" || data.responcecode === "200")) {
            const balanceAmt = data.accountInfo.accountBalance;
            balAmountTxt.textContent = parseFloat(balanceAmt).toLocaleString('en-US', {minimumFractionDigits: 2});
            balanceDisplay.classList.remove('hidden');
            showToast('Balance retrieved.', true);
        } else {
            showToast(data.responcemessage || 'Account not found.', false);
        }
    } catch (error) {
        showToast('Network error.', false);
    } finally {
        setBtnLoading(btn, false);
    }
});

// 5. Bank Statement
document.getElementById('statementForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button[type="submit"]');
    const table = document.getElementById('statementTable');
    const tbody = document.getElementById('statementTableBody');
    const emptyState = document.getElementById('emptyState');
    
    setBtnLoading(btn, true);
    table.classList.add('hidden');
    emptyState.classList.add('hidden');
    tbody.innerHTML = '';

    const accountNumber = document.getElementById('stmtAccountNumber').value.trim();
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    const queryParams = new URLSearchParams({ accountNumber, startDate, endDate });

    try {
        const response = await fetch(`${API_BASE_URL}/bankstatement?${queryParams.toString()}`);
        const data = await response.json();
        
        if (response.ok && Array.isArray(data)) {
            if (data.length === 0) {
                 emptyState.classList.remove('hidden');
                 showToast('No transactions found.', true);
            } else {
                 table.classList.remove('hidden');
                 data.forEach(tx => {
                     const isCredit = tx.transactionType.toLowerCase().includes('credit');
                     const typeIcon = isCredit ? 
                         '<i class="fa-solid fa-arrow-down text-success"></i>' : 
                         '<i class="fa-solid fa-arrow-up text-danger"></i>';
                         
                     const tr = document.createElement('tr');
                     tr.innerHTML = `
                        <td>${tx.createdAt ? new Date(tx.createdAt).toLocaleDateString() : '-'}</td>
                        <td><span style="display:flex; align-items:center; gap:8px;">${typeIcon} ${tx.transactionType}</span></td>
                        <td>$${parseFloat(tx.amount).toLocaleString('en-US', {minimumFractionDigits: 2})}</td>
                        <td><span class="status-badge ${tx.status.toLowerCase()}">${tx.status}</span></td>
                     `;
                     tbody.appendChild(tr);
                 });
                 showToast('Statement loaded.', true);
            }
        } else {
            emptyState.classList.remove('hidden');
            showToast('Failed to fetch statement.', false);
        }
    } catch (error) {
        emptyState.classList.remove('hidden');
        showToast('Network error.', false);
    } finally {
        setBtnLoading(btn, false);
    }
});

// Initialize session check
window.addEventListener('load', checkSession);

// Sign Out handler
document.querySelector('a[href="login.html"]')?.addEventListener('click', (e) => {
    e.preventDefault();
    logout();
});
