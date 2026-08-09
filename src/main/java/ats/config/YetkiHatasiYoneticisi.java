package ats.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class YetkiHatasiYoneticisi implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest istek, HttpServletResponse cevap,
                       AccessDeniedException hata) throws IOException {
        cevap.setStatus(HttpServletResponse.SC_FORBIDDEN);   // 403
        cevap.setContentType("application/json;charset=UTF-8");
        cevap.getWriter().write(
                "{\"status\":403,\"hata\":\"Forbidden\",\"mesaj\":\"Bu islem icin yetkiniz yok\"}"
        );
    }
}