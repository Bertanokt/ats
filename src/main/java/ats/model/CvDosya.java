package ats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class CvDosya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "aday_id" , unique = true)
    @JsonIgnore
    private Aday aday;

    private String dosyaAdi;

    private String icerikTipi;

    private Long boyut;

    private LocalDateTime yuklemeTarihi;

    @Lob
    @JsonIgnore
    private byte[] icerik;
}
