const titles = [
    "Đăng ký tài khoản",
    "Chỉ số cơ thể",
    "Mục tiêu sức khỏe",
    "Dị ứng và bệnh lý"
];

const subtitles = [
    "Tạo tài khoản mới để bắt đầu hành trình sức khỏe",
    "Cho chúng tôi biết về bạn nhé",
    "Giúp chúng tôi tính toán chính xác hơn",
    "Giúp chúng tôi gợi ý món ăn an toàn hơn"
];

// --- 1. XỬ LÝ CHUYỂN BƯỚC ---
function showStep(step) {
    document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
    document.getElementById(`step-${step}`).classList.add('active');

    // Cập nhật tiêu đề
    const titleElem = document.getElementById('step-title');
    const subtitleElem = document.getElementById('step-subtitle');
    if(titleElem) titleElem.innerText = titles[step - 1];
    if(subtitleElem) subtitleElem.innerText = subtitles[step - 1];

    // Cập nhật thanh tiến trình (Step Bar)
    const indicators = document.querySelectorAll('.step-indicator');
    indicators.forEach((ind, index) => {
        ind.classList.toggle('active', index < step); // Làm sáng các bước đã qua
    });
}

function nextStep(currentStep) {
    // Có thể thêm logic kiểm tra validate dữ liệu từng bước ở đây
    showStep(currentStep + 1);
}

function prevStep(currentStep) {
    showStep(currentStep - 1);
}

// --- 2. XỬ LÝ CHỌN MỤC TIÊU (STEP 3) ---
document.addEventListener('DOMContentLoaded', function() {
    const goalButtons = document.querySelectorAll('.goal-group button');
    goalButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            goalButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
        });
    });
});

// --- 3. XỬ LÝ XÓA TAG (STEP 4) ---
function removeTag(element) {
    element.parentElement.remove();
}

// --- 4. GỬI DỮ LIỆU VỀ BACKEND (HOÀN TẤT) ---
function submitForm() {
    // Thu thập dữ liệu từ các ID đã đặt trong HTML
    const registrationData = {
        // Step 1
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        
        // Step 2
        gender: document.getElementById('gender').value === "true",
        age: parseInt(document.getElementById('age').value) || 0,
        height: parseFloat(document.getElementById('height').value) || 0,
        weight: parseFloat(document.getElementById('weight').value) || 0,
        
        // Step 3
        desiredHeight: parseFloat(document.getElementById('desiredHeight').value) || 0,
        desiredWeight: parseFloat(document.getElementById('desiredWeight').value) || 0,
        goalType: document.querySelector('.goal-group button.active').innerText,

        // Step 4: Lấy danh sách ID từ các tag đang hiển thị
        diseaseIds: Array.from(document.querySelectorAll('#diseaseContainer .tag'))
                         .map(tag => parseInt(tag.getAttribute('data-id'))),
        ingredientIds: Array.from(document.querySelectorAll('#allergyContainer .tag'))
                            .map(tag => parseInt(tag.getAttribute('data-id')))
    };

    console.log("Dữ liệu gửi đi:", registrationData);

    // Gửi API về AuthController
    fetch('/api/auth/register-complete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(registrationData)
    })
    .then(response => {
        if (response.ok) {
            alert("Chúc mừng Thành! Bạn đã hoàn tất đăng ký thông tin sức khỏe.");
            window.location.href = "/login"; // Chuyển hướng về trang đăng nhập
        } else {
            response.text().then(text => alert("Lỗi: " + text));
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert("Đã có lỗi xảy ra khi kết nối server.");
    });
}