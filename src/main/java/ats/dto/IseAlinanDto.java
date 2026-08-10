package ats.dto;

import java.time.LocalDate;

public record IseAlinanDto(
        Long id,
        Long adayId,
        String adSoyad,
        Long ilanId,
        String ilanPozisyon,
        String departman,
        LocalDate basvuruTarihi
) {
}
