document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const foodId = urlParams.get('id');
    let isFavorite = false;

    if (!foodId) {
        window.location.href = '/foods';
        return;
    }

    // 🔥 LOAD DATA
    async function fetchFoodDetail() {
        try {
            const response = await fetch(`/api/user/foods/${foodId}`);

            if (!response.ok) {
                throw new Error("API lỗi: " + response.status);
            }

            const data = await response.json();
            console.log("DATA:", data); // debug

            renderData(data);

        } catch (error) {
            console.error("Lỗi khi tải chi tiết món ăn:", error);
        }
    }

    fetchFoodDetail();

    // 🔥 RENDER
    function renderData(food) {
        const ingredients = food.ingredients || [];
        isFavorite = food.isFavorite || false;

        // Thông tin cơ bản
        document.getElementById('foodName').innerText = food.foodName || "";
        document.getElementById('foodDesc').innerText = food.description || "";
        document.getElementById('foodRecipe').innerText = food.recipe || "Đang cập nhật...";
        
        if (food.imageUrl) {
            document.getElementById('foodHeroImage').src = `/images/${food.imageUrl}`;
        }

        // Dinh dưỡng
        document.getElementById('valCalo').innerText = food.calories ?? 0;
        document.getElementById('valProtein').innerText = (food.protein ?? 0) + 'g';
        document.getElementById('valFat').innerText = (food.fat ?? 0) + 'g';
        document.getElementById('valCarb').innerText = (food.carbohydrate ?? 0) + 'g';

        // Link
        document.getElementById('linkCustomize').href = `/customize-recipe?id=${food.foodId}`;

        // Favorite UI
        updateFavoriteUI();

        // 🔥 INGREDIENTS
        const ingGrid = document.getElementById('ingredientGrid');

        if (ingredients.length > 0) {
            ingGrid.innerHTML = ingredients.map(ing => `
                <div class="ing-item">
                    <span class="ing-name">${ing.ingredientName}</span>
                    <span class="ing-qty">${ing.quantity} ${ing.unit}</span>
                </div>
            `).join('');
        } else {
            ingGrid.innerHTML = `
                <p style="color:#999;font-style:italic;text-align:center;width:100%">
                    Chưa có dữ liệu nguyên liệu
                </p>`;
        }
    }

    // 🔥 FAVORITE
    const btnFav = document.getElementById('btnToggleFavorite');

    if (btnFav) {
        btnFav.addEventListener('click', async function () {
            try {

                const response = await fetch(`/api/user/favorites/toggle`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ foodId: Number(foodId) })
                });

                if (response.ok) {
                    isFavorite = !isFavorite;
                    updateFavoriteUI();
                    showToast(
                        isFavorite ? "Đã thêm vào yêu thích!" : "Đã bỏ yêu thích.",
                        isFavorite
                    );
                }

            } catch (err) {
                console.error("Lỗi toggle favorite:", err);
            }
        });
    }

    function updateFavoriteUI() {
        if (!btnFav) return;

        const icon = btnFav.querySelector('i');
        const text = document.getElementById('favText');

        if (isFavorite) {
            btnFav.classList.add('is-favorite');
            icon.className = 'fa-solid fa-heart';
            text.innerText = 'Đã yêu thích';
        } else {
            btnFav.classList.remove('is-favorite');
            icon.className = 'fa-regular fa-heart';
            text.innerText = 'Thêm yêu thích';
        }
    }

    function showToast(message, isAdd) {
        const toast = document.getElementById('toast');
        if (!toast) return;

        toast.innerHTML = `<i class="fa-${isAdd ? 'solid' : 'regular'} fa-heart"></i> ${message}`;
        toast.style.display = 'block';
        toast.style.opacity = '1';
        toast.style.background = isAdd ? '#ef4444' : '#6b7280';

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => { toast.style.display = 'none'; }, 400);
        }, 2000);
    }
});