package br.com.dbc.vemser.financeiro.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Contato {

    @Id
    @Column(name = "ID_CONTATO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTATO_SEQ")
    @SequenceGenerator(name = "CONTATO_SEQ", sequenceName = "SEQ_CONTATO", allocationSize = 1)
    private Integer idContato;

    @Column(name = "ID_CLIENTE")
    private Integer idCliente;

    @Column(name = "TELEFONE")
    private String telefone;

    @Column(name = "EMAIL")
    private String email;
}
