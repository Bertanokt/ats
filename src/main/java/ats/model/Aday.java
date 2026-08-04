package ats.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ad soyad bos olamaz")
    private String adSoyad;
    @NotBlank(message = "Ad soyad bos olamaz")
    @Email(message = "Gecerli bir email adresi giriniz")
    private String email;

    private String telefon;

    @NotBlank(message = "Yetenekler bos olamaz")
    private String yetenekler;

    private String ozet;


}
