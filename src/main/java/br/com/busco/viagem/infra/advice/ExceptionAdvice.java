package br.com.busco.viagem.infra.advice;

import br.com.busco.viagem.infra.exceptions.runtime.ConflictException;
import br.com.busco.viagem.infra.exceptions.runtime.ForbiddenException;
import br.com.busco.viagem.infra.exceptions.runtime.ObjectNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler({EntityNotFoundException.class, ObjectNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handlerNotFound(Exception exception) {
        return ResponseEntity.status(NOT_FOUND)
                .body(ExceptionResponse.from(exception));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class,
            org.springframework.web.bind.MethodArgumentNotValidException.class,
            jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<ExceptionResponse> handlerBadRequest(Exception exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.from(exception));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponse> handlerConflict(Exception exception) {
        return ResponseEntity.status(CONFLICT)
                .body(ExceptionResponse.from(exception));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handlerForbidden(Exception exception) {
        return ResponseEntity.status(FORBIDDEN)
                .body(ExceptionResponse.from(exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handler(Exception exception) {
        log.error("Erro não tratado", exception);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.from(exception));
    }
}
