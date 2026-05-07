document.addEventListener("DOMContentLoaded", function() {
    initTabs();
    fetchDiseases();
    fetchCompatibility();
    loadDropdownData(); // Load danh sách món ăn & bệnh cho Modal đánh giá
});

// 1. Chuyển Tab
function initTabs() {
    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            const target = this.getAttribute('data-tab');
            document.querySelectorAll('.tab-content').forEach(c => c.style.display = 'none');
            document.getElementById(target).style.display = 'block';
        });
    });
}

// 2. Xử lý Bệnh lý (Disease)
async function fetchDiseases() {
    const response = await fetch('/api/admin/diseases');
    const data = await response.json();
    const tbody = document.getElementById('disease-tbody');
    tbody.innerHTML = data.map(d => `
        <tr style="border-bottom: 1px solid #f3f4f6;">
            <td style="padding: 15px; font-weight: 500;">${d.diseaseName}</td>
            <td style="color: #6b7280;">${d.diseaseDescription}</td>
            <td style="text-align: center;"><span class="badge-disease">${d.foodCount || 0}</span></td>
            <td style="text-align: center;">
                <button onclick="editDisease(${d.id}, '${d.diseaseName}', '${d.diseaseDescription}')" style="border:none; background:none; cursor:pointer; color:#6b7280; margin-right:10px;"><i class="fa-regular fa-pen-to-square"></i></button>
                <button onclick="deleteDisease(${d.id})" style="border:none; background:none; cursor:pointer; color:#ef4444;"><i class="fa-regular fa-trash-can"></i></button>
            </td>
        </tr>
    `).join('');
}

// 3. Xử lý Tương thích (Compatibility)
async function fetchCompatibility() {
    const response = await fetch('/api/admin/compatibility');
    const data = await response.json();
    const tbody = document.getElementById('compatibility-tbody');
    tbody.innerHTML = data.map(item => `
        <tr style="border-bottom: 1px solid #f3f4f6;">
            <td style="padding: 15px; font-weight: 500;">${item.foodName}</td>
            <td><span style="background: #e0f2fe; color: #0284c7; padding: 4px 10px; border-radius: 20px; font-size: 13px;">${item.diseaseName}</span></td>
            <td><span style="color: #f59e0b; font-size: 18px;">${'★'.repeat(item.rating)}${'☆'.repeat(5-item.rating)}</span></td>
            <td style="text-align: center;">
                <button onclick="deleteRating(${item.id})" style="border:none; background:none; cursor:pointer; color:#ef4444;"><i class="fas fa-trash"></i> Xóa</button>
            </td>
        </tr>
    `).join('');
}

// 4. Modal Logic
function openModal(modalId) {
    document.getElementById(modalId).style.display = 'flex';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
    if(modalId === 'diseaseModal') document.getElementById('diseaseForm').reset();
}

function editDisease(id, name, desc) {
    document.getElementById('modalTitle').innerText = "Sửa bệnh lý";
    document.getElementById('diseaseId').value = id;
    document.getElementById('diseaseName').value = name;
    document.getElementById('diseaseDesc').value = desc;
    openModal('diseaseModal');
}

// 5. Load data cho Select Box trong Modal
async function loadDropdownData() {
    const [foods, diseases] = await Promise.all([
        fetch('/api/admin/foods').then(r => r.json()),
        fetch('/api/admin/diseases').then(r => r.json())
    ]);
    
    document.getElementById('selectFood').innerHTML = foods.map(f => `<option value="${f.id}">${f.foodName}</option>`).join('');
    document.getElementById('selectDisease').innerHTML = diseases.map(d => `<option value="${d.id}">${d.diseaseName}</option>`).join('');
}

// 6. Xử lý Submit (Thêm/Sửa)
document.getElementById('diseaseForm').onsubmit = async function(e) {
    e.preventDefault();
    const id = document.getElementById('diseaseId').value;
    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/admin/diseases/${id}` : '/api/admin/diseases';
    
    const response = await fetch(url, {
        method: method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            diseaseName: document.getElementById('diseaseName').value,
            diseaseDescription: document.getElementById('diseaseDesc').value
        })
    });

    if(response.ok) {
        closeModal('diseaseModal');
        fetchDiseases();
    }
};