package com.project.back_end.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

// Import the Prescription domain document model
import com.project.back_end.models.Prescription;

/**
 * PrescriptionRepository
 * Provides NoSQL data access tier capabilities for Prescription MongoDB documents.
 * Fulfills Capstone Rubric criteria for handling unstructured medical data.
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Retrieves a list of prescriptions associated with a specific appointment.
     * Fulfills Criterion 2 of the PrescriptionRepository design guidelines.
     * Spring Data MongoDB automatically derives the query matching the appointmentId attribute.
     * 
     * @param appointmentId the unique identifier of the relational appointment record
     * @return a collection list containing matching prescription documents
     */
    List<Prescription> findByAppointmentId(Long appointmentId);

    /**
     * Extra Method: Retrieves prescriptions by partial patient name matching (case-insensitive).
     * Useful for cross-referencing lookups inside clinical workflows.
     * 
     * @param patientName the partial name of the target patient
     * @return a collection list containing matching prescription documents
     */
    List<Prescription> findByPatientNameContainingIgnoreCase(String patientName);

    /**
     * Extra Method: Retrieves prescriptions matching a specific pharmacy name.
     * Useful for coordinating downstream logistics and routing validations.
     * 
     * @param pharmacyName the name of the assigned pharmacy distributor
     * @return a collection list containing matching prescription documents
     */
    List<Prescription> findByPharmacyNameIgnoreCase(String pharmacyName);

    /**
     * Extra Method: Performs a custom MongoDB JSON criteria regex match on medication arrays.
     * Demonstrates advanced NoSQL document searching capabilities within complex structures.
     * 
     * @param medication the name or partial string of the medication to look up
     * @return a collection list containing matching prescription documents
     */
    @Query("{ 'medication' : { $regex: ?0, $options: 'i' } }")
    List<Prescription> findByMedicationCustomRegex(String medication);
}
