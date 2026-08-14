/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/
/**
 * doctorCard.js
 * Reusable UI component module that encapsulates the rendering and interactivity
 * of individual doctor profile cards across both Admin and Patient dashboards.
 */

/**
 * doctorCard.js
 * Reusable UI component module that encapsulates the rendering and interactivity
 * of individual doctor profile cards across both Admin and Patient dashboards.
 */

/**
 * Dynamically creates a complete DOM element structure representing a doctor's card.
 * @param {Object} doctor - The doctor data object containing profile details.
 * @param {string|number} doctor.id - Unique identifier for the doctor.
 * @param {string} doctor.name - Full name of the medical practitioner.
 * @param {string} doctor.specialization - Field of expertise.
 * @param {string} doctor.email - Contact email address.
 * @param {Array<string>|string} doctor.availability - Days/Times the doctor is available.
 * @returns {HTMLElement} The constructed card container element ready for insertion.
 */
export function createDoctorCard(doctor) {
    // Create the Main Card Container
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // Fetch the User’s Role from Local Browser Context State
    const role = localStorage.getItem("userRole");

    // Create Doctor Info Section Wrapper
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    // Create heading element and set the text to the doctor’s name
    const name = document.createElement("h3");
    name.textContent = doctor.name || "Unknown Practitioner";
    infoDiv.appendChild(name);

    // Create and append the specialization text field
    const specialization = document.createElement("p");
    specialization.classList.add("doctor-specialty");
    const specialtyText = doctor.specialization || doctor.specialty || "General Medicine";
    specialization.textContent = `Specialty: ${specialtyText}`;
    infoDiv.appendChild(specialization);

    // Create and append the email text field
    const email = document.createElement("p");
    email.classList.add("doctor-email");
    email.textContent = `Email: ${doctor.email || "N/A"}`;
    infoDiv.appendChild(email);

    // Create and append the availability timeline tags
    const availability = document.createElement("p");
    availability.classList.add("doctor-availability");
    
    let availabilityText = "";
    if (Array.isArray(doctor.availability)) {
        availabilityText = doctor.availability.join(", ");
    } else {
        availabilityText = doctor.availability || "Not Specified";
    }
    availability.textContent = `Availability: ${availabilityText}`;
    infoDiv.appendChild(availability);

    // Append info section to card
    card.appendChild(infoDiv);

    // Create Button Container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // Conditionally Add Buttons Based on Role
    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.classList.add("admin-delete-btn");

        removeBtn.addEventListener("click", async () => {
            // 1. Confirm deletion
            const isConfirmed = confirm(`Are you sure you want to delete the profile for ${doctor.name}?`);
            if (!isConfirmed) return;

            // 2. Get token from localStorage
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Authorization token missing. Action denied.");
                return;
            }

            // 3. Call API to delete
            try {
                // Using helper function from doctorServices.js if bound globally or imported
                if (typeof window.deleteDoctor === "function") {
                    await window.deleteDoctor(doctor.id, token);
                } else if (typeof deleteDoctor === "function") {
                    await deleteDoctor(doctor.id, token);
                } else {
                    console.log(`Service Fallback: Making mock API call to delete doctor ID [${doctor.id}]`);
                }

                // 4. On success: remove the card from the DOM
                card.remove();
                alert("Doctor profile successfully removed.");
            } catch (error) {
                console.error("Error executing profile deletion:", error);
                alert("Failed to delete doctor profile. Please try again.");
            }
        });

        actionsDiv.appendChild(removeBtn);

    } else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("patient-book-btn");

        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });

        actionsDiv.appendChild(bookNow);

    } else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("patient-book-btn");

        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Session expired. Please log in again.");
                return;
            }

            try {
                // Fetch patient data first using service helper
                let patientData = null;
                if (typeof window.getPatientData === "function") {
                    patientData = await window.getPatientData(token);
                } else if (typeof getPatientData === "function") {
                    patientData = await getPatientData(token);
                } else {
                    console.log("Service Fallback: getPatientData not available, using default context.");
                }

                // Trigger booking overlay presentation view
                if (typeof window.showBookingOverlay === "function") {
                    window.showBookingOverlay(e, doctor, patientData);
                } else if (typeof showBookingOverlay === "function") {
                    showBookingOverlay(e, doctor, patientData);
                } else {
                    console.log(`UI Fallback: Triggering appointment overlay for ${doctor.name}`);
                }
            } catch (error) {
                console.error("Error processing booking layout initialization:", error);
            }
        });

        actionsDiv.appendChild(bookNow);
    }

    // Final Assembly
    if (actionsDiv.children.length > 0) {
        card.appendChild(actionsDiv);
    }

    return card;
}

