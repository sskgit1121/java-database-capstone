/**
 * Authentication Service
 *
 * File:
 * static/js/services/index.js
 *
 * Responsibilities:
 *  - Open login/signup modals
 *  - Authenticate Admin
 *  - Authenticate Doctor
 *  - Authenticate Patient
 *  - Register Patient
 *  - Store authentication token
 *  - Redirect to dashboard
 */


import { openModal } from "../components/modals.js";

import { API_BASE_URL } from "../config/config.js";


/*
 * =========================================================
 * API ENDPOINTS
 * =========================================================
 */

const ADMIN_API =
    `${API_BASE_URL}/admin/login`;

const DOCTOR_API =
    `${API_BASE_URL}/doctor/login`;

const PATIENT_LOGIN_API =
    `${API_BASE_URL}/patient/login`;

const PATIENT_SIGNUP_API =
    `${API_BASE_URL}/patient/signup`;


/*
 * =========================================================
 * DOM EVENT REGISTRATION
 * =========================================================
 */

document.addEventListener("DOMContentLoaded", () => {

    /*
     * Admin role button
     */
    const adminButton =
        document.getElementById("role-admin");

    if (adminButton) {

        adminButton.addEventListener("click", () => {

            openModal("adminLogin");
        });
    }


    /*
     * Doctor role button
     */
    const doctorButton =
        document.getElementById("role-doctor");

    if (doctorButton) {

        doctorButton.addEventListener("click", () => {

            openModal("doctorLogin");
        });
    }


    /*
     * Patient role button
     */
    const patientButton =
        document.getElementById("role-patient");

    if (patientButton) {

        patientButton.addEventListener("click", () => {

            openModal("patientLogin");
        });
    }

});


/*
 * =========================================================
 * HEADER EVENT DELEGATION
 * =========================================================
 *
 * Header is dynamically injected.
 *
 * Therefore we cannot depend on the buttons existing when
 * DOMContentLoaded executes.
 */

document.addEventListener("click", (event) => {

    const target = event.target;

    if (!(target instanceof Element)) {
        return;
    }


    const button = target.closest("#header button");

    if (!button) {
        return;
    }


    const buttonText =
        button.textContent
            .trim()
            .toLowerCase();


    /*
     * Header Login
     */
    if (buttonText.includes("login")) {

        openModal("patientLogin");

        return;
    }


    /*
     * Header Signup
     */
    if (buttonText.includes("signup")) {

        openModal("patientSignup");
    }

});


/*
 * =========================================================
 * ADMIN LOGIN
 * =========================================================
 */

window.adminLoginHandler = async function () {

    try {

        const usernameElement =
            document.getElementById("adminUsername");

        const passwordElement =
            document.getElementById("adminPassword");


        if (!usernameElement || !passwordElement) {

            console.error(
                "Admin login fields are missing."
            );

            alert(
                "Admin login form fields could not be found."
            );

            return;
        }


        const username =
            usernameElement.value.trim();

        const password =
            passwordElement.value;


        /*
         * Basic client-side validation.
         */
        if (!username || !password) {

            alert(
                "Username and password are required."
            );

            return;
        }


        /*
         * Payload.
         *
         * Keeping both username and email because the existing
         * backend contract supplied in the source sends both.
         */
        const payload = {

            username: username,

            email: username,

            password: password
        };


        const response =
            await fetch(ADMIN_API, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(payload)
            });


        if (!response.ok) {

            alert("Invalid admin credentials.");

            return;
        }


        const data =
            await response.json();


        if (!data || !data.token) {

            console.error(
                "Admin login response did not contain a token.",
                data
            );

            alert(
                "Login failed: authentication token was not returned."
            );

            return;
        }


        /*
         * Save authentication token.
         */
        localStorage.setItem(
            "token",
            data.token
        );


        /*
         * Save role and redirect.
         */
        if (typeof window.selectRole === "function") {

            window.selectRole("admin");

        } else {

            localStorage.setItem(
                "role",
                "admin"
            );

            window.location.href =
                `/adminDashboard/${encodeURIComponent(data.token)}`;
        }

    } catch (error) {

        console.error(
            "Admin Login Error:",
            error
        );

        alert(
            "Unable to connect to the server."
        );
    }
};


/*
 * =========================================================
 * DOCTOR LOGIN
 * =========================================================
 */

window.doctorLoginHandler = async function () {

    try {

        const emailElement =
            document.getElementById("doctorEmail");

        const passwordElement =
            document.getElementById("doctorPassword");


        if (!emailElement || !passwordElement) {

            console.error(
                "Doctor login fields are missing."
            );

            alert(
                "Doctor login form fields could not be found."
            );

            return;
        }


        const email =
            emailElement.value.trim();

        const password =
            passwordElement.value;


        if (!email || !password) {

            alert(
                "Email and password are required."
            );

            return;
        }


        const payload = {

            email: email,

            password: password
        };


        const response =
            await fetch(DOCTOR_API, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(payload)
            });


        if (!response.ok) {

            alert(
                "Invalid doctor credentials."
            );

            return;
        }


        const data =
            await response.json();


        if (!data || !data.token) {

            console.error(
                "Doctor login response did not contain a token.",
                data
            );

            alert(
                "Login failed: authentication token was not returned."
            );

            return;
        }


        localStorage.setItem(
            "token",
            data.token
        );


        if (typeof window.selectRole === "function") {

            window.selectRole("doctor");

        } else {

            localStorage.setItem(
                "role",
                "doctor"
            );

            window.location.href =
                `/doctorDashboard/${encodeURIComponent(data.token)}`;
        }

    } catch (error) {

        console.error(
            "Doctor Login Error:",
            error
        );

        alert(
            "Unable to connect to the server."
        );
    }
};


/*
 * =========================================================
 * PATIENT LOGIN
 * =========================================================
 */

window.loginPatient =
window.patientLoginHandler = async function () {

    try {

        const emailElement =
            document.getElementById("patientEmail");

        const passwordElement =
            document.getElementById("patientPassword");


        if (!emailElement || !passwordElement) {

            console.error(
                "Patient login fields are missing."
            );

            alert(
                "Patient login form fields could not be found."
            );

            return;
        }


        const email =
            emailElement.value.trim();

        const password =
            passwordElement.value;


        if (!email || !password) {

            alert(
                "Email and password are required."
            );

            return;
        }


        const payload = {

            email: email,

            password: password
        };


        const response =
            await fetch(PATIENT_LOGIN_API, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(payload)
            });


        if (!response.ok) {

            alert(
                "Invalid patient credentials."
            );

            return;
        }


        const data =
            await response.json();


        if (!data || !data.token) {

            console.error(
                "Patient login response did not contain a token.",
                data
            );

            alert(
                "Login failed: authentication token was not returned."
            );

            return;
        }


        localStorage.setItem(
            "token",
            data.token
        );


        if (typeof window.selectRole === "function") {

            window.selectRole("patient");

        } else {

            localStorage.setItem(
                "role",
                "patient"
            );

            window.location.href =
                `/patientDashboard/${encodeURIComponent(data.token)}`;
        }

    } catch (error) {

        console.error(
            "Patient Login Error:",
            error
        );

        alert(
            "Unable to connect to the server."
        );
    }
};


/*
 * =========================================================
 * PATIENT SIGNUP
 * =========================================================
 */

window.signupPatient =
window.patientSignupHandler = async function () {

    try {

        const nameElement =
            document.getElementById("patientName");

        const emailElement =
            document.getElementById("patientEmail");

        const passwordElement =
            document.getElementById("patientPassword");

        const phoneElement =
            document.getElementById("patientPhone");

        const addressElement =
            document.getElementById("patientAddress");

        const dateOfBirthElement =
            document.getElementById("patientDateOfBirth");

        const insuranceProviderElement =
            document.getElementById(
                "patientInsuranceProvider"
            );


        if (
            !nameElement ||
            !emailElement ||
            !passwordElement ||
            !phoneElement ||
            !addressElement
        ) {

            console.error(
                "Patient registration fields are missing."
            );

            alert(
                "Registration form fields could not be found."
            );

            return;
        }


        const name =
            nameElement.value.trim();

        const email =
            emailElement.value.trim();

        const password =
            passwordElement.value;

        const phone =
            phoneElement.value.trim();

        const address =
            addressElement.value.trim();


        /*
         * Validate required fields.
         */
        if (
            !name ||
            !email ||
            !password ||
            !phone ||
            !address
        ) {

            alert(
                "Please fill in all required registration fields."
            );

            return;
        }


        /*
         * Optional fields.
         */
        const dateOfBirth =
            dateOfBirthElement
                ? dateOfBirthElement.value
                : null;

        const insuranceProvider =
            insuranceProviderElement
                ? insuranceProviderElement.value.trim()
                : "";


        const payload = {

            name: name,

            email: email,

            password: password,

            phone: phone,

            address: address,

            dateOfBirth: dateOfBirth,

            insuranceProvider:
                insuranceProvider || "None"
        };


        const response =
            await fetch(PATIENT_SIGNUP_API, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(payload)
            });


        if (!response.ok) {

            let message =
                "Registration failed. Please check your details.";


            /*
             * Try to read backend error response.
             */
            try {

                const errorData =
                    await response.json();

                if (errorData.message) {

                    message =
                        errorData.message;
                }

            } catch (_) {

                /*
                 * Backend did not return JSON.
                 */
            }


            alert(message);

            return;
        }


        alert(
            "Registration successful! Please log in."
        );


        /*
         * Open login modal after successful registration.
         */
        openModal("patientLogin");

    } catch (error) {

        console.error(
            "Patient Signup Error:",
            error
        );

        alert(
            "Unable to connect to the server."
        );
    }
};