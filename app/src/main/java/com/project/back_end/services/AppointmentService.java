package com.project.back_end.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Domain models and data access layer repository mappings
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

/**
 * AppointmentService
 * 1. Annotated with @Service to mark this class as a Spring-managed business logic component bean.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    /**
     * 2. Constructor Injection for Dependencies
     * Injects required structural infrastructure beans, explicitly omitting any non-existent service references.
     */
    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              TokenService tokenService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * 4. Book Appointment Method
     * Saves a new appointment to the database with scheduled baseline states.
     * Annotated with @Transactional to ensure operation consistency.
     * 
     * @param appointment the new appointment payload entity
     * @return 1 for operational success, 0 for failure exceptions
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointment.setStatus(0); // 0 = Scheduled base status state
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 5. Update Appointment Method
     * Validates whether patient ownership matches, checks lock criteria status, and evaluates physician availability.
     * 
     * @param appointment the incoming modifications mapping payload
     * @param patientId the identifier verifying security ownership bounds
     * @return "SUCCESS" string token text or a descriptive error message narrative
     */
    @Transactional
    public String updateAppointment(Appointment appointment, Long patientId) {
        try {
            // Step 1: Verify targeted record exists inside relational storage
            Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
            if (existingOpt.isEmpty()) {
                return "Error: Targeted appointment record could not be found inside persistent state trackers.";
            }
            Appointment existing = existingOpt.get();

            // Step 2: Validate whether the patient ID matches ownership criteria properties
            if (existing.getPatient() == null || !existing.getPatient().getId().equals(patientId)) {
                return "Error: Security ownership token mismatch. Patient profile lacks modification authority.";
            }

            // Step 3: Check if the appointment state is actively scheduled (status = 0)
            if (existing.getStatus() != 0) {
                return "Error: Modification constraint triggered. Completed or cancelled schedules are locked permanently.";
            }

            // Step 4: Ensure target physician is unengaged during the proposed timeframe
            LocalDateTime proposedTime = appointment.getAppointmentTime() != null ? appointment.getAppointmentTime() : existing.getAppointmentTime();
            Long targetDoctorId = appointment.getDoctor() != null ? appointment.getDoctor().getId() : existing.getDoctor().getId();

            List<Appointment> potentialCollisions = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    targetDoctorId,
                    proposedTime.minusMinutes(1),
                    proposedTime.plusMinutes(1)
            );

            // Filter out self-reference checks to avoid false positive alarms on basic hour edits
            boolean hourIsAlreadyAllocated = potentialCollisions.stream()
                    .anyMatch(app -> !app.getId().equals(appointment.getId()));

            if (hourIsAlreadyAllocated) {
                return "Error: Time allocation conflict. The requested physician is engaged during this calendar window.";
            }

            // Step 5: Map approved parameters to persistent memory context mappings
            existing.setAppointmentTime(proposedTime);
            if (appointment.getDurationMinutes() != null && appointment.getDurationMinutes() > 0) {
                existing.setDurationMinutes(appointment.getDurationMinutes());
            }
            if (appointment.getReasonForVisit() != null) {
                existing.setReasonForVisit(appointment.getReasonForVisit());
            }

            appointmentRepository.save(existing);
            return "SUCCESS";
        } catch (Exception e) {
            return "Error: An unhandled exception forced a transaction rollback: " + e.getMessage();
        }
    }

    /**
     * 6. Cancel Appointment Method
     * Deletes an appointment from the storage system after confirming ownership alignment records.
     * 
     * @param appointmentId the target identity key to remove
     * @param patientId the validation constraint property verifying authority
     * @return true if cleanly processed, false otherwise
     */
    @Transactional
    public boolean cancelAppointment(Long appointmentId, Long patientId) {
        try {
            Optional<Appointment> appOpt = appointmentRepository.findById(appointmentId);
            if (appOpt.isEmpty()) {
                return false;
            }
            Appointment appointment = appOpt.get();
            
            if (appointment.getPatient() == null || !appointment.getPatient().getId().equals(patientId)) {
                return false; // Security ownership validation check failed
            }

            appointmentRepository.deleteById(appointmentId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 7. Get Appointments Method
     * Retrieves daily calendars for a doctor, applying optional case-insensitive patient search strings.
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        if (patientName == null || patientName.trim().isEmpty()) {
            return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, patientName, start, end
            );
        }
    }

    /**
     * 8. Change Status Method
     * Updates an appointment's status field atomically within a transaction bounds environment.
     */
    @Transactional
    public void changeStatus(Long appointmentId, int status) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
}
