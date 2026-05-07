document.addEventListener("DOMContentLoaded", function() {
    // 1. Khởi tạo dữ liệu
    fetchProgressData();

    // 2. Logic Modal
    const modal = document.getElementById("updateModalOverlay");
    document.getElementById("openUpdateModalBtn").addEventListener("click", () => modal.classList.add("active"));
    document.getElementById("closeUpdateModalBtn").addEventListener("click", () => modal.classList.remove("active"));
    modal.addEventListener("click", (e) => { if (e.target === modal) modal.classList.remove("active"); });

    // 3. Gọi API lấy dữ liệu tiến trình
    async function fetchProgressData() {
        try {
            const response = await fetch('/api/user/progress-summary');
            const data = await response.json();

            renderStats(data);
            renderProgressBars(data);
            renderHistory(data.recentMealHistory);
        } catch (error) {
            console.error("Lỗi tải tiến trình:", error);
        }
    }

    // 4. Render các thẻ con số
    function renderStats(data) {
        document.getElementById('today-calo').innerText = `${Math.round(data.todayCalories)} kcal`;
        document.getElementById('current-weight-display').innerText = `${data.currentWeight.toFixed(1)} kg`;
        document.getElementById('total-days').innerText = data.totalDaysFollowed;
        document.getElementById('goal-label').innerText = data.goalLabel;
        document.getElementById('bmi-display').innerText = data.bmi.toFixed(2);
        
        // Fill sẵn vào form modal
        document.getElementById('inputHeight').value = data.currentHeight;
        document.getElementById('inputWeight').value = data.currentWeight;
    }

    // 5. Tính toán và chạy thanh Progress
    function renderProgressBars(data) {
        // Cân nặng
        const wPercent = Math.min(Math.max(data.weightProgressPercent, 0), 100);
        document.getElementById('weight-percent').innerText = `${Math.round(wPercent)}%`;
        document.getElementById('weight-progress-fill').style.width = `${wPercent}%`;
        document.getElementById('meta-current-weight').innerText = `Hiện tại: ${data.currentWeight} kg`;
        document.getElementById('meta-target-weight').innerText = `Mục tiêu: ${data.targetWeight} kg`;

        // Chiều cao
        const hPercent = Math.min(Math.max(data.heightProgressPercent, 0), 100);
        document.getElementById('height-percent').innerText = `${Math.round(hPercent)}%`;
        document.getElementById('height-progress-fill').style.width = `${hPercent}%`;
        document.getElementById('meta-current-height').innerText = `Hiện tại: ${data.currentHeight} cm`;
        document.getElementById('meta-target-height').innerText = `Mục tiêu: ${data.targetHeight} cm`;
    }

    // 6. Render Lịch sử ăn uống
    function renderHistory(history) {
        const container = document.getElementById('history-list');
        if (!history || history.length === 0) {
            container.innerHTML = '<p class="empty-text">Chưa có lịch sử ăn uống.</p>';
            return;
        }

        container.innerHTML = history.map(day => `
            <article class="history-day">
                <h4>${new Date(day.date).toLocaleDateString('vi-VN')}</h4>
                ${day.foods.map(food => `
                    <div class="history-item">
                        <span>${food.foodName}</span>
                        <span>${Math.round(food.calories)} calo</span>
                    </div>
                `).join('')}
            </article>
        `).join('');
    }

    // 7. Xử lý gửi form cập nhật
    document.getElementById('updateStatsForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        const payload = {
            current_height: document.getElementById('inputHeight').value,
            current_weight: document.getElementById('inputWeight').value
        };

        const res = await fetch('/api/user/update-stats', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            modal.classList.remove("active");
            fetchProgressData(); // Tải lại dữ liệu mới mà không reload trang
        }
    });
});