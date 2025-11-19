package com.startsoftbr.domestikapro.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.startsoftbr.domestikapro.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Gera uma chave forte e fixa depois em uma ENV
    private final String SECRET = "uma_chave_secreta_muito_grande_mesmo_para_jwt_256_bits";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plus(7, ChronoUnit.DAYS);

        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("userId", usuario.getId()) 
                .setIssuedAt(Date.from(agora))
                .setExpiration(Date.from(expiracao))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String extrairEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Long extrairUserId(String token) {
        Object id = getClaims(token).get("userId");
        if (id instanceof Integer i) {
            return i.longValue();
        }
        if (id instanceof Long l) {
            return l;
        }
        return Long.valueOf(id.toString());
    }

    public boolean tokenValido(String token, String emailEsperado) {
        try {
            Claims claims = getClaims(token);
            String email = claims.getSubject();
            Date exp = claims.getExpiration();
            return email.equals(emailEsperado) && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
