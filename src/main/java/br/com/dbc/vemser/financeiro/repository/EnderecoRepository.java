package br.com.dbc.vemser.financeiro.repository;

import br.com.dbc.vemser.financeiro.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
}
