document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById('addIngredientForm');

    form.addEventListener('submit', async function(e) {
        e.preventDefault(); // Chặn load lại trang

        // Gom dữ liệu từ form
        const ingredientData = {
            name: document.getElementById('ingName').value,
            category: document.getElementById('ingCategory').value,
            calories: parseFloat(document.getElementById('ingCal').value),
            protein: parseFloat(document.getElementById('ingPro').value),
            fat: parseFloat(document.getElementById('ingFat').value),
            carbohydrate: parseFloat(document.getElementById('ingCarb').value)
        };

        try {
            const response = await fetch('/api/admin/add-ingredient', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(ingredientData)
            });

            if (response.ok) {
                alert('Thêm nguyên liệu thành công!');
                window.location.href = '/admin/manage_ingredient';
            } else {
                const error = await response.json();
                alert('Lỗi: ' + (error.message || 'Không thể lưu nguyên liệu'));
            }
        } catch (err) {
            console.error('Lỗi kết nối API:', err);
            alert('Đã xảy ra lỗi kết nối đến máy chủ.');
        }
    });
});