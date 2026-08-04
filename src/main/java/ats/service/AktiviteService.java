package ats.service;


import ats.exception.GecersizIstekException;
import ats.exception.KaynakBulunamadiException;
import ats.model.Aktivite;
import ats.model.AktiviteTipi;
import ats.model.Basvuru;
import ats.model.BasvuruAsamasi;
import ats.repository.AktiviteRepository;
import ats.repository.BasvuruRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AktiviteService {
    private final AktiviteRepository aktiviteRepository;
    private final BasvuruRepository basvuruRepository;

    public AktiviteService(AktiviteRepository aktiviteRepository,
                           BasvuruRepository basvuruRepository) {
        this.aktiviteRepository = aktiviteRepository;
        this.basvuruRepository = basvuruRepository;
    }

    public Aktivite ekle(Long basvuruId, AktiviteTipi tip, String icerik, Integer puan) {
        //1.Başvuru var mı
        Basvuru basvuru = basvuruRepository.findById(basvuruId)
                .orElseThrow(() -> new KaynakBulunamadiException("Basvuru bulunamadi: " + basvuruId));

        // 2. Degerlendirmede ise puan zorunlu ve 1-5 arasi olmali
        if (tip == AktiviteTipi.DEGERLENDIRME) {
            if (puan == null) {
                throw new GecersizIstekException("Degerlendirme icin puan zorunludur.");
            }
            if (puan < 1 || puan > 5) {
                throw new GecersizIstekException("Puan 1 ile 5 arasında olmalidir.");
            }
        }
        // 3. Icerik bos olamaz
        if (icerik == null || icerik.isBlank()) {
            throw new GecersizIstekException("Icerik bos olamaz");
        }

        Aktivite aktivite = new Aktivite();
        aktivite.setBasvuru(basvuru);
        aktivite.setTip(tip);
        aktivite.setIcerik(icerik);
        aktivite.setPuan(tip == AktiviteTipi.DEGERLENDIRME ? puan : null);
        aktivite.setTarih(LocalDateTime.now());

        return aktiviteRepository.save(aktivite);
    }

    public List<Aktivite> basvuruAktiviteleri(Long basvuruId) {
        return aktiviteRepository.findByBasvuruIdOrderByTarihAsc(basvuruId);
    }

}