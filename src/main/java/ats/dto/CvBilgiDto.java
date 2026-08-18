package ats.dto;

import java.time.LocalDateTime;

public record CvBilgiDto(
        String dosyaAdi,
        Long boyut,
        LocalDateTime yuklemeTarihi
) {
}
