function login() {
   
    const nameInput = document.getElementById("name");
    const passwordInput = document.getElementById("password");

    if (!nameInput.value.trim() || !passwordInput.value.trim()) {
        alert("Vui lòng nhập đầy đủ tên tài khoản và mật khẩu!");
        return;
    }

    const loginData = {
        name: nameInput.value.trim(),
        password: passwordInput.value.trim()
    };

    console.log("Dữ liệu gửi đi:", loginData);

    // 2. Gọi API đến AuthController
    fetch("/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(loginData)
    })
    .then(response => {
        if (response.ok) {
            return response.json(); // Đăng nhập thành công, nhận về UserSessionDTO
        } else {
            // Nếu sai tài khoản hoặc mật khẩu (401 Unauthorized)
            throw new Error("Tên tài khoản hoặc mật khẩu không chính xác!");
        }
    })
    .then(data => {
        // 3. Lưu thông tin người dùng vào localStorage để dùng cho các trang sau
        localStorage.setItem("user", JSON.stringify(data));

        // 4. Kiểm tra Role để điều hướng (Redirect)
        // Lưu ý: data.role lấy từ UserSessionDTO trong Java
        const role = data.role ? data.role.toUpperCase() : "USER";

        if (role === "ADMIN") {
            window.location.href = "/admin";
        } else {
            window.location.href = "/user";
        }
    })
    .catch(error => {
        console.error("Lỗi đăng nhập:", error);
        alert(error.message);
    });
}

document.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        login();
    }
});