package br.com.dbc.vemser.financeiro.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class CartaoDeCredito extends Cartao {

    @Column(name = "LIMITE")
    private double limite;

    public CartaoDeCredito() {
        this.limite = 1000;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    @Override
    public String toString() {
        return  super.toString() + "\n CartaoDeCredito{" +
                "limite=" + limite +
                '}';
    }
}
