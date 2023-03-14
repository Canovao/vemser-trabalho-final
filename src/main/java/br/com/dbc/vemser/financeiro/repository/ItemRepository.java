package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByIdCompra(Integer id);
}
