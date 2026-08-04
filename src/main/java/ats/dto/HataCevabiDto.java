package ats.dto;

import java.time.LocalDateTime;

public record HataCevabiDto(
        int status,
        String hata,
        String mesaj,
        LocalDateTime zaman
) {
}
