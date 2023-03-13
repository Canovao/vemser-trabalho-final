package br.com.dbc.vemser.financeiro.controller;

import br.com.dbc.vemser.financeiro.dto.CartaoCreateDTO;
import br.com.dbc.vemser.financeiro.dto.CartaoDTO;
import br.com.dbc.vemser.financeiro.dto.CartaoPagarDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.model.TipoCartao;
import br.com.dbc.vemser.financeiro.service.CartaoService;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/cartao")
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Cartão")
public class CartaoController implements ControleListar<List<CartaoDTO>> {

    private final CartaoService cartaoService;

    @Operation(summary = "Retornar os cartões da conta", description = "Retornar os cartões da conta do cliente do Banco de Dados")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Cartões retornados"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping("/listarDaConta")
    public ResponseEntity<List<CartaoDTO>> listarPorNumeroConta(@RequestHeader("numeroConta") Integer numeroConta,
                                                       @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return new ResponseEntity<>(cartaoService.listarPorNumeroConta(numeroConta, senha), HttpStatus.OK);
    }

    @Operation(summary = "Adicionar um cartão à conta", description = "Adicionar um cartão à conta no Banco de Dados")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Cartão adicionado"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PostMapping("/criar/{tipo}")
    public ResponseEntity<CartaoDTO> criar(@RequestHeader("numeroConta") Integer numeroConta,
                                           @RequestHeader("senha") String senha,
                                           @PathVariable("tipo") TipoCartao tipo) throws Exception {
        return new ResponseEntity<>(cartaoService.criar(numeroConta, senha, tipo), HttpStatus.OK);
    }

    @Operation(summary = "Pagar uma conta com um cartão", description = "Paga uma conta com um cartão")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Conta paga"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PutMapping("/pagar")
    public ResponseEntity<CartaoDTO> pagar(@RequestBody @Valid CartaoPagarDTO cartaoPagarDTO,
                                           @RequestParam("valor") @NotNull Double valor,
                                           @RequestHeader("numeroConta") Integer numeroConta,
                                           @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Operação pagar com cartão iniciada!");
        CartaoDTO cartaoDTOAtualizado = cartaoService.pagar(cartaoPagarDTO, valor, numeroConta, senha);
        log.info("Operação conluída!");
        return ResponseEntity.ok(cartaoDTOAtualizado);
    }

    @Operation(summary = "Atualizar os dados de um cartão", description = "Atualiza os dados de um cartão")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Cartão atualizado"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PutMapping("/{numeroCartao}")
    public ResponseEntity<CartaoDTO> atualizar(@PathVariable("numeroCartao") Long numeroCartao,
                                               @RequestHeader("numeroConta") Integer numeroConta,
                                               @RequestHeader("senha") String senha,
                                               @RequestBody CartaoCreateDTO cartaoCreateDTO) throws RegraDeNegocioException, BancoDeDadosException {
        return new ResponseEntity<>(cartaoService.atualizar(numeroCartao, cartaoCreateDTO, numeroConta, senha), HttpStatus.OK);
    }

    @Operation(summary = "Remover um cartão da conta", description = "Remove um cartão da conta")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Cartão removido"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @DeleteMapping("/{numeroCartao}")
    public ResponseEntity<Void> deletar(@NotNull @PathVariable("numeroCartao") Long numeroCartao,
                                        @RequestHeader("numeroConta") Integer numeroConta,
                                        @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        cartaoService.deletar(numeroCartao, numeroConta, senha);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/listar")
    public ResponseEntity<List<CartaoDTO>> listar(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return new ResponseEntity<>(cartaoService.listar(login, senha), HttpStatus.OK);
    }
}
