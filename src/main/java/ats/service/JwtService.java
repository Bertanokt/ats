package ats.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String gizliAnahtar;

    @Value("${jwt.expiration}")
    private long gecerlilikSuresi;


    // --- 1. Token uret ---
    public String tokenUret(String email, String rol){
        Date simdi = new Date();
        Date bitis = new Date(simdi.getTime() + gecerlilikSuresi);

        return Jwts.builder()
                .subject(email)              // token kime ait
                .claim("rol", rol)           // ek bilgi: rolu
                .issuedAt(simdi)             // ne zaman uretildi
                .expiration(bitis)           // ne zaman gecersiz olacak
                .signWith(anahtar())         // imzala
                .compact();                  // metne cevir
    }

    // --- 2. Token'dan email oku ---
    public String emailOku(String token) {
        return iceriginiOku(token).getSubject();
    }

    // --- 3. Token gecerli mi ---
    public boolean gecerliMi(String token) {
        try {
            iceriginiOku(token);   // imza bozuksa veya suresi dolduysa hata firlatir
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Yardimci: token'i coz ---
    private Claims iceriginiOku(String token) {
        return Jwts.parser()
                .verifyWith(anahtar())       // imzayi dogrula
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // --- Yardimci: gizli anahtari kullanilabilir hale getir ---
    private SecretKey anahtar() {
        return Keys.hmacShaKeyFor(gizliAnahtar.getBytes(StandardCharsets.UTF_8));
    }

}
