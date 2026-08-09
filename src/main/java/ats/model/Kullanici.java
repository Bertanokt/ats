package ats.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Kullanici {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Email bos olamaz")
    @Email(message = "Gecerli bir email adresi giriniz")
    private String email;

    @JsonIgnore
    @NotBlank(message = "Sifre bos olamaz")
    private String sifre;

    @NotBlank(message = "Ad soyad bos olamaz")
    private String adSoyad;

    @Enumerated(EnumType.STRING)
    private Rol rol;
}
