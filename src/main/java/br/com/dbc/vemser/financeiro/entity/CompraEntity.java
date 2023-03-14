package br.com.dbc.vemser.financeiro.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class CompraEntity {

    @Id
    @Column(name = "ID_COMPRA")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPRA_SEQ")
    @SequenceGenerator(name = "COMPRA_SEQ", sequenceName = "SEQ_COMPRA", allocationSize = 1)
    private Integer idCompra;

    @Column(name = "NUMERO_CARTAO")
    private Long numeroCartao;

    @Column(name = "DOC_VENDEDOR")
    private String docVendedor;

    @Column(name = "DATA")
    private LocalDate data;

    @OneToMany(mappedBy = "compra")
    private List<ItemEntity> items;
}
