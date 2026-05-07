document.addEventListener("DOMContentLoaded", function() {
    loadAdminInfo();

    // Xử lý cập nhật thông tin cá nhân
    document.getElementById('personalInfoForm').onsubmit = async function(e) {
        e.preventDefault();
        const data = {
            fullName: document.getElementById('fullName').value,
            email: document.getElementById('email').value
        };
        const res = await sendUpdate('/api/user/settings/personal', data);
        renderAlert('msg-info', res);
    };

    // Xử lý đổi mật khẩu
    document.getElementById('passwordForm').onsubmit = async function(e) {
        e.preventDefault();
        const newPass = document.getElementById('newPassword').value;
        const confirmPass = document.getElementById('confirmPassword').value;

        if (newPass !== confirmPass) {
            renderAlert('msg-pass', { success: false, message: "Mật khẩu xác nhận không khớp!" });
            return;
        }

        const data = {
            oldPassword: document.getElementById('oldPassword').value,
            newPassword: newPass
        };
        const res = await sendUpdate('/api/user/settings/password', data);
        renderAlert('msg-pass', res);
        if(res.success) e.target.reset();
    };
});

async function loadAdminInfo() {
    try {
        const res = await fetch('/api/user/settings/data');
        const admin = await res.json();
        document.getElementById('adminFullName').value = admin.user.fullName;
        document.getElementById('adminEmail').value = admin.user.email;
    } catch (err) {
        console.error("Lỗi load thông tin:", err);
    }
}

async function postData(url, data) {
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        return await response.json();
    } catch (err) {
        return { success: false, message: "Lỗi kết nối máy chủ!" };
    }
}

function showAlert(containerId, isSuccess, message) {
    const container = document.getElementById(containerId);
    container.innerHTML = `<div class="alert ${isSuccess ? 'alert-success' : 'alert-error'}">${message}</div>`;
    setTimeout(() => container.innerHTML = '', 4000); // Tự ẩn sau 4 giây
}