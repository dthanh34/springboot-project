document.addEventListener("DOMContentLoaded", function() {
    // 1. Lấy dữ liệu user từ localStorage
    const user = JSON.parse(localStorage.getItem("user"));
    
    if (!user) {
        window.location.href = "/login";
        return;
    }

    // 2. Hiển thị thông tin cơ bản
    document.getElementById("user-name").innerText = "Xin chào, " + (user.name || "Người dùng");
    document.getElementById("goal-value").innerText = user.goalType || "Giảm cân";

    // 3. Logic tính toán BMI, BMR, TDEE
    const weight = parseFloat(user.weight);
    const heightCm = parseFloat(user.height);
    const heightM = heightCm / 100;
    const age = parseInt(user.age);
    const isMale = user.gender; // true/false

    // BMI
    const bmi = (weight / (heightM * heightM)).toFixed(1);
    document.getElementById("bmi-value").innerText = bmi;
    
    // Status BMI
    const statusEl = document.getElementById("bmi-status");
    if (bmi < 18.5) { statusEl.innerText = "Hơi gầy"; statusEl.className = "badge yellow"; }
    else if (bmi < 24.9) { statusEl.innerText = "Bình thường"; statusEl.className = "badge green"; }
    else { statusEl.innerText = "Thừa cân"; statusEl.className = "badge red"; }

    // BMR (Công thức Mifflin-St Jeor)
    let bmr = (10 * weight) + (6.25 * heightCm) - (5 * age);
    bmr = isMale ? (bmr + 5) : (bmr - 161);
    document.getElementById("bmr-value").innerText = Math.round(bmr);

    // TDEE (Calo tiêu thụ 1 ngày - giả sử vận động nhẹ x1.2)
    const tdee = Math.round(bmr * 1.2);
    document.getElementById("tdee-value").innerText = tdee;
});
function saveMetricsToDatabase(bmi, bmr, tdee) {
    const user = JSON.parse(localStorage.getItem("user"));
    
    const healthData = {
        userId: user.id, 
        bmi: bmi,
        bmr: bmr,
        tdee: tdee
    };

    fetch("/api/health/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(healthData)
    })
    .then(response => response.text())
    .then(msg => console.log("Hệ thống báo:", msg))
    .catch(error => console.error("Lỗi lưu DB:", error));
}