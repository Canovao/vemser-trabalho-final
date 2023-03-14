package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Integer> {
}
