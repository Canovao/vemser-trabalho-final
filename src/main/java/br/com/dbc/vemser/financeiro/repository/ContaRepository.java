package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContaRepository extends JpaRepository<ContaEntity, Integer> {
    @Query(value = "UPDATE conta SET status = 0 WHERE numero_conta = :numeroConta")
    ContaEntity softDelete(Integer numeroConta);
}
