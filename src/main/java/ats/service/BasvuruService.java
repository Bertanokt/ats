package ats.service;

import ats.dto.AsamaSayimDto;
import ats.exception.CakismaException;
import ats.exception.GecersizIstekException;
import ats.exception.KaynakBulunamadiException;
import ats.model.Aday;
import ats.model.Basvuru;
import ats.model.BasvuruAsamasi;
import ats.model.Ilan;
import ats.repository.AdayRepository;
import ats.repository.BasvuruRepository;
import ats.repository.IlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BasvuruService {

    private final BasvuruRepository basvuruRepository;
    private final AdayRepository adayRepository;
    private final IlanRepository ilanRepository;

    public BasvuruService(BasvuruRepository basvuruRepository,
                          AdayRepository adayRepository,
                          IlanRepository ilanRepository) {
        this.basvuruRepository = basvuruRepository;
        this.adayRepository = adayRepository;
        this.ilanRepository = ilanRepository;
    }

    public Basvuru olustur(Long adayId, Long ilanId){
        // 1. Aday ve ilan gerçekten var mı?

      Aday aday = adayRepository.findById(adayId)
              .orElseThrow(() -> new KaynakBulunamadiException("Aday bulunamadı: " + adayId));
        Ilan ilan = ilanRepository.findById(ilanId)
                .orElseThrow(() -> new KaynakBulunamadiException("Ilan bulunamadi: " + ilanId));

        // 2. Bu aday bu ilana zaten basvurmus mu?
        if (basvuruRepository.existsByAdayIdAndIlanId(adayId, ilanId)) {
            throw new CakismaException("Bu aday bu ilana zaten basvurmus");

        }
        // 3. Yeni basvuruyu kur
        Basvuru basvuru = new Basvuru();
        basvuru.setAday(aday);
        basvuru.setIlan(ilan);
        basvuru.setAsama(BasvuruAsamasi.BASVURU);   // her basvuru ilk asamada baslar
        basvuru.setBasvuruTarihi(LocalDate.now());

        return basvuruRepository.save(basvuru);
}
       public List<Basvuru> hepsiniGetir(){
    return basvuruRepository.findAll();
}
public Basvuru getirById(Long id) {
    return basvuruRepository.findById(id)
            .orElseThrow(() -> new KaynakBulunamadiException("Basvuru bulunamadi: " + id));
  }
  public Basvuru asamaIlerlet(Long basvuruId){
        Basvuru basvuru = getirById(basvuruId);
        BasvuruAsamasi mevcut = basvuru.getAsama();

        if (mevcut == BasvuruAsamasi.ISE_ALINDI || mevcut == BasvuruAsamasi.ELENDI){
    throw new CakismaException("Bu basvuru sonlanmıs, ilerletilemez: " + mevcut);
      }

        BasvuruAsamasi sonraki = switch (mevcut){
            case BASVURU -> BasvuruAsamasi.ON_ELEME;
            case ON_ELEME -> BasvuruAsamasi.MULAKAT;
            case MULAKAT -> BasvuruAsamasi.TEKLIF;
            default -> throw new GecersizIstekException("Gecersiz arama: " + mevcut);
        };
        basvuru.setAsama(sonraki);
        return basvuruRepository.save(basvuru);
  }

  public Basvuru ele(Long basvuruId){
        Basvuru basvuru = getirById(basvuruId);
        if (basvuru.getAsama() == BasvuruAsamasi.ISE_ALINDI){
            throw new CakismaException("Ise alınmıs aday elenemez");
        }
        basvuru.setAsama(BasvuruAsamasi.ELENDI);
        return basvuruRepository.save(basvuru);
  }

  public List<AsamaSayimDto> ilanAsamaRaporu(Long ilanId){
        return basvuruRepository.ilanAsamaSayimlari(ilanId);  //spesifik ilana başvuranların aşama dağılımı.
  }

  public List<AsamaSayimDto> funnelRaporu(){
        return basvuruRepository.genelAsamaSayimlari(); //Tüm ilanlara başvuranşarın aşama dağılımı
  }
  public List<Basvuru> ilanBasvurulari(Long ilanId){
        return basvuruRepository.findByIlanId(ilanId);  //kimler başvurmuş listesi
  }

}
