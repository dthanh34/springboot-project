document.addEventListener("DOMContentLoaded", function() {
    fetchFoods();

    // Lắng nghe sự kiện tìm kiếm
    document.getElementById('foodSearch').addEventListener('input', function(e) {
        filterFoods(e.target.value);
    });
});

let allFoods = []; // Lưu trữ danh sách gốc để tìm kiếm nhanh

// 1. Lấy danh sách món ăn từ API
async function fetchFoods() {
    try {
        const response = await fetch('/api/admin/foods');
        allFoods = await response.json();
        renderFoodTable(allFoods);
    } catch (error) {
        console.error("Lỗi tải danh sách món ăn:", error);
    }
}

// 2. Render dữ liệu vào bảng
function renderFoodTable(foods) {
    const tbody = document.getElementById('food-tbody');
    if (foods.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 30px; color: #9ca3af;">Chưa có dữ liệu món ăn.</td></tr>`;
        return;
    }

    tbody.innerHTML = foods.map(f => `
        <tr style="border-bottom: 1px dashed #eee;">
            <td style="padding: 10px 15px;">
                <img src="/images/${f.imageUrl}" 
                     alt="${f.foodName}" 
                     class="food-img-td"
                     onerror="this.onerror=null; this.src='https://via.placeholder.com/150?text=No+Image'">
            </td>
            <td style="font-weight: 500;">${f.foodName}</td>
            <td>${f.calories}</td>
            <td>${f.protein}g</td>
            <td>${f.fat}g</td>
            <td>${f.carbohydrate}g</td>
            <td>
                <a href="/admin/edit_food?id=${f.foodId}" class="action-btn" style="color: #6b7280; margin-right: 10px;"><i class="fa-regular fa-pen-to-square"></i></a>
                <a href="javascript:void(0);" class="action-btn" style="color: #ef4444;" onclick="showDeleteModal('${f.foodId}', '${f.foodName}')">
                    <i class="fa-regular fa-trash-can"></i>
                </a>
            </td>
        </tr>
    `).join('');
}

// 3. Xử lý tìm kiếm (Client-side filter)
function filterFoods(keyword) {
    const filtered = allFoods.filter(f => 
        f.foodName.toLowerCase().includes(keyword.toLowerCase())
    );
    renderFoodTable(filtered);
}

// 4. Logic Modal Xóa
let currentDeleteId = null;

function showDeleteModal(id, name) {
    currentDeleteId = id;
    document.getElementById('deleteModal').style.display = 'flex';
    document.getElementById('deleteFoodName').innerText = name;
}

function closeDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
    currentDeleteId = null;
}

document.getElementById('confirmDeleteBtn').onclick = async function() {
    if (!currentDeleteId) return;

    try {
        const response = await fetch(`/api/admin/foods/${currentDeleteId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showAlert('success', 'Đã xóa món ăn thành công!');
            fetchFoods(); // Tải lại bảng
        } else {
            showAlert('error', 'Có lỗi xảy ra, không thể xóa món ăn này!');
        }
    } catch (error) {
        console.error("Lỗi xóa món ăn:", error);
        showAlert('error', 'Lỗi kết nối đến máy chủ.');
    } finally {
        closeDeleteModal();
    }
};

// 5. Hàm hiển thị thông báo
function showAlert(type, message) {
    const container = document.getElementById('alert-container');
    const isSuccess = type === 'success';
    container.innerHTML = `
        <div style="background: ${isSuccess ? '#d1fae5' : '#fee2e2'}; 
                    color: ${isSuccess ? '#059669' : '#dc2626'}; 
                    padding: 15px; border-radius: 8px; margin-bottom: 20px;">
            <i class="fa-solid ${isSuccess ? 'fa-circle-check' : 'fa-circle-exclamation'}"></i> ${message}
        </div>
    `;
    // Tự ẩn sau 3 giây
    setTimeout(() => container.innerHTML = '', 3000);
}