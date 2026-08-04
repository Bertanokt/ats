package ats.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
public class Ilan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pozisyon bos olamaz")
    private String pozisyon;
    @NotBlank(message = "Departman bos olamaz")
    private String departman;
    @NotBlank(message = "Nitelikler bos olamaz")
    private String nitelikler;

    private String aciklama;

    @Enumerated(EnumType.STRING)
    private IlanDurumu durum;


}
