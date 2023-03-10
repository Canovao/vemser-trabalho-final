package br.com.dbc.vemser.financeiro.controller;

import br.com.dbc.vemser.financeiro.dto.*;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@RequestMapping("/conta")
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Conta")
public class ContaController implements ControleListar<List<ContaDTO>>{

    private final ContaService contaService;

    @Override
    @Operation(summary = "FUNÇÃO ADM", description = "Lista todas as contas do banco")
    @GetMapping("/lista")
    public ResponseEntity<List<ContaDTO>> listar(@RequestHeader("login") String login,
                                                       @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(contaService.listar(login, senha));
    }

    @Operation(summary = "Logando na conta e retornando informações do cliente", description = "Logando na conta através do numero e senha da conta.")
    @GetMapping("/cliente")
    public ResponseEntity<ContaDTO> retornarContaCliente(@RequestHeader("numeroConta") Integer numeroConta,
                                                         @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return ResponseEntity.ok(contaService.retornarContaCliente(numeroConta, senha));
    }

    @Operation(summary = "Criar nova conta", description = "Está requisição cria um cliente com conta e um cartão de débito.")
    @PostMapping
    public ResponseEntity<ContaDTO> criar(@RequestBody @Valid ContaCreateDTO contaCreateDTO) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Criando Conta!");
        ContaDTO contaDTO = contaService.criar(contaCreateDTO);
        log.info("Conta Criada!");
        return ResponseEntity.ok(contaDTO);
    }

    @Operation(summary = "Alterar senha", description = "Alterar senha da conta.")
    @PutMapping("/alterarsenha")
    public ResponseEntity<ContaDTO> alterarSenha(@RequestBody @Valid ContaUpdateDTO contaUpdateDTO,
                                                 @RequestHeader("numeroConta") Integer numeroConta,
                                                 @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Alterando Senha!");
        ContaDTO contaDTO = contaService.alterarSenha(contaUpdateDTO.getSenhaNova(), numeroConta, senha);
        log.info("Senha Alterada!");
        return ResponseEntity.ok(contaDTO);
    }

    @Operation(summary = "Sacar", description = "Sacar valor da conta.")
    @PutMapping("/sacar/{valor}")
    public ResponseEntity<ContaDTO> sacar(@PathVariable("valor") @Positive @Digits(integer = 10, fraction = 0, message = "Valor deve ser inteiro!") @Valid Double valor,
                                          @RequestHeader("numeroConta") @NotNull @Valid Integer numeroConta,
                                          @RequestHeader("senha") @NotBlank @Size(min = 6, max = 6) @Valid String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Sacando valor: R$" + valor);
        ContaDTO contaDTO = contaService.sacar(valor, numeroConta, senha);
        log.info("Valor Sacado: R$" + valor);
        return ResponseEntity.ok(contaDTO);
    }

    @Operation(summary = "Depositar", description = "Depositar valor na conta.")
    @PutMapping("/depositar/{valor}")
    public ResponseEntity<ContaDTO> depositar(@PathVariable("valor") @Positive @Digits(integer = 10, fraction = 0, message = "Valor deve ser inteiro!") @Valid Double valor,
                                              @RequestHeader("numeroConta") @NotNull @Valid Integer numeroConta,
                                              @RequestHeader("senha") @NotBlank @Size(min = 6, max = 6) @Valid String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Depositando valor: R$" + valor);
        ContaDTO contaDTO = contaService.depositar(valor, numeroConta, senha);
        log.info("Valor Depositado: R$" + valor);
        return ResponseEntity.ok(contaDTO);
    }

    @Operation(summary = "FUNÇÃO ADM", description = "Reativar conta, cliente e cartões.")
    @PutMapping("/{cpf}/reativar")
    public ResponseEntity<Boolean> reativarConta(@PathVariable("cpf") String cpf,
                                                 @RequestHeader("login") String login,
                                                 @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Reativando Conta!");
        boolean simOrNao = contaService.reativarConta(cpf,login,senha);
        log.info("Conta Reativada!");
        return ResponseEntity.ok(simOrNao);
    }

    @Operation(summary = "FUNÇÃO ADM", description = "Desativar conta, cliente e cartões.")
    @DeleteMapping("/delete/{numeroConta}")
    public ResponseEntity<String> removerConta(
                                @PathVariable("numeroConta") Integer numeroConta,
                                @RequestHeader("login") String login,
                                @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Deletando Conta!");
        contaService.removerConta(numeroConta, login, senha);
        log.info("Conta Deletada!");
        return ResponseEntity.ok("Conta desativada!");
    }
}
