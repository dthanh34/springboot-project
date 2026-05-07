document.addEventListener("DOMContentLoaded", function() {
    // 1. Dữ liệu mặc định (Thành có thể gọi API để lấy số thật từ DB)
    const statsData = {
        users: 1701,
        foods: 856,
        menus: 3421
    };

    // 2. Hàm chạy số (Count Up Animation)
    function animateValue(obj, start, end, duration) {
        let startTimestamp = null;
        const step = (timestamp) => {
            if (!startTimestamp) startTimestamp = timestamp;
            const progress = Math.min((timestamp - startTimestamp) / duration, 1);
            obj.innerHTML = Math.floor(progress * (end - start) + start).toLocaleString();
            if (progress < 1) {
                window.requestAnimationFrame(step);
            }
        };
        window.requestAnimationFrame(step);
    }

    // 3. Xử lý cuộn chuột để kích hoạt hiệu ứng (Observer)
    const statsSection = document.querySelector('.stats');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateValue(document.getElementById("stat-users"), 0, statsData.users, 2000);
                animateValue(document.getElementById("stat-foods"), 0, statsData.foods, 2000);
                animateValue(document.getElementById("stat-menus"), 0, statsData.menus, 2000);
                observer.unobserve(statsSection); // Chỉ chạy 1 lần
            }
        });
    }, { threshold: 0.5 });

    observer.observe(statsSection);
});