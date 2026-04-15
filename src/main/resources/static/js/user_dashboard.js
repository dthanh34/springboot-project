document.addEventListener("DOMContentLoaded", function() {
    // 1. Lấy thông tin User từ LocalStorage
    const user = JSON.parse(localStorage.getItem("user"));
    
    if (!user || !user.id) {
        console.error("Không tìm thấy thông tin User!");
        window.location.href = "/login.html"; 
        return;
    }

    // 2. Hiển thị thông tin cơ bản lên giao diện
    if(document.getElementById("user-name")) {
        document.getElementById("user-name").innerText = "Xin chào, " + (user.name || "Người dùng");
    }
    if(document.getElementById("goal-value")) {
        document.getElementById("goal-value").innerText = user.goalType || "Duy trì sức khỏe";
    }

    // 3. Khởi tạo dữ liệu ban đầu
    calculateAndDisplayHealthMetrics(user);
    loadAcuteDiseases(); 

    // 4. Lắng nghe sự kiện Nút Gạt (Cơm mâm / Ăn quán)
    const toggle = document.getElementById("meal-mode-toggle");
    const modeText = document.getElementById("mode-text");
    if (toggle && modeText) {
        toggle.addEventListener("change", function() {
            modeText.innerText = this.checked ? "Cơm mâm (Gia đình)" : "Ăn nhanh (Tại quán)";
        });
    }

    // 5. Sự kiện khi nhấn "Xác nhận thực đơn"
    const btnGenerate = document.getElementById("btn-generate-meal");
    if (btnGenerate) {
        btnGenerate.addEventListener("click", function() {
            const isFamilyMeal = toggle ? toggle.checked : false;
            
            // Lấy TDEE thực tế từ giao diện để gửi lên Backend
            const tdeeText = document.getElementById("tdee-value").innerText;
            const tdeeValue = parseFloat(tdeeText) || 2000;

            const selectDisease = document.getElementById("acute-diseases");
            let selectedDiseases = [];
            if (selectDisease && selectDisease.selectedOptions) {
                selectedDiseases = Array.from(selectDisease.selectedOptions)
                                        .map(option => option.value)
                                        .filter(val => val !== "");
            }

            console.log("Đang gửi yêu cầu sinh thực đơn...", { isFamilyMeal, tdeeValue });
            generateSmartMeal(user.id, isFamilyMeal, selectedDiseases, tdeeValue);
        });
    }

    // 6. Sự kiện Xem tất cả món ăn
    const btnViewAll = document.getElementById("btn-view-all");
    if (btnViewAll) {
        btnViewAll.addEventListener("click", function() {
            window.location.href = "/all-foods"; 
        });
    }
});

/**
 * Tải danh sách bệnh ngắn ngày và đổ vào Select
 */
function loadAcuteDiseases() {
    fetch('/api/diseases/acute')
    .then(res => res.json())
    .then(data => {
        const select = document.getElementById("acute-diseases");
        if (!select) return;
        
        select.innerHTML = '<option value="">-- Hôm nay bạn cảm thấy thế nào? --</option>';
        data.forEach(disease => {
            let opt = document.createElement("option");
            // Khớp với tên biến Entity Disease (disease_id, disease_name)
            opt.value = disease.disease_id || disease.diseaseId || disease.id; 
            opt.text = disease.disease_name || disease.diseaseName || disease.name || "Bệnh không tên";
            select.add(opt);
        });
    })
    .catch(err => console.error("Lỗi tải danh sách bệnh:", err));
}

/**
 * Tính toán BMI, BMR và TDEE
 */
function calculateAndDisplayHealthMetrics(user) {
    const weight = parseFloat(user.weight);
    const heightCm = parseFloat(user.height);
    const age = parseInt(user.age);
    const isMale = user.gender; // true = Nam, false = Nữ
    const activityLevel = parseFloat(user.activityLevel) || 1.2;

    if (!weight || !heightCm || !age) return;

    // Tính BMI
    const heightM = heightCm / 100;
    const bmi = (weight / (heightM * heightM)).toFixed(1);
    if (document.getElementById("bmi-value")) document.getElementById("bmi-value").innerText = bmi;

    const statusEl = document.getElementById("bmi-status");
    if (statusEl) {
        if (bmi < 18.5) { statusEl.innerText = "Hơi gầy"; statusEl.className = "badge yellow"; }
        else if (bmi < 24.9) { statusEl.innerText = "Bình thường"; statusEl.className = "badge green"; }
        else { statusEl.innerText = "Thừa cân"; statusEl.className = "badge red"; }
    }

    // Tính BMR (Công thức Mifflin-St Jeor)
    let bmr = (10 * weight) + (6.25 * heightCm) - (5 * age);
    bmr = isMale ? (bmr + 5) : (bmr - 161);
    if(document.getElementById("bmr-value")) {
        document.getElementById("bmr-value").innerText = Math.round(bmr);
    }

    // Tính TDEE
    const tdee = Math.round(bmr * activityLevel);
    if(document.getElementById("tdee-value")) {
        document.getElementById("tdee-value").innerText = tdee + " kcal";
    }
}

/**
 * Gửi yêu cầu sinh thực đơn lên Backend
 */
function generateSmartMeal(userId, isFamilyMeal, acuteDiseaseIds, tdee) {
    const payload = {
        userId: userId,
        isFamilyMeal: isFamilyMeal,
        acuteDiseaseIds: acuteDiseaseIds,
        tdee: tdee
    };

    fetch('/api/meals/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(res => res.json())
    .then(data => {
        // Lưu toàn bộ dữ liệu gợi ý để trang "Xem tất cả" sử dụng
        sessionStorage.setItem("allSuggestedMeals", JSON.stringify(data));
        render3Meals(data); 
    })
    .catch(err => {
        console.error("Lỗi tạo thực đơn:", err);
        alert("Không thể kết nối với máy chủ!");
    });
}

/**
 * Hiển thị 3 bữa ăn lên giao diện Dashboard
 */
function render3Meals(meals) {
    const container = document.getElementById("suggestion-cards");
    if (!container || !meals || meals.length === 0) return;
    container.innerHTML = "";

    // Kiểm tra xem đây là mâm cơm (có dish_type 1,2,3,4) hay là món đơn
    const isTray = meals.some(f => f.dish_type !== 0 && f.dish_type !== undefined);

    let displayMeals = [];

    if (isTray) {
        // --- CHẾ ĐỘ CƠM MÂM ---
        // Gom tất cả món lại thành 1 chuỗi tên để hiển thị cho đẹp
        const trayName = meals.map(f => f.foodName || f.name).join(" + ");
        const totalCalo = meals.reduce((sum, f) => sum + (f.calories || f.Calories || 0), 0);
        const mainImg = meals.find(f => f.dish_type === 1)?.imageUrl || meals[0].imageUrl;

        // Tạo 3 bữa nhưng cùng ăn chung mâm cơm này (hoặc Thành có thể bốc món khác cho Sáng/Tối)
        displayMeals = [
            { label: "Bữa sáng", name: "Bún/Phở/Xôi nhẹ nhàng", calories: 500, imageUrl: 'breakfast.jpg' }, // Có thể fix cứng hoặc bốc từ list khác
            { label: "Bữa trưa", name: trayName, calories: totalCalo, imageUrl: mainImg },
            { label: "Bữa tối", name: "Cơm gia đình (Tương tự trưa)", calories: totalCalo, imageUrl: mainImg }
        ];
    } else {
        // --- CHẾ ĐỘ ĂN QUÁN ---
        displayMeals = [
            { ...meals[0], label: "Bữa sáng" },
            { ...meals[Math.floor(meals.length / 2)], label: "Bữa trưa" },
            { ...meals[meals.length - 1], label: "Bữa tối" }
        ];
    }

    displayMeals.forEach(food => {
        const calo = food.calories || food.Calories || 0;
        const name = food.foodName || food.name || "Món ăn";
        const img = food.imageUrl || food.image_url || 'default-food.jpg';

        const card = `
            <div class="food-card">
                <div class="meal-tag" style="position: absolute; top: 10px; left: 10px; background: #2ecc71; color: white; padding: 2px 10px; border-radius: 10px; font-size: 11px; z-index: 10;">
                    ${food.label}
                </div>
                <img src="/images/${img}" alt="${name}" onerror="this.src='/images/default-food.jpg'">
                <div class="food-card-body">
                    <h4 style="font-size: 14px; color: #333;">${name}</h4>
                    <p><strong>${Math.round(calo)} kcal</strong></p>
                </div>
            </div>`;
        container.innerHTML += card;
    });

    // Cập nhật các vòng tròn calo
    if(document.getElementById("breakfast-target-calo")) document.getElementById("breakfast-target-calo").innerText = Math.round(displayMeals[0].calories) + " kcal";
    if(document.getElementById("lunch-target-calo")) document.getElementById("lunch-target-calo").innerText = Math.round(displayMeals[1].calories) + " kcal";
    if(document.getElementById("dinner-target-calo")) document.getElementById("dinner-target-calo").innerText = Math.round(displayMeals[2].calories) + " kcal";
}