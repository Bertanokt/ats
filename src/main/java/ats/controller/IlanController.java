package ats.controller;

import ats.model.Ilan;
import ats.service.IlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ilanlar")
public class IlanController {

    private final IlanService ilanService;

    public IlanController(IlanService ilanService) {
        this.ilanService = ilanService;
    }

    @PostMapping
    public Ilan olustur(@Valid @RequestBody Ilan ilan) {
        return ilanService.olustur(ilan);
    }

    @GetMapping
    public List<Ilan> hepsiniGetir() {
        return ilanService.hepsiniGetir();
    }

    @GetMapping("/{id}")
    public Ilan idileGetir(@PathVariable Long id) {
        return ilanService.getirById(id);
    }

    @DeleteMapping("/{id}")
    public void sil(@PathVariable Long id) {
        ilanService.sil(id);
    }

}