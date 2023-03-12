package br.com.dbc.vemser.financeiro.controller;

import br.com.dbc.vemser.financeiro.dto.ClienteCreateDTO;
import br.com.dbc.vemser.financeiro.dto.ClienteDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/cliente")
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Cliente")
public class ClienteController implements ControleListar<List<ClienteDTO>> {

    private final ClienteService clienteService;

    @Operation(summary = "Exibir nome/cpf do cliente", description = "Exibir inforamções do cliente, nome e cpf.")
    @GetMapping("/{idCliente}")
    public ResponseEntity<ClienteDTO> exibirCliente(@PathVariable("idCliente") Integer idCliente) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(clienteService.visualizarCliente(idCliente));
    }

    @Operation(summary = "Alterar nome do cliente", description = "Alternar nome de exibição no banco.")
    @PutMapping("/{idCliente}")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable("idCliente") Integer idCliente,
                                                @RequestBody @Valid ClienteCreateDTO cliente) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Atualizando Cliente!");
        ClienteDTO clienteDTO = clienteService.alterarCliente(idCliente, cliente);
        log.info("Cliente Atualizado!");
        return ResponseEntity.ok(clienteDTO);
    }

    @Override
    @Operation(summary = "FUNÇÃO ADM", description = "LISTAR TODOS OS CLIENTES DO BANCO")
    @GetMapping("/lista")
    public ResponseEntity<List<ClienteDTO>> listar(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(clienteService.listarClientes(login, senha));
    }
}
