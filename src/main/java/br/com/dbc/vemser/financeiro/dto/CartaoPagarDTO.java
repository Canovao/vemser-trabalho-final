package br.com.dbc.vemser.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartaoPagarDTO {
    @NotNull(message = "Informe o número do cartão!")
    private Long numeroCartao;
    @NotNull(message = "Código inválido")
    private Integer codigoSeguranca;
}
