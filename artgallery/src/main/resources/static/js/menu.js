document.addEventListener("DOMContentLoaded", function () {
    const avatarMenus = document.querySelectorAll(".avatar-menu");

    avatarMenus.forEach(function (menu) {
        const button = menu.querySelector(".avatar-btn");

        if (!button) return;

        button.addEventListener("click", function (event) {
            event.preventDefault();
            event.stopPropagation();

            menu.classList.toggle("open");
        });
    });

    document.addEventListener("click", function () {
        avatarMenus.forEach(function (menu) {
            menu.classList.remove("open");
        });
    });
});