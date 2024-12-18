# Med Voll API

## Description
The **Med Voll API** is a Java-based application developed with Spring Boot, designed to optimize medical operations by providing secure authentication, robust resource management, and flexible data handling. With its modular architecture, the API ensures scalability, maintainability, and integration with modern healthcare systems. It includes comprehensive features for managing doctors, patients, and consultations, backed by strict validation rules and secure JWT-based authentication.

The API integrates advanced error handling, role-based access control, and OpenAPI documentation for seamless developer interaction. Unit-tested controllers and services guarantee reliability and business rule compliance, making it a powerful tool for real-world healthcare management.

## Features Overview:
- **Authentication and Security**: JWT-based tokens, role management, and secure authentication.
- **Doctor and Patient Management**: CRUD operations with validations for active status and unique identifiers.
- **Consultation Scheduling**: Advanced validators ensure proper appointment timings and prevent conflicts.
- **Custom Configuration**: Supports multiple environments (production, testing) with Spring configurations.
- **Extensive Documentation**: Integrated OpenAPI (via SpringDoc) for easy API exploration and integration.
- **Automated Testing**: Unit tests for controllers, repositories, and services to ensure system reliability.
- **Error Handling**: Centralized exception handling for consistent API responses.

This comprehensive system is ideal for healthcare organizations needing a reliable, scalable, and secure API.


## Key Features

### 1. Main Application
- **`med.voll.api.ApiApplication`**: The entry point of the Spring Boot application, initializing the environment and launching the API.

### 2. Authentication Controller
- **`med.voll.api.controller.AutenticacionController`**: Handles user authentication, generating secure JWT tokens to manage API access.

### 3. Consultation Controller
- **`med.voll.api.controller.ConsultaController`**: Manages the reservation and cancellation of medical consultations, ensuring validation and proper resource creation.

### 4. Doctor Controller
- **`med.voll.api.controller.MedicoController`**: Provides endpoints for managing doctors, including registration, updates, deactivation, and listing active doctors.

### 5. Patient Controller
- **`med.voll.api.controller.PacienteController`**: Manages patient-related operations, including registration, updates, deactivation, and listing active patients.

### 6. Consultation Anticipation Validator
- **`med.voll.api.domain.consulta.validaciones.ValidadorConsultaConAnticipacion`**: Ensures consultations are scheduled with a minimum anticipation of 30 minutes.

### 7. Consultation Validator Interface
- **`med.voll.api.domain.consulta.validaciones.ValidadorDeConsultas`**: Defines a contract for implementing various consultation validation rules.

### 8. Outside Business Hours Validator
- **`med.voll.api.domain.consulta.validaciones.ValidadorFueraHorarioConsultas`**: Validates that consultations are scheduled within working hours and not on Sundays.

### 9. Active Doctor Validator
- **`med.voll.api.domain.consulta.validaciones.ValidadorMedicoActivo`**: Ensures that the doctor associated with a consultation is active in the system.

### 10. Doctor Conflict Validator
- **`med.voll.api.domain.consulta.validaciones.ValidadorMedicoConOtraConsultaEnElMismoHorario`**: Ensures that a doctor does not have another consultation scheduled at the same time.

### 11. Active Patient Validator
- **`med.voll.api.domain.consulta.validaciones.ValidadorPacienteActivo`**: Validates that the patient involved in a consultation is active in the system.

### 12. Patient Duplicate Appointment Validator
- **`med.voll.api.domain.consulta.validaciones.ValidadorPacienteSinOtraConsultaEnElMismoDia`**: Ensures that a patient does not have another consultation scheduled on the same day.

### 13. Consultation Model
- **`med.voll.api.domain.consulta.Consulta`**: Represents the consultation entity, linking doctors and patients, with fields for date and cancellation reasons.

### 14. Consultation Repository
- **`med.voll.api.domain.consulta.ConsultaRepository`**: Provides database operations for consultations, including checks for scheduling conflicts.

### 15. Cancellation Data
- **`med.voll.api.domain.consulta.DatosCancelamientoConsulta`**: Represents the data required to cancel a consultation, including the reason for cancellation.

### 16. Consultation Details
- **`med.voll.api.domain.consulta.DatosDetalleConsulta`**: Provides a structured representation of consultation details, including doctor and patient IDs.

### 17. Consultation Reservation Data
- **`med.voll.api.domain.consulta.DatosReservaConsulta`**: Captures the data required for booking a consultation, such as patient and doctor IDs, date, and specialty.

### 18. Cancellation Reason Enum
- **`med.voll.api.domain.consulta.MotivoCancelamiento`**: An enumeration defining possible reasons for consultation cancellations.

### 19. Cancellation Reason Converter
- **`med.voll.api.domain.consulta.MotivoCancelamientoConverter`**: Converts cancellation reasons between their string representation and the enumeration used in the application.

### 20. Cancellation Reason Deserializer
- **`med.voll.api.domain.consulta.MotivoCancelamientoDeserializer`**: A custom deserializer to map JSON cancellation reasons to the corresponding enum values.

### 21. Consultation Reservation Service
- **`med.voll.api.domain.consulta.ReservaDeConsultas`**: A service that facilitates the reservation and cancellation of consultations, ensuring compliance with business rules through a series of validators.

### 22. Address Data
- **`med.voll.api.domain.direccion.DatosDireccion`**: Represents structured address data with validation constraints for fields such as street, district, city, and number.

### 23. Address Model
- **`med.voll.api.domain.direccion.Direccion`**: An embeddable entity for storing address details, supporting updates through structured methods.

### 24. Doctor Update Data
- **`med.voll.api.domain.medico.DatosActualizaMedico`**: Defines the structure for updating doctor details, including optional updates for name, document, and address.

### 25. Doctor List Data
- **`med.voll.api.domain.medico.DatosListadoMedico`**: Provides a simplified view of doctor information for listing purposes, including attributes such as name, specialty, and email.

### 26. Doctor Registration Data
- **`med.voll.api.domain.medico.DatosRegistroMedico`**: Captures the necessary data for registering a new doctor, with validation for fields like email, phone, and specialty.

### 27. Doctor Response Data
- **`med.voll.api.domain.medico.DatosRespuestaMedico`**: A structured representation of doctor data, including contact details and address information, intended for API responses.

### 28. Specialty Enum
- **`med.voll.api.domain.medico.Especialidad`**: An enumeration defining medical specialties, including methods for flexible deserialization and consistent database storage.

### 29. Specialty Converter
- **`med.voll.api.domain.medico.EspecialidadConverter`**: Handles the conversion of specialty data between its enumeration format and database string representation.

### 30. Specialty Deserializer
- **`med.voll.api.domain.medico.EspecialidadDeserializer`**: Provides custom logic for deserializing JSON strings into the `Especialidad` enumeration, ensuring data consistency.

### 31. Doctor Model
- **`med.voll.api.domain.medico.Medico`**: Represents the `Medico` entity, including attributes like name, specialty, address, and active status. Supports updates and logical deactivation.

### 32. Doctor Repository
- **`med.voll.api.domain.medico.MedicoRepository`**: Provides database operations for managing doctors, including custom queries to find available doctors for consultations.

### 33. Doctor Service
- **`med.voll.api.domain.medico.MedicoService`**: Implements business logic for registering, updating, and deactivating doctors, ensuring data consistency and validation.

### 34. Patient Update Data
- **`med.voll.api.domain.paciente.DatosActualizaPaciente`**: Defines the structure for updating patient details, including optional fields for name, identity document, and address.

### 35. Patient List Data
- **`med.voll.api.domain.paciente.DatosListadoPaciente`**: Provides a summary of patient information for listing purposes, including attributes like name, email, and phone.

### 36. Patient Registration Data
- **`med.voll.api.domain.paciente.DatosRegistroPaciente`**: Captures the data required for registering a new patient, with validations for fields like email, phone, and identity document.

### 37. Patient Response Data
- **`med.voll.api.domain.paciente.DatosRespuestaPaciente`**: A structured representation of patient data, including contact details and address, intended for API responses.

### 38. Patient Model
- **`med.voll.api.domain.paciente.Paciente`**: Represents the `Paciente` entity, including attributes such as name, identity document, address, and active status. Supports updates and logical deactivation.

### 39. Patient Repository
- **`med.voll.api.domain.paciente.PacienteRepository`**: Provides database operations for managing patients, including queries for active patients and unique validations for emails and identity documents.

### 40. Patient Service
- **`med.voll.api.domain.paciente.PacienteService`**: Implements business logic for registering, updating, and deactivating patients, ensuring data integrity and proper handling of validations.

### 41. User Authentication Data
- **`med.voll.api.domain.usuario.DatosAutenticacionUsuario`**: Represents user authentication data, including login and password.

### 42. Role Enumeration
- **`med.voll.api.domain.usuario.RoleEnum`**: Defines the roles available in the system, such as ADMIN, USER_MEDIC, and USER_PATIENT.

### 43. User Model
- **`med.voll.api.domain.usuario.Usuario`**: Represents the `Usuario` entity, including login credentials, roles, and security details. Implements `UserDetails` for integration with Spring Security.

### 44. User Repository
- **`med.voll.api.domain.usuario.UsuarioRepository`**: Provides database operations for the `Usuario` entity, including methods to find users by their login.

### 45. Global Error Handler
- **`med.voll.api.infra.errors.GlobalErrorHandler`**: Manages global exception handling for the application, providing consistent error responses for validation, authentication, and runtime exceptions.

### 46. Authentication Service
- **`med.voll.api.infra.security.AutenticacionService`**: Implements user authentication and generates JWT tokens, ensuring secure access to the system.

### 47. JWT Token Data
- **`med.voll.api.infra.security.DatosJwtToken`**: Represents the data embedded in a JWT token, including user ID, role, and login.

### 48. JWT Authentication Filter
- **`med.voll.api.infra.security.JwtAuthenticationFilter`**: Validates incoming requests by processing JWT tokens and setting the security context for authenticated users.

### 49. Security Configuration
- **`med.voll.api.infra.security.SecurityConfiguration`**: Configures Spring Security, defining access control rules, authentication mechanisms, and password encryption.

### 50. Token Service
- **`med.voll.api.infra.security.TokenService`**: Provides functionalities for generating, validating, and extracting data from JWT tokens, ensuring secure and stateless authentication.

### 51. SpringDoc Configuration
- **`med.voll.api.infra.springdoc.SpringDocConfiguration`**: Configures OpenAPI documentation using SpringDoc, including JWT authentication schemes, API information, and contact details.

### 52. Application Configuration (Default)
- **`src/main/resources/application.yml`**: Contains default application settings, including database configuration, Hibernate properties, and security parameters.

### 53. Application Configuration (Production)
- **`src/main/resources/application-prod.yml`**: Specifies production-level configuration for database connections and Hibernate behavior.

### 54. Application Configuration (Testing)
- **`src/main/resources/application-test.yml`**: Defines configurations for the testing environment, such as database settings for integration tests.

### 55. Authentication Controller Test
- **`med.voll.api.controller.AutenticacionControllerTest`**: Includes unit tests for user authentication endpoints, ensuring proper validation and token generation.

### 56. Consultation Controller Test
- **`med.voll.api.controller.ConsultaControllerTest`**: Verifies the functionality of consultation booking and cancellation endpoints, ensuring compliance with business rules.

### 57. Doctor Controller Test
- **`med.voll.api.controller.MedicoControllerTest`**: Tests endpoints for managing doctors, including listing, registering, updating, and deactivating doctors.

### 58. Patient Controller Test
- **`med.voll.api.controller.PacienteControllerTest`**: Verifies patient management endpoints, ensuring proper handling of registration, updates, and deactivation.

### 59. Doctor Repository Test
- **`med.voll.api.domain.medico.MedicoRepositoryTest`**: Tests custom repository queries, including methods for finding available doctors based on date and specialty.

### 60. API Deployment Artifact
- **`target/api-0.0.1-SNAPSHOT.jar`**: The compiled and packaged API ready for deployment to a production environment.

---

## System Requirements
To run this application:
- **Java SDK 17 or higher**: Ensure compatibility with the latest Java features.
- **Spring Boot Framework**: Facilitates dependency injection and simplifies configuration.
- **Internet Connection**: Required for database and API integrations.

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/DiiegoA/documentar-probar-y-preparar-una-API-para-su-implementacion.git
