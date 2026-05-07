document.addEventListener("DOMContentLoaded", function() {
    loadCurrentSettings();

    // 1. Cập nhật thông tin cá nhân
    document.getElementById('personalInfoForm').onsubmit = async function(e) {
        e.preventDefault();
        const data = {
            fullName: document.getElementById('fullName').value,
            email: document.getElementById('email').value
        };
        const res = await sendUpdate('/api/user/settings/personal', data);
        renderAlert('msg-info', res);
    };

    // 2. Cập nhật mục tiêu dinh dưỡng
    document.getElementById('nutritionGoalForm').onsubmit = async function(e) {
        e.preventDefault();
        const data = {
            goalType: document.getElementById('goalType').value,
            targetCalories: document.getElementById('targetCalories').value
        };
        const res = await sendUpdate('/api/user/settings/goal', data);
        renderAlert('msg-goal', res);
    };

    // 3. Đổi mật khẩu
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

async function loadCurrentSettings() {
    try {
        const response = await fetch('/api/user/settings/data');
        const data = await response.json();
        
        // Điền data vào form
        document.getElementById('fullName').value = data.user.fullName;
        document.getElementById('email').value = data.user.email;
        document.getElementById('goalType').value = data.goal.goalType;
        document.getElementById('targetCalories').value = data.goal.targetCalories || 2000;
    } catch (err) {
        console.error("Lỗi tải cài đặt:", err);
    }
}

async function sendUpdate(url, body) {
    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        return await res.json();
    } catch (err) {
        return { success: false, message: "Lỗi kết nối máy chủ!" };
    }
}

function renderAlert(containerId, res) {
    const container = document.getElementById(containerId);
    const type = res.success ? 'alert-success' : 'alert-error';
    container.innerHTML = `<div class="alert ${type}">${res.message}</div>`;
    setTimeout(() => container.innerHTML = '', 4000);
}