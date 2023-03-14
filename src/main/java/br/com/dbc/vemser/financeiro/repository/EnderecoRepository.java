package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Integer> {
}
