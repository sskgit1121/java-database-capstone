/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/
/**
 * Doctor Services Module
 * Responsible for handling all API interactions related to doctor data.
 */

/**
 * Doctor Data API Interaction Service Module
 * File: app/src/main/resources/static/js/services/doctorServices.js
 * Centralizes all communication logic for doctor CRUD operations and filtering.
 */

// Import the API base URL from the central configuration file
import { API_BASE_URL } from "../config/config.js";

// Define a constant for the doctor-related base endpoint
const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Fetch all available doctors from the system
 * Used by Admin and Patient dashboards to display data cards.
 * @returns {Promise<Array>} Resolves to a list of doctor records or an empty array on failure.
 */
export async function getDoctors() {
    try {
        // Step 1: Send a GET request to the doctor endpoint base URL
        const response = await fetch(DOCTOR_API);

        // Step 2: Ensure the network response stream was successful
        if (!response.ok) {
            throw new Error(`Failed to fetch doctor records: ${response.statusText}`);
        }

        // Step 3: Extract and return the parsed list of doctors from the response JSON
        return await response.json();
    } catch (error) {
        // Step 4: Catch errors and log them to prevent crashing the UI layer
        console.error("Error encountered in getDoctors service function:", error);
        
        // Return an empty list if something goes wrong to avoid breaking the frontend mapping scripts
        return [];
    }
}

/**
 * Remove a doctor record securely from the backend infrastructure
 * @param {string|number} id - Unique identifier of the target doctor entity.
 * @param {string} token - Client authorization bearer token.
 * @returns {Promise<Object>} Structured outcome payload containing a success status flag and text message.
 */
export async function deleteDoctor(id, token) {
    try {
        // Step 1: Construct the full deletion endpoint URL using the unique ID as a path parameter
        const url = `${DOCTOR_API}/${id}`;

        // Step 2: Execute a secure DELETE request passing authorization context headers
        const response = await fetch(url, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Step 3: Parse the backend server response JSON payload
        const data = await response.json();

        // Step 4: Return a consistent structured response format back to dashboard handlers
        if (response.ok) {
            return { success: true, message: data.message || "Doctor record removed successfully." };
        } else {
            return { success: false, message: data.message || "Unauthorized or failed to process deletion request." };
        }
    } catch (error) {
        // Step 5: Catch and handle network or server exceptions cleanly
        console.error("Error encountered in deleteDoctor service function:", error);
        return { success: false, message: "An explicit communication error occurred during doctor removal operations." };
    }
}

/**
 * Save and persist a new doctor profile into the database system
 * @param {Object} doctor - Target payload parameters (name, email, availability, specialty).
 * @param {string} token - Admin authentication proof vector.
 * @returns {Promise<Object>} Structured object confirming save operations outcome.
 */
export async function saveDoctor(doctor, token) {
    try {
        // Step 1: Send a structured JSON POST request including authentication headers
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            // Step 2: Convert the javascript object properties into a valid minified JSON payload string
            body: JSON.stringify(doctor)
        });

        // Step 3: Wait for the network confirmation and parse results
        const data = await response.json();

        // Step 4: Route outcome metrics using uniform object schemas
        if (response.ok) {
            return { success: true, message: data.message || "New doctor successfully integrated." };
        } else {
            return { success: false, message: data.message || "Failed to commit doctor registration entries." };
        }
    } catch (error) {
        // Step 5: Log specific failures to assist tracking in developer sandboxes
        console.error("Error encountered in saveDoctor service function:", error);
        return { success: false, message: "Server connection failed while saving the doctor." };
    }
}

/**
 * Dynamic filtering engine evaluating practitioners against specific attributes
 * Maps parameters directly as path parameters according to your configuration rules.
 * @param {string} name - Explicit matching name sub-strings.
 * @param {string} time - Target workflow availability time shift.
 * @param {string} specialty - Target medical profile specialization filter field.
 * @returns {Promise<Array>} A list matching filter constraints or an empty fallback array list.
 */
export async function filterDoctors(name, time, specialty) {
    try {
        // Step 1: Format safe variables cleaning out empty inputs or default parameters
        const searchName = name ? name : "";
        const searchTime = time ? time : "";
        const searchSpecialty = specialty ? specialty : "";

        // Step 2: Build the clean target endpoint route string by passing the input filters as path parameters
        const url = `${DOCTOR_API}/filter/${searchName}/${searchTime}/${searchSpecialty}`;

        // Step 3: Dispatch the GET operation request
        const response = await fetch(url);

        // Step 4: Evaluate query resolution status checks
        if (!response.ok) {
            throw new Error(`Filter request responded with error state: ${response.status}`);
        }

        // Step 5: Return the filtered collection array data set back to UI rendering engines
        return await response.json();
    } catch (error) {
        // Step 6: Trigger alert notifications on system interruptions
        console.error("Error encountered in filterDoctors service function:", error);
        alert("System encountered an error executing dynamic search parameter filters.");
        return [];
    }
}
