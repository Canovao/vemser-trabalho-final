package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<CompraEntity, Integer> {
    List<CompraEntity> findByNumeroCartao(Long numeroCartao);
}
