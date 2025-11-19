package com.startsoftbr.domestikapro.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.startsoftbr.domestikapro.entity.RegistroPonto;

public interface RegistroPontoRepository extends JpaRepository<RegistroPonto, Long> {

    List<RegistroPonto> findByFuncionarioIdAndDataHoraBetween(
            Long funcionarioId,
            LocalDateTime inicio,
            LocalDateTime fim);

    List<RegistroPonto> findByUsuarioIdAndDataHoraBetween(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fim);
}
