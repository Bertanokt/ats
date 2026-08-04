package ats.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Ilan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pozisyon;
    private String departman;
    private String nitelikler;
    private String aciklama;

    @Enumerated(EnumType.STRING)
    private IlanDurumu durum;


}
