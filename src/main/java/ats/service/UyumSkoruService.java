package ats.service;

import ats.dto.UyumSkoruDto;
import ats.exception.KaynakBulunamadiException;
import ats.model.Aday;
import ats.model.Basvuru;
import ats.model.Ilan;
import ats.repository.BasvuruRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class UyumSkoruService {

    private final BasvuruRepository basvuruRepository;

    public UyumSkoruService(BasvuruRepository basvuruRepository) {
        this.basvuruRepository = basvuruRepository;
    }

    public UyumSkoruDto hesapla(Long basvuruId) {
        Basvuru basvuru = basvuruRepository.findById(basvuruId)
                .orElseThrow(() -> new KaynakBulunamadiException("Basvuru bulunamadi: " + basvuruId));

        Ilan ilan = basvuru.getIlan();
        Aday aday = basvuru.getAday();

        Set<String> istenenler = ayikla(ilan.getNitelikler());
        Set<String> adaydakiler = ayikla(aday.getYetenekler());

        // Ilan hicbir nitelik istemiyorsa skor hesaplanamaz
        if (istenenler.isEmpty()) {
            return new UyumSkoruDto(0, List.of(), List.of());
        }

        List<String> eslesenler = new ArrayList<>();
        List<String> eksikler = new ArrayList<>();

        for (String istenen : istenenler) {
            if (adaydakiler.contains(istenen)) {
                eslesenler.add(istenen);
            } else {
                eksikler.add(istenen);
            }
        }

        int skor = (eslesenler.size() * 100) / istenenler.size();

        return new UyumSkoruDto(skor, eslesenler, eksikler);
    }

    // "Java, Spring, SQL" -> ["java", "spring", "sql"]
    private Set<String> ayikla(String metin) {
        if (metin == null || metin.isBlank()) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(metin.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }
}