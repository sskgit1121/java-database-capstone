Section 1:Artecture Summary: 
This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.


Section2:Numbered flow of data and control: 
1.Client Interaction: Users interact with the presentation tier via server-side UI Dashboards (Admin/Doctor) or exchange JSON data through external REST Modules (Appointments/Patient).

2.Controller Routing: Requests enter the Spring Boot backend where Thymeleaf Controllers handle web page rendering and REST Controllers process raw API requests.

3.Business Logic Execution: Controllers forward requests to a unified Service Layer that coordinates all core clinic workflows and business logic rules.

4.Data Access Layer: The Service Layer communicates with the data persistence layer using MySQL Repositories (via Spring Data JPA) or MongoDB Repositories (via Spring Data MongoDB).

5.Database Interaction: Repositories query or modify the physical data storages, targeting either the relational MySQL Database or the document-oriented MongoDB Database.

6.Model Mapping: Raw data retrieved from the databases is structured into native Java object constructs classified as MySQL Models or MongoDB Models.

7.Entity Management: Model objects are fully instantiated into specific JPA Entities (Patient, Doctor, Appointment, Admin) or flexible text Documents (Prescription) to be passed back up to the user.
