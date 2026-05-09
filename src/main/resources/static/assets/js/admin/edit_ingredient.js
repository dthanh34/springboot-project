document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    const ingId = urlParams.get('id');
    const form = document.getElementById('editIngredientForm');

    if (!ingId) {
        alert("Không tìm thấy ID nguyên liệu!");
        window.location.href = "/admin/manage_ingredient";
        return;
    }

    // 1. Tải dữ liệu cũ của nguyên liệu
    async function loadIngredientData() {
        try {
            const response = await fetch(`/api/admin/ingredients/${ingId}`);
            if (!response.ok) throw new Error("Không thể tải dữ liệu");
            
            const data = await response.json();
            
            // Đổ dữ liệu vào form
             document.getElementById('ingId').value = data.ingredientId;
            document.getElementById('ingName').value = data.ingredientName;
             setCategoryValue(data.category);
            document.getElementById('ingCal').value = data.calories;
            document.getElementById('ingPro').value = data.protein;
            document.getElementById('ingFat').value = data.fat;
            document.getElementById('ingCarb').value = data.carbohydrate;
        } catch (error) {
            console.error(error);
            alert("Lỗi khi tải thông tin nguyên liệu.");
        }
    }

    loadIngredientData();

    // 2. Xử lý cập nhật dữ liệu
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const updatedData = {
            ingredientId: parseInt(document.getElementById('ingId').value),
            ingredientName: document.getElementById('ingName').value,
            category: document.getElementById('ingCategory').value,
            calories: parseFloat(document.getElementById('ingCal').value),
            protein: parseFloat(document.getElementById('ingPro').value),
            fat: parseFloat(document.getElementById('ingFat').value),
            carbohydrate: parseFloat(document.getElementById('ingCarb').value)
        };

        try {
            const response = await fetch(`/api/admin/ingredients/${ingId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedData)
            });

            if (response.ok) {
                alert('Cập nhật nguyên liệu thành công!');
                window.location.href = '/admin/manage_ingredient';
            } else {
                const error = await response.json();
                alert('Lỗi: ' + (error.message || 'Cập nhật thất bại'));
            }
        } catch (err) {
            console.error('Lỗi kết nối:', err);
            alert('Lỗi kết nối đến máy chủ.');
        }
    });
});
    function setCategoryValue(rawCategory) {
        const select = document.getElementById('ingCategory');
        const normalized = (rawCategory || '').trim().toLowerCase();

        const matchedOption = Array.from(select.options).find(
            option => option.value.trim().toLowerCase() === normalized
        );

        if (matchedOption) {
            select.value = matchedOption.value;
        }
    }