package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.ClienteEntity;
import br.com.dbc.vemser.financeiro.entity.ContatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContatoRepository extends JpaRepository<ContatoEntity, Integer> {
    List<ContatoEntity> findAllByCliente(ClienteEntity cliente);
}
