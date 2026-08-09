package ats.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtGirisNoktasi implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest istek, HttpServletResponse cevap,
                         AuthenticationException hata) throws IOException {
        cevap.setStatus(HttpServletResponse.SC_UNAUTHORIZED);   // 401
        cevap.setContentType("application/json;charset=UTF-8");
        cevap.getWriter().write(
                "{\"status\":401,\"hata\":\"Unauthorized\",\"mesaj\":\"Giris yapmaniz gerekiyor\"}"
        );
    }
}