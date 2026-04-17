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

    fetch("/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(loginData)
    })
    .then(response => {
        if (response.ok) {
            return response.json(); 
        } else {
            throw new Error("Tên tài khoản hoặc mật khẩu không chính xác!");
        }
    })
    .then(data => {
        localStorage.setItem("user", JSON.stringify(data));
        const role = data.role ? data.role.toUpperCase() : "USER";

        if (role === "ADMIN") {
            window.location.href = "/admin";
        } else {
            window.location.href = "/dashboard";
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