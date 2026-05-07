document.addEventListener("DOMContentLoaded", function() {
    Chart.register(ChartDataLabels);
    loadDashboardData();
});

async function loadDashboardData() {
    try {
        const response = await fetch('/api/admin/dashboard-summary');
        const data = await response.json();

        // 1. Cập nhật thông tin chào hỏi
        document.getElementById('admin-name').innerText = data.name || 'Admin';

        // 2. Đổ dữ liệu vào các thẻ Stats
        updateStatCard('total-users', 'user-growth-val', 'user-growth-container', data.totalUsers, data.userGrowth);
        updateStatCard('total-foods', 'food-growth-val', 'food-growth-container', data.totalFoods, data.foodGrowth);
        updateStatCard('total-menus', 'menu-growth-val', 'menu-growth-container', data.totalMenus, data.menuGrowth);
        document.getElementById('today-activities').innerText = data.todayActivities.toLocaleString();

        // 3. Vẽ biểu đồ đăng nhập (Line Chart)
        renderLoginChart(data.chartLabels, data.chartData);

        // 4. Vẽ biểu đồ đăng ký (Bar Chart)
        renderRegisterChart(data.userChartData);

        // 5. Render Top món ăn
        renderTopFoods(data.topFoods);

        // 6. Render Mục tiêu phổ biến
        renderPopularGoals(data.popularGoals);

    } catch (error) {
        console.error("Lỗi tải dashboard:", error);
    }
}

function updateStatCard(idVal, idGrowth, idContainer, value, growth) {
    document.getElementById(idVal).innerText = value.toLocaleString();
    const growthElem = document.getElementById(idGrowth);
    growthElem.innerText = (growth >= 0 ? "+" : "") + growth.toFixed(1) + "%";
    document.getElementById(idContainer).style.color = growth >= 0 ? "#10b981" : "#dc2626";
}

function renderLoginChart(labels, dataValues) {
    const ctx = document.getElementById('loginChart').getContext('2d');
    let gradient = ctx.createLinearGradient(0, 0, 0, 300);
    gradient.addColorStop(0, 'rgba(16, 185, 129, 0.4)');
    gradient.addColorStop(1, 'rgba(16, 185, 129, 0.0)');

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels || [],
            datasets: [{
                label: 'Lượt đăng nhập',
                data: dataValues || [],
                borderColor: '#10b981',
                backgroundColor: gradient,
                borderWidth: 3,
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { display: false, beginAtZero: true } }
        }
    });
}

function renderRegisterChart(rawRegisterData) {
    const currentMonth = new Date().getMonth() + 1;
    const labels = Array.from({length: currentMonth}, (_, i) => `T${i + 1}`);
    const dataValues = (rawRegisterData || []).slice(0, currentMonth);

    const ctx = document.getElementById('registerChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Người dùng mới',
                data: dataValues,
                backgroundColor: '#0ea5e9',
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } }
        }
    });
}

function renderTopFoods(foods) {
    const container = document.getElementById('top-foods-list');
    if (!foods || Object.keys(foods).length === 0) {
        container.innerHTML = '<div class="food-item" style="justify-content: center; color: #9ca3af;">Chưa có dữ liệu</div>';
        return;
    }
    container.innerHTML = Object.entries(foods).map(([name, count]) => `
        <div class="food-item">
            <span>${name}</span>
            <span class="badge">${count} Lượt</span>
        </div>
    `).join('');
}

function renderPopularGoals(goals) {
    const container = document.getElementById('popular-goals-list');
    if (!goals || Object.keys(goals).length === 0) {
        container.innerHTML = '<div style="text-align: center; color: #9ca3af;">Chưa có dữ liệu</div>';
        return;
    }
    container.innerHTML = Object.entries(goals).map(([name, percent]) => `
        <div class="progress-item">
            <div class="progress-label">
                <span>${name}</span>
                <strong>${percent.toFixed(0)}%</strong>
            </div>
            <div class="progress-bg">
                <div class="progress-fill" style="width: ${percent}%;"></div>
            </div>
        </div>
    `).join('');
}