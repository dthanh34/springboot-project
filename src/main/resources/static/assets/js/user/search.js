document.addEventListener("DOMContentLoaded", function() {
    const searchInput = document.getElementById('searchInput');
    const btnSearch = document.getElementById('btnSearch');
    const btnClear = document.getElementById('btnClear');
    const resultContainer = document.getElementById('food-results');
    const countNumber = document.getElementById('count-number');

    // 1. Tự động load tất cả món ăn khi vào trang
    fetchFoods('');

    // 2. Sự kiện nút Tìm kiếm
    btnSearch.addEventListener('click', () => {
        fetchFoods(searchInput.value);
    });

    // 3. Tìm kiếm khi nhấn Enter
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            fetchFoods(searchInput.value);
        }
    });

    // 4. Xử lý nút Xóa (Clear)
    searchInput.addEventListener('input', () => {
        btnClear.style.display = searchInput.value ? 'block' : 'none';
    });

    btnClear.addEventListener('click', () => {
        searchInput.value = '';
        btnClear.style.display = 'none';
        fetchFoods('');
    });

    async function fetchFoods(keyword) {
        try {
            resultContainer.innerHTML = '<div class="empty-state">Đang tìm kiếm...</div>';
            
            const response = await fetch(`/api/user/search?keyword=${encodeURIComponent(keyword)}`);
            const listF = await response.json();

            countNumber.innerText = listF.length;

            if (listF.length === 0) {
                renderEmpty(keyword);
                return;
            }

            renderResults(listF);
        } catch (error) {
            console.error("Lỗi tìm kiếm:", error);
            resultContainer.innerHTML = '<div class="empty-state">Có lỗi xảy ra, vui lòng thử lại sau.</div>';
        }
    }

    function renderResults(foods) {
        resultContainer.innerHTML = foods.map(f => {
            
            return `
                <a href="/food_detail?id=${f.foodId}" class="food-card">
                    <div class="img-wrapper">
                        <img src="/images/${f.imageUrl}" alt="${f.foodName}" 
                             onerror="this.onerror=null; this.src='https://via.placeholder.com/300x200?text=No+Image'">
                        <div class="icon-heart"><i class="fa-regular fa-heart"></i></div>
                       
                    </div>
                    <div class="card-info">
                         <h3>${f.foodName}</h3>
                        <div class="calo">${Math.round(f.calories)} calo</div>
                        
                    </div>
                </a>
            `;
        }).join('');
    }

    function renderEmpty(keyword) {
        resultContainer.innerHTML = `
            <div class="empty-state">
                <i class="fa-regular fa-face-frown" style="font-size: 50px; margin-bottom: 15px; color: #ddd;"></i>
                <h3 style="color: #555;">Không tìm thấy món ăn nào</h3>
                <p>Không có kết quả cho từ khóa "<strong>${keyword}</strong>". Vui lòng thử lại!</p>
            </div>
        `;
    }
});