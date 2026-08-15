package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// Import the Appointment domain entity model
import com.project.back_end.models.Appointment;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

   // 1. Extend JpaRepository:
//    - The repository extends JpaRepository<Appointment, Long>, which gives it basic CRUD functionality.
//    - The methods such as save, delete, update, and find are inherited without the need for explicit implementation.
//    - JpaRepository also includes pagination and sorting features.

// Example: public interface AppointmentRepository extends JpaRepository<Appointment, Long> {}

// 2. Custom Query Methods:

//    - **findByDoctorIdAndAppointmentTimeBetween**:
//      - This method retrieves a list of appointments for a specific doctor within a given time range.
//      - The doctor’s available times are eagerly fetched to avoid lazy loading.
//      - Return type: List<Appointment>
//      - Parameters: Long doctorId, LocalDateTime start, LocalDateTime end
//      - It uses a LEFT JOIN to fetch the doctor’s available times along with the appointments.

//    - **findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween**:
//      - This method retrieves appointments for a specific doctor and patient name (ignoring case) within a given time range.
//      - It performs a LEFT JOIN to fetch both the doctor and patient details along with the appointment times.
//      - Return type: List<Appointment>
//      - Parameters: Long doctorId, String patientName, LocalDateTime start, LocalDateTime end

//    - **deleteAllByDoctorId**:
//      - This method deletes all appointments associated with a particular doctor.
//      - It is marked as @Modifying and @Transactional, which makes it a modification query, ensuring that the operation is executed within a transaction.
//      - Return type: void
//      - Parameters: Long doctorId

//    - **findByPatientId**:
//      - This method retrieves all appointments for a specific patient.
//      - Return type: List<Appointment>
//      - Parameters: Long patientId

//    - **findByPatient_IdAndStatusOrderByAppointmentTimeAsc**:
//      - This method retrieves all appointments for a specific patient with a given status, ordered by the appointment time.
//      - Return type: List<Appointment>
//      - Parameters: Long patientId, int status

//    - **filterByDoctorNameAndPatientId**:
//      - This method retrieves appointments based on a doctor’s name (using a LIKE query) and the patient’s ID.
//      - Return type: List<Appointment>
//      - Parameters: String doctorName, Long patientId

//    - **filterByDoctorNameAndPatientIdAndStatus**:
//      - This method retrieves appointments based on a doctor’s name (using a LIKE query), patient’s ID, and a specific appointment status.
//      - Return type: List<Appointment>
//      - Parameters: String doctorName, Long patientId, int status

//    - **updateStatus**:
//      - This method updates the status of a specific appointment based on its ID.
//      - Return type: void
//      - Parameters: int status, long id

// 3. @Modifying and @Transactional annotations:
//    - The @Modifying annotation is used to indicate that the method performs a modification operation (like DELETE or UPDATE).
//    - The @Transactional annotation ensures that the modification is done within a transaction, meaning that if any exception occurs, the changes will be rolled back.

// 4. @Repository annotation:
//    - The @Repository annotation marks this interface as a Spring Data JPA repository.
//    - Spring Data JPA automatically implements this repository, providing the necessary CRUD functionality and custom queries defined in the interface.

	
	/**
     * Retrieves a list of appointments for a specific doctor within a given time range.
     * Aligned with schema table 'appointments' and columns 'doctor_id' and 'appointment_time'.
     */
    @Query(value = "SELECT * FROM appointments WHERE doctor_id = :doctorId AND appointment_time BETWEEN :start AND :end", nativeQuery = true)
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end
    );

    /**
     * Retrieves appointments for a specific doctor and patient name (ignoring case) within a given time range.
     * Aligned with schema: Joins 'patients' table and uses 'first_name' and 'last_name' columns.
     */
    @Query(value = "SELECT a.* FROM appointments a JOIN patients p ON a.patient_id = p.id WHERE a.doctor_id = :doctorId AND LOWER(CONCAT(p.first_name, ' ', p.last_name)) LIKE LOWER(CONCAT('%', :patientName, '%')) AND a.appointment_time BETWEEN :start AND :end", nativeQuery = true)
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId, 
            @Param("patientName") String patientName, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end
    );

    /**
     * Deletes all appointments associated with a particular doctor.
     * Aligned with schema: Uses 'doctor_id' column inside the 'appointments' table.
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM appointments WHERE doctor_id = :doctorId", nativeQuery = true)
    void deleteAllByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * Retrieves all appointments for a specific patient.
     * Aligned with schema: Uses 'patient_id' column inside the 'appointments' table.
     */
    @Query(value = "SELECT * FROM appointments WHERE patient_id = :patientId", nativeQuery = true)
    List<Appointment> findByPatientId(@Param("patientId") Long patientId);

    /**
     * Retrieves all appointments for a specific patient with a given status, ordered by time.
     * Aligned with schema: Uses 'patient_id', integer 'status', and 'appointment_time'.
     */
    @Query(value = "SELECT * FROM appointments WHERE patient_id = :patientId AND status = :status ORDER BY appointment_time ASC", nativeQuery = true)
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
            @Param("patientId") Long patientId, 
            @Param("status") int status
    );

    /**
     * Retrieves appointments based on a doctor's name (partial lookup) and the patient's ID.
     * Aligned with schema: Joins 'doctors' table and concatenates 'first_name' and 'last_name'.
     */
    @Query(value = "SELECT a.* FROM appointments a JOIN doctors d ON a.doctor_id = d.id WHERE LOWER(CONCAT(d.first_name, ' ', d.last_name)) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patient_id = :patientId", nativeQuery = true)
    List<Appointment> filterByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName, 
            @Param("patientId") Long patientId
    );

    /**
     * Retrieves appointments based on a doctor's name, patient's ID, and specific status.
     */
    @Query(value = "SELECT a.* FROM appointments a JOIN doctors d ON a.doctor_id = d.id WHERE LOWER(CONCAT(d.first_name, ' ', d.last_name)) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.patient_id = :patientId AND a.status = :status", nativeQuery = true)
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            @Param("doctorName") String doctorName, 
            @Param("patientId") Long patientId, 
            @Param("status") int status
    );

    /**
     * Updates the status of a specific appointment based on its ID.
     * Aligned with schema: Targets 'appointments' table, 'status' column, and 'id' primary key column.
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE appointments SET status = :status WHERE id = :id", nativeQuery = true)
    void updateStatus(@Param("status") int status, @Param("id") long id);
}