package br.com.dbc.vemser.financeiro.controller;

import br.com.dbc.vemser.financeiro.dto.ContatoCreateDTO;
import br.com.dbc.vemser.financeiro.dto.ContatoDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.service.ContatoService;
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

import java.util.List;

@RequestMapping("/contato")
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Contato")
public class ContatoController implements ControleAdicionar<ContatoCreateDTO, ContatoDTO>, ControleListar<List<ContatoDTO>>, ControleDeletar, ControleAtualizar<ContatoCreateDTO, ContatoDTO>{

    private final ContatoService contatoService;

    @Operation(summary = "Retorna os contatos do cliente", description = "Retorna os contatos do cliente do Banco de Dados")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Contatos retornados"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping("/cliente")
    public ResponseEntity<List<ContatoDTO>> listarContatosDoCliente(@RequestHeader("numeroConta") Integer numeroConta,
                                                                    @RequestHeader("senha") String senha) throws BancoDeDadosException, RegraDeNegocioException {
        return new ResponseEntity<>(contatoService.listarContatosDoCliente(numeroConta, senha), HttpStatus.OK);
    }

    @Override
    @GetMapping("/lista")
    @Operation(summary = "FUNÇÃO ADM", description = "LISTAR TODOS OS CONTATOS DO BANCO")
    public ResponseEntity<List<ContatoDTO>> listar(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {//Função do ADM
        return ResponseEntity.ok(contatoService.listarContatos(login, senha));
    }

    @Override
    public ResponseEntity<Boolean> deletar(Integer id, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Deletando Contato!");
        Boolean deletado = contatoService.deletar(id, numeroConta, senha);
        log.info("Contato Deletado!");
        return ResponseEntity.ok(deletado);
    }

    @Override
    public ResponseEntity<ContatoDTO> adicionar(ContatoCreateDTO dado, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Criando Contato!");
        ContatoDTO contatoDTO = contatoService.adicionar(dado, numeroConta, senha);
        log.info("Contato Criado!");
        return ResponseEntity.ok(contatoDTO);
    }

    @Override
    public ResponseEntity<ContatoDTO> atualizar(ContatoCreateDTO dado, Integer id, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        log.info("Atualizando Contato!");
        ContatoDTO contatoDTO = contatoService.atualizar(id, dado, numeroConta, senha);
        log.info("Contato Atualizado!");
        return ResponseEntity.ok(contatoDTO);
    }
}
