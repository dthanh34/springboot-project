document.addEventListener("DOMContentLoaded", function() {
    fetchIngredients(1);

    // Tìm kiếm real-time
    document.getElementById('ingSearch').addEventListener('input', function(e) {
        fetchIngredients(1, e.target.value);
    });
});

async function fetchIngredients(page, keyword = '') {
    try {
        const response = await fetch(`/api/admin/ingredients?page=${page}&keyword=${keyword}`);
        const data = await response.json();

        document.getElementById('total-count').innerText = data.totalRecords;
        renderTable(data.ingredients);
        renderPagination(data.currentPage, data.totalPages, keyword);
    } catch (error) {
        console.error("Lỗi tải danh sách nguyên liệu:", error);
    }
}

function renderTable(list) {
    const tbody = document.getElementById('ing-tbody');
    if (list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; padding: 30px; color: #9ca3af;">Chưa có dữ liệu nguyên liệu nào.</td></tr>`;
        return;
    }

    tbody.innerHTML = list.map(item => `
        <tr>
            <td style="font-weight: 500;">${item.ingredientName}</td>
            <td><span class="badge-category">${item.category}</span></td>
            <td>${item.calories}</td>
            <td>${item.protein}</td>
            <td>${item.fat}</td>
            <td>${item.carbohydrate}</td>
            <td>100g</td>
            <td>
                <a href="/admin/edit_ingredient?id=${item.ingredientId}" class="action-btn" style="color: #6b7280; margin-right: 10px;">
                    <i class="fa-regular fa-pen-to-square"></i>
                </a>
                <a href="javascript:void(0);" class="action-btn" style="color: #ef4444;" onclick="showDeleteModal('${item.ingredientId}', '${item.ingredientName}')">
                    <i class="fa-regular fa-trash-can"></i>
                </a>
            </td>
        </tr>
    `).join('');
}

function renderPagination(current, total, keyword) {
    const pagination = document.getElementById('ing-pagination');
    if (total <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let html = '';
    if (current > 1) {
        html += `<a onclick="fetchIngredients(${current - 1}, '${keyword}')"><i class="fa-solid fa-angle-left"></i></a>`;
    }

    for (let i = 1; i <= total; i++) {
        html += `<a class="${i === current ? 'active' : ''}" onclick="fetchIngredients(${i}, '${keyword}')">${i}</a>`;
    }

    if (current < total) {
        html += `<a onclick="fetchIngredients(${current + 1}, '${keyword}')"><i class="fa-solid fa-angle-right"></i></a>`;
    }
    pagination.innerHTML = html;
}

// Logic xóa
let deleteId = null;

function showDeleteModal(id, name) {
    deleteId = id;
    document.getElementById('deleteModal').style.display = 'flex';
    document.getElementById('deleteIngredientName').innerText = name;
}

function closeDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
}

document.getElementById('confirmDeleteBtn').onclick = async function() {
    if (!deleteId) return;
    try {
        const response = await fetch(`/api/admin/ingredients/${deleteId}`, { method: 'DELETE' });
        if (response.ok) {
            showAlert('success', 'Đã xóa nguyên liệu thành công!');
            fetchIngredients(1);
        } else {
            showAlert('error', 'Không thể xóa nguyên liệu này!');
        }
    } catch (e) {
        showAlert('error', 'Lỗi kết nối máy chủ.');
    } finally {
        closeDeleteModal();
    }
};

function showAlert(type, msg) {
    const container = document.getElementById('alert-container');
    const isSuccess = type === 'success';
    container.innerHTML = `
        <div style="background: ${isSuccess ? '#d1fae5' : '#fee2e2'}; color: ${isSuccess ? '#059669' : '#dc2626'}; padding: 15px; border-radius: 8px; margin-bottom: 20px;">
            <i class="fa-solid ${isSuccess ? 'fa-circle-check' : 'fa-circle-exclamation'}"></i> ${msg}
        </div>
    `;
    setTimeout(() => container.innerHTML = '', 3000);
}