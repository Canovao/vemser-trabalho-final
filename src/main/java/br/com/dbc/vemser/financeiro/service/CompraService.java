package br.com.dbc.vemser.financeiro.service;

import br.com.dbc.vemser.financeiro.dto.*;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.model.Compra;
import br.com.dbc.vemser.financeiro.repository.CompraRepository;
import br.com.dbc.vemser.financeiro.utils.AdminValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraService extends Servico {
    private final CompraRepository compraRepository;
    private final ItemService itemService;
    private final ContaService contaService;
    private final CartaoService cartaoService;

    public CompraService(CompraRepository compraRepository, ObjectMapper objectMapper, ContaService contaService, CartaoService cartaoService, ItemService itemService) {
        super(objectMapper);
        this.compraRepository = compraRepository;
        this.contaService = contaService;
        this.cartaoService = cartaoService;
        this.itemService = itemService;
    }
    public List<CompraDTO> list(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        if (AdminValidation.validar(login, senha)) {
            return compraRepository.listar().stream()
                    .map(compra -> objectMapper.convertValue(compra, CompraDTO.class))
                    .toList();
        } else {
            throw new RegraDeNegocioException("Credenciais de Administrador inválidas!");
        }
    }

    public CompraItensDTO getById(String login, String senha, Integer id) throws RegraDeNegocioException, BancoDeDadosException {
        if (AdminValidation.validar(login, senha)) {

            CompraItensDTO compraItensDTO =  objectMapper.convertValue(
                    compraRepository.getById(id),
                    CompraItensDTO.class
            );
            compraItensDTO.setItens(itemService.listarItensPorIdCompra(id));
            return compraItensDTO;
        } else {
            throw new RegraDeNegocioException("Credenciais de Administrador inválidas!");
        }
    }

    public List<CompraItensDTO> retornarComprasCartao(Long numeroCartao, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        contaService.validandoAcessoConta(numeroConta, senha);

        CartaoDTO cartao = cartaoService.listarPorNumeroConta(numeroConta).stream()
                .filter(cartaoDTO -> cartaoDTO.getNumeroCartao().equals(numeroCartao))
                .findFirst()
                .orElseThrow(()-> new RegraDeNegocioException("Cartão não existente na conta informada!"));

        List<CompraItensDTO> comprasDTO =  compraRepository.listarPorCartao(numeroCartao).stream()
                .map(compra -> objectMapper.convertValue(compra, CompraItensDTO.class))
                .collect(Collectors.toList());

        for (CompraItensDTO compraItensDTO : comprasDTO) {
            compraItensDTO.setItens(itemService.listarItensPorIdCompra(compraItensDTO.getIdCompra()));
        }

        return comprasDTO;
    }

    public CompraItensDTO adicionar(CompraCreateDTO compraCreateDTO, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException{
        contaService.validandoAcessoConta(numeroConta, senha);

        Double valorTotal = compraCreateDTO.getItens().stream()
                .mapToDouble(ItemCreateDTO::getValor).sum();

        CartaoDTO cartao = new CartaoDTO();
        cartao.setNumeroCartao(compraCreateDTO.getNumeroCartao());
        cartao.setCodigoSeguranca(compraCreateDTO.getCodigoSeguranca());

        cartaoService.pagar(cartao, valorTotal, numeroConta, senha);

        Compra compra = compraRepository.adicionar(objectMapper.convertValue(compraCreateDTO, Compra.class));
        CompraItensDTO compraItensDTO = objectMapper.convertValue(compra, CompraItensDTO.class);

        for (ItemCreateDTO item : compraCreateDTO.getItens()) {
            item.setIdCompra(compra.getIdCompra());
        }
        List<ItemDTO> listItemDTO = itemService.adicionar(compraCreateDTO.getItens(), numeroConta, senha);

        compraItensDTO.setItens(listItemDTO);

        return compraItensDTO;
    }
}
