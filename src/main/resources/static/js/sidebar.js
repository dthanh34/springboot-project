document.addEventListener("DOMContentLoaded", function () {
    const currentPath = window.location.pathname;

    const menuLinks = document.querySelectorAll(".menu a");

    menuLinks.forEach(link => {
        const linkPath = new URL(link.href).pathname;

        if (currentPath === linkPath) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });
});