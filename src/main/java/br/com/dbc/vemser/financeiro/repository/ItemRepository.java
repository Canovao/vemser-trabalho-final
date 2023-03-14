package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {
    List<ItemEntity> findAllByIdCompra(Integer id);
}
