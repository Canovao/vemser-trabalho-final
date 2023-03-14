package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {
}
