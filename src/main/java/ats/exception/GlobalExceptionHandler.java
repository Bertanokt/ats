package ats.exception;

import ats.dto.HataCevabiDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

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
}