document.addEventListener("DOMContentLoaded", function() {
    loadUserProfile();
});

function loadUserProfile() {
    fetch('/user/profile-data')
        .then(response => {
            if (response.status === 401) {
                alert("Vui lòng đăng nhập để xem hồ sơ!");
                window.location.href = "/login";
                return;
            }
            return response.json();
        })
        .then(data => {
            if (!data) return;
            const genderText = data.gender ? "Nam" : "Nữ";

            document.getElementById('weight').value = data.weight || "";
            document.getElementById('height').value = data.height || "";
            document.getElementById('fullName').value = data.fullName || "";
            document.getElementById('gender').value = genderText;
            document.getElementById('age').value = data.age || "";
            document.getElementById('goalType').value = data.goalType || "";
            document.getElementById('desiredWeight').value = data.desiredWeight || "";
            document.getElementById('desiredHeight').value = data.desiredHeight || "";

            const dList = document.getElementById("disease-list");
            if (data.disease && data.disease.length > 0) {
                dList.innerHTML = "";
                data.disease.forEach(d => {
                    const tag = document.createElement("div");
                    tag.className = "tag-item";
                    tag.innerHTML = `${d.diseaseName} (${d.level}) <i class="fas fa-times" onclick="this.parentElement.remove()"></i>`;
                    dList.appendChild(tag);
                });
            } else {
                dList.innerHTML = '<span class="placeholder-text">Khỏe mạnh không mắc bệnh</span>';
            }

            const aList = document.getElementById("allergy-list");
            if (data.allergy && data.allergy.length > 0) {
                aList.innerHTML = "";
                data.allergy.forEach(a => {
                    const tag = document.createElement("div");
                    tag.className = "tag-item";
                    tag.style.background = "#fff5f5"; tag.style.color = "#c53030";
                    tag.innerHTML = `${a.ingredientName} <i class="fas fa-times" onclick="this.parentElement.remove()"></i>`;
                    aList.appendChild(tag);
                });
            } else {
                aList.innerHTML = '<span class="placeholder-text">Không bị dị ứng thực phẩm</span>';
            }
        })
        .catch(err => console.error("Lỗi fetch dữ liệu:", err));
}

function openModal(id) { document.getElementById(id).style.display = "block"; }
function closeModal(id) { document.getElementById(id).style.display = "none"; }
window.onclick = function(event) { if (event.target.className === 'modal') event.target.style.display = "none"; }

function addDiseaseTag() {
    const disease = document.getElementById("disease-select").value;
    const level = document.getElementById("disease-level").value;
    if (!disease) return;
    const container = document.getElementById("disease-list");
    if (container.querySelector(".placeholder-text")) container.innerHTML = "";
    const tag = document.createElement("div");
    tag.className = "tag-item";
    tag.innerHTML = `${disease} (${level}) <i class="fas fa-times" onclick="this.parentElement.remove()"></i>`;
    container.appendChild(tag);
    closeModal('diseaseModal');
}

function addAllergyTag(name) {
    const container = document.getElementById("allergy-list");
    if (container.querySelector(".placeholder-text")) container.innerHTML = "";
    const tag = document.createElement("div");
    tag.className = "tag-item";
    tag.style.background = "#fff5f5"; tag.style.color = "#c53030";
    tag.innerHTML = `${name} <i class="fas fa-times" onclick="this.parentElement.remove()"></i>`;
    container.appendChild(tag);
    closeModal('allergyModal');
}