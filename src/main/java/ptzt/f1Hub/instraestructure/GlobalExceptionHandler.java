package ptzt.f1Hub.instraestructure;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ptzt.f1Hub.domain.exceptions.EntityNotFoundException;
import ptzt.f1Hub.domain.exceptions.UnproccesableEntityException;
import ptzt.f1Hub.instraestructure.dto.out.shared.ErrorResponseDto;

import java.time.LocalDateTime;

import org.springframework.context.support.DefaultMessageSourceResolvable;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(EntityNotFoundException e){

        return ResponseEntity.status(404)
                .body(
                        new ErrorResponseDto(404, LocalDateTime.now(), e.getMessage())
                );

    }

    @ExceptionHandler(UnproccesableEntityException.class)
    public ResponseEntity<ErrorResponseDto> handleUnproccesableEntityException(UnproccesableEntityException e){

        return ResponseEntity.status(422)
                .body(
                        new ErrorResponseDto(422, LocalDateTime.now(), e.getMessage())
                );

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.
                status(HttpStatusCode.
                        valueOf(400)).
                body((new ErrorResponseDto(400, LocalDateTime.now(),String.format("Formato de la petición no valida: %s",
                        ex.getAllErrors()
                                .stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .toList()))));
    }
}
