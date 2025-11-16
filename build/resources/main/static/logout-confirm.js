document.addEventListener("DOMContentLoaded", function () {
console.log("Logout confirmation script loaded successfully");
    const logoutLink = document.getElementById("logout-link");
    if (logoutLink) {
        logoutLink.addEventListener("click", function (event) {
            const confirmed = confirm("Do you really want to log out?");
            if (!confirmed) {
                event.preventDefault();
            }
        });
    }
});
