let currentViewDate = new Date(); 

document.addEventListener("DOMContentLoaded", function() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) { window.location.href = "/login"; return; }

    renderWeekCalendar(currentViewDate);

    const todayStr = currentViewDate.toISOString().split('T')[0];
    fetchMenu(user.id, todayStr);
});

function renderWeekCalendar(referenceDate) {
    const calendarList = document.getElementById("calendar-list");
    calendarList.innerHTML = ""; 

    const day = referenceDate.getDay();
    const diff = referenceDate.getDate() - day + (day === 0 ? -6 : 1); 
    const monday = new Date(referenceDate.setDate(diff));

    createNavButton('left');

    for (let i = 0; i < 7; i++) {
        let d = new Date(monday);
        d.setDate(monday.getDate() + i);
        
        const dayName = d.toLocaleDateString('vi-VN', { weekday: 'short' });
        const dateNum = d.getDate();
        const fullDate = d.toISOString().split('T')[0];
        const todayReal = new Date().toISOString().split('T')[0];

        const dateItem = document.createElement("div");
        dateItem.className = "date-item";

        if (fullDate === currentViewDate.toISOString().split('T')[0]) {
            dateItem.classList.add("active");
        }
        if (fullDate === todayReal) {
            dateItem.style.border = "1px solid #2ecc71";
        }

        dateItem.innerHTML = `<span>${dayName}</span><strong>${dateNum}</strong>`;
        
        dateItem.onclick = () => {
            currentViewDate = new Date(fullDate);
            document.querySelectorAll('.date-item').forEach(el => el.classList.remove('active'));
            dateItem.classList.add('active');
            const user = JSON.parse(localStorage.getItem("user"));
            fetchMenu(user.id, fullDate);
        };
        calendarList.appendChild(dateItem);
    }

    createNavButton('right');
}

function createNavButton(direction) {
    const calendarList = document.getElementById("calendar-list");
    const btn = document.createElement("button");
    btn.className = "btn-nav-week";
    btn.innerHTML = direction === 'left' ? '<i class="fa-solid fa-chevron-left"></i>' : '<i class="fa-solid fa-chevron-right"></i>';
    
    btn.onclick = () => {
        const offset = direction === 'left' ? -7 : 7;
        currentViewDate.setDate(currentViewDate.getDate() + offset);
        renderWeekCalendar(currentViewDate);

        const user = JSON.parse(localStorage.getItem("user"));
        fetchMenu(user.id, currentViewDate.toISOString().split('T')[0]);
    };
    calendarList.prepend(direction === 'left' ? btn : ''); 
    if(direction === 'right') calendarList.appendChild(btn); 
}

function fetchMenu(userId, date) {
    const content = document.getElementById("meal-content");
    content.innerHTML = "<p style='text-align:center; padding:20px;'>Đang tải thực đơn...</p>";

    fetch(`/api/meals/get-daily-menu?userId=${userId}&date=${date}`)
    .then(res => res.json())
    .then(data => {
        if (!data || data.length === 0) {
            content.innerHTML = `
                <div style="text-align:center; padding:50px; color:#999;">
                    <i class="fa-regular fa-folder-open" style="font-size: 40px; margin-bottom:10px;"></i>
                    <p>Chưa có thực đơn cho ngày này</p>
                </div>`;
            updateCaloSummary(0);
            return;
        }
        renderMenu(data);
    })
    .catch(err => {
        content.innerHTML = "<p style='text-align:center; color:red;'>Lỗi tải dữ liệu. Hãy kiểm tra Backend!</p>";
        console.error(err);
    });
}

function renderMenu(data) {
    const content = document.getElementById("meal-content");
    content.innerHTML = "";
    
    const mealTypes = { 1: "Bữa sáng", 2: "Bữa trưa", 3: "Bữa phụ", 4: "Bữa tối" };
    let totalCalo = 0;

    const grouped = data.reduce((acc, item) => {
        if (!acc[item.mealTypeId]) acc[item.mealTypeId] = [];
        acc[item.mealTypeId].push(item);
        return acc;
    }, {});

    Object.keys(mealTypes).forEach(typeId => {
        if (grouped[typeId]) {
            let section = `<div class="meal-box"><h3>${mealTypes[typeId]}</h3>`;
            grouped[typeId].forEach(food => {
                totalCalo += food.calories;
                section += `
                    <div class="food-item">
                        <img src="/images/${food.imageUrl || 'default-food.jpg'}" onerror="this.src='/images/default-food.jpg'">
                        <div class="food-info">
                            <div class="name">${food.foodName}</div>
                            <div class="cal">${Math.round(food.calories)} kcal</div>
                        </div>
                    </div>`;
            });
            section += `</div>`;
            content.innerHTML += section;
        }
    });

    updateCaloSummary(totalCalo);
}

function updateCaloSummary(total) {
    document.getElementById("total-day-calories").innerText = Math.round(total) + " kcal";
    const user = JSON.parse(localStorage.getItem("user"));
    const tdee = user.tdee || 2000;
    const percent = Math.min((total / tdee) * 100, 100);
    document.getElementById("calo-progress").style.width = percent + "%";
}