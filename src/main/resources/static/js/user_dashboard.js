let allSafeFoods = [];
let currentSlotId = ''; 
let selectedDiseaseIds = []; 

document.addEventListener("DOMContentLoaded", function() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) { window.location.href = "/login"; return; }
    if (user.name) {
        document.getElementById("user-name").innerText = "Xin chào, " + user.name;
    }

    calculateAndDisplayHealthMetrics(user);

    loadAcuteDiseasesPicker();

    renderMealRows(3, user);

    document.getElementById("meal-count-select").addEventListener("change", function() {
        renderMealRows(parseInt(this.value), user);
    });

    document.getElementById("btn-update-safe-foods").addEventListener("click", function() {
        fetchSafeFoods(user.id, selectedDiseaseIds);
    });
});

function loadAcuteDiseasesPicker() {
    fetch('/api/diseases/acute')
        .then(res => res.json())
        .then(data => {
            const group = document.getElementById("disease-chip-picker"); 
            if (!group) return;
            group.innerHTML = ""; 

            const healthyChip = document.createElement("div");
            healthyChip.className = "disease-chip healthy selected";
            healthyChip.innerText = "Khỏe mạnh (Không bị gì)";
            healthyChip.dataset.id = "healthy";
            group.appendChild(healthyChip);

            data.forEach(d => {
                const chip = document.createElement("div");
                chip.className = "disease-chip";
                chip.innerText = d.diseaseName || d.disease_name;
                chip.dataset.id = d.diseaseId || d.disease_id;
                group.appendChild(chip);
            });

            group.onclick = function(e) {
                const chip = e.target.closest(".disease-chip");
                if (!chip) return;

                if (chip.dataset.id === "healthy") {
                    group.querySelectorAll(".disease-chip").forEach(c => c.classList.remove("selected"));
                    chip.classList.add("selected");
                    selectedDiseaseIds = [];
                } else {
                    group.querySelector(".disease-chip.healthy").classList.remove("selected");
                    chip.classList.toggle("selected");
                    selectedDiseaseIds = Array.from(group.querySelectorAll(".disease-chip.selected:not(.healthy)"))
                                              .map(c => c.dataset.id);
                    if (selectedDiseaseIds.length === 0) {
                        group.querySelector(".disease-chip.healthy").classList.add("selected");
                    }
                }
                console.log("Danh sách bệnh chọn:", selectedDiseaseIds);
            };
        });
}

function calculateAndDisplayHealthMetrics(user) {
    const weight = parseFloat(user.weight), heightCm = parseFloat(user.height), age = parseInt(user.age);
    const isMale = user.gender, activityLevel = parseFloat(user.activityLevel) || 1.2;

    if (!weight || !heightCm || !age) return;

    const bmi = (weight / ((heightCm/100) * (heightCm/100))).toFixed(1);
    document.getElementById("bmi-value").innerText = bmi;
    const statusEl = document.getElementById("bmi-status");
    if (bmi < 18.5) { statusEl.innerText = "Hơi gầy"; statusEl.className = "badge yellow"; }
    else if (bmi < 24.9) { statusEl.innerText = "Bình thường"; statusEl.className = "badge green"; }
    else { statusEl.innerText = "Thừa cân"; statusEl.className = "badge red"; }

    let bmr = (10 * weight) + (6.25 * heightCm) - (5 * age);
    bmr = isMale ? (bmr + 5) : (bmr - 161);
    document.getElementById("bmr-value").innerText = Math.round(bmr);

    const tdee = Math.round(bmr * activityLevel);
    document.getElementById("tdee-value").innerText = tdee + " kcal";
    document.getElementById("goal-value").innerText = user.goalType || "Duy trì";
}

const mealConfigs = {
    "2": [{ id: "morn", label: "Bữa sáng", r: 0.4 }, { id: "dinn", label: "Bữa tối", r: 0.6 }],
    "3": [{ id: "morn", label: "Bữa sáng", r: 0.3 }, { id: "lunch", label: "Bữa trưa", r: 0.4 }, { id: "dinn", label: "Bữa tối", r: 0.3 }],
    "4": [{ id: "morn", label: "Bữa sáng", r: 0.25 }, { id: "lunch", label: "Bữa trưa", r: 0.35 }, { id: "snack", label: "Bữa phụ", r: 0.15 }, { id: "dinn", label: "Bữa tối", r: 0.25 }]
};

function renderMealRows(count, user) {
    const container = document.getElementById("meal-planner-container");
    container.innerHTML = "";
    const tdeeValue = parseInt(document.getElementById("tdee-value").innerText) || 2000;
    const config = mealConfigs[count.toString()];

    config.forEach(meal => {
        let ratio = meal.r;
        if (user.goalType === "Giảm cân" && meal.id === "dinn") ratio -= 0.1;
        if (user.goalType === "Giảm cân" && meal.id === "lunch") ratio += 0.1;

        const target = Math.round(tdeeValue * ratio);
        const row = `
            <div class="meal-slot">
                <div style="min-width:150px;">
                    <strong>${meal.label}</strong><br>
                    <small>Mục tiêu: <span id="target-${meal.id}">${target}</span> kcal</small><br>
                    <small>Đã chọn: <span id="current-${meal.id}" style="font-weight:bold;">0</span> kcal</small>
                </div>
                <div id="selected-${meal.id}" class="selected-items-container"></div>
                <button class="btn-add" onclick="openFoodPicker('${meal.id}')">+ Thêm món</button>
            </div>`;
        container.innerHTML += row;
    });
}

function fetchSafeFoods(userId, acuteIds) {
    fetch('/api/meals/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: userId, acuteDiseaseIds: acuteIds })
    })
    .then(res => res.json())
    .then(data => {
        allSafeFoods = data;
        alert("Đã cập nhật thực đơn an toàn!");
    });
}

function openFoodPicker(slotId) {
    currentSlotId = slotId;
    document.getElementById("foodModal").style.display = "block";
    const list = document.getElementById("modal-food-list");
    list.innerHTML = "";

    const filteredFoods = allSafeFoods.filter(food => {
        const isMorn = food.isBreakfast === 1 || food.isBreakfast === true;
        return slotId === 'morn' ? isMorn : !isMorn;
    });

    if (filteredFoods.length === 0) {
        list.innerHTML = "<p style='grid-column: 1/-1; text-align:center;'>Vui lòng nhấn Cập nhật để lấy món!</p>";
        return;
    }

    filteredFoods.forEach(food => {
        const card = document.createElement("div");
        card.className = "food-card-selection";
        const imgPath = food.imageUrl  || 'default-food.jpg';
        
        card.innerHTML = `
            <img src="/images/${imgPath}" onerror="this.src='/images/default-food.jpg'" style="width:100%; height:100px; object-fit:cover; border-radius:8px;">
            <p><strong>${food.foodName}</strong></p>
            <small>${food.calories} kcal</small>
        `;
        
        card.onclick = () => {
            selectFood(food);
            closeModal();
        };
        list.appendChild(card);
    });
}
function selectFood(food) {
    const container = document.getElementById(`selected-${currentSlotId}`);
    const foodCard = document.createElement("div");
    foodCard.className = "selected-food-card"; 
    foodCard.dataset.calo = food.calories;

    foodCard.innerHTML = `
        <div class="card-content">
            <img src="/images/${food.imageUrl || 'default-food.jpg'}" 
                 onerror="this.src='/images/default-food.jpg'">
            <div class="food-info">
                <h4>${food.foodName}</h4>
                <p>${Math.round(food.calories)} kcal</p>
            </div>
            <div class="card-actions">
                <a href="/food/detail/${food.foodId}" class="btn-detail">Xem chi tiết</a>
                <button class="btn-remove" onclick="removeFood(this, '${currentSlotId}')">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        </div>
    `;
    
    container.appendChild(foodCard);
    updateSlotCalo(currentSlotId);
}
function removeFood(el, slotId) {
    el.parentElement.remove();
    updateSlotCalo(slotId);
}

function updateSlotCalo(slotId) {
    let total = 0;
    document.querySelectorAll(`#selected-${slotId} .food-chip`).forEach(c => total += parseFloat(c.dataset.calo));
    const target = parseFloat(document.getElementById(`target-${slotId}`).innerText);
    const currentEl = document.getElementById(`current-${slotId}`);
    currentEl.innerText = Math.round(total);
    currentEl.style.color = total > target ? "red" : "#2ecc71";
}

function closeModal() { document.getElementById("foodModal").style.display = "none"; }