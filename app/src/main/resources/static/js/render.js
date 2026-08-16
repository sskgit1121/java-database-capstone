/**
 * Role Selection / Dashboard Routing
 *
 * File:
 * static/js/render.js
 */


/**
 * Redirect the authenticated user to the appropriate dashboard.
 *
 * @param {string} role - admin, doctor, patient
 */
function selectRole(role) {

    if (!role) {
        console.warn("selectRole() called without a role.");
        return;
    }

    const normalizedRole = String(role).trim().toLowerCase();

    /*
     * Store selected role.
     *
     * util.js may provide setRole().
     * If not available, localStorage is used directly.
     */
    if (typeof window.setRole === "function") {

        window.setRole(normalizedRole);

    } else {

        localStorage.setItem("role", normalizedRole);
    }


    /*
     * Authentication token.
     */
    const token = localStorage.getItem("token");


    /*
     * Admin Dashboard
     */
    if (normalizedRole === "admin") {

        if (!token) {
            console.warn("Admin dashboard requires authentication.");
            return;
        }

        window.location.href =
            `/adminDashboard/${encodeURIComponent(token)}`;

        return;
    }


    /*
     * Doctor Dashboard
     */
    if (normalizedRole === "doctor") {

        if (!token) {
            console.warn("Doctor dashboard requires authentication.");
            return;
        }

        window.location.href =
            `/doctorDashboard/${encodeURIComponent(token)}`;

        return;
    }


    /*
     * Patient Dashboard
     */
    if (normalizedRole === "patient") {

        if (!token) {
            console.warn("Patient dashboard requires authentication.");
            return;
        }

        window.location.href =
            `/patientDashboard/${encodeURIComponent(token)}`;

        return;
    }


    /*
     * Logged Patient Dashboard
     */
    if (normalizedRole === "loggedpatient") {

        window.location.href = "/loggedPatientDashboard.html";

        return;
    }


    console.warn(`Unknown role: ${normalizedRole}`);
}


/**
 * Protect a page based on the stored role.
 *
 * This function can be called from dashboard pages.
 */
function renderContent() {

    const role =
        typeof window.getRole === "function"
            ? window.getRole()
            : localStorage.getItem("role");


    if (!role) {

        window.location.href = "/";

        return;
    }
}


/**
 * Logout helper.
 */
function logout() {

    localStorage.removeItem("token");
    localStorage.removeItem("role");

    window.location.href = "/";
}


/*
 * Explicitly expose functions globally.
 *
 * services/index.js is an ES module and therefore cannot
 * directly access normal script functions unless they are
 * attached to window.
 */
window.selectRole = selectRole;
window.renderContent = renderContent;
window.logout = logout;