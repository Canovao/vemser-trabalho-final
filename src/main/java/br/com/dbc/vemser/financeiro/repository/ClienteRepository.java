package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCpf(String cpf);

    @Query(value = "UPDATE CLIENTE SET STATUS = 0 WHERE ID_CLIENTE = :id")
    Cliente softDelete(Integer id);
}
