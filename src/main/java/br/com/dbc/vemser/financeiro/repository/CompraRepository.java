package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<CompraEntity, Integer> {
    List<CompraEntity> findAllByNumeroCartao(Long numeroCartao);
}
