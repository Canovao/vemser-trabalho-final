package br.com.dbc.vemser.financeiro.dto;

import br.com.dbc.vemser.financeiro.entity.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClienteDTO extends ClienteCreateDTO{

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer idCliente;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Status status;
}