const API_URL = "http://localhost:8080/api/auth";
let currentUser = JSON.parse(localStorage.getItem("currentUser")) || null;

// Show modals
function showLogin() {
    document.getElementById("loginModal").style.display = "flex";
}
function showRegister() {
    document.getElementById("registerModal").style.display = "flex";
}
function closeModal(id) {
    document.getElementById(id).style.display = "none";
}

// Register
async function register() {
    const username = document.getElementById("regUser").value;
    const password = document.getElementById("regPass").value;

    if (!username || !password) return alert("Enter all fields!");

    const res = await fetch(`${API_URL}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });
    const data = await res.json();
    alert(data.message);
    closeModal('registerModal');
}

// Login
async function login() {
    const username = document.getElementById("loginUser").value;
    const password = document.getElementById("loginPass").value;

    const res = await fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    const data = await res.json();

    if (res.ok) {
        currentUser = { username: data.username, role: data.role };
        localStorage.setItem("currentUser", JSON.stringify(currentUser));
        alert("Welcome " + data.username);
        closeModal('loginModal');
        updateUI();
    } else {
        alert(data.message);
    }
}

// Update UI after login
function updateUI() {
    const authBox = document.querySelector(".auth-box");
    if (currentUser) {
        authBox.innerHTML = `
            <span>Hi, ${currentUser.username} (${currentUser.role})</span>
            <button onclick="logout()">Logout</button>`;
    }
}

// Logout
function logout() {
    localStorage.removeItem("currentUser");
    currentUser = null;
    location.reload();
}

// Restrict editing based on role (example)
document.addEventListener("DOMContentLoaded", () => {
    updateUI();
});
