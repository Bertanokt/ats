package ats.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Aktivite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Basvuru basvuru;    // bu aktivite hangi başvuruya ait (birden fazla aktivite bir başvuru)

    @Enumerated(EnumType.STRING)
    private AktiviteTipi tip;

    private String icerik;  //görüşmenin metni

    private Integer puan;   // sadece değerlendirme için, diğerlerinde null

    private LocalDateTime tarih;   //Gün içinde birden fazla aktivite olabilir saat bilgisi lazım

}
