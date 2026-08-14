// patientRows.js
export function createPatientRow(patient, appointmentId, doctorId) {
  const tr = document.createElement("tr");
  console.log("CreatePatientRow :: ", doctorId)
  tr.innerHTML = `
      <td class="patient-id">${patient.id}</td>
      <td>${patient.name}</td>
      <td>${patient.phone}</td>
      <td>${patient.email}</td>
      <td><img src="../assets/images/addPrescriptionIcon/addPrescription.png" alt="addPrescriptionIcon" class="prescription-btn" data-id="${patient.id}"></img></td>
    `;

  // Attach event listeners
  tr.querySelector(".patient-id").addEventListener("click", () => {
    window.location.href = `/pages/patientRecord.html?id=${patient.id}&doctorId=${doctorId}`;
  });

  tr.querySelector(".prescription-btn").addEventListener("click", () => {
    window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${patient.name}`;
  });

  return tr;
}

/**
 * UI Component for Patient List Row Structure
 * File: app/src/main/resources/static/js/components/patientRows.js
 */

/**
 * Dynamically builds a <tr> table row matching the dashboard structure
 * @param {Object} appointment - Contains specific appointment entity properties
 * @returns {HTMLTableRowElement} Constructed DOM row object element
 */
export function createPatientRow(appointment) {
    const row = document.createElement("tr");

    // Map properties with safe fallbacks to protect against broken values
    const id = appointment.patientId || appointment.id || "N/A";
    const name = appointment.patientName || "Unknown Patient";
    const phone = appointment.patientPhone || appointment.phone || "N/A";
    const email = appointment.patientEmail || appointment.email || "N/A";
    const status = appointment.status || "pending";

    // Build responsive data labels to enable easy cell-to-card conversions on mobile
    row.innerHTML = `
        <td data-label="Patient ID">${id}</td>
        <td data-label="Name">${name}</td>
        <td data-label="Phone No.">${phone}</td>
        <td data-label="Email">${email}</td>
        <td data-label="Prescription">
            <span class="status-tag tag-${status.toLowerCase()}">${status.toUpperCase()}</span>
            <button class="btn-action-view" data-id="${id}">Prescribe / View</button>
        </td>
    `;

    // Attach event listeners directly to the view action elements
    const viewButton = row.querySelector(".btn-action-view");
    if (viewButton && typeof window.showHistory === "function") {
        viewButton.addEventListener("click", () => window.showHistory(id));
    }

    return row;
}

