package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Cliente;
import br.com.dbc.vemser.financeiro.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Integer> {
    List<Contato> findAllByCliente(Cliente cliente);
}
