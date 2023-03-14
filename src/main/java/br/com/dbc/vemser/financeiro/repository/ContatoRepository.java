package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.ClienteEntity;
import br.com.dbc.vemser.financeiro.entity.ContatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContatoRepository extends JpaRepository<ContatoEntity, Integer> {
    List<ContatoEntity> findAllByCliente(ClienteEntity cliente);
}
