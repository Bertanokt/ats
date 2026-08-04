package ats.exception;

import ats.dto.HataCevabiDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KaynakBulunamadiException.class)
    public ResponseEntity<HataCevabiDto> kaynakBulunamadi(KaynakBulunamadiException ex) {
        return cevapOlustur(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CakismaException.class)
    public ResponseEntity<HataCevabiDto> cakisma(CakismaException ex) {
        return cevapOlustur(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(GecersizIstekException.class)
    public ResponseEntity<HataCevabiDto> gecersizIstek(GecersizIstekException ex) {
        return cevapOlustur(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<HataCevabiDto> cevapOlustur(HttpStatus durum, String mesaj) {
        HataCevabiDto cevap = new HataCevabiDto(
                durum.value(),
                durum.getReasonPhrase(),
                mesaj,
                LocalDateTime.now()
        );
        return ResponseEntity.status(durum).body(cevap);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HataCevabiDto> constraintHatasi(ConstraintViolationException ex) {
        return cevapOlustur(HttpStatus.BAD_REQUEST, ex.getMessage());
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HataCevabiDto> validationHatasi(MethodArgumentNotValidException ex) {
        String mesajlar = ex.getBindingResult().getFieldErrors().stream()
                .map(hata -> hata.getField() + ": " + hata.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return cevapOlustur(HttpStatus.BAD_REQUEST, mesajlar);
    }
}