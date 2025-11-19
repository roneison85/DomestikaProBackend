package com.startsoftbr.domestikapro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.startsoftbr.domestikapro.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
