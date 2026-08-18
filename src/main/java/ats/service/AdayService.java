package ats.service;

import ats.dto.CvBilgiDto;
import ats.dto.CvParseDto;
import ats.exception.CakismaException;
import ats.exception.GecersizIstekException;
import ats.exception.KaynakBulunamadiException;
import ats.model.Aday;
import ats.model.CvDosya;
import ats.repository.AdayRepository;
import ats.repository.BasvuruRepository;
import ats.repository.CvDosyaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class AdayService {
    private final AdayRepository adayRepository;
    private final CvDosyaRepository cvDosyaRepository;
    private final BasvuruRepository basvuruRepository;
    private final CvParseService cvParseService;

    public AdayService(AdayRepository adayRepository,
                       CvDosyaRepository cvDosyaRepository,
                       BasvuruRepository basvuruRepository,
                       CvParseService cvParseService) {
        this.adayRepository = adayRepository;
        this.cvDosyaRepository = cvDosyaRepository;
        this.basvuruRepository = basvuruRepository;
        this.cvParseService = cvParseService;
    }

    public Aday olustur(Aday aday){
        return adayRepository.save(aday);
    }

    public List<Aday> hepsiniGetir(){
        return adayRepository.findAll();
    }

    public Aday getirById(Long id){
        return adayRepository.findById(id)
                .orElseThrow(() -> new KaynakBulunamadiException("Aday bulunamadı: " + id));
    }

    /**
     * Aday silme kurallari:
     * - Basvurusu varsa engellenir. Basvuru; asama gecmisi, gorusme notlari ve
     *   degerlendirme puanlariyla birlikte is kaydidir, sessizce silinmemeli.
     * - CV dosyasi basamakli silinir. Dosya adaya ait bir ektir, bagimsiz bir
     *   anlami yoktur; birakilirsa oksuz bir kisisel veri kaydi kalir.
     */
    @Transactional
    public void sil(Long id){
        Aday aday = getirById(id);   // yoksa 404

        long basvuruSayisi = basvuruRepository.countByAdayId(id);
        if (basvuruSayisi > 0) {
            throw new CakismaException(
                    "Bu adayin " + basvuruSayisi + " basvurusu var. Aday silinemez; "
                            + "basvuru gecmisi korunuyor.");
        }

        cvDosyaRepository.findByAdayId(id).ifPresent(cvDosyaRepository::delete);
        adayRepository.delete(aday);
    }

    public Aday guncelle(Long id, Aday yeniAday) {
        Aday mevcut = getirById(id);

        mevcut.setAdSoyad(yeniAday.getAdSoyad());
        mevcut.setEmail(yeniAday.getEmail());
        mevcut.setTelefon(yeniAday.getTelefon());
        mevcut.setYetenekler(yeniAday.getYetenekler());
        mevcut.setOzet(yeniAday.getOzet());

        return adayRepository.save(mevcut);
    }

    public CvBilgiDto cvBilgisi(Long adayId){
        return cvDosyaRepository.findByAdayId(adayId)
                .map(cv -> new CvBilgiDto(cv.getDosyaAdi(),cv.getBoyut(),cv.getYuklemeTarihi()))
                .orElse(null);
    }

    /**
     * Kayitli CV'yi mevcut CvParseService ile isler ve donen bilgilerden
     * yetenekler/ozet alanlarini adaya yazar. Ad, e-posta ve telefon bilerek
     * ellenmez: bu alanlari aday basvururken kendisi girdi, LLM ciktisi
     * onlarin uzerine yazmamali.
     */
    @Transactional
    public Aday cvdenGuncelle(Long adayId){
        Aday aday = getirById(adayId);
        CvDosya cv = cvDosyaRepository.findByAdayId(adayId)
                .orElseThrow(() -> new GecersizIstekException(
                        "Bu adayin kayitli bir CV dosyasi yok"));

        CvParseDto sonuc = cvParseService.parseEt(cv.getIcerik());

        if (sonuc.yetenekler() != null && !sonuc.yetenekler().isBlank()) {
            aday.setYetenekler(sonuc.yetenekler());
        }
        if (sonuc.ozet() != null && !sonuc.ozet().isBlank()) {
            aday.setOzet(sonuc.ozet());
        }
        return adayRepository.save(aday);
    }

    public CvDosya cvGetir(Long adayId){
        return cvDosyaRepository.findByAdayId(adayId)
                .orElseThrow(() -> new KaynakBulunamadiException("Bu adayın CV'si yok"));
    }
}
