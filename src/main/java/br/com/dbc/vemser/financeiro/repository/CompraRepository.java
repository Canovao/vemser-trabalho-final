package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Integer> {
    List<Compra> findAllByNumeroCartao(Long numeroCartao);
}
