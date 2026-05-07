document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    const foodId = urlParams.get('id');
    let allIngredientsData = []; // Cache danh sách nguyên liệu từ server

    if (!foodId) {
        window.location.href = '/foods';
        return;
    }

    // 1. Khởi tạo dữ liệu
    async function init() {
        try {
            // Lấy danh sách tất cả nguyên liệu để chọn thêm
            const ingRes = await fetch('/api/ingredients');
            allIngredientsData = await ingRes.json();

            // Lấy thông tin món ăn hiện tại và nguyên liệu của nó
            const foodRes = await fetch(`/api/user/customize-recipe/${foodId}`);
            const data = await foodRes.json();

            document.getElementById('foodId').value = data.food.food_id;
            document.getElementById('foodNameDisplay').value = data.food.food_name;
            
            if (data.ingredientList && data.ingredientList.length > 0) {
                data.ingredientList.forEach(item => renderIngredientRow(item));
            } else {
                document.getElementById('no-data-msg').style.display = 'block';
            }

            updateMacros();
        } catch (error) {
            console.error("Lỗi khởi tạo:", error);
        }
    }

    init();

    // 2. Render dòng nguyên liệu
    function renderIngredientRow(item = null) {
        const container = document.getElementById('ingredient-container');
        const msg = document.getElementById('no-data-msg');
        if (msg) msg.style.display = 'none';

        const template = document.getElementById('ingredient-template').content.cloneNode(true);
        const row = template.querySelector('.ingredient-item');
        const select = row.querySelector('.name-select');
        const qtyInput = row.querySelector('.qty-input');
        const btnTrash = row.querySelector('.btn-trash');

        // Đổ option vào select
        allIngredientsData.forEach(ing => {
            const opt = document.createElement('option');
            opt.value = ing.ingredientId;
            opt.text = ing.ingredientName;
            opt.dataset.calo = ing.caloPerGram;
            opt.dataset.fat = ing.fatPerGram;
            opt.dataset.protein = ing.proteinPerGram;
            opt.dataset.carbs = ing.carbsPerGram;
            if (item && ing.ingredientId == item.ingredientId) opt.selected = true;
            select.appendChild(opt);
        });

        if (item) {
            qtyInput.value = item.quantity;
            qtyInput.dataset.calo = item.caloPerGram;
            qtyInput.dataset.fat = item.fatPerGram;
            qtyInput.dataset.protein = item.proteinPerGram;
            qtyInput.dataset.carbs = item.carbsPerGram;
        }

        // Event: Thay đổi nguyên liệu
        select.addEventListener('change', function() {
            const opt = this.options[this.selectedIndex];
            qtyInput.dataset.calo = opt.dataset.calo;
            qtyInput.dataset.fat = opt.dataset.fat;
            qtyInput.dataset.protein = opt.dataset.protein;
            qtyInput.dataset.carbs = opt.dataset.carbs;
            updateMacros();
        });

        // Event: Thay đổi số lượng
        qtyInput.addEventListener('input', updateMacros);

        // Event: Xóa dòng
        btnTrash.addEventListener('click', function() {
            row.remove();
            updateMacros();
            if (container.children.length === 0) document.getElementById('no-data-msg').style.display = 'block';
        });

        container.appendChild(row);
    }

    // 3. Logic tính toán Macros
    function updateMacros() {
        let tCalo = 0, tFat = 0, tPro = 0, tCarb = 0;
        const inputs = document.querySelectorAll('.qty-input');

        inputs.forEach(input => {
            const qty = parseFloat(input.value) || 0;
            tCalo += qty * (parseFloat(input.dataset.calo) || 0);
            tFat += qty * (parseFloat(input.dataset.fat) || 0);
            tPro += qty * (parseFloat(input.dataset.protein) || 0);
            tCarb += qty * (parseFloat(input.dataset.carbs) || 0);
        });

        // Hiển thị lên UI
        document.getElementById('displayCalo').value = Math.round(tCalo);
        document.getElementById('displayFat').value = tFat.toFixed(1);
        document.getElementById('displayProtein').value = tPro.toFixed(1);
        document.getElementById('displayCarbs').value = tCarb.toFixed(1);

        // Gán vào hidden inputs để submit
        document.getElementById('calculatedCalories').value = Math.round(tCalo);
        document.getElementById('calculatedFat').value = tFat.toFixed(2);
        document.getElementById('calculatedProtein').value = tPro.toFixed(2);
        document.getElementById('calculatedCarbs').value = tCarb.toFixed(2);
    }

    // 4. Thêm nguyên liệu mới
    document.getElementById('btnAddIngredient').addEventListener('click', () => renderIngredientRow());

    // 5. Submit Form
    document.getElementById('customForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const formData = new FormData(this);
        
        try {
            const res = await fetch('/api/user/customize-recipe', {
                method: 'POST',
                body: new URLSearchParams(formData)
            });

            if (res.ok) {
                showToast("Cập nhật công thức thành công!");
                setTimeout(() => window.location.href = `/food-detail?id=${foodId}`, 2000);
            } else {
                const err = await res.text();
                document.getElementById('alert-error').innerText = err;
                document.getElementById('alert-error').style.display = 'block';
            }
        } catch (error) {
            console.error(error);
        }
    });

    function showToast(msg) {
        const toast = document.getElementById("toast");
        toast.innerHTML = `<i class="fa-solid fa-circle-check"></i> ${msg}`;
        toast.className = "show";
        setTimeout(() => toast.className = "", 3000);
    }
});