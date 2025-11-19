package com.startsoftbr.domestikapro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.startsoftbr.domestikapro.entity.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    List<Funcionario> findByUsuarioId(Long usuarioId);
}
