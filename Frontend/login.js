const API_BASE_URL = 'http://localhost:8080';

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
    
    // Auto removal
    setTimeout(() => {
        toast.style.animation = 'toastSlideOut 0.4s cubic-bezier(0.36, 0, 0.66, -0.56) forwards';
        setTimeout(() => toast.remove(), 400);
    }, 4000);
}

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const accountNumber = document.getElementById('accountNumber').value.trim();
    const username = document.getElementById('username').value.trim();
    const btn = document.getElementById('loginBtn');
    
    if (!accountNumber || !username) {
        showToast("Please fill in all fields.", false);
        return;
    }

    btn.classList.add('loading');
    btn.disabled = true;

    const payload = {
        accountNumber: accountNumber,
        username: username
    };

    try {
        const response = await fetch(`${API_BASE_URL}/api/users/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok && (data.responcecode === "200" || data.responcecode === "000")) {
            // Save session
            localStorage.setItem('bank_user', JSON.stringify({
                accountNumber: data.accountInfo.accountNumber,
                accountName: data.accountInfo.accountName,
                isLoggedIn: true
            }));

            showToast("Login Successful! Redirecting...", true);
            setTimeout(() => {
                window.location.href = "index.html";
            }, 1000);
        } else {
            showToast(data.responcemessage || "Invalid login credentials", false);
        }

    } catch (error) {
        console.error("Login Error:", error);
        showToast("Network error: Unable to connect to server.", false);
    } finally {
        btn.classList.remove('loading');
        btn.disabled = false;
    }
});