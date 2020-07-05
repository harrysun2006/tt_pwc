package com.pwc.helper;

import com.pwc.exception.ApplicationException;
import com.pwc.exception.AuthenticationException;
import com.pwc.exception.AuthorisationException;
import com.pwc.exception.BadRequestException;
import com.pwc.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

import static com.pwc.helper.ErrorSeverity.FATAL;
import static com.pwc.helper.ErrorSeverity.HIGH;
import static com.pwc.helper.ErrorSeverity.MEDIUM;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@ControllerAdvice
public class ControllerErrorAdvice {

    // 400 - Bad Request
    @ResponseBody
    @ExceptionHandler({ConstraintViolationException.class, BadRequestException.class})
    @ResponseStatus(BAD_REQUEST)
    GenericError badRequest(Exception ex) {
        return buildError(ex, BAD_REQUEST, MEDIUM, ErrorCodes.INVALID_JSON_REQUEST_9009);
    }

    @ResponseBody
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    @ResponseStatus(BAD_REQUEST)
    public GenericError exceptionHandler(MethodArgumentNotValidException ex) {
        StringBuilder msg = new StringBuilder();
        for (final FieldError error :  ex.getBindingResult().getFieldErrors()) {
            msg.append(error.getDefaultMessage());
            msg.append(" ");
        }
        return buildError(getRootCause(ex), BAD_REQUEST, MEDIUM, ErrorCodes.REQUEST_PARAMETER_MISSING_9045, msg.toString().trim());
    }

    // 401 - Unauthorized: missing UserId request header
    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(UNAUTHORIZED)
    GenericError invalidCredential(Exception ex) {
        return buildError(ex, UNAUTHORIZED, HIGH, ErrorCodes.INVALID_CREDENTIAL_9004);
    }

    // 403 - Forbidden: user has no read entitlement for property
    @ResponseBody
    @ExceptionHandler(AuthorisationException.class)
    @ResponseStatus(FORBIDDEN)
    GenericError notAuthorised(Exception ex) {
        return buildError(ex, FORBIDDEN, HIGH, ErrorCodes.NOT_AUTHORISED_9005);
    }

    // 404 - Not Found: invalid endpoint
    @ResponseBody
    @ExceptionHandler({ResourceNotFoundException.class, org.springframework.data.rest.webmvc.ResourceNotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    GenericError resourceNotFound(Exception ex) {
        return buildError(ex, NOT_FOUND, MEDIUM, ErrorCodes.RESOURCE_NOT_FOUND_9046);
    }

    // 500 - Internal Server Error
    @ResponseBody
    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    GenericError applicationError(ApplicationException ex) {
        return buildError(getRootCause(ex), INTERNAL_SERVER_ERROR, FATAL, ErrorCodes.GENERIC_FAILURE_9003);
    }

    @ResponseBody
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    GenericError dataIntegrityError(Exception ex) {
        return buildError(getRootCause(ex), INTERNAL_SERVER_ERROR, FATAL, ErrorCodes.RESOURCE_CONFLICT_9049);
    }

    // 500 - Internal Server Error: unexpected/unhandled exception
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    GenericError unexpectedError(Exception ex) {
        return buildError(getRootCause(ex), INTERNAL_SERVER_ERROR, FATAL, ErrorCodes.GENERIC_FAILURE_9003);
    }

    private Throwable getRootCause(Exception ex) {
        Throwable cause = ex;
        while (cause.getCause() != null && !cause.equals(cause.getCause())) {
            cause = cause.getCause();
        }
        return cause;
    }

    private GenericError buildError(Throwable ex, HttpStatus status, ErrorSeverity severity, ErrorCodes code) {
        return buildError(ex, status, severity, code.getCode(), code.getMessage(), ex.getMessage());
    }

    private GenericError buildError(Throwable ex, HttpStatus status, ErrorSeverity severity, ErrorCodes code, String message) {
        return buildError(ex, status, severity, code.getCode(), message, ex.getMessage());
    }

    private GenericError buildError(Throwable ex, HttpStatus status, ErrorSeverity severity, int code, String message, String description) {
        log.error(status.getReasonPhrase(), ex);
        GenericError error = new GenericError();
        error.setCode(code);
        error.setMessage(message);
        error.setDescription(description == null ? ex.getMessage() : description);
        error.setSeverity(severity);
        return error;
    }

}
