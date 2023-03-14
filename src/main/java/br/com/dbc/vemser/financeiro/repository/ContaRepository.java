package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Integer> {

}
