package ats.service;

import ats.dto.AsamaSayimDto;
import ats.dto.BasvuruDto;
import ats.dto.IseAlinanDto;
import ats.exception.CakismaException;
import ats.exception.GecersizIstekException;
import ats.exception.KaynakBulunamadiException;
import ats.model.Aday;
import ats.model.Basvuru;
import ats.model.BasvuruAsamasi;
import ats.model.Ilan;
import ats.model.IlanDurumu;
import ats.repository.AdayRepository;
import ats.repository.BasvuruRepository;
import ats.repository.IlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BasvuruService {

    // Adayin o ilanda hala devam eden bir sureci oldugunu gosteren asamalar.
    // ELENDI ve ISE_ALINDI disarida: aday yeniden degerlendirilebilir ya da geri donebilir.
    private static final List<BasvuruAsamasi> AKTIF_ASAMALAR = List.of(
            BasvuruAsamasi.BASVURU,
            BasvuruAsamasi.ON_ELEME,
            BasvuruAsamasi.MULAKAT,
            BasvuruAsamasi.TEKLIF
    );

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

        // 2. Kapali ilana yeni basvuru alinmaz
        if (ilan.getDurum() == IlanDurumu.KAPALI) {
            throw new CakismaException("Bu ilan kapali, yeni basvuru alinmiyor");
        }

        // 3. Bu adayin bu ilanda devam eden bir basvurusu var mi?
        if (basvuruRepository.existsByAdayIdAndIlanIdAndAsamaIn(adayId, ilanId, AKTIF_ASAMALAR)) {
            throw new CakismaException("Bu adayin bu ilanda devam eden bir basvurusu var");

        }
        // 4. Yeni basvuruyu kur
        Basvuru basvuru = new Basvuru();
        basvuru.setAday(aday);
        basvuru.setIlan(ilan);
        basvuru.setAsama(BasvuruAsamasi.BASVURU);   // her basvuru ilk asamada baslar
        basvuru.setBasvuruTarihi(LocalDate.now());

        return basvuruRepository.save(basvuru);
}
       public List<BasvuruDto> hepsiniGetir(){
    return basvuruRepository.findAll().stream()
            .map(this::toDto)
                   .toList();
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
            case TEKLIF -> BasvuruAsamasi.ISE_ALINDI;
            default -> throw new GecersizIstekException("Gecersiz asama: " + mevcut);
        };
        basvuru.setAsama(sonraki);
        return basvuruRepository.save(basvuru);
  }

  /**
   * Basvuruyu ve bagli aktiviteleri siler.
   * Aktiviteler (gorusme notu, degerlendirme puani) basvuru baglami olmadan
   * anlamsizdir; bu yuzden basamakli silinir. Cascade Basvuru.aktiviteler
   * uzerinde zaten ALL olarak tanimli.
   */
  @Transactional
  public void sil(Long basvuruId){
        Basvuru basvuru = getirById(basvuruId);   // yoksa 404
        basvuruRepository.delete(basvuru);
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
  public List<BasvuruDto> ilanBasvurulari(Long ilanId){
        return basvuruRepository.findByIlanId(ilanId).stream()          //kimler başvurmuş listesi
                .map(this::toDto)
                .toList();
  }

  // Ise alinmis basvurular; departman verilmezse tumu doner
  public List<IseAlinanDto> iseAlinanlar(String departman){
        List<Basvuru> basvurular = (departman == null || departman.isBlank())
                ? basvuruRepository.findByAsama(BasvuruAsamasi.ISE_ALINDI)
                : basvuruRepository.findByAsamaAndIlan_Departman(BasvuruAsamasi.ISE_ALINDI, departman);

        return basvurular.stream()
                .map(this::toIseAlinanDto)
                .toList();
  }

    private BasvuruDto toDto(Basvuru b) {
        return new BasvuruDto(
                b.getId(),
                b.getAday().getId(),
                b.getAday().getAdSoyad(),
                b.getIlan().getId(),
                b.getIlan().getPozisyon(),
                b.getAsama(),
                b.getBasvuruTarihi(),
                b.getAktiviteler() == null ? 0 : b.getAktiviteler().size()
        );
    }

    private IseAlinanDto toIseAlinanDto(Basvuru b) {
        return new IseAlinanDto(
                b.getId(),
                b.getAday().getId(),
                b.getAday().getAdSoyad(),
                b.getIlan().getId(),
                b.getIlan().getPozisyon(),
                b.getIlan().getDepartman(),
                b.getBasvuruTarihi()
        );
    }


}
