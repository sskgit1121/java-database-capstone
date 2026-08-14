/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/
/**
 * Doctor Dashboard Controller
 * Manages appointments logs, search terms queries, and date picker bindings.
 */

/**
 * Doctor Dashboard Controller
 * File: app/src/main/resources/static/js/doctorDashboard.js
 */

// Import Required Modules
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Initialize Global Variables
let patientTableBody = null;
let selectedDate = new Date().toISOString().split('T')[0]; // Initialized to today's date (YYYY-MM-DD)
let token = localStorage.getItem("token"); // Retrieved from localStorage used for authentication
let patientName = "null"; // Initialized as literal "null" string for search filtering default rules


// Pagination state vector extensions
let currentPage = 0;
const pageSize = 10;

/**
 * Increments the page number and updates the view
 */
async function loadNextPage() {
    currentPage++;
    await loadAppointments();
}

/**
 * Decrements the page number and updates the view
 */
async function loadPreviousPage() {
    if (currentPage > 0) {
        currentPage--;
        await loadAppointments();
    }
}

// Initial Render on Page Load
document.addEventListener("DOMContentLoaded", async () => {
    // Define and store a reference to the appointment table body where rows will be rendered
    patientTableBody = document.getElementById("patientTableBody");

    const datePicker = document.getElementById("datePicker");
    if (datePicker) {
        datePicker.value = selectedDate;
    }

    // Setup Search Bar and Control Listeners
    setupDashboardControls();

    // Call renderContent() if defined globally in render.js
    if (typeof window.renderContent === 'function') {
        window.renderContent();
    } else if (typeof renderContent === 'function') {
        renderContent();
    }

    // Call loadAppointments() to load today's appointments by default
    await loadAppointments();
});

/**
 * Binds interaction event listeners to input elements and filter controls
 */
function setupDashboardControls() {
    const searchBar = document.getElementById("searchBar");
    const todayButton = document.getElementById("todayButton");
    const datePicker = document.getElementById("datePicker");

    // Add an event listener to the search bar (#searchBar)
    if (searchBar) {
        searchBar.addEventListener("input", async (e) => {
            const query = e.target.value.trim();
            // On input change, update the patientName variable. If empty, default to "null"
            patientName = query === "" ? "null" : query;
            // Call loadAppointments() to refresh the list with the filtered data
            await loadAppointments();
        });
    }

    // Bind event listener to "Today's Appointments" button (#todayButton)
    if (todayButton) {
        todayButton.addEventListener("click", async () => {
            // Resets the selectedDate to today
            selectedDate = new Date().toISOString().split('T')[0];
            // Updates the date picker field to reflect today's date
            if (datePicker) datePicker.value = selectedDate;
            // Calls loadAppointments()
            await loadAppointments();
        });
    }

    // Bind event listener to Date picker (#datePicker)
    if (datePicker) {
        datePicker.addEventListener("change", async (e) => {
            // Updates the selectedDate variable when changed
            selectedDate = e.target.value;
            // Calls loadAppointments() to fetch and display appointments for the selected date
            await loadAppointments();
        });
    }
}

/**
 * Uses getAllAppointments to fetch appointment data and update table rows
 */
async function loadAppointments() {
    if (!patientTableBody) return;

    // Clears existing content in the table
    patientTableBody.innerHTML = "";

    // Wrap this logic in a try-catch block
    try {
        // Uses getAllAppointments(selectedDate, patientName, token) to fetch data
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        // If no appointments are found
        if (!appointments || appointments.length === 0) {
            const emptyRow = document.createElement("tr");
            emptyRow.innerHTML = `<td colspan="5" style="text-align: center;">No Appointments found for today</td>`;
            patientTableBody.appendChild(emptyRow);
            return;
        }

        // If appointments exist: For each appointment, extract patient's details and render
        appointments.forEach(appointment => {
            // Use createPatientRow() to create a <tr> for each
            const rowNode = createPatientRow(appointment);
            // Append each row to the appointment table body
            patientTableBody.appendChild(rowNode);
        });

    } catch (error) {
        // In case of error, display a fallback error message row in the table
        console.error("Error executing loadAppointments pipeline:", error);
        const errorRow = document.createElement("tr");
        errorRow.innerHTML = `<td colspan="5" style="text-align: center; color: red; font-weight: bold;">System connection error. Failed to load appointments.</td>`;
        patientTableBody.appendChild(errorRow);
    }
}

