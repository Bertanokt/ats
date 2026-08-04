package ats.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Basvuru {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Aday aday;      // bu başvuru hangi adaya ait

    @ManyToOne
    private Ilan ilan;      // bu başvuru hangi ilana ait

    @Enumerated(EnumType.STRING)
    private BasvuruAsamasi asama;

    private LocalDate basvuruTarihi;

    @OneToMany(mappedBy = "basvuru", cascade = CascadeType.ALL)   //
    @OrderBy("tarih ASC")           //aktiviteler veritabanından tarih sırasıyla gelsin
    private List<Aktivite> aktiviteler = new ArrayList<>();
}