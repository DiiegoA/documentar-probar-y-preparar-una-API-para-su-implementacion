package med.voll.api.infra.errors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

    // =======================================
    // Excepciones relacionadas con HTTP
    // =======================================

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleDuplicateRecordException(IllegalStateException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        if ("ERR_DUPLICATE_EMAIL".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_DUPLICATE_EMAIL");
            errorResponse.put("message", "Ya existe un registro con este correo.");
        } else if ("ERR_DUPLICATE_DOCUMENT".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_DUPLICATE_DOCUMENT");
            errorResponse.put("message", "Ya existe un registro con este documento.");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // Código 409
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        // Obtén el mensaje más específico de la excepción
        String exceptionMessage = ex.getMostSpecificCause().getMessage();

        if (exceptionMessage != null) {
            // Caso: Especialidad no válida
            if (exceptionMessage.contains("Especialidad")) {
                return buildErrorResponse(
                        "ERR_INVALID_SPECIALITY",
                        "La especialidad ingresada no es válida.",
                        HttpStatus.BAD_REQUEST
                );
            }

            // Caso: Enum inválido (genérico)
            if (exceptionMessage.contains("Enum")) {
                return buildErrorResponse(
                        "ERR_INVALID_SPECIALITY",
                        "La especialidad ingresada no es válida.",
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        // Manejo genérico para otras excepciones HttpMessageNotReadable
        return buildErrorResponse(
                "ERR_INVALID_REQUEST",
                "Solicitud inválida.",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        // Crear una lista para almacenar todos los errores
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        // Agregar los errores al cuerpo de la respuesta
        errorResponse.put("code", "ERR_VALIDATION_FAILED");
        errorResponse.put("errors", fieldErrors); // Lista de errores por campo

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // Código 400
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", "ERR_RECORD_NOT_FOUND");
        errorResponse.put("message", "El recurso solicitado no fue encontrado.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", "ERR_USER_NOT_FOUND");
        errorResponse.put("message", "El usuario no fue encontrado. Verifica el login.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", "ERR_INVALID_PASSWORD");
        errorResponse.put("message", "La contraseña es incorrecta. Inténtalo de nuevo.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse); // Código 401
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        if ("ERR_PACIENTE_NOT_FOUND".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_PATIENT_NOT_FOUND");
            errorResponse.put("message", "El paciente especificado no fue encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("ERR_MEDICAL_NOT_FOUND".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_MEDICAL_NOT_FOUND");
            errorResponse.put("message", "El médico especificado no fue encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("ERR_INVALID_REQUEST".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_INVALID_REQUEST");
            errorResponse.put("message", "Los datos de la reserva son inválidos.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("INVALID_SPECIALTY".equals(ex.getMessage())) {
            errorResponse.put("code", "INVALID_SPECIALTY");
            errorResponse.put("message", "La especialidad es inválida o está vacía.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("ERR_CANCELLATION_NOT_FOUND".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_CANCELLATION_NOT_FOUND");
            errorResponse.put("message", "EL Id de la consulta informado no existe!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("INVALID_CANCELLATION_REASON".equals(ex.getMessage())) {
            errorResponse.put("code", "INVALID_CANCELLATION_REASON");
            errorResponse.put("message", "El motivo de la cancelación es inválido o está vacío.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("OUT_OF_BUSINESS_HOURS".equals(ex.getMessage())) {
            errorResponse.put("code", "OUT_OF_BUSINESS_HOURS");
            errorResponse.put("message", "Fuera del horario de atención!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("INSUFFICIENT_ANTICIPATION".equals(ex.getMessage())) {
            errorResponse.put("code", "INSUFFICIENT_ANTICIPATION");
            errorResponse.put("message", "La consulta debe ser programada con al menos 30 minutos de anticipación");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("PATIENT_NOT_ACTIVE".equals(ex.getMessage())) {
            errorResponse.put("code", "PATIENT_NOT_ACTIVE");
            errorResponse.put("message", "El paciente no está activo");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("DOCTOR_NOT_ACTIVE".equals(ex.getMessage())) {
            errorResponse.put("code", "MEDICAL_NOT_ACTIVE");
            errorResponse.put("message", "El médico no está disponible");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("DUPLICATE_APPOINTMENT".equals(ex.getMessage())) {
            errorResponse.put("code", "DUPLICATE_APPOINTMENT");
            errorResponse.put("message", "El paciente ya tiene una consulta programada en el mismo día.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("MEDICAL_SCHEDULE_CONFLICT".equals(ex.getMessage())) {
            errorResponse.put("code", "MEDICAL_SCHEDULE_CONFLICT");
            errorResponse.put("message", "El médico ya tiene una consulta programada en este horario.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("ERR_MEDICAL_NOT_AVAILABLE".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_MEDICAL_NOT_AVAILABLE");
            errorResponse.put("message", "El médico seleccionado no está disponible para la consulta.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("ERR_PATIENT_NOT_AVAILABLE".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_PATIENT_NOT_AVAILABLE");
            errorResponse.put("message", "El paciente seleccionado no está disponible para la consulta.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("ERR_NO_RANDOM_MEDICAL_AVAILABLE".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_NO_RANDOM_MEDICAL_AVAILABLE");
            errorResponse.put("message", "No hay médicos disponibles para la especialidad y fecha seleccionadas.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404
        } else if ("ERR_SPECIALITY_REQUIRED".equals(ex.getMessage())) {
            errorResponse.put("code", "ERR_SPECIALITY_REQUIRED");
            errorResponse.put("message", "La especialidad es obligatoria.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // Código 400
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // Código 404 o Código 400
    }

    // Método común para construir la respuesta de error
    private ResponseEntity<Map<String, String>> buildErrorResponse(String code, String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", code);
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse); // Código 400
    }

    // =======================================
    // Excepciones Genericas
    // =======================================


}