package com.project.back_end.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

// Domain models and data access layer repository mappings
import com.project.back_end.models.Patient;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.AppointmentRepository;

/**
 * PatientService
 * Marked with @Service to register this class as a Spring-managed business logic component.
 */
@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    /**
     * Constructor Injection for Dependencies
     * Injects the required repository and security infrastructure components for proper dependency management.
     */
    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * createPatient Method
     * Saves a new patient profile into the relational database.
     * 
     * @param patient the incoming patient profile payload entity
     * @return 1 for successful persistence execution, 0 if an exception occurs
     */
    @Transactional
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            logger.error("Error encountered while creating patient record: ", e);
            return 0;
        }
    }

    /**
     * getPatientAppointment Method
     * Retrieves all appointments scheduled for a specific patient and maps them into safe DTO objects.
     * Marked with @Transactional to maintain read consistency over transactional bounds.
     * 
     * @param patientId the identifier of the target patient record
     * @return a list containing mapped AppointmentDTO objects
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            
            if (appointments == null || appointments.isEmpty()) {
                return Collections.emptyList();
            }

            return appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error encountered while retrieving appointment history for patient ID {}: ", patientId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Private helper method to securely map an Appointment entity to an AppointmentDTO.
     */
    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        
        if (appointment.getAppointmentTime() != null) {
            dto.setAppointmentTime(appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        
        dto.setStatus(appointment.getStatus());
        dto.setReasonForVisit(appointment.getReasonForVisit());

        // FIX: Replaced split name fields with the singular getName() call matching your actual Doctor model class definition
        if (appointment.getDoctor() != null) {
            dto.setDoctorName(appointment.getDoctor().getName());
        } else {
            dto.setDoctorName("Unknown Doctor");
        }

        return dto;
    }

    /**
     * AppointmentDTO
     * Data Transfer Object used to encapsulate structured appointment details safely for API consumers.
     */
    public static class AppointmentDTO {
        private Long id;
        private String doctorName;
        private String appointmentTime;
        private int status;
        private String reasonForVisit;

        public AppointmentDTO() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

        public String getAppointmentTime() { return appointmentTime; }
        public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }

        public String getReasonForVisit() { return reasonForVisit; }
        public void setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; }
    }
}
