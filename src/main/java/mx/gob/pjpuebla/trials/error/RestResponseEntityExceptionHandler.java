package mx.gob.pjpuebla.trials.error;

import mx.gob.pjpuebla.trials.util.Messages;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getLocalizedMessage(), ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.UNAUTHORIZED,
                request
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(Messages.INVALID_TOKEN, ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.UNAUTHORIZED,
                request
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getName(), ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        ));
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getField(), Optional.ofNullable(ex.getReason()).orElse(Messages.UNKNOWN_ERROR))
                ),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        ));
    }

    @ExceptionHandler(InvalidVersionException.class)
    protected ResponseEntity<Object> handleInvalidVersionException(InvalidVersionException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getEntity() + ".version", Optional.ofNullable(ex.getReason()).orElse(Messages.UNKNOWN_ERROR))
                ),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getField(), Optional.ofNullable(ex.getReason()).orElse(Messages.UNKNOWN_ERROR))
                ),
                new HttpHeaders(),
                HttpStatus.CONFLICT,
                request
        ));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    protected ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistException ex, WebRequest request) {
        return Objects.requireNonNull(handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getField(), Optional.ofNullable(ex.getReason()).orElse(Messages.UNKNOWN_ERROR))
                ),
                new HttpHeaders(),
                HttpStatus.CONFLICT,
                request
        ));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex,
                buildBindingResultErrorsList(ex.getBindingResult()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getRequestURL(), ex.getMessage())
                ),
                new HttpHeaders(),
                status,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getParameterName(), ex.getMessage())
                ),
                new HttpHeaders(),
                status,
                request
        );
    }

    private List<ErrorRecord> buildBindingResultErrorsList(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<ObjectError> globalErrors = bindingResult.getGlobalErrors();

        List<ErrorRecord> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
        errors.addAll(buildFieldErrorsList(fieldErrors));
        errors.addAll(buildGlobalErrorsList(globalErrors));
        return errors;
    }

    private List<ErrorRecord> buildGlobalErrorsList(Collection<ObjectError> globalErrors) {
        List<ErrorRecord> errors = new ArrayList<>();
        for (ObjectError objectError : globalErrors) {
            errors.add(
                    new ErrorRecord(objectError.getObjectName(), Optional.ofNullable(objectError.getDefaultMessage()).orElse(Messages.UNKNOWN_ERROR))
            );
        }
        return errors;
    }

    private List<ErrorRecord> buildFieldErrorsList(Collection<FieldError> fieldErrors) {
        List<ErrorRecord> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errors.add(
                    new ErrorRecord(fieldError.getField(), Optional.ofNullable(fieldError.getDefaultMessage()).orElse(Messages.UNKNOWN_ERROR))
            );
        }
        return errors;
    }
}
