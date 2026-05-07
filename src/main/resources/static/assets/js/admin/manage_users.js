document.addEventListener("DOMContentLoaded", function() {
    fetchUsers(1);
    
    // Đóng modal khi click ra ngoài
    window.onclick = function(event) {
        if (event.target == document.getElementById("emailModal")) {
            closeEmailModal();
        }
    };
});

async function fetchUsers(page) {
    const keyword = document.getElementById('searchKeyword').value;
    const role = document.getElementById('filterRole').value;
    
    try {
        const response = await fetch(`/api/admin/users?page=${page}&keyword=${keyword}&role=${role}`);
        const data = await response.json();

        // Cập nhật stats
        document.getElementById('stat-total').innerText = data.totalCount;
        document.getElementById('stat-active').innerText = data.activeCount;
        document.getElementById('stat-inactive').innerText = data.inactiveCount;

        renderUserTable(data.users);
        renderPagination(data.currentPage, data.totalPages);
    } catch (error) {
        console.error("Lỗi tải người dùng:", error);
    }
}

function renderUserTable(users) {
    const tbody = document.getElementById('user-table-body');
    tbody.innerHTML = users.map(u => `
        <tr>
            <td>${u.name}</td>
            <td>${u.email}</td>
            <td><strong>${u.role}</strong></td>
            <td>
                <span class="status-badge" style="background:${u.isActivate ? '#d1fae5' : '#fee2e2'}; color:${u.isActivate ? '#065f46' : '#991b1b'}; padding: 5px 12px; border-radius: 20px; font-size: 13px;">
                    ${u.isActivate ? 'Hoạt động' : 'Bị vô hiệu'}
                </span>
            </td>
            <td>${new Date(u.createAt).toLocaleDateString('vi-VN')}</td>
            <td>
                <div class="action-wrapper" style="position: relative;">
                    <button onclick="toggleActionMenu(this)" class="action-btn"><i class="fa-solid fa-ellipsis"></i></button>
                    <div class="action-menu" style="display:none; position:absolute; right:0; background:#fff; box-shadow:0 4px 8px rgba(0,0,0,0.15); z-index:10; border-radius:5px; padding:10px; width:150px;">
                        <a href="javascript:void(0)" onclick="openEmailModal('${u.email}')" style="display:block; text-decoration:none; color:#333; margin-bottom:10px;"><i class="fa-regular fa-envelope"></i> Gửi email</a>
                        <a href="javascript:void(0)" onclick="updateStatus(${u.id}, ${!u.isActivate})" style="display:block; text-decoration:none; color:${u.isActivate ? '#e74c3c' : '#16a34a'};">
                            <i class="fa-solid ${u.isActivate ? 'fa-user-lock' : 'fa-user-check'}"></i> ${u.isActivate ? 'Vô hiệu hóa' : 'Kích hoạt'}
                        </a>
                    </div>
                </div>
            </td>
        </tr>
    `).join('');
}

function renderPagination(current, total) {
    const pagin = document.getElementById('user-pagination');
    let html = '';
    for (let i = 1; i <= total; i++) {
        html += `<a class="${current === i ? 'active' : ''}" onclick="fetchUsers(${i})">${i}</a>`;
    }
    pagin.innerHTML = html;
}

function toggleActionMenu(btn) {
    const allMenus = document.querySelectorAll('.action-menu');
    allMenus.forEach(m => m.style.display = 'none');
    const menu = btn.nextElementSibling;
    menu.style.display = menu.style.display === 'none' ? 'block' : 'none';
}

// Modal Functions
function openEmailModal(email) {
    document.getElementById("emailModal").style.display = "block";
    document.getElementById("modalEmail").value = email;
}

function closeEmailModal() {
    document.getElementById("emailModal").style.display = "none";
    document.getElementById("emailForm").reset();
}

document.getElementById('emailForm').onsubmit = async function(e) {
    e.preventDefault();
    alert("Đã gửi email thông báo thành công!");
    closeEmailModal();
};

async function updateStatus(userId, status) {
    if (confirm(`Bạn có chắc muốn ${status ? 'kích hoạt' : 'vô hiệu hóa'} tài khoản này?`)) {
        const response = await fetch(`/api/admin/users/${userId}/status?active=${status}`, { method: 'PUT' });
        if (response.ok) fetchUsers(1);
    }
}