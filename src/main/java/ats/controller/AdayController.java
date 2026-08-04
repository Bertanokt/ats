package ats.controller;


import ats.model.Aday;
import ats.model.Aday;
import ats.service.AdayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adaylar")
public class AdayController {

    private final AdayService adayService;

    public AdayController(AdayService adayService) {
        this.adayService = adayService;
    }

    @PostMapping
    public Aday olustur(@Valid @RequestBody Aday aday) {
        return adayService.olustur(aday);
    }

    @GetMapping
    public List<Aday> hepsiniGetir() {
        return adayService.hepsiniGetir();
    }

    @GetMapping("/{id}")
    public Aday idileGetir(@PathVariable Long id) {
        return adayService.getirById(id);
    }

    @DeleteMapping("/{id}")
    public void sil(@PathVariable Long id) {
        adayService.sil(id);
    }
}

