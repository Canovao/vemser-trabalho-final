package br.com.dbc.vemser.financeiro.controller;

import br.com.dbc.vemser.financeiro.dto.CompraCreateDTO;
import br.com.dbc.vemser.financeiro.dto.CompraDTO;
import br.com.dbc.vemser.financeiro.dto.CompraItensDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/compra")
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Compra")
public class CompraController implements ControleListar<List<CompraDTO>>, ControleAdicionar<CompraCreateDTO, CompraItensDTO>{

    private final CompraService compraService;

    @Override
    @GetMapping("/listar")
    public ResponseEntity<List<CompraDTO>> listar(@RequestHeader("login") String login,
                                                  @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(compraService.list(login, senha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraItensDTO> getById(@RequestHeader("login") String login,
                                                  @RequestHeader("senha") String senha,
                                                  @PathVariable("id") Integer id) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(compraService.getById(login, senha, id));
    }

    @Operation(summary = "Retorna as compras de um cartão do cliente", description = "Retorna as compras de um cartão do cliente do Banco de Dados")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Compras retornadas"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping("/{numeroCartao}/cartao")
    public ResponseEntity<List<CompraItensDTO>> listarComprasDoCartao(@NotNull @PathVariable("numeroCartao") Long numeroCartao,
                                                                      @RequestHeader("numeroConta") Integer numeroConta,
                                                                      @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(compraService.retornarComprasCartao(numeroCartao, numeroConta, senha));
    }

    @Override
    @PostMapping("/{numeroCartao}/cartao")
    public ResponseEntity<CompraItensDTO> adicionar(@RequestBody @Valid CompraCreateDTO dado,
                                                @RequestHeader("numeroConta") Integer numeroConta,
                                                @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(compraService.adicionar(dado, numeroConta, senha));
    }
}
