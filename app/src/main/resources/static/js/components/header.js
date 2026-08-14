/*
  Step-by-Step Explanation of Header Section Rendering

  This code dynamically renders the header section of the page based on the user's role, session status, and available actions (such as login, logout, or role-switching).

  1. Define the `renderHeader` Function

     * The `renderHeader` function is responsible for rendering the entire header based on the user's session, role, and whether they are logged in.

  2. Select the Header Div

     * The `headerDiv` variable retrieves the HTML element with the ID `header`, where the header content will be inserted.
       ```javascript
       const headerDiv = document.getElementById("header");
       ```

  3. Check if the Current Page is the Root Page

     * The `window.location.pathname` is checked to see if the current page is the root (`/`). If true, the user's session data (role) is removed from `localStorage`, and the header is rendered without any user-specific elements (just the logo and site title).
       ```javascript
       if (window.location.pathname.endsWith("/")) {
         localStorage.removeItem("userRole");
         headerDiv.innerHTML = `
           <header class="header">
             <div class="logo-section">
               <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
               <span class="logo-title">Hospital CMS</span>
             </div>
           </header>`;
         return;
       }
       ```

  4. Retrieve the User's Role and Token from LocalStorage

     * The `role` (user role like admin, patient, doctor) and `token` (authentication token) are retrieved from `localStorage` to determine the user's current session.
       ```javascript
       const role = localStorage.getItem("userRole");
       const token = localStorage.getItem("token");
       ```

  5. Initialize Header Content

     * The `headerContent` variable is initialized with basic header HTML (logo section), to which additional elements will be added based on the user's role.
       ```javascript
       let headerContent = `<header class="header">
         <div class="logo-section">
           <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
           <span class="logo-title">Hospital CMS</span>
         </div>
         <nav>`;
       ```

  6. Handle Session Expiry or Invalid Login

     * If a user with a role like `loggedPatient`, `admin`, or `doctor` does not have a valid `token`, the session is considered expired or invalid. The user is logged out, and a message is shown.
       ```javascript
       if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
         localStorage.removeItem("userRole");
         alert("Session expired or invalid login. Please log in again.");
         window.location.href = "/";   or a specific login page
         return;
       }
       ```

  7. Add Role-Specific Header Content

     * Depending on the user's role, different actions or buttons are rendered in the header:
       - **Admin**: Can add a doctor and log out.
       - **Doctor**: Has a home button and log out.
       - **Patient**: Shows login and signup buttons.
       - **LoggedPatient**: Has home, appointments, and logout options.
       ```javascript
       else if (role === "admin") {
         headerContent += `
           <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "doctor") {
         headerContent += `
           <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "patient") {
         headerContent += `
           <button id="patientLogin" class="adminBtn">Login</button>
           <button id="patientSignup" class="adminBtn">Sign Up</button>`;
       } else if (role === "loggedPatient") {
         headerContent += `
           <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <a href="#" onclick="logoutPatient()">Logout</a>`;
       }
       ```



  9. Close the Header Section



  10. Render the Header Content

     * Insert the dynamically generated `headerContent` into the `headerDiv` element.
       ```javascript
       headerDiv.innerHTML = headerContent;
       ```

  11. Attach Event Listeners to Header Buttons

     * Call `attachHeaderButtonListeners` to add event listeners to any dynamically created buttons in the header (e.g., login, logout, home).
       ```javascript
       attachHeaderButtonListeners();
       ```


  ### Helper Functions

  13. **attachHeaderButtonListeners**: Adds event listeners to login buttons for "Doctor" and "Admin" roles. If clicked, it opens the respective login modal.

  14. **logout**: Removes user session data and redirects the user to the root page.

  15. **logoutPatient**: Removes the patient's session token and redirects to the patient dashboard.

  16. **Render the Header**: Finally, the `renderHeader()` function is called to initialize the header rendering process when the page loads.
*/
/**
 * Header.js
 * Reusable component managing the global system navbar header frame.
 * Controls context links dynamically, checks active authentication state,
 * and attaches explicit event listeners to newly instantiated elements.
 */

// Automatically trigger component generation when the DOM pipeline loads completely
document.addEventListener("DOMContentLoaded", () => {
    renderHeader();
});

/**
 * Compiles structural state checks and injects context-appropriate HTML into the #header wrapper.
 */
function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) {
        console.error("Target node framework placeholder (#header) was not located inside the current page DOM context.");
        return;
    }

    // Flush active context parameters if visiting the application landing root index page
    if (window.location.pathname === "/" || window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Active security guardrail: session expiration token checking mechanism
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Initial baseline structural identity shell template string
    let headerContent = `
        <header class="global-navbar" style="display: flex; justify-content: space-between; align-items: center; padding: 1rem 2rem; background-color: #ffffff; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
            <div class="brand-logo" style="font-size: 1.25rem; font-weight: 700; color: #0284c7; cursor: pointer;" onclick="window.location.href='/'">
                🏥 CarePulse Clinic
            </div>
            <nav class="nav-controls" style="display: flex; align-items: center; gap: 1.25rem;">
    `;

    // Populate variable conditional buttons mapping based entirely on verified client profile flags
    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn">Add Doctor</button>
            <a href="#" id="logoutLink" class="nav-link" style="text-decoration: none; color: #475569; font-weight: 500;">Logout</a>
        `;
    } else if (role === "doctor") {
        headerContent += `
            <button id="homeBtn" class="nav-btn" style="padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer;">Home</button>
            <a href="#" id="logoutLink" class="nav-link" style="text-decoration: none; color: #475569; font-weight: 500;">Logout</a>
        `;
    } else if (role === "loggedPatient") {
        headerContent += `
            <button id="homeBtn" class="nav-btn" style="padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer;">Home</button>
            <button id="appointmentsBtn" class="nav-btn" style="padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer;">Appointments</button>
            <a href="#" id="logoutPatientLink" class="nav-link" style="text-decoration: none; color: #475569; font-weight: 500;">Logout</a>
        `;
    } else {
        // Default unauthenticated baseline state (role === 'patient' or guest visitor)
        headerContent += `
            <button id="loginBtn" class="nav-btn secondary-btn" style="padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer;">Login</button>
            <button id="signupBtn" class="nav-btn primary-btn" style="padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer;">Sign Up</button>
        `;
    }

    // Terminate structural layout controls wrappers
    headerContent += `
            </nav>
        </header>
    `;

    // Inject compiled contextual markup template configuration block into target node
    headerDiv.innerHTML = headerContent;

    // Attach production event listeners handler map to runtime DOM nodes safely
    attachHeaderButtonListeners();
}

/**
 * Searches the updated viewport target identifiers to hook event loop triggers systematically.
 */
function attachHeaderButtonListeners() {
    // Admin modal activation bindings
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("addDoctor");
            } else {
                console.warn("Global operational registration window anchor framework 'openModal()' is not detected.");
            }
        });
    }

    // Portal home view routing mechanics
    const homeBtn = document.getElementById("homeBtn");
    if (homeBtn) {
        homeBtn.addEventListener("click", () => {
            const currentRole = localStorage.getItem("userRole");
            if (currentRole === "doctor") {
                window.location.href = "/templates/doctor/doctorDashboard.html";
            } else if (currentRole === "loggedPatient") {
                window.location.href = "/pages/patientDashboard.html";
            }
        });
    }

    // Patient booking management list navigation pipeline
    const appointmentsBtn = document.getElementById("appointmentsBtn");
    if (appointmentsBtn) {
        appointmentsBtn.addEventListener("click", () => {
            window.location.href = "/pages/appointments.html";
        });
    }

    // Secure cross-role terminal system logout trigger
    const logoutLink = document.getElementById("logoutLink");
    if (logoutLink) {
        logoutLink.addEventListener("click", (event) => {
            event.preventDefault();
            logout();
        });
    }

    // Patient specific granular tracking clean toggle channel
    const logoutPatientLink = document.getElementById("logoutPatientLink");
    if (logoutPatientLink) {
        logoutPatientLink.addEventListener("click", (event) => {
            event.preventDefault();
            logoutPatient();
        });
    }

    // Public authentication interface listeners mapping handlers
    const loginBtn = document.getElementById("loginBtn");
    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            if (typeof openModal === "function") openModal("login");
        });
    }

    const signupBtn = document.getElementById("signupBtn");
    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            if (typeof openModal === "function") openModal("signup");
        });
    }
}

/**
 * Flushes active authorization credentials and transfers control frames back to landing pages.
 */
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

/**
 * Truncates patient session profile keys but maintains basic guest status parameters configurations.
 */
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}
