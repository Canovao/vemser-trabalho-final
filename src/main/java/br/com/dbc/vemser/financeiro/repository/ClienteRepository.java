package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Integer> {
    Optional<ClienteEntity> findByCpf(String cpf);

    @Query(value = "UPDATE CLIENTE SET STATUS = 0 WHERE ID_CLIENTE = :id")
    ClienteEntity softDelete(Integer id);
}
