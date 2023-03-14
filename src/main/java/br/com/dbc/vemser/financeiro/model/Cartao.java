package br.com.dbc.vemser.financeiro.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Cartao {

    @Id
    @Column(name = "NUMERO_CARTAO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CARTAO_SEQ")
    @SequenceGenerator(name = "CARTAO_SEQ", sequenceName = "SEQ_CARTAO", allocationSize = 1)
    private Long numeroCartao;

    @Column(name = "NUMERO_CONTA")
    private Integer numeroConta;

    @Column(name = "DATA_EXPEDICAO")
    private LocalDate dataExpedicao;

    @Column(name = "CODIGO_SEGURANCA")
    private Integer codigoSeguranca;

    @Column(name = "TIPO")
    private TipoCartao tipo;

    @Column(name = "VENCIMENTO")
    private LocalDate vencimento;

    @Column(name = "STATUS")
    private Status status = Status.ATIVO;

}
