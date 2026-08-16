package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Import the Admin domain entity model
import com.project.back_end.models.Admin;

/**
 * AdminRepository
 * Provides the data access layer operations for the Admin relational entity.
 * Inherits standard CRUD capabilities from JpaRepository.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Retrieves an administrative profile by their unique username.
     * Derived query method automatically implemented by Spring Data JPA.
     * 
     * @param username the unique username of the administrator
     * @return the matching Admin entity if found; returns null if no record matches
     */
    Admin findByUsername(String username);

    /**
     * EXTRA UTILITY METHOD: Retrieves an administrative profile by their email address.
     * Added to seamlessly support email-based lookups or logins inside your AdminController layer.
     * 
     * @param email the unique email address of the administrator
     * @return an Optional containing the matching Admin profile if it exists inside the database
     */
    Optional<Admin> findByEmail(String email);
}
