// patientDashboard.js
/**
 * Patient Dashboard Controller
 * Facilitates authentication routing overlays, searching parameters, and doctor listings.
 */

/**
 * Patient Dashboard Controller
 * File: app/src/main/resources/static/js/patientDashboard.js
 */

// Import Required Modules
import createDoctorCard from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';

// Load Doctor Cards on Page Load
document.addEventListener("DOMContentLoaded", async () => {
    // Bind Modal Triggers for Login and Signup
    const signupBtn = document.getElementById("patientSignup");
    if (signupBtn) {
        signupBtn.addEventListener("click", () => openModal("patientSignup"));
    }

    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) {
        loginBtn.addEventListener("click", () => openModal("patientLogin"));
    }

    // Set up listeners for search and filter controls
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);

    // Call loadDoctorCards inside the listener
    await loadDoctorCards();
});

/**
 * Calls getDoctors() to fetch the list of all available doctors and injects them
 */
async function loadDoctorCards() {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    // Clears any existing content inside the #content div
    contentDiv.innerHTML = "";

    // Calls getDoctors() to fetch the list of all available doctors
    const doctors = await getDoctors();

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found.</p>";
        return;
    }

    // Iterates over the results and renders each doctor using createDoctorCard()
    doctors.forEach(doctor => {
        const cardNode = createDoctorCard(doctor);
        // Appends each card to the #content section
        contentDiv.appendChild(cardNode);
    });
}

/**
 * Gathers values from filter/search inputs and updates results
 */
async function filterDoctorsOnChange() {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    // Gathers values from all three filter/search inputs
    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    // Uses filterDoctors(name, time, specialty) to fetch filtered results
    const doctors = await filterDoctors(name, time, specialty);

    // Clears the existing content
    contentDiv.innerHTML = "";

    // If doctors are found, renders them. If not, displays a fallback message
    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors match the selected filters.</p>";
        return;
    }

    doctors.forEach(doctor => {
        const cardNode = createDoctorCard(doctor);
        contentDiv.appendChild(cardNode);
    });
}

