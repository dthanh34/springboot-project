$(document).ready(function() {
    let allIngredients = []; 

    // 1. Tải danh sách nguyên liệu khi vào trang
    async function fetchIngredients() {
    try {
        const response = await fetch('/api/admin/ingredients?page=1&keyword=');
        const data = await response.json();

        // ✅ lấy đúng danh sách
        allIngredients = data.ingredients;

        const $select = $('.template-row .ing-select');

        allIngredients.forEach(ing => {
            $select.append(`<option value="${ing.ingredientId}" 
                data-cal="${ing.calories}" 
                data-pro="${ing.protein}" 
                data-fat="${ing.fat}" 
                data-carb="${ing.carbohydrate}">
                ${ing.ingredientName} (${ing.category})
            </option>`);
        });

        $('#btnAddRow').click();

    } catch (error) {
        console.error("Lỗi khi tải nguyên liệu:", error);
    }
}

    fetchIngredients();

    // 2. Hàm tính toán dinh dưỡng tổng
    function calculateMacros() {
        let totalCal = 0, totalPro = 0, totalFat = 0, totalCarb = 0;
        $('.ingredient-row:not(.template-row)').each(function() {
            let $selectedOption = $(this).find('.ing-select option:selected');
            let qty = parseFloat($(this).find('.ing-qty').val()) || 0;
            if($selectedOption.val()) {
                let ratio = qty / 100;
                totalCal += ratio * parseFloat($selectedOption.data('cal') || 0);
                totalPro += ratio * parseFloat($selectedOption.data('pro') || 0);
                totalFat += ratio * parseFloat($selectedOption.data('fat') || 0);
                totalCarb += ratio * parseFloat($selectedOption.data('carb') || 0);
            }
        });
        $('#totalCal').val(totalCal.toFixed(1));
        $('#totalPro').val(totalPro.toFixed(1));
        $('#totalFat').val(totalFat.toFixed(1));
        $('#totalCarb').val(totalCarb.toFixed(1));
    }

    // 3. Thêm dòng nguyên liệu mới
    $('#btnAddRow').click(function() {
        let $newRow = $('.template-row').clone().removeClass('template-row').show();
        $newRow.find('input, select').removeAttr('disabled');
        $('#ingredient-container').append($newRow);
        $newRow.find('.ing-select').select2({ placeholder: "Tìm tên nguyên liệu...", width: '100%' });
    });

    // 4. Xử lý UI file upload
    $('#foodType').on('change', function() {
        $(this).removeClass('error-border');
    });
    $('#imageFile').on('change', function() {
        if (this.files.length > 0) {
            $('#fileNameDisplay').text(this.files[0].name).css('color', '#10b981');
        }
    });

    // 5. Event listeners cho tính toán
    $('#ingredient-container').on('change', '.ing-select', function() {
        $(this).next('.select2-container').find('.select2-selection').removeClass('error-border');
        calculateMacros();
    });
    $('#ingredient-container').on('input', '.ing-qty', function() {
        $(this).removeClass('error-border');
        calculateMacros();
    });
    $('#ingredient-container').on('click', '.btn-remove-row', function() {
        $(this).closest('.ingredient-row').remove();
        calculateMacros(); 
    });

    // 6. Xử lý SUBMIT FORM qua API
    $('#addFoodForm').on('submit', async function(e) {
        e.preventDefault();
        let isValid = true;
        let activeRows = $('.ingredient-row:not(.template-row)');

        // Validate cơ bản
        if (activeRows.length === 0) {
            alert('Vui lòng thêm ít nhất 1 nguyên liệu!');
            isValid = false;
        } else {
            activeRows.each(function() {
                if (!$(this).find('.ing-select').val()) {
                    $(this).find('.select2-selection').addClass('error-border');
                    isValid = false;
                }
                if (!$(this).find('.ing-qty').val() || $(this).find('.ing-qty').val() <= 0) {
                    $(this).find('.ing-qty').addClass('error-border');
                    isValid = false;
                }
            });
        }

        let fileInput = $('#imageFile')[0];
        if (fileInput.files.length === 0) {
            $('#fileNameDisplay').text("Vui lòng chọn ảnh!").css('color', '#dc2626');
            isValid = false;
        }
        if (!$('#foodType').val()) {
            $('#foodType').addClass('error-border');
            isValid = false;
        }
        if (!isValid) return;

        // Chuẩn bị dữ liệu gửi đi (FormData vì có file)
        let formData = new FormData();
        formData.append('name', $('#foodName').val());
        formData.append('description', $('#description').val());
        formData.append('recipe', $('#recipe').val());
        formData.append('calories', $('#totalCal').val());
        formData.append('protein', $('#totalPro').val());
        formData.append('fat', $('#totalFat').val());
        formData.append('carbohydrate', $('#totalCarb').val());
        formData.append('foodType', $('#foodType').val());
        formData.append('imageFile', fileInput.files[0]);

        // Thêm danh sách nguyên liệu dưới dạng array
        activeRows.each(function(index) {
            formData.append(`ingredientIds`, $(this).find('.ing-select').val());
            formData.append(`quantities`, $(this).find('.ing-qty').val());
            formData.append(`units`, $(this).find('.ing-unit').val());
        });

        try {
            const response = await fetch('/api/admin/add-food', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                alert("Thêm món ăn thành công!");
                window.location.href = "/admin/manage_food";
            } else {
                alert("Có lỗi xảy ra khi lưu món ăn.");
            }
        } catch (error) {
            console.error("Lỗi submit:", error);
            alert("Không thể kết nối đến máy chủ.");
        }
    });
});