document.addEventListener('DOMContentLoaded', () => {
    const counters = document.querySelectorAll('.counter');
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if(entry.isIntersecting) {
                const counter = entry.target;
                const target = +counter.getAttribute('data-target');
                const updateCount = () => {
                    const count = +counter.innerText.replace(',', '');
                    const inc = target / 100;
                    if (count < target) {
                        counter.innerText = Math.ceil(count + inc).toLocaleString();
                        setTimeout(updateCount, 20);
                    } else {
                        counter.innerText = target.toLocaleString();
                    }
                };
                updateCount();
                observer.unobserve(counter);
            }
        });
    }, { threshold: 1 });

    counters.forEach(c => observer.observe(c));
});

document.getElementById('btnLogin').addEventListener('click', function() {
    window.location.href = 'login.html';
});

document.getElementById('btnRegister').addEventListener('click', function() {
    window.location.href = '/register';
});