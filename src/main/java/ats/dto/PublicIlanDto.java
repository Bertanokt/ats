package ats.dto;

public record PublicIlanDto(
        Long id,
        String pozisyon,
        String departman,
        String aciklama,
        String nitelikler
) {
}