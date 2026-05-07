document.addEventListener("DOMContentLoaded", function() {
    const foodGrid = document.getElementById('food-grid');
    const countText = document.getElementById('food-count-text');

    // 1. Tự động kiểm tra param success trên URL để hiện Toast
    checkSuccessParam();

    // 2. Gọi API lấy danh sách gợi ý
    async function fetchSuggestedFoods() {
        try {
            const response = await fetch('/api/user/suggested-foods');
            const foods = await response.json();

            if (foods && foods.length > 0) {
                countText.innerHTML = `Có <strong>${foods.length}</strong> món ăn phù hợp với bạn hôm nay`;
                renderFoodCards(foods);
            } else {
                countText.innerText = "Hiện chưa tìm thấy món ăn nào phù hợp với chỉ số của bạn.";
                foodGrid.innerHTML = `<p style="grid-column: 1/-1; text-align: center; color: #999; padding: 40px;">Hãy cập nhật hồ sơ sức khỏe để nhận gợi ý!</p>`;
            }
        } catch (error) {
            console.error("Lỗi tải danh sách món ăn:", error);
            countText.innerText = "Không thể kết nối đến máy chủ.";
        }
    }

    fetchSuggestedFoods();

    // 3. Render danh sách thẻ món ăn
    function renderFoodCards(foods) {
        foodGrid.innerHTML = foods.map(food => {
            const safetyBadge = food.allergyConflictCount === 0 
                ? `<span class="badge-safe">✔ An toàn</span>` 
                : `<span class="badge-safe" style="background:#fff7ed; color:#c2410c; border-color:#fdba74;">⚠ Cân nhắc</span>`;

            return `
                <a href="/food_detail?id=${food.foodId}" class="card">
                    <div class="card-img-wrapper">
                        <img src="/images/${food.imageUrl}" alt="${food.foodName}" 
                             onerror="this.onerror=null; this.src='https://via.placeholder.com/300x200?text=No+Image'">
                        ${safetyBadge}
                    </div>
                    <div class="card-body">
                        <h3 class="card-title">${food.foodName}</h3>
                        <div class="card-calo">${Math.round(food.calories)} calo</div>
                        <div class="card-rating">
                            <i class="fa-solid fa-star"></i> 
                            ${Math.round(food.suitabilityScore)}% phù hợp
                        </div>
                    </div>
                </a>
            `;
        }).join('');
    }

    // 4. Logic hiển thị Toast và dọn dẹp URL
    function checkSuccessParam() {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('success') === 'true') {
            const toast = document.getElementById("toast");
            toast.style.display = 'block';
            toast.style.opacity = '1';

            setTimeout(() => {
                toast.style.opacity = '0';
                setTimeout(() => { 
                    toast.style.display = 'none';
                    // Xóa parameter thành công trên URL cho sạch
                    window.history.replaceState(null, null, window.location.pathname);
                }, 500);
            }, 3000);
        }
    }
});