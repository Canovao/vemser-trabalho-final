package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartaoRepository extends JpaRepository<Cartao, Integer> {
    @Query(value = "UPDATE CARTAO SET STATUS = 0 WHERE NUMERO_CARTAO = :numeroCartao")
    Cartao softDelete(Long numeroCartao);

    List<Cartao> findAllByNumeroConta(Integer numeroConta);

    Optional<Cartao> findByNumeroCartao(Long numeroCartao);
}
