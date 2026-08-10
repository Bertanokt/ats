package ats.controller;


import ats.dto.AsamaSayimDto;
import ats.dto.BasvuruDto;
import ats.dto.IseAlinanDto;
import ats.dto.UyumSkoruDto;
import ats.model.Basvuru;
import ats.service.BasvuruService;
import ats.service.UyumSkoruService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/basvurular")
public class BasvuruController {

    private final BasvuruService basvuruService;
    private final UyumSkoruService uyumSkoruService;

    public BasvuruController(BasvuruService basvuruService,
                             UyumSkoruService uyumSkoruService) {
        this.basvuruService = basvuruService;
        this.uyumSkoruService = uyumSkoruService;
    }

    @PostMapping
    public Basvuru olustur(@RequestParam Long adayId, @RequestParam Long ilanId) {
        return basvuruService.olustur(adayId, ilanId);
    }

    @GetMapping
    public List<BasvuruDto> hepsiniGetir() {
        return basvuruService.hepsiniGetir();
    }
    @GetMapping("/{id}/uyum")
    public UyumSkoruDto uyumSkoru(@PathVariable Long id) {
        return uyumSkoruService.hesapla(id);
    }

    // Sabit yol, /{id} kalibindan once tanimli olmali
    @GetMapping("/ise-alinanlar")
    public List<IseAlinanDto> iseAlinanlar(@RequestParam(required = false) String departman) {
        return basvuruService.iseAlinanlar(departman);
    }

    @GetMapping("/{id}")
    public Basvuru getirById(@PathVariable Long id) {
        return basvuruService.getirById(id);
    }


    @PostMapping("/{id}/ilerlet")
    public Basvuru ilerlet(@PathVariable Long id) {
        return basvuruService.asamaIlerlet(id);
    }

    @PostMapping("/{id}/ele")
    public Basvuru ele(@PathVariable Long id) {
        return basvuruService.ele(id);
    }

    @GetMapping("/rapor/funnel")
    public List<AsamaSayimDto> funnelRaporu(){
        return basvuruService.funnelRaporu();
    }

    @GetMapping("/ilan/{ilanId}/asama-raporu")
    public List<AsamaSayimDto> ilanAsamaRaporu(@PathVariable Long ilanId){
        return basvuruService.ilanAsamaRaporu(ilanId);
    }

    @GetMapping("/ilan/{ilanId}")
    public List<BasvuruDto> ilanBasvurulari(@PathVariable Long ilanId){
        return basvuruService.ilanBasvurulari(ilanId);
    }



}