document.addEventListener("DOMContentLoaded", function() {
    let currentTab = 'favorites'; // Mặc định là tab yêu thích
    const grid = document.getElementById('foods-grid');
    const emptyState = document.getElementById('empty-state');
    const searchInput = document.getElementById('favSearchInput');

    // 1. Khởi tạo dữ liệu
    fetchData();

    // 2. Xử lý chuyển Tab
    document.querySelectorAll('.switch-tab').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.switch-tab').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            currentTab = this.dataset.tab;
            fetchData();
        });
    });

    // 3. Xử lý tìm kiếm
    document.getElementById('btnSearchFav').addEventListener('click', fetchData);
    searchInput.addEventListener('keypress', (e) => { if(e.key === 'Enter') fetchData(); });

    async function fetchData() {
        const keyword = searchInput.value;
        try {
            const response = await fetch(`/api/user/favorites?tab=${currentTab}&q=${encodeURIComponent(keyword)}`);
            const data = await response.json();
            renderGrid(data);
        } catch (error) {
            console.error("Lỗi tải dữ liệu:", error);
        }
    }

    function renderGrid(foods) {
        grid.innerHTML = '';
        if (foods.length === 0) {
            grid.style.display = 'none';
            emptyState.style.display = 'block';
            if (currentTab === 'favorites') {
                document.getElementById('empty-icon').className = 'fa-regular fa-heart';
                document.getElementById('empty-text').innerText = 'Bạn chưa có món ăn yêu thích nào.';
            } else {
                document.getElementById('empty-icon').className = 'fa-regular fa-pen-to-square';
                document.getElementById('empty-text').innerText = 'Bạn chưa có công thức chỉnh sửa nào.';
            }
            return;
        }

        grid.style.display = 'grid';
        emptyState.style.display = 'none';

        grid.innerHTML = foods.map(food => {
            if (currentTab === 'favorites') {
                return renderFavoriteCard(food);
            } else {
                return renderCustomizedCard(food);
            }
        }).join('');
    }

    function renderFavoriteCard(food) {
        return `
            <article class="card">
            <a href="/food_detail?id=${food.foodId}" class="card-link">
                <div class="card-img-wrapper">
                        <img src="/images/${food.imageUrl}" alt="${food.foodName}" onerror="this.src='https://via.placeholder.com/300x200?text=No+Image'">
                    </a>
                    <span class="badge-favorite"><i class="fa-solid fa-heart"></i> Đã thích</span>
                    <button onclick="removeFavorite(${food.foodId})" class="btn-remove-favorite" title="Xóa khỏi yêu thích">
                        <i class="fa-regular fa-trash-can"></i>
                    </button>
                </div>
                <a href="/food_detail?id=${food.foodId}" class="card-link">
                    <div class="card-body">
                        <h3 class="card-title">${food.foodName}</h3>
                        <div class="card-calo">${Math.round(food.calories)} calo</div>
                        <div class="card-rating"><i class="fa-solid fa-star"></i> Ưu tiên hiển thị</div>
                    </div>
                </a>
            </article>
        `;
    }

    function renderCustomizedCard(food) {
        return `
            <a href="/food-detail?id=${food.foodId}&source=customized" class="card">
                <div class="card-img-wrapper">
                    <img src="/images/${food.imageUrl}" alt="${food.foodName}" onerror="this.src='https://via.placeholder.com/300x200?text=No+Image'">
                    <span class="badge-custom"><i class="fa-regular fa-pen-to-square"></i> Công thức riêng</span>
                </div>
                <div class="card-body">
                    <h3 class="card-title">${food.foodName}</h3>
                    <div class="card-calo">${Math.round(food.calories)} calo</div>
                    <div class="card-rating"><i class="fa-solid fa-flask"></i> Đã tùy chỉnh công thức</div>
                </div>
            </a>
        `;
    }

    // 4. Hàm xóa yêu thích
    window.removeFavorite = async function(foodId) {
        if (!confirm('Bạn muốn xóa món này khỏi mục yêu thích?')) return;
        try {
            const response = await fetch(`/api/user/favorites/remove?foodId=${foodId}`, { method: 'POST' });
            if (response.ok) {
                showSuccessAlert("Đã xóa món khỏi mục yêu thích.");
                fetchData();
            }
        } catch (error) {
            console.error("Lỗi xóa:", error);
        }
    }

    function showSuccessAlert(msg) {
        const container = document.getElementById('alert-container');
        container.innerHTML = `<div class="alert-success"><i class="fa-solid fa-check"></i> ${msg}</div>`;
        setTimeout(() => container.innerHTML = '', 3000);
    }
});