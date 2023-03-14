package br.com.dbc.vemser.financeiro.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Transferencia{

    @Id
    @Column(name = "ID_TRANSFERENCIA")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANSFERENCIA_SEQ")
    @SequenceGenerator(name = "TRANSFERENCIA_SEQ", sequenceName = "SEQ_TRANSFERENCIA", allocationSize = 1)
    private Integer idTransferencia;//PK

    @Column(name = "CONTA_ENVIOU")
    private Long contaEnviou;

    @Column(name = "CONTA_RECEBEU")
    private Long contaRecebeu;

    @Column(name = "VALOR")
    private Double valor;
}