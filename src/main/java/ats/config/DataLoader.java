package ats.config;

import ats.model.*;
import ats.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final IlanRepository ilanRepository;
    private final AdayRepository adayRepository;
    private final BasvuruRepository basvuruRepository;
    private final AktiviteRepository aktiviteRepository;
    private final KullaniciRepository kullaniciRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(IlanRepository ilanRepository,
                      AdayRepository adayRepository,
                      BasvuruRepository basvuruRepository,
                      AktiviteRepository aktiviteRepository,
                      KullaniciRepository kullaniciRepository,
                      PasswordEncoder passwordEncoder) {
        this.ilanRepository = ilanRepository;
        this.adayRepository = adayRepository;
        this.basvuruRepository = basvuruRepository;
        this.aktiviteRepository = aktiviteRepository;
        this.kullaniciRepository = kullaniciRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (kullaniciRepository.count() == 0) {
            kullaniciOlustur("admin@ats.com", "demo1234", "Bertan Öktem", Rol.ADMIN);
            kullaniciOlustur("ik@ats.com", "demo1234", "Ayse Yılmaz", Rol.IK_UZMANI);
        }
        // Veritabani doluysa hicbir sey yapma
        if (ilanRepository.count() > 0) {
            return;
        }

        Ilan ilan1 = ilanOlustur("Backend Developer", "Yazılım",
                "Java, Spring, SQL", "Spring Boot ile REST API geliştirme", IlanDurumu.ACIK);
        Ilan ilan2 = ilanOlustur("Frontend Developer", "Yazılım",
                "React, JavaScript, CSS", "React ile arayüz geliştirme", IlanDurumu.ACIK);
        Ilan ilan3 = ilanOlustur("Veri Analisti", "Veri",
                "Python, SQL, Excel", "Raporlama ve veri analizi", IlanDurumu.KAPALI);

        Aday aday1 = adayOlustur("Ahmet Yılmaz", "ahmet@ornek.com", "5551112233",
                "Java, Spring, SQL, Git", "3 yıl backend deneyimi");
        Aday aday2 = adayOlustur("Ayşe Demir", "ayse@ornek.com", "5552223344",
                "Java, Spring", "2 yıl backend deneyimi");
        Aday aday3 = adayOlustur("Mehmet Kaya", "mehmet@ornek.com", "5553334455",
                "Python, SQL", "Yeni mezun, veri analizi ilgisi");
        Aday aday4 = adayOlustur("Zeynep Şahin", "zeynep@ornek.com", "5554445566",
                "React, JavaScript, CSS, TypeScript", "4 yıl frontend deneyimi");
        Aday aday5 = adayOlustur("Emre Ak", "emre@ornek.com", "5555556677",
                "React, JavaScript, CSS", "5 yıl frontend deneyimi");

        // Backend ilanina 3 basvuru, farkli asamalarda
        Basvuru b1 = basvuruOlustur(aday1, ilan1, BasvuruAsamasi.MULAKAT);
        Basvuru b2 = basvuruOlustur(aday2, ilan1, BasvuruAsamasi.ON_ELEME);
        basvuruOlustur(aday3, ilan1, BasvuruAsamasi.ELENDI);

        // Frontend ilanina 1 basvuru
        Basvuru b4 = basvuruOlustur(aday4, ilan2, BasvuruAsamasi.TEKLIF);

        // Ise alinmis basvurular: departman raporunun anlamli sonuc vermesi icin
        // iki farkli departmanda birer kayit (Yazilim ve Veri)
        basvuruOlustur(aday5, ilan2, BasvuruAsamasi.ISE_ALINDI);   // Yazilim
        basvuruOlustur(aday3, ilan3, BasvuruAsamasi.ISE_ALINDI);   // Veri

        aktiviteOlustur(b1, AktiviteTipi.NOT, "CV incelendi, uygun görünüyor", null);
        aktiviteOlustur(b1, AktiviteTipi.GORUSME, "Telefon görüşmesi yapildi", null);
        aktiviteOlustur(b1, AktiviteTipi.DEGERLENDIRME, "Teknik mülakat", 4);
        aktiviteOlustur(b2, AktiviteTipi.NOT, "Ön eleme listesine alindi", null);
        aktiviteOlustur(b4, AktiviteTipi.DEGERLENDIRME, "Portfolyo çok iyi", 5);
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

    private void kullaniciOlustur(String email, String sifre, String adSoyad, Rol rol) {
        Kullanici k = new Kullanici();
        k.setEmail(email);
        k.setSifre(passwordEncoder.encode(sifre));   // ← kritik satır
        k.setAdSoyad(adSoyad);
        k.setRol(rol);
        kullaniciRepository.save(k);
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