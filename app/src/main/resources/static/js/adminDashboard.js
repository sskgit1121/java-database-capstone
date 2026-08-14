/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
/**
 * Admin Dashboard Controller
 * Handles managing doctor records, structural filters, and programmatic card updates.
 */

/**
 * Admin Dashboard Controller
 * File: app/src/main/resources/static/js/adminDashboard.js
 */

// Import Required Modules
import { openModal } from '../components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Load Doctor Cards on Page Load
document.addEventListener("DOMContentLoaded", async () => {
    // Event Binding for Add Doctor button
    const addDocBtn = document.getElementById('addDocBtn');
    if (addDocBtn) {
        addDocBtn.addEventListener('click', () => {
            openModal('addDoctor');
        });
    }

    // Attach submit event listener to the modal form
    const addDoctorForm = document.getElementById("addDoctorForm");
    if (addDoctorForm) {
        addDoctorForm.addEventListener("submit", adminAddDoctor);
    }

    // Implement Search and Filter Logic Listeners
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);

    // Initial load call
    await loadDoctorCards();
});

/**
 * Fetch all doctors and display them in the dashboard
 */
async function loadDoctorCards() {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    // Clears existing content
    contentDiv.innerHTML = "";

    // Calls getDoctors() to fetch doctor list from backend
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
}

/**
 * Utility function to render doctor cards when passed a list
 * @param {Array} doctors - List of doctor data objects
 */
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
    }

    // Iterates through results and appends each card using createDoctorCard
    doctors.forEach(doctor => {
        const cardNode = createDoctorCard(doctor);
        contentDiv.appendChild(cardNode);
    });
}

/**
 * Gathers current filter/search values and displays filtered results
 */
async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    // Fetches filtered results using filterDoctors()
    const matchingDoctors = await filterDoctors(name, time, specialty);
    renderDoctorCards(matchingDoctors);
}

/**
 * Handles processing and parsing data from the Add Doctor modal submission form
 * @param {Event} event - System submit event
 */
async function adminAddDoctor(event) {
    event.preventDefault();

    // Verifies that a valid login token exists to authenticate the admin
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Authentication failed. Token missing. Please log in again.");
        return;
    }

    // Collects any checkbox values for doctor availability if present
    const checkedShifts = [];
    document.querySelectorAll("input[name='docAvailabilityCheckbox']:checked").forEach(checkbox => {
        checkedShifts.push(checkbox.value);
    });

    // Populate data inputs
    const doctorData = {
        name: document.getElementById("docName").value,
        specialty: document.getElementById("docSpecialty").value,
        email: document.getElementById("docEmail").value,
        password: document.getElementById("docPassword").value,
        mobileNo: document.getElementById("docMobile").value,
        availabilityTime: document.getElementById("docTime").value || checkedShifts.join(", ")
    };

    // Send a POST request using saveDoctor()
    const result = await saveDoctor(doctorData, token);

    if (result && result.success) {
        alert(result.message || "Doctor added successfully!");
        
        // Closes the modal or resets form and refreshes the doctor list
        document.getElementById("addDoctorForm").reset();
        
        // Hide modal safely by checking common container layouts or utility layers
        const modalContainer = document.getElementById("addDoctorModal");
        if (modalContainer) modalContainer.style.display = "none";
        
        await loadDoctorCards();
    } else {
        // If failed, alerts the user with an error message
        alert(result?.message || "Failed to save the new doctor record.");
    }
}
