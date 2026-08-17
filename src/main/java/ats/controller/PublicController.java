package ats.controller;

import ats.dto.PublicBasvuruDto;
import ats.dto.PublicIlanDto;
import ats.service.PublicBasvuruService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PublicBasvuruService publicBasvuruService;

    public PublicController(PublicBasvuruService publicBasvuruService) {
        this.publicBasvuruService = publicBasvuruService;
    }

    @GetMapping("/ilanlar")
    public List<PublicIlanDto> acikIlanlar() {
        return publicBasvuruService.acikIlanlar();
    }

    @GetMapping("/ilanlar/{id}")
    public PublicIlanDto ilanGetir(@PathVariable Long id) {
        return publicBasvuruService.acikIlanGetir(id);
    }

    @PostMapping("/basvuru")
    public PublicBasvuruDto basvuruYap(
            @RequestParam Long ilanId,
            @RequestParam String adSoyad,
            @RequestParam String email,
            @RequestParam(required = false) String telefon,
            @RequestParam(required = false) String not,
            @RequestParam MultipartFile cv) {

        Long basvuruId = publicBasvuruService.basvuruOlustur(
                ilanId, adSoyad, email, telefon, not, cv);

        return new PublicBasvuruDto(basvuruId, "Basvurunuz alindi");
    }
}