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

        // ---------- ILANLAR ----------
        // ilanOlustur(pozisyon, departman, NITELIKLER, ACIKLAMA, durum)
        Ilan backend = ilanOlustur("Backend Developer", "Yazılım",
                "Java, Spring, SQL",
                "Spring Boot ile REST API geliştirme", IlanDurumu.ACIK);
        Ilan frontend = ilanOlustur("Frontend Developer", "Yazılım",
                "React, JavaScript, CSS",
                "React ile arayüz geliştirme", IlanDurumu.ACIK);
        Ilan veriAnalisti = ilanOlustur("Veri Analisti", "Veri",
                "Python, SQL, Excel",
                "Raporlama ve veri analizi", IlanDurumu.KAPALI);
        Ilan mobil = ilanOlustur("Mobil Developer", "Yazılım",
                "React Native, JavaScript, Git",
                "React Native ile iOS ve Android uygulama geliştirme", IlanDurumu.ACIK);
        Ilan tasarimci = ilanOlustur("Ürün Tasarımcısı", "Tasarım",
                "Figma, Prototipleme, Kullanıcı Araştırması",
                "Kullanıcı arayüzü ve deneyim tasarımı", IlanDurumu.ACIK);

        // ---------- ADAYLAR ----------
        Aday ahmet = adayOlustur("Ahmet Yılmaz", "ahmet.yilmaz@ornek.com", "5551112233",
                "Java, Spring, SQL, Git", "3 yıl backend deneyimi");
        Aday ayse = adayOlustur("Ayşe Demir", "ayse.demir@ornek.com", "5552223344",
                "Java, Spring, Docker", "2 yıl backend deneyimi");
        Aday mehmet = adayOlustur("Mehmet Kaya", "mehmet.kaya@ornek.com", "5553334455",
                "Python, SQL", "Yeni mezun, veri analizi ilgisi");
        Aday zeynep = adayOlustur("Zeynep Şahin", "zeynep.sahin@ornek.com", "5554445566",
                "React, JavaScript, CSS, TypeScript", "4 yıl frontend deneyimi");
        Aday emre = adayOlustur("Emre Ak", "emre.ak@ornek.com", "5555556677",
                "React, JavaScript, CSS", "3 yıl frontend deneyimi");
        Aday selin = adayOlustur("Selin Aydın", "selin.aydin@ornek.com", "5321184762",
                "Java, Spring, SQL, PostgreSQL, Docker", "3 yıl backend, test otomasyonu");
        Aday burak = adayOlustur("Burak Özdemir", "burak.ozdemir@ornek.com", "5326677889",
                "JavaScript, React Native, Git", "2 yıl mobil uygulama geliştirme");
        Aday elif = adayOlustur("Elif Korkmaz", "elif.korkmaz@ornek.com", "5337788990",
                "Figma, Prototipleme, Kullanıcı Araştırması", "5 yıl ürün tasarımı");
        Aday canDemir = adayOlustur("Can Demir", "can.demir@ornek.com", "5348899001",
                "Java, SQL", "Yeni mezun, staj deneyimi var");
        Aday merve = adayOlustur("Merve Tuncel", "merve.tuncel@ornek.com", "5359900112",
                "React, JavaScript, CSS, Figma", "Frontend ve tasarım arası profil");
        Aday okan = adayOlustur("Okan Yıldız", "okan.yildiz@ornek.com", "5361011223",
                "Python, SQL, Excel, Power BI", "6 yıl veri analizi");
        Aday deniz = adayOlustur("Deniz Arslan", "deniz.arslan@ornek.com", "5372122334",
                "", "Özgeçmiş inceleniyor");
        Aday sevgi = adayOlustur("Sevgi Polat", "sevgi.polat@ornek.com", "5383233445",
                "React Native, JavaScript, TypeScript, Git", "4 yıl mobil geliştirme");
        Aday kaan = adayOlustur("Kaan Erdoğan", "kaan.erdogan@ornek.com", "5394344556",
                "", "Yeni başvuru, bilgiler eksik");

        // ---------- BASVURULAR ----------
        // Backend Developer — dolu bir huni
        Basvuru b1 = basvuruOlustur(ahmet, backend, BasvuruAsamasi.MULAKAT);
        Basvuru b2 = basvuruOlustur(ayse, backend, BasvuruAsamasi.ON_ELEME);
        Basvuru b3 = basvuruOlustur(selin, backend, BasvuruAsamasi.TEKLIF);
        basvuruOlustur(canDemir, backend, BasvuruAsamasi.BASVURU);
        Basvuru b5 = basvuruOlustur(mehmet, backend, BasvuruAsamasi.ELENDI);
        basvuruOlustur(deniz, backend, BasvuruAsamasi.BASVURU);

        // Frontend Developer
        Basvuru b7 = basvuruOlustur(zeynep, frontend, BasvuruAsamasi.TEKLIF);
        Basvuru b8 = basvuruOlustur(emre, frontend, BasvuruAsamasi.ISE_ALINDI);
        Basvuru b9 = basvuruOlustur(merve, frontend, BasvuruAsamasi.MULAKAT);
        basvuruOlustur(kaan, frontend, BasvuruAsamasi.BASVURU);

        // Veri Analisti (kapali ilan — gecmis basvurular)
        Basvuru b11 = basvuruOlustur(mehmet, veriAnalisti, BasvuruAsamasi.ISE_ALINDI);
        Basvuru b12 = basvuruOlustur(okan, veriAnalisti, BasvuruAsamasi.ELENDI);

        // Mobil Developer
        Basvuru b13 = basvuruOlustur(burak, mobil, BasvuruAsamasi.MULAKAT);
        basvuruOlustur(sevgi, mobil, BasvuruAsamasi.ON_ELEME);
        basvuruOlustur(zeynep, mobil, BasvuruAsamasi.BASVURU);

        // Urun Tasarimcisi
        Basvuru b16 = basvuruOlustur(elif, tasarimci, BasvuruAsamasi.TEKLIF);
        basvuruOlustur(merve, tasarimci, BasvuruAsamasi.ON_ELEME);

        // Coklu basvuru ornekleri
        basvuruOlustur(ayse, mobil, BasvuruAsamasi.ELENDI);
        basvuruOlustur(okan, backend, BasvuruAsamasi.BASVURU);

        // ---------- AKTIVITELER ----------
        // Ahmet — Backend, mulakat asamasinda
        aktiviteOlustur(b1, AktiviteTipi.NOT, "Özgeçmiş incelendi, deneyim uygun görünüyor", null);
        aktiviteOlustur(b1, AktiviteTipi.GORUSME, "Telefon görüşmesi yapıldı, iletişimi iyi", null);
        aktiviteOlustur(b1, AktiviteTipi.DEGERLENDIRME, "Teknik mülakat: Spring bilgisi yeterli", 4);

        // Ayse — Backend, on elemede
        aktiviteOlustur(b2, AktiviteTipi.NOT, "Docker deneyimi dikkat çekti", null);
        aktiviteOlustur(b2, AktiviteTipi.GORUSME, "Kısa tanışma görüşmesi yapıldı", null);

        // Selin — Backend, teklif asamasinda (en guclu aday)
        aktiviteOlustur(b3, AktiviteTipi.NOT, "PostgreSQL ve test otomasyonu deneyimi güçlü", null);
        aktiviteOlustur(b3, AktiviteTipi.GORUSME, "Teknik ekiple görüşme yapıldı", null);
        aktiviteOlustur(b3, AktiviteTipi.DEGERLENDIRME, "Teknik yeterlilik yüksek", 5);
        aktiviteOlustur(b3, AktiviteTipi.DEGERLENDIRME, "Takım uyumu değerlendirmesi olumlu", 4);

        // Mehmet — Backend'de elenmis
        aktiviteOlustur(b5, AktiviteTipi.NOT, "Java deneyimi bu pozisyon için yetersiz", null);
        aktiviteOlustur(b5, AktiviteTipi.DEGERLENDIRME, "Teknik değerlendirme", 2);

        // Zeynep — Frontend, teklif asamasinda
        aktiviteOlustur(b7, AktiviteTipi.GORUSME, "Portföy incelendi, projeler nitelikli", null);
        aktiviteOlustur(b7, AktiviteTipi.DEGERLENDIRME, "TypeScript bilgisi ek artı", 5);

        // Emre — Frontend, ise alindi
        aktiviteOlustur(b8, AktiviteTipi.GORUSME, "Son görüşme tamamlandı", null);
        aktiviteOlustur(b8, AktiviteTipi.DEGERLENDIRME, "Teklif kabul edildi", 4);

        // Merve — Frontend, mulakatta
        aktiviteOlustur(b9, AktiviteTipi.NOT, "Tasarım tarafı da güçlü, iki pozisyona uygun", null);

        // Mehmet — Veri Analisti, ise alindi
        aktiviteOlustur(b11, AktiviteTipi.GORUSME, "Veri analizi pozisyonu için uygun bulundu", null);
        aktiviteOlustur(b11, AktiviteTipi.DEGERLENDIRME, "SQL ve Python yeterli", 4);

        // Okan — Veri Analisti'nde elendi
        aktiviteOlustur(b12, AktiviteTipi.NOT, "Beklenen ücret aralığının üzerinde", null);

        // Burak — Mobil, mulakatta
        aktiviteOlustur(b13, AktiviteTipi.GORUSME, "React Native projeleri incelendi", null);

        // Elif — Tasarim, teklif asamasinda
        aktiviteOlustur(b16, AktiviteTipi.DEGERLENDIRME, "Portföy ve süreç anlatımı çok iyi", 5);
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
        // Tarihler son 30 gune dagilsin: hepsi ayni gun gorunmesin, siralama anlamli olsun
        basvuru.setBasvuruTarihi(LocalDate.now().minusDays((long) (Math.random() * 30)));
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