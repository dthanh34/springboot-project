document.addEventListener("DOMContentLoaded", function() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) {
        window.location.href = "/login";
        return;
    }

    loadFoods(user.id);
});

function loadFoods(userId) {
    const foodGrid = document.getElementById("food-grid");
    const countText = document.getElementById("food-count-text");

    const requestData = {
        userId: userId,
        isFamilyMeal: true,
        targetCal: 2000,
        acuteDiseaseIds: [] 
    };

    fetch('/api/meals/all-safe', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => {
        if (!response.ok) throw new Error("Không thể tải danh sách món ăn");
        return response.json();
    })
    .then(foods => {
        if (!foods || foods.length === 0) {
            foodGrid.innerHTML = "<p>Không tìm thấy món ăn nào phù hợp với sức khỏe hiện tại của bạn.</p>";
            countText.innerText = "Có 0 món ăn phù hợp với bạn hôm nay";
            return;
        }

        countText.innerText = `Có ${foods.length} món ăn phù hợp với bạn hôm nay`;
        foodGrid.innerHTML = ""; 

        foods.forEach(food => {
            const matchScore = food.calories < 600 ? 95 : 88;
            
            const cardHtml = `
                <div class="food-card">
                    <div class="card-image-wrapper">
                        <img src="/images/${food.imageUrl || 'default-food.jpg'}" 
                             onerror="this.src='/images/default-food.jpg'" alt="${food.foodName}">
                        <button class="heart-btn"><i class="far fa-heart"></i></button>
                        <span class="badge-safe"><i class="fas fa-check-circle"></i> An toàn</span>
                    </div>
                    <div class="card-content">
                        <h3>${food.foodName}</h3>
                        <p class="calories">${Math.round(food.calories)} calo</p>
                        <div class="match-status">
                            <i class="far fa-star"></i>
                            <span>${matchScore}% phù hợp</span>
                        </div>
                    </div>
                </div>
            `;
            foodGrid.innerHTML += cardHtml;
        });
    })
    .catch(error => {
        console.error("Error:", error);
        foodGrid.innerHTML = "<p>Lỗi khi tải dữ liệu từ máy chủ.</p>";
    });
}