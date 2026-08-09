package ats.dto;

import ats.model.Rol;

public record LoginCevabiDto(
        String token,
        String email,
        String adSoyad,
        Rol rol
) {
}
