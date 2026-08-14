// patientServices
/**
 * Patient Services Module
 * Centralizes all API communication related to patient data, accounts, and appointment pipelines.
 */

/**
 * Patient Account and Management Pipeline Service Module
 * File: app/src/main/resources/static/js/services/patientServices.js
 * Separates data fetching logic from interface components for cleaner design and reuse.
 */

// Import the base URL from your central application configuration file
import { API_BASE_URL } from "../config/config.js";

// Define a constant representing the base path for all patient-related server requests
const PATIENT_API = API_BASE_URL + '/patient';

/**
 * Submit consumer payload data to finalize account creation
 * @param {Object} data - Collected parameter details (name, email, password, profile info).
 * @returns {Promise<Object>} Unified response object defining success profile values.
 */
export async function patientSignup(data) {
    try {
        // Step 1: Issue a POST call down to the assigned signup endpoint path context
        const response = await fetch(`${PATIENT_API}/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        // Step 2: Extract text metrics out of response JSON strings
        const result = await response.json();

        // Step 3: Build structured return objects separating error indicators from logs
        if (response.ok) {
            return { success: true, message: result.message || "Patient signup executed successfully." };
        } else {
            return { success: false, message: result.message || "System declined registration payload." };
        }
    } catch (error) {
        // Step 4: Handle network communication interruptions gracefully via structured clean falls
        console.error("Error encountered in patientSignup service function:", error);
        return { success: false, message: "Unable to establish network handshake protocols during account submission." };
    }
}

/**
 * Handle verification processing requests for consumer access
 * @param {Object} data - Input credentials object fields (email, password).
 * @returns {Promise<Response>} The raw Fetch Response stream context to allow advanced header checking.
 */
export async function patientLogin(data) {
    try {
        // Step 1: Execute POST processing pipeline requests directly targeting login paths
        const response = await fetch(`${PATIENT_API}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        // Step 2: Pass the complete response handle cleanly back to consumer dashboard loops
        return response;
    } catch (error) {
        // Step 3: Shield logic using try-catch blocks to prevent thread locking exceptions
        console.error("Error encountered in patientLogin service function:", error);
        throw error;
    }
}

/**
 * Download specific metadata attributes for an actively logged-in system user
 * @param {string} token - Authorization bearer security key value.
 * @returns {Promise<Object|null>} Returns the patient details data map, or null value indicators on failure.
 */
export async function getPatientData(token) {
    try {
        // Step 1: Request patient account specifics passing security tokens securely inside standard headers
        const response = await fetch(`${PATIENT_API}/profile`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Step 2: Evaluate connection state parameters
        if (!response.ok) {
            throw new Error(`Profile metrics download failed with code: ${response.status}`);
        }

        // Step 3: Pass back verified structural entity payload maps
        return await response.json();
    } catch (error) {
        // Step 4: Return null clean overrides if exceptions occur
        console.error("Error encountered in getPatientData service function:", error);
        return null;
    }
}

/**
 * Shared dynamic retrieval service sourcing scheduled events across multi-role layouts
 * @param {string|number} id - Subject target record identifier indicator context mapping.
 * @param {string} token - Client authorization bearer state entry.
 * @param {string} user - Runtime request origin indicator profile flag ("patient" or "doctor").
 * @returns {Promise<Array|null>} Array compilation block tracking planned visits or null maps.
 */
export async function getPatientAppointments(id, token, user) {
    try {
        // Step 1: Construct a polymorphic dynamic API path supporting both dashboards with role-based behavior
        const url = `${PATIENT_API}/appointments?id=${id}&role=${user}`;

        // Step 2: Execute an authenticated GET data fetch call
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Step 3: Evaluate tracking response codes
        if (!response.ok) {
            throw new Error(`Appointment records fetching failed: ${response.statusText}`);
        }

        // Step 4: Extract and return structural appointment arrays mapping dashboard columns
        return await response.json();
    } catch (error) {
        // Step 5: Log processing context issues and resolve to empty fallbacks
        console.error("Error encountered in getPatientAppointments service function:", error);
        return null;
    }
}

/**
 * Filter operational appointments records using runtime parameters
 * @param {string} condition - Processing visit status milestone selection ("pending" or "consulted").
 * @param {string} name - Patient query search term value string.
 * @param {string} token - Client authorization security vector validation field.
 * @returns {Promise<Array>} A valid records subset selection list matching values or clean arrays.
 */
export async function filterAppointments(condition, name, token) {
    try {
        // Step 1: Initialize query tracking instances passing fallbacks appropriately
        const filterStatus = condition ? condition : "";
        const filterName = name ? name : "";

        // Step 2: Construct the targeted filter endpoint path URL string securely
        const url = `${PATIENT_API}/appointments/filter/${filterStatus}/${filterName}`;

        // Step 3: Dispatch the authenticated server data call request
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Step 4: Validate server execution states
        if (!response.ok) {
            throw new Error(`Server tracking response error mapping dynamic filters: ${response.statusText}`);
        }

        // Step 5: Deliver sorted data array collections down to view layouts
        return await response.json();
    } catch (error) {
        // Step 6: Log internal tracing points for faster issue tracking
        console.error("Error encountered in filterAppointments service function:", error);
        
        // Step 7: Alert users explicitly when experiencing catastrophic server or connection crashes
        alert("Unexpected communication error processing appointment grid variables.");
        return [];
    }
}
