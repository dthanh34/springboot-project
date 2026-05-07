$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const foodId = urlParams.get('id');
    let allIngredients = [];

    if (!foodId) {
        window.location.href = "/meal_plan";
        return;
    }

    // 1. Khởi tạo dữ liệu từ API dành cho User
    async function initPage() {
        try {
            // Lấy danh sách nguyên liệu để User có thể thêm món khác vào
            const ingRes = await fetch('/api/user/ingredients');
            allIngredients = await ingRes.json();

            // Lấy thông tin món ăn User đang chọn
            const foodRes = await fetch(`/api/user/foods/${foodId}`);
            const foodData = await foodRes.json();

            fillData(foodData);
        } catch (err) {
            console.error("Lỗi:", err);
        }
    }

    initPage();

    function fillData(data) {
        $('#foodId').val(data.id);
        $('#foodName').val(data.name);
        $('#recipe').val(data.userNote || data.recipe); // Ưu tiên note cá nhân nếu có

        if (data.ingredients && data.ingredients.length > 0) {
            data.ingredients.forEach(item => {
                renderNewRow(item.ingredientId, item.quantity, item.unit);
            });
        }
        calculateUserMacros();
    }

    // 2. Render dòng nguyên liệu
    function renderNewRow(id = '', qty = '', unit = 'g') {
        const template = document.getElementById('ing-row-template').content.cloneNode(true);
        const $row = $(template.querySelector('.ing-row'));
        const $select = $row.find('.ing-select');

        allIngredients.forEach(ing => {
            $select.append(`<option value="${ing.id}" 
                data-cal="${ing.calories}" data-pro="${ing.protein}" 
                data-fat="${ing.fat}" data-carb="${ing.carbohydrate}"
                ${ing.id == id ? 'selected' : ''}>${ing.name}</option>`);
        });

        $row.find('.ing-qty').val(qty);
        $row.find('.ing-unit').val(unit);
        
        $('#ingredient-container').append($row);
        $select.select2({ width: '100%' });
    }

    // 3. Tính toán dinh dưỡng real-time cho User
    function calculateUserMacros() {
        let tCal = 0, tPro = 0, tFat = 0, tCarb = 0;
        $('.ing-row').each(function() {
            let opt = $(this).find('.ing-select option:selected');
            let qty = parseFloat($(this).find('.ing-qty').val()) || 0;
            if (opt.val()) {
                let ratio = qty / 100;
                tCal += ratio * (parseFloat(opt.data('cal')) || 0);
                tPro += ratio * (parseFloat(opt.data('pro')) || 0);
                tFat += ratio * (parseFloat(opt.data('fat')) || 0);
                tCarb += ratio * (parseFloat(opt.data('carb')) || 0);
            }
        });
        $('#totalCal').val(tCal.toFixed(0));
        $('#totalPro').val(tPro.toFixed(1));
        $('#totalFat').val(tFat.toFixed(1));
        $('#totalCarb').val(tCarb.toFixed(1));
    }

    // 4. Sự kiện UI
    $('#btnAddRow').click(() => renderNewRow());
    $('#ingredient-container').on('change input', '.ing-select, .ing-qty', calculateUserMacros);
    $('#ingredient-container').on('click', '.btn-remove', function() {
        $(this).closest('.ing-row').remove();
        calculateUserMacros();
    });

    // 5. User lưu thay đổi vào thực đơn cá nhân
    $('#userEditFoodForm').on('submit', async function(e) {
        e.preventDefault();
        
        const payload = {
            foodId: $('#foodId').val(),
            note: $('#recipe').val(),
            ingredients: []
        };

        $('.ing-row').each(function() {
            payload.ingredients.push({
                ingredientId: $(this).find('.ing-select').val(),
                quantity: $(this).find('.ing-qty').val(),
                unit: $(this).find('.ing-unit').val()
            });
        });

        const response = await fetch('/api/user/save-custom-food', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert("Đã cập nhật món ăn vào thực đơn cá nhân!");
            window.location.href = "/meal_plan";
        }
    });
});