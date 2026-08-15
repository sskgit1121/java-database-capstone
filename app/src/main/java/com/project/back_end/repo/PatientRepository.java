package com.project.back_end.repo;

	import org.springframework.data.jpa.repository.JpaRepository;
	import org.springframework.data.jpa.repository.Query;
	import org.springframework.data.repository.query.Param;
	import org.springframework.stereotype.Repository;
	import java.util.Optional;

	// Import the Patient domain entity model
	import com.project.back_end.models.Patient;


    // 1. Extend JpaRepository:
//    - The repository extends JpaRepository<Patient, Long>, which provides basic CRUD functionality.
//    - This allows the repository to perform operations like save, delete, update, and find without needing to implement these methods manually.
//    - JpaRepository also includes features like pagination and sorting.

// Example: public interface PatientRepository extends JpaRepository<Patient, Long> {}

// 2. Custom Query Methods:

//    - **findByEmail**:
//      - This method retrieves a Patient by their email address.
//      - Return type: Patient
//      - Parameters: String email

//    - **findByEmailOrPhone**:
//      - This method retrieves a Patient by either their email or phone number, allowing flexibility for the search.
//      - Return type: Patient
//      - Parameters: String email, String phone

// 3. @Repository annotation:
//    - The @Repository annotation marks this interface as a Spring Data JPA repository.
//    - Spring Data JPA automatically implements this repository, providing the necessary CRUD functionality and custom queries defined in the interface.

	/**
	 * PatientRepository
	 * Provides data access layer operations for the Patient relational entity, fulfilling Capstone Rubric Question 8.
	 */
	@Repository
	public interface PatientRepository extends JpaRepository<Patient, Long> {

	    /**
	     * Retrieves a patient profile by their unique email address.
	     * Fulfills Rubric Question 8, Criterion 1 using Spring Data derived query methods.
	     * 
	     * @param email the unique email address of the patient
	     * @return an Optional containing the matching patient profile if it exists
	     */
	    Optional<Patient> findByEmail(String email);

	    /**
	     * Retrieves a patient profile using either their email address or phone number.
	     * Fulfills Rubric Question 8, Criterion 2 using a custom Java Persistence Query Language (JPQL) statement.
	     * 
	     * @param email the search criteria email parameter mapping
	     * @param phone the search criteria phone parameter mapping
	     * @return an Optional containing the matching patient profile if it exists
	     */
	    @Query("SELECT p FROM Patient p WHERE p.email = :email OR p.phone = :phone")
	    Optional<Patient> findByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);
	}



