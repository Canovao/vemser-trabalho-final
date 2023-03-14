package br.com.dbc.vemser.financeiro.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Item {

    @Id
    @Column(name = "ID_ITEM")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITEM_SEQ")
    @SequenceGenerator(name = "ITEM_SEQ", sequenceName = "SEQ_ITEM", allocationSize = 1)
    private Integer idItem;//PK

//    @Column(name = "ID_COMPRA")
//    private Integer idCompra;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "VALOR")
    private Double valor;

    @Column(name = "QUANTIDADE")
    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "ID_COMPRA")
    private Compra compra;

}
