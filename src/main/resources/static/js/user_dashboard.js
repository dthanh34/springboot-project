document.addEventListener("DOMContentLoaded", function() {
    // 1. Lấy thông tin User đã lưu lúc Đăng nhập
    const user = JSON.parse(localStorage.getItem("user"));
    
    // Kiểm tra nếu chưa đăng nhập thì đuổi về trang login
    if (!user || !user.id) {
        console.error("Không tìm thấy thông tin User!");
        window.location.href = "/login.html"; 
        return;
    }

    console.log("Đang tải dữ liệu cho User:", user.name);

    // 2. Hiển thị thông tin cơ bản lên giao diện
    if(document.getElementById("user-name")) {
        document.getElementById("user-name").innerText = "Xin chào, " + (user.name || "Người dùng");
    }
    if(document.getElementById("goal-value")) {
        document.getElementById("goal-value").innerText = user.goalType || "Duy trì sức khỏe";
    }

    // 3. Tính toán các chỉ số sức khỏe để hiển thị (Dashboard)
    calculateAndDisplayHealthMetrics(user);

    // 4. Gọi API lấy thực đơn gợi ý từ Backend
    loadMealSuggestions(user.id);
});

/**
 * Hàm tính toán BMI, BMR và TDEE dựa trên dữ liệu User
 */
function calculateAndDisplayHealthMetrics(user) {
    const weight = parseFloat(user.weight);
    const heightCm = parseFloat(user.height);
    const age = parseInt(user.age);
    const isMale = user.gender; // Giả sử true là Nam, false là Nữ
    const activityLevel = parseFloat(user.activityLevel) || 1.2;

    if (!weight || !heightCm || !age) return;

    // Tính BMI
    const heightM = heightCm / 100;
    const bmi = (weight / (heightM * heightM)).toFixed(1);
    const bmiEl = document.getElementById("bmi-value");
    if (bmiEl) bmiEl.innerText = bmi;

    // Trạng thái BMI
    const statusEl = document.getElementById("bmi-status");
    if (statusEl) {
        if (bmi < 18.5) { statusEl.innerText = "Hơi gầy"; statusEl.className = "badge yellow"; }
        else if (bmi < 24.9) { statusEl.innerText = "Bình thường"; statusEl.className = "badge green"; }
        else { statusEl.innerText = "Thừa cân"; statusEl.className = "badge red"; }
    }

    // Tính BMR (Mifflin-St Jeor)
    let bmr = (10 * weight) + (6.25 * heightCm) - (5 * age);
    bmr = isMale ? (bmr + 5) : (bmr - 161);
    if(document.getElementById("bmr-value")) {
        document.getElementById("bmr-value").innerText = Math.round(bmr);
    }

    // Tính TDEE (Dùng hệ số vận động THẬT từ DB)
    const tdee = Math.round(bmr * activityLevel);
    if(document.getElementById("tdee-value")) {
        document.getElementById("tdee-value").innerText = tdee + " kcal";
    }
}

// Hàm hiển thị từng món ăn
function displayMeal(type, food) {
    if (!food) return;

    const nameId = `${type}-name`;
    const caloId = `${type}-calo`;
    const imgId = `${type}-img`;

    // 1. Điền tên món ăn (Sửa thành foodName cho khớp Entity của Thành)
    const nameEl = document.getElementById(nameId);
    if (nameEl) nameEl.innerText = food.foodName || "Món ăn chưa đặt tên";

    // 2. Điền Calo
    const caloEl = document.getElementById(caloId);
    if (caloEl) caloEl.innerText = Math.round(food.calories) + " kcal";

    // 3. Đổ ảnh vào (Sửa thành imageUrl cho khớp Entity của Thành)
    const imgEl = document.getElementById(imgId);
    if (imgEl) {
        if (food.imageUrl) {
            // Đường dẫn này khớp với thư mục static/images của Thành
            imgEl.src = "/images/" + food.imageUrl;
        } else {
            // Ảnh mặc định nếu trong DB cột image_url bị trống
            imgEl.src = "/images/default-food.jpg";
        }
    }
}

// Hàm load dữ liệu (Gọi API)
function loadMealSuggestions(userId) {
    fetch(`http://localhost:8080/api/meals/suggest/${userId}`)
    .then(response => response.json())
    .then(meals => {
        if (meals && meals.length >= 3) {
            // 1. Điền calo mục tiêu vào phần "Bữa ăn hôm nay"
            document.getElementById("breakfast-target-calo").innerText = Math.round(meals[0].calories) + " kcal";
            document.getElementById("lunch-target-calo").innerText = Math.round(meals[1].calories) + " kcal";
            document.getElementById("dinner-target-calo").innerText = Math.round(meals[2].calories) + " kcal";

            // 2. Tạo Card món ăn cho phần "Gợi ý cho bạn"
            const container = document.getElementById("suggestion-cards");
            container.innerHTML = ""; // Xóa trắng trước khi nạp

            meals.forEach(food => {
                const card = `
                    <div class="food-card">
                        <img src="/images/${food.imageUrl}" alt="${food.foodName}">
                        <div class="food-card-body">
                            <h4>${food.foodName}</h4>
                            <p>${Math.round(food.calories)} kcal</p>
                            <small>⭐ Phù hợp mục tiêu của bạn</small>
                        </div>
                    </div>
                `;
                container.innerHTML += card;
            });
        }
    });
}