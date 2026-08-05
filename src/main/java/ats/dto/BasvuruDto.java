package ats.dto;

import ats.model.BasvuruAsamasi;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BasvuruDto(
        Long id,
        Long adayId,
        String adSoyad,
        Long ilanId,
        String ilanPozisyon,
        BasvuruAsamasi asama,
        LocalDate basvuruTarihi,
        int aktiviteSayisi
) {
}
