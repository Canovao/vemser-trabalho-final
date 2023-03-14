package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, Integer> {
}
