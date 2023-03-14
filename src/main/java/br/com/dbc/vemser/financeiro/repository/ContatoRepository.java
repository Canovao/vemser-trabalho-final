package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoRepository extends JpaRepository<Contato, Integer> {
}
