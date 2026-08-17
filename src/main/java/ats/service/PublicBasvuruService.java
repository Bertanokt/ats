package ats.service;

import ats.dto.PublicIlanDto;
import ats.exception.GecersizIstekException;
import ats.exception.KaynakBulunamadiException;
import ats.model.*;
import ats.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PublicBasvuruService {
    private final IlanRepository ilanRepository;
    private final AdayRepository adayRepository;
    private final CvDosyaRepository cvDosyaRepository;
    private final BasvuruService basvuruService;

    public PublicBasvuruService(IlanRepository ilanRepository, AdayRepository adayRepository, CvDosyaRepository cvDosyaRepository, BasvuruService basvuruService) {
        this.ilanRepository = ilanRepository;
        this.adayRepository = adayRepository;
        this.cvDosyaRepository = cvDosyaRepository;
        this.basvuruService = basvuruService;
    }

    // --- Acik ilanlari listele ---
    public List<PublicIlanDto> acikIlanlar() {
        return ilanRepository.findAll().stream()
                .filter(i -> i.getDurum() == IlanDurumu.ACIK)
                .map(i -> new PublicIlanDto(
                        i.getId(),
                        i.getPozisyon(),
                        i.getDepartman(),
                        i.getAciklama(),
                        i.getNitelikler()))
                .toList();
    }

    public PublicIlanDto acikIlanGetir(Long id){
        Ilan ilan = ilanRepository.findById(id)
                .orElseThrow(() -> new KaynakBulunamadiException("Ilan Bulunamadı: " + id));
        if ( ilan.getDurum() != IlanDurumu.ACIK){
            throw new GecersizIstekException("Bu ilan artık açık değil");
        }
        return new PublicIlanDto(
                ilan.getId(), ilan.getPozisyon(), ilan.getDepartman(),
                ilan.getAciklama(), ilan.getNitelikler());
    }
    // --- Basvuru olustur ---
    public Long basvuruOlustur(Long ilanId, String adSoyad, String email,
                               String telefon, String not, MultipartFile cv){
        cvDogrula(cv);

        // 1. Adayı bul ya da oluştur
        Aday aday = adayRepository.findByEmail(email)
                .orElseGet(() -> yeniAdayOlustur(adSoyad, email, telefon, not));

        // 2. Başvuruyu oluştur
        Basvuru basvuru = basvuruService.olustur(aday.getId(), ilanId);

        cvKaydet(aday, cv);
        return basvuru.getId();
    }


    private void cvDogrula(MultipartFile cv) {
        if (cv == null || cv.isEmpty()) {
            throw new GecersizIstekException("Ozgecmis dosyasi zorunludur");
        }
        if (!"application/pdf".equals(cv.getContentType())) {
            throw new GecersizIstekException("Yalnizca PDF dosyasi yuklenebilir");
        }
    }

    private Aday yeniAdayOlustur(String adSoyad, String email, String telefon, String not) {
        Aday aday = new Aday();
        aday.setAdSoyad(adSoyad);
        aday.setEmail(email);
        aday.setTelefon(telefon);
        aday.setOzet(not);
        aday.setYetenekler("");
        return adayRepository.save(aday);
    }

    private void cvKaydet(Aday aday, MultipartFile dosya) {
        CvDosya cvDosya = cvDosyaRepository.findByAdayId(aday.getId())
                .orElseGet(CvDosya::new);

        cvDosya.setAday(aday);
        cvDosya.setDosyaAdi(dosya.getOriginalFilename());
        cvDosya.setIcerikTipi(dosya.getContentType());
        cvDosya.setBoyut(dosya.getSize());
        cvDosya.setYuklemeTarihi(LocalDateTime.now());

        try {
            cvDosya.setIcerik(dosya.getBytes());
        } catch (IOException e) {
            throw new GecersizIstekException("Dosya okunamadi");
        }

        cvDosyaRepository.save(cvDosya);
    }
}
