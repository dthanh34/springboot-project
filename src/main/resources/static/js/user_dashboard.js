
let allSafeFoods = [];
let currentSlotId = ''; 
let selectedDiseaseIds = []; 
let originalDetails = [];
let selectedFoodIdsMap = {
    morn: [],
    lunch: [],
    snack: [],
    dinn: []
};

const mealConfigs = {
    "2": [{ id: "morn", label: "Bữa sáng", r: 0.4 }, { id: "dinn", label: "Bữa tối", r: 0.6 }],
    "3": [{ id: "morn", label: "Bữa sáng", r: 0.3 }, { id: "lunch", label: "Bữa trưa", r: 0.4 }, { id: "dinn", label: "Bữa tối", r: 0.3 }],
    "4": [{ id: "morn", label: "Bữa sáng", r: 0.25 }, { id: "lunch", label: "Bữa trưa", r: 0.35 }, { id: "snack", label: "Bữa phụ", r: 0.15 }, { id: "dinn", label: "Bữa tối", r: 0.25 }]
};

document.addEventListener("DOMContentLoaded", function() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) { window.location.href = "/login"; return; }
    
    if (user.name) {
        document.getElementById("user-name").innerText = "Xin chào, " + user.name;
    }

    // tinh chi so BMI, BMR, TDEE
    calculateAndDisplayHealthMetrics(user);

    // load benh cap tinh 
    loadAcuteDiseasesPicker();

    renderMealRows(3, user);

    checkAndLoadTodayMenu(user.id);

    // thay doi so luonh bua an
    document.getElementById("meal-count-select").addEventListener("change", function() {
        renderMealRows(parseInt(this.value), user);
    });

    document.getElementById("btn-update-safe-foods").addEventListener("click", function() {
        fetchSafeFoods(user.id, selectedDiseaseIds);
    });

    const btnSave = document.getElementById("btn-save-daily-plan");
    if(btnSave) {
        btnSave.addEventListener("click", function() {
            saveDailyMenu(user.id);
        });
    }
});
// tinh tian suc khoe 
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
// chon benh cap tinh 
function loadAcuteDiseasesPicker() {
    fetch('/api/diseases/acute')
        .then(res => res.json())
        .then(data => {
            const group = document.getElementById("disease-chip-picker"); 
            if (!group) return;
            group.innerHTML = ""; 

            const healthyChip = document.createElement("div");
            healthyChip.className = "disease-chip healthy selected";
            healthyChip.innerText = "Khỏe mạnh";
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
            };
        });
}
// giao dien bua an
function renderMealRows(count, user) {
    const container = document.getElementById("meal-planner-container");
    container.innerHTML = "";
    const tdeeValue = parseInt(document.getElementById("tdee-value").innerText) || 2000;
    const config = mealConfigs[count.toString()];

    config.forEach(meal => {
        let ratio = meal.r;
        if (user.goalType === "Giảm cân" && meal.id === "dinn") ratio -= 0.05;
        const target = Math.round(tdeeValue * ratio);

        const row = `
            <div class="meal-slot" style="margin-bottom: 25px; background: #fff; padding: 20px; border-radius: 15px; border: 1px solid #eee; display: flex; flex-direction: column;">
                <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #f9f9f9; padding-bottom: 12px; margin-bottom: 15px;">
                    <div>
                        <strong style="font-size: 1.1rem; color: #2d3436;">${meal.label}</strong>
                        <div style="margin-top: 5px;">
                            <small>Mục tiêu: <span id="target-${meal.id}" style="font-weight:600;">${target}</span> kcal</small>
                            <small style="margin-left: 15px;">Đã chọn: <span id="current-${meal.id}" style="font-weight:bold; color: #2ecc71;">0</span> kcal</small>
                        </div>
                    </div>
                    <div class="meal-actions" style="display: flex; gap: 10px;">
                        <button class="btn-suggest-home" onclick="getSmartSuggestion('${meal.id}', ${target}, true)">🏠 Cơm nhà</button>
                        <button class="btn-suggest-out" onclick="getSmartSuggestion('${meal.id}', ${target}, false)">🚗 Ăn ngoài</button>
                    </div>
                </div>
                <div id="selected-${meal.id}" class="selected-items-container"></div>
                <button class="btn-add-manual" onclick="openFoodPicker('${meal.id}')" style="width: 100%; margin-top: 10px; border: 1px dashed #ccc; padding: 8px; border-radius: 10px; cursor: pointer; color: #666;">+ Tự chọn thủ công</button>
            </div>`;
        container.innerHTML += row;
    });
}
// goi y mon an
function getSmartSuggestion(slotId, targetCal, isFamilyMeal) {
    const user = JSON.parse(localStorage.getItem("user"));
    const container = document.getElementById(`selected-${slotId}`);
    container.innerHTML = "<p style='grid-column: 1/-1; text-align:center; padding: 20px;'>Đang tính toán thực đơn an toàn...</p>";

    fetch('/api/meals/suggest', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            userId: user.id,
            familyMeal: isFamilyMeal,
            targetCal: parseFloat(targetCal),
            acuteDiseaseIds: selectedDiseaseIds.map(Number)
        })
    })
    .then(res => res.json())
    .then(dishes => {
        container.innerHTML = ""; 
        selectedFoodIdsMap[slotId] = []; 
        dishes.forEach(food => addFoodToUI(slotId, food));
    })
    .catch(err => {
        container.innerHTML = "<p style='grid-column: 1/-1; text-align:center; color:red; padding: 20px;'>Không tìm thấy tổ hợp món phù hợp với sức khỏe hiện tại.</p>";
    });
}
function checkAndLoadTodayMenu(userId) {
    const today = new Date().toISOString().split('T')[0];
    
    fetch(`/api/meals/get-daily-menu?userId=${userId}&date=${today}`)
    .then(res => res.json())
    .then(data => {
        if (data && data.length > 0) {
            console.log("Tìm thấy thực đơn đã lưu hôm nay:", data);
             originalDetails = [];
            
            const reverseMap = { 1: "morn", 2: "lunch", 3: "snack", 4: "dinn" };
            
            data.forEach(item => {
                originalDetails.push({
                    foodId: item.foodId,
                    mealTypeId: item.mealTypeId
                });
                const slotId = reverseMap[item.mealTypeId];
                if (slotId) {
                    addFoodToUI(slotId, {
                        foodId: item.foodId,
                        foodName: item.foodName,
                        calories: item.calories,
                        imageUrl: item.imageUrl
                    });
                }
            });
        }
    })
    .catch(err => console.error("Lỗi khi tải thực đơn hôm nay:", err));
}

function addFoodToUI(slotId, food) {
    const container = document.getElementById(`selected-${slotId}`);
    const foodCard = document.createElement("div");
    foodCard.className = "selected-food-card"; 
    foodCard.dataset.calo = food.calories;
    foodCard.dataset.id = food.foodId;

    foodCard.innerHTML = `
        <div class="card-content">
            <img src="/images/${food.imageUrl || 'default-food.jpg'}" onerror="this.src='/images/default-food.jpg'">
            <div class="food-info">
                <h4>${food.foodName}</h4>
                <p>${Math.round(food.calories)} kcal</p>
            </div>
            <button class="btn-remove" onclick="removeFood(this, '${slotId}')">×</button>
        </div>
    `;
    container.appendChild(foodCard);
    updateSlotCalo(slotId);
}
// xu ly & luu db
function updateSlotCalo(slotId) {
    let total = 0;
    const container = document.getElementById(`selected-${slotId}`);
    const cards = container.querySelectorAll(".selected-food-card");
    cards.forEach(c => total += parseFloat(c.dataset.calo));
    
    const target = parseFloat(document.getElementById(`target-${slotId}`).innerText);
    const currentEl = document.getElementById(`current-${slotId}`);
    currentEl.innerText = Math.round(total);
    currentEl.style.color = total > (target + 50) ? "#e74c3c" : "#2ecc71";

    selectedFoodIdsMap[slotId] = Array.from(cards).map(c => parseInt(c.dataset.id));
}

function removeFood(el, slotId) {
    el.closest(".selected-food-card").remove();
    updateSlotCalo(slotId);
}

function fetchSafeFoods(userId, acuteIds) {
    fetch('/api/meals/all-safe', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: userId, acuteDiseaseIds: acuteIds.map(Number) })
    })
    .then(res => res.json())
    .then(data => {
        allSafeFoods = data;
        alert("Thực đơn an toàn đã được cập nhật dựa trên tình trạng sức khỏe của bạn!");
    });
}

function openFoodPicker(slotId) {
    currentSlotId = slotId;
    document.getElementById("foodModal").style.display = "block";
    const list = document.getElementById("modal-food-list");
    list.innerHTML = "";

    const filteredFoods = allSafeFoods.filter(food => {
        const isMorn = (food.isBreakfast === 1 || food.isBreakfast === true);
        return slotId === 'morn' ? isMorn : !isMorn;
    });

    if (filteredFoods.length === 0) {
        list.innerHTML = "<p style='grid-column: 1/-1; text-align:center; padding: 20px;'>Nhấn 'Cập nhật thực đơn an toàn' để lấy danh sách món!</p>";
        return;
    }

    filteredFoods.forEach(food => {
        const card = document.createElement("div");
        card.className = "food-card-selection";
        card.innerHTML = `
            <img src="/images/${food.imageUrl || 'default-food.jpg'}" onerror="this.src='/images/default-food.jpg'">
            <p><strong>${food.foodName}</strong></p>
            <small>${food.calories} kcal</small>
        `;
        card.onclick = () => { addFoodToUI(currentSlotId, food); closeModal(); };
        list.appendChild(card);
    });
}

function saveDailyMenu(userId) {
    const today = new Date().toISOString().split('T')[0];
    const mealTypeMap = { morn: 1, lunch: 2, snack: 3, dinn: 4 };
    
    let details = [];

    const slots = ['morn', 'lunch', 'snack', 'dinn'];
    
    slots.forEach(slotId => {
        const container = document.getElementById(`selected-${slotId}`);
        if (container) {
            const cards = container.querySelectorAll(".selected-food-card");
            cards.forEach(card => {
                const foodId = parseInt(card.dataset.id);
                if (foodId) { 
                    details.push({
                        foodId: foodId,
                        mealTypeId: mealTypeMap[slotId]
                    });
                }
            });
        }
    });

    console.log("Danh sách gửi lên BE:", details);

    if (details.length === 0) { 
        alert("Thực đơn đang trống, vui lòng chọn món trước khi lưu!"); 
        return; 
    }
    let mergedDetails = [...originalDetails];

    details.forEach(newItem => {
        mergedDetails = mergedDetails.filter(
            item => item.mealTypeId !== newItem.mealTypeId
        );
        mergedDetails.push(newItem);
    });
    fetch('/api/meals/save-daily-menu', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            userId: userId,
            menuDate: today,
            details: mergedDetails
        })
    })
    .then(res => {
        if (res.ok) {
            alert("Đã cập nhật thực đơn cả ngày thành công!");
        } else {
            alert("Lưu thất bại! Vui lòng kiểm tra lại dữ liệu.");
        }
    })
    .catch(err => {
        console.error("Lỗi kết nối:", err);
        alert("Không thể kết nối đến máy chủ.");
    });
}
function closeModal() { document.getElementById("foodModal").style.display = "none"; }