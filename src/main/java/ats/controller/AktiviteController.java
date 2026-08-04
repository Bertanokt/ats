package ats.controller;

import ats.model.Aktivite;
import ats.model.AktiviteTipi;
import ats.service.AktiviteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/basvurular/{basvuruId}/aktiviteler") //1 numaralı başvurunun aktiviteleri.
public class AktiviteController {

    private final AktiviteService aktiviteService;

    public AktiviteController(AktiviteService aktiviteService) {
        this.aktiviteService = aktiviteService;
    }

    @PostMapping
    public Aktivite ekle(@PathVariable Long basvuruId,
                         @RequestParam AktiviteTipi tip,
                         @RequestParam String icerik,
                         @RequestParam(required = false) Integer puan) {
        return aktiviteService.ekle(basvuruId, tip, icerik, puan);
    }

    @GetMapping
    public List<Aktivite> listele(@PathVariable Long basvuruId) {
        return aktiviteService.basvuruAktiviteleri(basvuruId);
    }
}