package ats.controller;


import ats.dto.CvBilgiDto;
import ats.dto.CvParseDto;
import ats.model.Aday;
import ats.model.Aday;
import ats.model.CvDosya;
import ats.model.Ilan;
import ats.service.AdayService;
import ats.service.CvParseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/adaylar")
public class AdayController {

    private final AdayService adayService;
    private final CvParseService cvParseService;

    public AdayController(AdayService adayService, CvParseService cvParseService) {
        this.adayService = adayService;
        this.cvParseService = cvParseService;
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

    @PutMapping("/{id}")
    public Aday guncelle(@PathVariable Long id, @Valid @RequestBody Aday aday) {
        return adayService.guncelle(id, aday);
    }

    @PostMapping("/cv-parse")
    public CvParseDto cvParse(@RequestParam("dosya") MultipartFile dosya) {
        return cvParseService.parseEt(dosya);
    }

    // Kayitli CV'den bilgi cikarma. Korumali: public uclarda parse calismaz
    // (API maliyeti ve kotuye kullanim riski).
    @PostMapping("/{id}/cv-parse-kayitli")
    public Aday cvdenGuncelle(@PathVariable Long id) {
        return adayService.cvdenGuncelle(id);
    }

    @GetMapping("/{id}/cv-bilgi")
    public CvBilgiDto cvBilgisi(@PathVariable Long id) {
        return adayService.cvBilgisi(id);
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<byte[]> cvIndir(@PathVariable Long id) {
        CvDosya cv = adayService.cvGetir(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + cv.getDosyaAdi() + "\"")
                .body(cv.getIcerik());
    }
}