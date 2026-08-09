package ats.controller;

import ats.dto.LoginCevabiDto;
import ats.dto.LoginIstegiDto;
import ats.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginCevabiDto giris(@RequestBody LoginIstegiDto istek) {
        return authService.giris(istek);
    }
}