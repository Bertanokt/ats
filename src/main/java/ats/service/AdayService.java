package ats.service;

import ats.exception.KaynakBulunamadiException;
import ats.model.Aday;
import ats.repository.AdayRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class AdayService {
    private final AdayRepository adayRepository;

    public AdayService(AdayRepository adayRepository) {
        this.adayRepository = adayRepository;
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

    public void  sil(Long id){
        adayRepository.deleteById(id);
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
}
