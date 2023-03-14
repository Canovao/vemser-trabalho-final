package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.TransferenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferenciaRepository extends JpaRepository<TransferenciaEntity, Integer> {
}
