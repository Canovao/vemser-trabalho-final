package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.entity.CartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartaoRepository extends JpaRepository<CartaoEntity, Integer> {
    @Query(value = "UPDATE CARTAO SET STATUS = 0 WHERE NUMERO_CARTAO = :numeroCartao")
    CartaoEntity softDelete(Long numeroCartao);

    List<CartaoEntity> findByNumeroConta(Integer numeroConta);

    Optional<CartaoEntity> findByNumeroCartao(Long numeroCartao);
}
