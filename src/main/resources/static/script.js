const API_BASE_URL = "http://localhost:8080";

const loginForm = document.getElementById("loginForm");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const errorMessage = document.getElementById("errorMessage");

loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    errorMessage.textContent = "";

    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    if (!email || !password) {
        errorMessage.textContent = "Please enter both email and password";
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (!data.success) {
            errorMessage.textContent = "email or password is invalid";
            return;
        }

        window.location.href = "main.html";
    } catch (error) {
        errorMessage.textContent = "email or password is invalid";
    }
});
