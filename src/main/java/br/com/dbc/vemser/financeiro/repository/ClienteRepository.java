package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
