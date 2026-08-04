package ats.service;

import ats.model.Ilan;
import ats.repository.IlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IlanService {

    private final IlanRepository ilanRepository;

    public IlanService(IlanRepository ilanRepository) {
        this.ilanRepository = ilanRepository;
    }

    public Ilan olustur(Ilan ilan) {
        return ilanRepository.save(ilan);
    }

    public List<Ilan> hepsiniGetir() {
        return ilanRepository.findAll();
    }

    public Ilan getirById(Long id) {
        return ilanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İlan bulunamadı: " + id));
    }

    public void sil(Long id) {
        ilanRepository.deleteById(id);
    }
}