package ats.service;

import ats.dto.LoginCevabiDto;
import ats.dto.LoginIstegiDto;
import ats.exception.GecersizIstekException;
import ats.model.Kullanici;
import ats.repository.KullaniciRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
    private final KullaniciRepository kullaniciRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(KullaniciRepository kullaniciRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.kullaniciRepository = kullaniciRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginCevabiDto giris(LoginIstegiDto istek){
        //1. kullanıcıyı bul
        Kullanici kullanici = kullaniciRepository.findByEmail(istek.email())
                .orElseThrow(() -> new GecersizIstekException("Email veya şifre hatali"));
        //2. Sifreyi doğrula
        if (!passwordEncoder.matches(istek.sifre(), kullanici.getSifre())){
            throw new GecersizIstekException("Email veya sifre hatali");
        }
        //3. token üret
        String token = jwtService.tokenUret(kullanici.getEmail(), kullanici.getRol().name());

        return new LoginCevabiDto(
                token,
                kullanici.getEmail(),
                kullanici.getAdSoyad(),
                kullanici.getRol()
        );
    }


}
