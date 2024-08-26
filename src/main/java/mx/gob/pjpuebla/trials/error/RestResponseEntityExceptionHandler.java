package mx.gob.pjpuebla.trials.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getName(), ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(ex.getField(), ex.getReason())
                ),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) ex.getCause();
            return handleExceptionInternal(ex,
                    Collections.singleton(
                            new ErrorRecord(cause.getPath().get(0).getFieldName(), cause.getOriginalMessage())
                    ),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST,
                    request
            );
        }
        return handleExceptionInternal(ex,
                Collections.singleton(
                        new ErrorRecord(null, ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex,
                buildBindingResultErrorsList(ex.getBindingResult()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex,
                buildBindingResultErrorsList(ex.getBindingResult()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
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
                    new ErrorRecord(objectError.getObjectName(), objectError.getDefaultMessage())
//                            .setMessage(objectError.getCode())
//                            .setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
//                            .setField(objectError.getObjectName())
            );
        }
        return errors;
    }

    private List<ErrorRecord> buildFieldErrorsList(Collection<FieldError> fieldErrors) {
        List<ErrorRecord> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errors.add(
                    new ErrorRecord(fieldError.getField(), fieldError.getDefaultMessage())
//                            .setMessage(fieldError.getCode())
//                            .setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
//                            .setField(fieldError.getField())
            );
        }
        return errors;
    }
}
