package ats.service;

import ats.exception.KaynakBulunamadiException;
import ats.model.Aday;
import ats.repository.AdayRepository;
import ats.repository.IlanRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class AdayService {
    private final AdayRepository adayRepository;
    private final IlanRepository ilanRepository;

    public AdayService(AdayRepository adayRepository, IlanRepository ilanRepository) {
        this.adayRepository = adayRepository;
        this.ilanRepository = ilanRepository;
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
        ilanRepository.deleteById(id);
    }
}
