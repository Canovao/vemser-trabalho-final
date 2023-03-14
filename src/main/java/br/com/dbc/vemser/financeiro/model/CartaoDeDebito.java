package br.com.dbc.vemser.financeiro.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class CartaoDeDebito extends Cartao {

    public CartaoDeDebito(){}

}
