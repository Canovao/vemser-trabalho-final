package br.com.dbc.vemser.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompraDTO {
    private Integer idCompra;
    private Long numeroCartao;
    private String docVendedor;
    private LocalDate data;

}