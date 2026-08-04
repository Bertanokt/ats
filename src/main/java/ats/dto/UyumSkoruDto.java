package ats.dto;

import java.util.List;

public record UyumSkoruDto(
        int skor,
        List<String> eslesenler,
        List<String> eksikler
) {
}
