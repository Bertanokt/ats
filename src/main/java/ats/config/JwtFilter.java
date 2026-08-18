package ats.config;

import ats.model.Kullanici;
import ats.repository.KullaniciRepository;
import ats.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final KullaniciRepository kullaniciRepository;

    public JwtFilter(JwtService jwtService, KullaniciRepository kullaniciRepository) {
        this.jwtService = jwtService;
        this.kullaniciRepository = kullaniciRepository;
    }

    /**
     * OncePerRequestFilter varsayilan olarak ERROR dispatch'ini atlar. O durumda
     * SecurityContext bos kalir, /error kimlik dogrulama ister ve gercek hata
     * (500/409) istemciye 401 olarak doner; kullanici da bosuna cikis yapmis olur.
     */
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest istek,
                                    HttpServletResponse cevap,
                                    FilterChain zincir) throws ServletException, IOException {

        // 1. Authorization basligini al
        String baslik = istek.getHeader("Authorization");

        // 2. Token yoksa veya format yanlissa: dokunmadan devam et
        if (baslik == null || !baslik.startsWith("Bearer ")) {
            zincir.doFilter(istek, cevap);
            return;
        }

        // 3. "Bearer " kismini at, token'i al
        String token = baslik.substring(7);

        // 4. Token gecerli mi
        if (jwtService.gecerliMi(token)) {
            String email = jwtService.emailOku(token);
            Optional<Kullanici> kullanici = kullaniciRepository.findByEmail(email);

            if (kullanici.isPresent()) {
                // 5. Spring'e "bu istegi su kullanici yapiyor" de
                var yetki = new SimpleGrantedAuthority("ROLE_" + kullanici.get().getRol().name());

                var kimlik = new UsernamePasswordAuthenticationToken(
                        kullanici.get(),      // kim
                        null,                 // sifre (token'la geldigi icin gerek yok)
                        List.of(yetki)        // yetkileri
                );

                SecurityContextHolder.getContext().setAuthentication(kimlik);
            }
        }

        // 6. Zinciri devam ettir
        zincir.doFilter(istek, cevap);
    }
}