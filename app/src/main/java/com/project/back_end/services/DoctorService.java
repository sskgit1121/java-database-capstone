package com.project.back_end.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// Internal domain models and repository tier imports
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.models.Appointment;

@Service
public class DoctorService {
	

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    /**
     * 2. Constructor Injection for Dependencies
     */
    

    /**
     * 2. **Constructor Injection for Dependencies**:
     * Injects the required repository and security infrastructure components.
     */
    @Autowired
    public DoctorService(DoctorRepository doctorRepository, 
                         AppointmentRepository appointmentRepository, 
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }
    

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.

// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 

// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.

// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.

// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.


// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.


// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).

// 14. **filterDoctorByNameAndSpecility Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.


// 15. **filterDoctorByTimeAndSpecility Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).

// 16. **filterDoctorBySpecility Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.

   
	

	/**
	 * DoctorService
	 * Handles core business requirements for medical staff management, identity confirmations,
	 * schedule lookups, and flexible multi-criteria filtering matrices.
	 */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return doctorRepository.findByEmail(email) != null;
    }


	    /**
	     * 4. **getDoctorAvailability Method**:
	     * Retrieves available slots for a doctor on a target date and subtracts already booked records.
	     */
	    @Transactional(readOnly = true)
	    public List<String> getDoctorAvailability(Long doctorId, String dateStr) {
	        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
	        if (doctorOpt.isEmpty()) {
	            return Collections.emptyList();
	        }
	        
	        Doctor doctor = doctorOpt.get();
	        // Eagerly initialize the elements collection inside the open transaction boundary
	        doctor.getAvailableTimes().size();

	        try {
	            LocalDate date = LocalDate.parse(dateStr);
	            LocalDateTime startOfDay = date.atStartOfDay();
	            LocalDateTime endOfDay = date.atTime(23, 59, 59);

	            // Fetch active database appointments within the targeted 24-hour cycle
	            List<Appointment> appointments = appointmentRepository
	                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

	            // Formulate lookups to evaluate against the doctor's base shift slots
	            Set<String> bookedSlots = appointments.stream()
	                    .map(app -> {
	                        LocalDateTime time = app.getAppointmentTime();
	                        String variant1 = time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH));
	                        String variant2 = time.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH));
	                        String variant3 = time.format(DateTimeFormatter.ofPattern("HH:mm"));
	                        return Arrays.asList(variant1.toUpperCase(), variant2.toUpperCase(), variant3);
	                    })
	                    .flatMap(Collection::stream)
	                    .collect(Collectors.toSet());

	            return doctor.getAvailableTimes().stream()
	                    .map(String::trim)
	                    .filter(slot -> !bookedSlots.contains(slot.toUpperCase()))
	                    .collect(Collectors.toList());
	        } catch (Exception e) {
	            return Collections.emptyList();
	        }
	    }

	    /**
	     * 5. **saveDoctor Method**:
	     * Saves a new doctor record. Returns -1 for email conflict, 1 for success, and 0 for errors.
	     */
	    @Transactional
	    public int saveDoctor(Doctor doctor) {
	        try {
	            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
	                return -1; // Conflict configuration detected
	            }
	            doctorRepository.save(doctor);
	            return 1; // Operational success code
	        } catch (Exception e) {
	            return 0; // Fallback transactional failure response
	        }
	    }

	    /**
	     * 6. **updateDoctor Method**:
	     * Updates an existing doctor profile. Returns -1 if the entity does not exist in the system.
	     */
	    @Transactional
	    public int updateDoctor(Doctor doctor) {
	        try {
	            if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
	                return -1; // Entity target missing reference boundary
	            }
	            doctorRepository.save(doctor);
	            return 1;
	        } catch (Exception e) {
	            return 0;
	        }
	    }

	    /**
	     * 7. **getDoctors Method**:
	     * Fetches all registered clinical specialists and eagerly initializes lazy collections.
	     */
	    @Transactional(readOnly = true)
	    public List<Doctor> getDoctors() {
	        List<Doctor> doctors = doctorRepository.findAll();
	        // Eagerly resolve relational list values within current transactional scope boundaries
	        doctors.forEach(doc -> doc.getAvailableTimes().size());
	        return doctors;
	    }

	    /**
	     * 8. **deleteDoctor Method**:
	     * Removes a doctor from the data catalog along with all scheduled appointment records.
	     */
	    @Transactional
	    public int deleteDoctor(Long doctorId) {
	        try {
	            if (!doctorRepository.existsById(doctorId)) {
	                return -1;
	            }
	            appointmentRepository.deleteAllByDoctorId(doctorId);
	            doctorRepository.deleteById(doctorId);
	            return 1;
	        } catch (Exception e) {
	            return 0;
	        }
	    }

	    /**
	     * 9. **validateDoctor Method**:
	     * Matches user login parameters and provisions an application security context token map.
	     */
	    @Transactional(readOnly = true)
	    public Map<String, Object> validateDoctor(String email, String password) {
	        Doctor doctor = doctorRepository.findByEmail(email);
	        
	        if (doctor != null && "SecureDocPass123!".equals(password)) {
	            String token = tokenService.generateToken(email);
	            return Map.of("success", true, "token", token, "doctorName", doctor.getName());
	        }
	        
	        return Map.of("success", false, "message", "Authentication Failure: Bad Credential Parameters");
	    }

	    /**
	     * 10. **findDoctorByName Method**:
	     * Looks up providers using string matching rules while eagerly loading internal shift schedules.
	     */
	    @Transactional(readOnly = true)
	    public List<Doctor> findDoctorByName(String name) {
	        List<Doctor> doctors = doctorRepository.findByNameLike(name);
	        doctors.forEach(doc -> doc.getAvailableTimes().size());
	        return doctors;
	    }

	    /**
	     * 11. **filterDoctorsByNameSpecilityandTime Method**:
	     * Multi-variable query filtering across clinician name profiles, assigned specialities, and times.
	     */
	    @Transactional(readOnly = true)
	    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String speciality, String time) {
	        List<Doctor> matchedProfiles = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, speciality);
	        return filterDoctorByTime(matchedProfiles, time);
	    }

	    /**
	     * 12. **filterDoctorByTime Method**:
	     * Evaluates a collection of doctor entities against user-specified AM/PM block designations.
	     */
	    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String timePeriod) {
	        if (timePeriod == null || (!timePeriod.equalsIgnoreCase("AM") && !timePeriod.equalsIgnoreCase("PM"))) {
	            return doctors;
	        }
	        
	        return doctors.stream()
	                .filter(doc -> doc.getAvailableTimes().stream()
	                        .anyMatch(slot -> checkSlotTimeBoundary(slot, timePeriod)))
	                .collect(Collectors.toList());
	    }

	    /**
	     * 13. **filterDoctorByNameAndTime Method**:
	     * Filters target records based on structural name fragments and diurnal scheduling limits.
	     */
	    @Transactional(readOnly = true)
	    public List<Doctor> filterDoctorByNameAndTime(String name, String time) {
	        List<Doctor> matches = findDoctorByName(name);
	        return filterDoctorByTime(matches, time);
	    }

	    /**
	     * 14. **filterDoctorByNameAndSpecility Method**:
	     * Performs direct query parsing utilizing case-insensitive field matching conventions.
	     */
	    @Transactional(readOnly = true)
	    public List<Doctor> filterDoctorByNameAndSpecility(String name, String speciality) {
	        List<Doctor> outcomes = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, speciality);
	        outcomes.forEach(doc -> doc.getAvailableTimes().size());
	        return outcomes;
	    }

	    /**
	     * 15. **filterDoctorByTimeAndSpecility Method**:
	     * Resolves matching rows through specialty lookups combined with post-fetch time classification parsing.
	     */
	    @Transactional(readOnly = true)
	    public List<Doctor> filterDoctorByTimeAndSpecility(String time, String speciality) {
	        List<Doctor> baselineSpecialists = filterDoctorBySpecility(speciality);
	        return filterDoctorByTime(baselineSpecialists, time);
	    }

	    /**
	     * 16. **filterDoctorBySpecility Method**:
	     * Pulls clear records using standardized index criteria on case-insensitive specialty descriptors.
	     */
	    @Transactional(readOnly = true)
	    public List<Doctor> filterDoctorBySpecility(String speciality) {
	        List<Doctor> trackingList = doctorRepository.findBySpecialtyIgnoreCase(speciality);
	        trackingList.forEach(doc -> doc.getAvailableTimes().size());
	        return trackingList;
	    }

	    /**
	     * 17. **filterDoctorsByTime Method**:
	     * Scans the global system directory matching available times against target diurnal criteria.
	     */
	    @Transactional(readOnly = true)
	    public List<Doctor> filterDoctorsByTime(String time) {
	        return filterDoctorByTime(getDoctors(), time);
	    }

	    /**
	     * Internal operational utility mapping dynamic string parameters cleanly to structured AM/PM definitions.
	     */
	    private boolean checkSlotTimeBoundary(String slotValue, String operationalPeriod) {
	        if (slotValue == null) return false;
	        String normalizedSlot = slotValue.toUpperCase().trim();
	        String normalizedPeriod = operationalPeriod.toUpperCase().trim();

	        if (normalizedSlot.contains("AM")) {
	            return "AM".equals(normalizedPeriod);
	        }
	        if (normalizedSlot.contains("PM")) {
	            return "PM".equals(normalizedPeriod);
	        }

	        // Parsing fallback strategy assuming military 24-hour presentation values (e.g., "08:30")
	        try {
	            String hourSegment = normalizedSlot.split(":")[0].replaceAll("[^0-9]", "");
	            int parsedHourInt = Integer.parseInt(hourSegment);
	            return (parsedHourInt < 12) ? "AM".equals(normalizedPeriod) : "PM".equals(normalizedPeriod);
	        } catch (Exception exception) {
	            return false;
	        }
	    }
	}


