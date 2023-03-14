package br.com.dbc.vemser.financeiro.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Conta {

    @Id
    @Column(name = "NUMERO_CONTA")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTA_SEQ")
    @SequenceGenerator(name = "COTNA_SEQ", sequenceName = "SEQ_CONTA", allocationSize = 1)
    private Integer numeroConta;

    @OneToOne
    @JoinColumn(name = "ID_CLIENTE")
    private Cliente cliente;

    @Column(name = "SENHA")
    private String senha;

    @Column(name = "AGENCIA")
    private Integer agencia;

    @Column(name = "SALDO")
    private Double saldo;

    @Column(name = "CHEQUE_ESPECIAL")
    private Double chequeEspecial = 200.0;

    @Column(name = "STATUS")
    private Status status;
}
