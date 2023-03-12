package br.com.dbc.vemser.financeiro.controller;

import br.com.dbc.vemser.financeiro.dto.CartaoCreateDTO;
import br.com.dbc.vemser.financeiro.dto.CartaoDTO;
import br.com.dbc.vemser.financeiro.dto.CartaoPagarDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.model.TipoCartao;
import br.com.dbc.vemser.financeiro.service.CartaoService;
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
public class CartaoController {

    private final CartaoService cartaoService;

    @GetMapping("/{numeroConta}")
    public ResponseEntity<List<CartaoDTO>> listarPorIdConta(@PathVariable("numeroConta") Integer numeroConta) throws BancoDeDadosException {
        return new ResponseEntity<>(cartaoService.listarPorNumeroConta(numeroConta), HttpStatus.OK);
    }

    @PostMapping("/criar/{tipo}")
    public ResponseEntity<CartaoDTO> criar(@RequestHeader("numeroConta") Integer numeroConta,
                                           @RequestHeader("senha") String senha,
                                           @PathVariable("tipo") TipoCartao tipo) throws Exception {
        return new ResponseEntity<>(cartaoService.criar(numeroConta, senha, tipo), HttpStatus.OK);
    }

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

    @PutMapping("/{numeroCartao}")
    public ResponseEntity<CartaoDTO> atualizar(@PathVariable("numeroCartao") Long numeroCartao,
                                               @RequestHeader("numeroConta") Integer numeroConta,
                                               @RequestHeader("senha") String senha,
                                               @RequestBody CartaoCreateDTO cartaoCreateDTO) throws RegraDeNegocioException, BancoDeDadosException {
        return new ResponseEntity<>(cartaoService.atualizar(numeroCartao, cartaoCreateDTO, numeroConta, senha), HttpStatus.OK);
    }

    @DeleteMapping("/{numeroCartao}")
    public ResponseEntity<Void> deletar(@NotNull @PathVariable("numeroCartao") Long numeroCartao,
                                        @RequestHeader("numeroConta") Integer numeroConta,
                                        @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        cartaoService.deletar(numeroCartao, numeroConta, senha);
        return ResponseEntity.ok().build();
    }
}
