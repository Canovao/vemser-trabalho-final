package br.com.dbc.vemser.financeiro.controller;

import br.com.dbc.vemser.financeiro.dto.CompraCreateDTO;
import br.com.dbc.vemser.financeiro.dto.CompraDTO;
import br.com.dbc.vemser.financeiro.dto.CompraItensDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.model.Compra;
import br.com.dbc.vemser.financeiro.service.CompraService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
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
public class CompraController implements ControleListar<List<CompraDTO>>{

    private final CompraService compraService;

    //adm
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

    @GetMapping("/{numeroCartao}/cartao")
    public ResponseEntity<List<CompraItensDTO>> listarComprasDoCartao(@NotNull @PathVariable("numeroCartao") Long numeroCartao,
                                                                      @RequestHeader("numeroConta") Integer numeroConta,
                                                                      @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(compraService.retornarComprasCartao(numeroCartao, numeroConta, senha));
    }

    @PostMapping("/{numeroCartao}/cartao")
    public ResponseEntity<CompraItensDTO> criar(@RequestBody @Valid CompraCreateDTO compra,
                                                @RequestHeader("numeroConta") Integer numeroConta,
                                                @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(compraService.adicionar(compra, numeroConta, senha));
    }
}
