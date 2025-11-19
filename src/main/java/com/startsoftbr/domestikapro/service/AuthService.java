package com.startsoftbr.domestikapro.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.startsoftbr.domestikapro.dto.AuthRequest;
import com.startsoftbr.domestikapro.dto.AuthResponse;
import com.startsoftbr.domestikapro.dto.RegisterRequest;
import com.startsoftbr.domestikapro.entity.Usuario;
import com.startsoftbr.domestikapro.repository.UsuarioRepository;
import com.startsoftbr.domestikapro.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    public AuthResponse register(RegisterRequest req) {
        if (usuarioRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        Usuario u = new Usuario();
        u.setNome(req.getNome());
        u.setEmail(req.getEmail());
        u.setSenhaHash(encoder.encode(req.getSenha()));

        usuarioRepository.save(u);

        String token = jwtService.gerarToken(u);
        return new AuthResponse(token, u.getNome());
    }

    public AuthResponse login(AuthRequest req) {
        Usuario u = usuarioRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!encoder.matches(req.getSenha(), u.getSenhaHash())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.gerarToken(u);
        return new AuthResponse(token, u.getNome());
    }
}

