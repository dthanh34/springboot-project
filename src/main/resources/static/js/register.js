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

let ingredientData = [];
let selectedIngredients = new Set();

let diseaseData = [];
let selectedDiseases = new Set();


// ================= STEP =================
function showStep(step) {
    document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
    document.getElementById(`step-${step}`).classList.add('active');

    document.getElementById('step-title').innerText = titles[step - 1];
    document.getElementById('step-subtitle').innerText = subtitles[step - 1];

    document.querySelectorAll('.step-indicator').forEach((ind, index) => {
        ind.classList.toggle('active', index < step);
    });
}

function nextStep(step) {
    showStep(step + 1);
}

function prevStep(step) {
    showStep(step - 1);
}


// ================= LOAD =================
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.goal-group button').forEach(btn => {
    btn.addEventListener('click', function () {

        // bỏ active tất cả
        document.querySelectorAll('.goal-group button')
            .forEach(b => b.classList.remove('active'));

        // set active cho cái vừa click
        this.classList.add('active');

        // cập nhật giá trị hidden
        document.getElementById('selectedGoal').value = this.dataset.value;
    });
    });

    // ===== BUTTON ADD ALLERGY =====
    document.getElementById('btnAddAllergy').onclick = () => {
        document.getElementById('ingredientModal').classList.remove('hidden');

        fetch('/api/ingredients')
            .then(res => res.json())
            .then(data => {
                ingredientData = data;
                renderIngredientList(data);
            })
            .catch(err => console.error(err));
    };

    // ===== BUTTON ADD DISEASE =====
    document.getElementById('btnAddDisease').onclick = () => {
        document.getElementById('diseaseModal').classList.remove('hidden');

        fetch('/api/diseases')
            .then(res => res.json())
            .then(data => {
                diseaseData = data;
                renderDiseaseList(data);
            })
            .catch(err => console.error(err));
    };

    // ===== CONFIRM =====
    document.getElementById('btnConfirmIngredient').onclick = confirmIngredient;
    document.getElementById('btnConfirmDisease').onclick = confirmDisease;

    // ===== SEARCH =====
    document.getElementById('searchIngredient').oninput = function () {
        const k = this.value.toLowerCase();
        renderIngredientList(
            ingredientData.filter(i => i.ingredient_name.toLowerCase().includes(k))
        );
    };

    document.getElementById('searchDisease').oninput = function () {
        const k = this.value.toLowerCase();
        renderDiseaseList(
            diseaseData.filter(i => i.disease_name.toLowerCase().includes(k))
        );
    };
});


// ================= RENDER =================
function renderIngredientList(list) {
    const container = document.getElementById('ingredientList');
    container.innerHTML = '';

    list.forEach(item => {
        const div = document.createElement('div');
        div.innerText = item.ingredient_name;
        div.dataset.id = item.ingredient_id;

        if (selectedIngredients.has(item.ingredient_id)) {
            div.classList.add('active');
        }

        div.onclick = () => {
            if (selectedIngredients.has(item.ingredient_id)) {
                selectedIngredients.delete(item.ingredient_id);
                div.classList.remove('active');
            } else {
                selectedIngredients.add(item.ingredient_id);
                div.classList.add('active');
            }
        };

        container.appendChild(div);
    });
}

function renderDiseaseList(list) {
    const container = document.getElementById('DiseaseList'); 
    container.innerHTML = '';

    list.forEach(item => {
        const div = document.createElement('div');
        div.innerText = item.disease_name;
        div.dataset.id = item.disease_id;

        if (selectedDiseases.has(item.disease_id)) {
            div.classList.add('active');
        }

        div.onclick = () => {
            if (selectedDiseases.has(item.disease_id)) {
                selectedDiseases.delete(item.disease_id);
                div.classList.remove('active');
            } else {
                selectedDiseases.add(item.disease_id);
                div.classList.add('active');
            }
        };

        container.appendChild(div);
    });
}


// ================= CONFIRM =================
function confirmIngredient() {
    const container = document.getElementById('allergyContainer');
    container.innerHTML = '';

    selectedIngredients.forEach(id => {
        const item = ingredientData.find(i => i.ingredient_id == id);

        const tag = document.createElement('div');
        tag.className = 'tag';
        tag.setAttribute('data-id', id);

        tag.innerHTML = `
            ${item.ingredient_name}
            <span onclick="removeTag(this)">x</span>
        `;

        container.appendChild(tag);
    });

    closeModal();
}

function confirmDisease() {
    const container = document.getElementById('diseaseContainer');
    container.innerHTML = '';

    selectedDiseases.forEach(id => {
        const item = diseaseData.find(i => i.disease_id == id);

        const tag = document.createElement('div');
        tag.className = 'tag';
        tag.setAttribute('data-id', id);

        tag.innerHTML = `
            ${item.disease_name}
            <span onclick="removeTag(this)">x</span>
        `;

        container.appendChild(tag);
    });

    closeDiseaseModal();
}


// ================= REMOVE =================
function removeTag(el) {
    const tag = el.parentElement;
    const id = parseInt(tag.getAttribute('data-id'));

    selectedIngredients.delete(id);
    selectedDiseases.delete(id);

    tag.remove();
}


// ================= MODAL =================
function closeModal() {
    document.getElementById('ingredientModal').classList.add('hidden');
}

function closeDiseaseModal() {
    document.getElementById('diseaseModal').classList.add('hidden');
}


// ================= SUBMIT =================
function submitForm() {
    const data = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,

        gender: document.getElementById('gender').value === "true",
        age: parseInt(document.getElementById('age').value) || 0,
        height: parseFloat(document.getElementById('height').value) || 0,
        weight: parseFloat(document.getElementById('weight').value) || 0,
        activityLevel: parseFloat(document.getElementById('activityLevel').value) || 1.2,

        desiredHeight: parseFloat(document.getElementById('desiredHeight').value) || 0,
        desiredWeight: parseFloat(document.getElementById('desiredWeight').value) || 0,
        goalType: document.getElementById('selectedGoal').value,

        diseaseIds: Array.from(document.querySelectorAll('#diseaseContainer .tag'))
            .map(t => parseInt(t.getAttribute('data-id'))),

        ingredientIds: Array.from(document.querySelectorAll('#allergyContainer .tag'))
            .map(t => parseInt(t.getAttribute('data-id')))
    };

    console.log(data);

    fetch('/api/auth/register-complete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(res => {
            if (res.ok) {
                alert("Đăng ký thành công!");
                window.location.href = "/login";
            } else {
                res.text().then(t => alert(t));
            }
        })
        .catch(err => console.error(err));
}