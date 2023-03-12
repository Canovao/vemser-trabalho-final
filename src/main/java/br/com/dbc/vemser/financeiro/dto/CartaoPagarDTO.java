package br.com.dbc.vemser.financeiro.dto;

import br.com.dbc.vemser.financeiro.model.TipoCartao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartaoPagarDTO {
    @NotNull(message = "Informe o número do cartão!")
    private Long numeroCartao;
    @NotNull(message = "Código inválido")
    private Integer codigoSeguranca;
}
