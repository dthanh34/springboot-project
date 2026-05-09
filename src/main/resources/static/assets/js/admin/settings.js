document.addEventListener("DOMContentLoaded", function() {
    loadAdminInfo();

    // Xử lý cập nhật thông tin cá nhân
    document.getElementById('infoForm').onsubmit = async function(e) {
        e.preventDefault();
        const data = {
            fullName: document.getElementById('adminFullName').value,
            email: document.getElementById('adminEmail').value
        };
        const res = await postData('/api/user/settings/personal', data);
        showAlert('alert-info', res.success, res.message || 'Cập nhật thông tin thành công');
    };

    // Xử lý đổi mật khẩu
    document.getElementById('passwordForm').onsubmit = async function(e) {
        e.preventDefault();
        const newPass = document.getElementById('newPassword').value;
        const confirmPass = document.getElementById('confirmPassword').value;

        if (newPass !== confirmPass) {
             showAlert('alert-pass', false, 'Mật khẩu xác nhận không khớp!');
            return;
        }

        const data = {
            oldPassword: document.getElementById('oldPassword').value,
            newPassword: newPass
        };
        const res = await postData('/api/user/settings/password', data);
        showAlert('alert-pass', res.success, res.message || 'Đổi mật khẩu thành công');
        if (res.success) e.target.reset();
    };
});

async function loadAdminInfo() {
    try {
        const res = await fetch('/api/user/settings/data');
       const payload = await res.json();
        if (payload?.user) {
            document.getElementById('adminFullName').value = payload.user.fullName || '';
            document.getElementById('adminEmail').value = payload.user.email || '';
        }
    } catch (err) {
       showAlert('alert-info', false, 'Không tải được thông tin quản trị viên.');
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
         return { success: false, message: 'Lỗi kết nối máy chủ!' };
    }
}

function showAlert(containerId, isSuccess, message) {
    const container = document.getElementById(containerId);
    container.innerHTML = `<div class="alert ${isSuccess ? 'alert-success' : 'alert-error'}">${message}</div>`;
    setTimeout(() => (container.innerHTML = ''), 4000);
}