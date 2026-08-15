package com.project.back_end.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

// Importing the core Doctor domain entity model
import com.project.back_end.models.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
   // 1. Extend JpaRepository:
//    - The repository extends JpaRepository<Doctor, Long>, which gives it basic CRUD functionality.
//    - This allows the repository to perform operations like save, delete, update, and find without needing to implement these methods manually.
//    - JpaRepository also includes features like pagination and sorting.

// Example: public interface DoctorRepository extends JpaRepository<Doctor, Long> {}

// 2. Custom Query Methods:

//    - **findByEmail**:
//      - This method retrieves a Doctor by their email.
//      - Return type: Doctor
//      - Parameters: String email

//    - **findByNameLike**:
//      - This method retrieves a list of Doctors whose name contains the provided search string (case-sensitive).
//      - The `CONCAT('%', :name, '%')` is used to create a pattern for partial matching.
//      - Return type: List<Doctor>
//      - Parameters: String name

//    - **findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase**:
//      - This method retrieves a list of Doctors where the name contains the search string (case-insensitive) and the specialty matches exactly (case-insensitive).
//      - It combines both fields for a more specific search.
//      - Return type: List<Doctor>
//      - Parameters: String name, String specialty

//    - **findBySpecialtyIgnoreCase**:
//      - This method retrieves a list of Doctors with the specified specialty, ignoring case sensitivity.
//      - Return type: List<Doctor>
//      - Parameters: String specialty

// 3. @Repository annotation:
//    - The @Repository annotation marks this interface as a Spring Data JPA repository.
//    - Spring Data JPA automatically implements this repository, providing the necessary CRUD functionality and custom queries defined in the interface.

	

	/**
	 * DoctorRepository
	 * Marks this interface as a Spring Data JPA repository to provide basic CRUD 
	 * functionality alongside custom query methods for advanced lookups.
	 */
	

	    /**
	     * Retrieves a Doctor by their unique email address.
	     * 
	     * @param email The target email address string.
	     * @return The matching Doctor entity.
	     */
	    Doctor findByEmail(String email);

	    /**
	     * Retrieves a list of Doctors whose name contains the provided search string (case-sensitive).
	     * Uses CONCAT('%', :name, '%') to construct the pattern match parameter safely.
	     * 
	     * @param name The substring pattern to query against doctor names.
	     * @return A list of matching Doctor entities.
	     */
	    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
	    List<Doctor> findByNameLike(@Param("name") String name);

	    /**
	     * Retrieves a list of Doctors where the name contains the search string (case-insensitive)
	     * and the specialty matches exactly (case-insensitive).
	     * 
	     * @param name The partial case-insensitive name token.
	     * @param specialty The exact case-insensitive medical specialty token.
	     * @return A list of filtered Doctor records.
	     */
	    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

	    /**
	     * Retrieves a list of Doctors with the specified specialty, ignoring case sensitivity.
	     * 
	     * @param specialty The target medical branch/specialty string.
	     * @return A list of matching Doctor entities.
	     */
	    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
	}

