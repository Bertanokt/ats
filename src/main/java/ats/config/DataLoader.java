package ats.config;

import ats.model.*;
import ats.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final IlanRepository ilanRepository;
    private final AdayRepository adayRepository;
    private final BasvuruRepository basvuruRepository;
    private final AktiviteRepository aktiviteRepository;

    public DataLoader(IlanRepository ilanRepository,
                      AdayRepository adayRepository,
                      BasvuruRepository basvuruRepository,
                      AktiviteRepository aktiviteRepository) {
        this.ilanRepository = ilanRepository;
        this.adayRepository = adayRepository;
        this.basvuruRepository = basvuruRepository;
        this.aktiviteRepository = aktiviteRepository;
    }

    @Override
    public void run(String... args) {
        // Veritabani doluysa hicbir sey yapma
        if (ilanRepository.count() > 0) {
            return;
        }

        Ilan ilan1 = ilanOlustur("Backend Developer", "Yazilim",
                "Java, Spring, SQL", "Spring Boot ile REST API gelistirme", IlanDurumu.ACIK);
        Ilan ilan2 = ilanOlustur("Frontend Developer", "Yazilim",
                "React, JavaScript, CSS", "React ile arayuz gelistirme", IlanDurumu.ACIK);
        Ilan ilan3 = ilanOlustur("Veri Analisti", "Veri",
                "Python, SQL, Excel", "Raporlama ve veri analizi", IlanDurumu.KAPALI);

        Aday aday1 = adayOlustur("Ahmet Yilmaz", "ahmet@ornek.com", "5551112233",
                "Java, Spring, SQL, Git", "3 yil backend deneyimi");
        Aday aday2 = adayOlustur("Ayse Demir", "ayse@ornek.com", "5552223344",
                "Java, Spring", "2 yil backend deneyimi");
        Aday aday3 = adayOlustur("Mehmet Kaya", "mehmet@ornek.com", "5553334455",
                "Python, SQL", "Yeni mezun, veri analizi ilgisi");
        Aday aday4 = adayOlustur("Zeynep Sahin", "zeynep@ornek.com", "5554445566",
                "React, JavaScript, CSS, TypeScript", "4 yil frontend deneyimi");

        // Backend ilanina 3 basvuru, farkli asamalarda
        Basvuru b1 = basvuruOlustur(aday1, ilan1, BasvuruAsamasi.MULAKAT);
        Basvuru b2 = basvuruOlustur(aday2, ilan1, BasvuruAsamasi.ON_ELEME);
        basvuruOlustur(aday3, ilan1, BasvuruAsamasi.ELENDI);

        // Frontend ilanina 1 basvuru
        Basvuru b4 = basvuruOlustur(aday4, ilan2, BasvuruAsamasi.TEKLIF);

        aktiviteOlustur(b1, AktiviteTipi.NOT, "CV incelendi, uygun gorunuyor", null);
        aktiviteOlustur(b1, AktiviteTipi.GORUSME, "Telefon gorusmesi yapildi", null);
        aktiviteOlustur(b1, AktiviteTipi.DEGERLENDIRME, "Teknik mulakat", 4);
        aktiviteOlustur(b2, AktiviteTipi.NOT, "On eleme listesine alindi", null);
        aktiviteOlustur(b4, AktiviteTipi.DEGERLENDIRME, "Portfolyo cok iyi", 5);
    }

    private Ilan ilanOlustur(String pozisyon, String departman, String nitelikler,
                             String aciklama, IlanDurumu durum) {
        Ilan ilan = new Ilan();
        ilan.setPozisyon(pozisyon);
        ilan.setDepartman(departman);
        ilan.setNitelikler(nitelikler);
        ilan.setAciklama(aciklama);
        ilan.setDurum(durum);
        return ilanRepository.save(ilan);
    }

    private Aday adayOlustur(String adSoyad, String email, String telefon,
                             String yetenekler, String ozet) {
        Aday aday = new Aday();
        aday.setAdSoyad(adSoyad);
        aday.setEmail(email);
        aday.setTelefon(telefon);
        aday.setYetenekler(yetenekler);
        aday.setOzet(ozet);
        return adayRepository.save(aday);
    }

    private Basvuru basvuruOlustur(Aday aday, Ilan ilan, BasvuruAsamasi asama) {
        Basvuru basvuru = new Basvuru();
        basvuru.setAday(aday);
        basvuru.setIlan(ilan);
        basvuru.setAsama(asama);
        basvuru.setBasvuruTarihi(LocalDate.now().minusDays(5));
        return basvuruRepository.save(basvuru);
    }

    private void aktiviteOlustur(Basvuru basvuru, AktiviteTipi tip, String icerik, Integer puan) {
        Aktivite aktivite = new Aktivite();
        aktivite.setBasvuru(basvuru);
        aktivite.setTip(tip);
        aktivite.setIcerik(icerik);
        aktivite.setPuan(puan);
        aktivite.setTarih(LocalDateTime.now().minusDays(3));
        aktiviteRepository.save(aktivite);
    }
}