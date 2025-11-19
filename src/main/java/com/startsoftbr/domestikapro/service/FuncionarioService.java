package com.startsoftbr.domestikapro.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.startsoftbr.domestikapro.dto.FuncionarioRequest;
import com.startsoftbr.domestikapro.dto.FuncionarioResponse;
import com.startsoftbr.domestikapro.entity.Funcionario;
import com.startsoftbr.domestikapro.entity.Usuario;
import com.startsoftbr.domestikapro.repository.FuncionarioRepository;
import com.startsoftbr.domestikapro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    private Long getUsuarioLogadoId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario u) {
            return u.getId();
        }
        throw new RuntimeException("Usuário não autenticado");
    }

    public FuncionarioResponse criar(FuncionarioRequest req) {
        Long idUsuario = getUsuarioLogadoId();

        Funcionario f = new Funcionario();
        f.setUsuarioId(idUsuario);
        f.setNome(req.getNome());
        f.setFuncao(req.getFuncao());

        funcionarioRepository.save(f);

        return new FuncionarioResponse(f.getId(), f.getNome(), f.getFuncao());
    }

    public List<FuncionarioResponse> listar() {
        Long idUsuario = getUsuarioLogadoId();

        return funcionarioRepository.findByUsuarioId(idUsuario)
                .stream()
                .map(f -> new FuncionarioResponse(f.getId(), f.getNome(), f.getFuncao()))
                .toList();
    }

    public FuncionarioResponse buscar(Long id) {
        Long idUsuario = getUsuarioLogadoId();

        Funcionario f = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionária não encontrada"));

        if (!f.getUsuarioId().equals(idUsuario)) {
            throw new RuntimeException("Acesso negado");
        }

        return new FuncionarioResponse(f.getId(), f.getNome(), f.getFuncao());
    }

    public FuncionarioResponse editar(Long id, FuncionarioRequest req) {
        Long idUsuario = getUsuarioLogadoId();

        Funcionario f = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionária não encontrada"));

        if (!f.getUsuarioId().equals(idUsuario)) {
            throw new RuntimeException("Acesso negado");
        }

        f.setNome(req.getNome());
        f.setFuncao(req.getFuncao());

        funcionarioRepository.save(f);

        return new FuncionarioResponse(f.getId(), f.getNome(), f.getFuncao());
    }

    public void remover(Long id) {
        Long idUsuario = getUsuarioLogadoId();

        Funcionario f = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionária não encontrada"));

        if (!f.getUsuarioId().equals(idUsuario)) {
            throw new RuntimeException("Acesso negado");
        }

        funcionarioRepository.delete(f);
    }
}
