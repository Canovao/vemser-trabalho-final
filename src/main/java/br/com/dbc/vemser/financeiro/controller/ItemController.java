package br.com.dbc.vemser.financeiro.controller;

import br.com.dbc.vemser.financeiro.dto.ItemDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/item")
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Item")
public class ItemController implements ControleListar<List<ItemDTO>>{

    private final ItemService itemService;

    @Operation(summary = "Listar os itens de uma compra do Banco de Dados", description = "Lista todos os itens de uma compra do Banco de Dados")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Itens retornados"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping("/{idCompra}/compra")
    public ResponseEntity<List<ItemDTO>> listarItensDaCompra(@NotNull @PathVariable("idCompra") Integer idCompra,
                                                             @RequestHeader("numeroConta") Integer numeroConta,
                                                             @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return new ResponseEntity<>(itemService.listarItensPorIdCompra(idCompra, numeroConta, senha), HttpStatus.OK);
    }

    @Override
    @GetMapping("/lista")
    @Operation(summary = "FUNÇÃO ADM", description = "LISTAR TODOS OS ITENS DO BANCO")
    public ResponseEntity<List<ItemDTO>> listar(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(itemService.listar(login, senha));
    }
}
