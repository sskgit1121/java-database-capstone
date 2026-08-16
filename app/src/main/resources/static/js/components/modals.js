/**
 * Modal Component
 *
 * File:
 * static/js/components/modals.js
 */


/**
 * Close the currently open modal.
 */
function closeModal() {

    const modal = document.getElementById("modal");

    if (!modal) {
        return;
    }

    modal.style.display = "none";
    modal.setAttribute("aria-hidden", "true");

    const modalBody = document.getElementById("modal-body");

    if (modalBody) {
        modalBody.innerHTML = "";
    }
}


/**
 * Open a specific modal.
 *
 * @param {string} type
 */
export function openModal(type) {

    const modal = document.getElementById("modal");
    const modalBody = document.getElementById("modal-body");

    if (!modal || !modalBody) {

        console.error("Modal DOM elements were not found.");

        return;
    }


    let modalContent = "";


    /*
     * =========================================================
     * ADMIN LOGIN
     * =========================================================
     */
    if (type === "adminLogin") {

        modalContent = `
            <h2 id="modal-title">Admin Login</h2>

            <input
                type="text"
                id="adminUsername"
                name="username"
                placeholder="Username"
                class="input-field"
                autocomplete="username"
            >

            <input
                type="password"
                id="adminPassword"
                name="password"
                placeholder="Password"
                class="input-field"
                autocomplete="current-password"
            >

            <button
                type="button"
                class="dashboard-btn"
                id="adminLoginBtn"
            >
                Login
            </button>
        `;

    }


    /*
     * =========================================================
     * DOCTOR LOGIN
     * =========================================================
     */
    else if (type === "doctorLogin") {

        modalContent = `
            <h2 id="modal-title">Doctor Login</h2>

            <input
                type="email"
                id="doctorEmail"
                name="email"
                placeholder="Email"
                class="input-field"
                autocomplete="username"
            >

            <input
                type="password"
                id="doctorPassword"
                name="password"
                placeholder="Password"
                class="input-field"
                autocomplete="current-password"
            >

            <button
                type="button"
                class="dashboard-btn"
                id="doctorLoginBtn"
            >
                Login
            </button>
        `;

    }


    /*
     * =========================================================
     * PATIENT LOGIN
     * =========================================================
     */
    else if (type === "patientLogin") {

        modalContent = `
            <h2 id="modal-title">Patient Login</h2>

            <input
                type="email"
                id="patientEmail"
                name="email"
                placeholder="Email"
                class="input-field"
                autocomplete="username"
            >

            <input
                type="password"
                id="patientPassword"
                name="password"
                placeholder="Password"
                class="input-field"
                autocomplete="current-password"
            >

            <button
                type="button"
                class="dashboard-btn"
                id="patientLoginBtn"
            >
                Login
            </button>
        `;

    }


    /*
     * =========================================================
     * PATIENT SIGNUP
     * =========================================================
     */
    else if (type === "patientSignup") {

        modalContent = `
            <h2 id="modal-title">Patient Signup</h2>

            <input
                type="text"
                id="patientName"
                name="name"
                placeholder="Full Name"
                class="input-field"
                autocomplete="name"
            >

            <input
                type="email"
                id="patientEmail"
                name="email"
                placeholder="Email"
                class="input-field"
                autocomplete="email"
            >

            <input
                type="password"
                id="patientPassword"
                name="password"
                placeholder="Password"
                class="input-field"
                autocomplete="new-password"
            >

            <input
                type="text"
                id="patientPhone"
                name="phone"
                placeholder="Phone"
                class="input-field"
                autocomplete="tel"
            >

            <input
                type="text"
                id="patientAddress"
                name="address"
                placeholder="Address"
                class="input-field"
                autocomplete="street-address"
            >

            <input
                type="date"
                id="patientDateOfBirth"
                name="dateOfBirth"
                class="input-field"
            >

            <input
                type="text"
                id="patientInsuranceProvider"
                name="insuranceProvider"
                placeholder="Insurance Provider"
                class="input-field"
            >

            <button
                type="button"
                class="dashboard-btn"
                id="patientSignupBtn"
            >
                Signup
            </button>
        `;

    }


    /*
     * =========================================================
     * ADD DOCTOR
     * =========================================================
     */
    else if (type === "addDoctor") {

        modalContent = `
            <h2 id="modal-title">Add Doctor</h2>

            <input
                type="text"
                id="doctorName"
                placeholder="Doctor Name"
                class="input-field"
            >

            <select
                id="specialization"
                class="input-field select-dropdown"
            >
                <option value="">Specialization</option>
                <option value="cardiologist">Cardiologist</option>
                <option value="dermatologist">Dermatologist</option>
                <option value="neurologist">Neurologist</option>
                <option value="pediatrician">Pediatrician</option>
                <option value="orthopedic">Orthopedic</option>
                <option value="gynecologist">Gynecologist</option>
                <option value="psychiatrist">Psychiatrist</option>
                <option value="dentist">Dentist</option>
                <option value="ophthalmologist">Ophthalmologist</option>
                <option value="ent">ENT Specialist</option>
                <option value="urologist">Urologist</option>
                <option value="oncologist">Oncologist</option>
                <option value="gastroenterologist">Gastroenterologist</option>
                <option value="general">General Physician</option>
            </select>

            <input
                type="email"
                id="doctorEmail"
                placeholder="Email"
                class="input-field"
            >

            <input
                type="password"
                id="doctorPassword"
                placeholder="Password"
                class="input-field"
            >

            <input
                type="text"
                id="doctorPhone"
                placeholder="Mobile No."
                class="input-field"
            >

            <div class="availability-container">

                <label class="availabilityLabel">
                    Select Availability:
                </label>

                <div class="checkbox-group">

                    <label>
                        <input
                            type="checkbox"
                            name="availability"
                            value="09:00-10:00"
                        >
                        9:00 AM - 10:00 AM
                    </label>

                    <label>
                        <input
                            type="checkbox"
                            name="availability"
                            value="10:00-11:00"
                        >
                        10:00 AM - 11:00 AM
                    </label>

                    <label>
                        <input
                            type="checkbox"
                            name="availability"
                            value="11:00-12:00"
                        >
                        11:00 AM - 12:00 PM
                    </label>

                    <label>
                        <input
                            type="checkbox"
                            name="availability"
                            value="12:00-13:00"
                        >
                        12:00 PM - 1:00 PM
                    </label>

                </div>

            </div>

            <button
                type="button"
                class="dashboard-btn"
                id="saveDoctorBtn"
            >
                Save
            </button>
        `;

    }


    /*
     * Unknown modal type.
     */
    else {

        console.error(`Unknown modal type: ${type}`);

        return;
    }


    /*
     * Inject modal content.
     */
    modalBody.innerHTML = modalContent;


    /*
     * Display modal.
     */
    modal.style.display = "block";
    modal.setAttribute("aria-hidden", "false");


    /*
     * =========================================================
     * CLOSE BUTTON
     * =========================================================
     */
    const closeButton = document.getElementById("closeModal");

    if (closeButton) {

        closeButton.onclick = closeModal;
    }


    /*
     * =========================================================
     * ADMIN LOGIN
     * =========================================================
     */
    const adminLoginBtn =
        document.getElementById("adminLoginBtn");

    if (adminLoginBtn) {

        adminLoginBtn.addEventListener("click", () => {

            if (typeof window.adminLoginHandler === "function") {

                window.adminLoginHandler();

            } else {

                console.error(
                    "adminLoginHandler is not available."
                );
            }
        });
    }


    /*
     * =========================================================
     * DOCTOR LOGIN
     * =========================================================
     */
    const doctorLoginBtn =
        document.getElementById("doctorLoginBtn");

    if (doctorLoginBtn) {

        doctorLoginBtn.addEventListener("click", () => {

            if (typeof window.doctorLoginHandler === "function") {

                window.doctorLoginHandler();

            } else {

                console.error(
                    "doctorLoginHandler is not available."
                );
            }
        });
    }


    /*
     * =========================================================
     * PATIENT LOGIN
     * =========================================================
     */
    const patientLoginBtn =
        document.getElementById("patientLoginBtn");

    if (patientLoginBtn) {

        patientLoginBtn.addEventListener("click", () => {

            if (typeof window.patientLoginHandler === "function") {

                window.patientLoginHandler();

            } else {

                console.error(
                    "patientLoginHandler is not available."
                );
            }
        });
    }


    /*
     * =========================================================
     * PATIENT SIGNUP
     * =========================================================
     */
    const patientSignupBtn =
        document.getElementById("patientSignupBtn");

    if (patientSignupBtn) {

        patientSignupBtn.addEventListener("click", () => {

            if (typeof window.patientSignupHandler === "function") {

                window.patientSignupHandler();

            } else {

                console.error(
                    "patientSignupHandler is not available."
                );
            }
        });
    }


    /*
     * =========================================================
     * ADD DOCTOR
     * =========================================================
     */
    const saveDoctorBtn =
        document.getElementById("saveDoctorBtn");

    if (saveDoctorBtn) {

        saveDoctorBtn.addEventListener("click", () => {

            if (typeof window.adminAddDoctor === "function") {

                window.adminAddDoctor();

            } else {

                console.error(
                    "adminAddDoctor is not available."
                );
            }
        });
    }
}


/*
 * Expose close function if another component needs it.
 */
window.closeModal = closeModal;