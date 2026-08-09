package ats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AnaSayfaController {

    @GetMapping("/")
    public Map<String, Object> karsilama() {
        return Map.of(
                "uygulama", "Mini Ise Alim Takip Sistemi (ATS)",
                "durum", "calisiyor",
                "endpointler", Map.of(
                        "ilanlar", "/api/ilanlar",
                        "adaylar", "/api/adaylar",
                        "basvurular", "/api/basvurular",
                        "funnel_raporu", "/api/basvurular/rapor/funnel",
                        "uyum_skoru", "/api/basvurular/{id}/uyum",
                        "cv_parse", "POST /api/adaylar/cv-parse"
                ),
                "giris", Map.of(
                        "adres", "POST /api/auth/login",
                        "demo_admin", "admin@ats.com / demo1234",
                        "demo_ik", "ik@ats.com / demo1234",
                        "kullanim", "Donen token'i sonraki isteklerde 'Authorization: Bearer <token>' basligiyla gonderin"
                )
        );
    }
}