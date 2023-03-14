package br.com.dbc.vemser.financeiro.service;

import br.com.dbc.vemser.financeiro.dto.*;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.entity.CompraEntity;
import br.com.dbc.vemser.financeiro.repository.CompraRepository;
import br.com.dbc.vemser.financeiro.utils.AdminValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraService extends Servico {
    private final CompraRepository compraRepository;
    private final ItemService itemService;
    private final ContaService contaService;
    private final CartaoService cartaoService;

    public CompraService(CompraRepository compraRepository, ObjectMapper objectMapper, ContaService contaService,
                         CartaoService cartaoService, @Lazy ItemService itemService) {
        super(objectMapper);
        this.compraRepository = compraRepository;
        this.contaService = contaService;
        this.cartaoService = cartaoService;
        this.itemService = itemService;
    }
    public List<CompraDTO> list(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        if (AdminValidation.validar(login, senha)) {
            return compraRepository.findAll().stream()
                    .map(compra -> objectMapper.convertValue(compra, CompraDTO.class))
                    .toList();
        } else {
            throw new RegraDeNegocioException("Credenciais de Administrador inválidas!");
        }
    }

    public CompraItensDTO getById(String login, String senha, Integer id) throws RegraDeNegocioException, BancoDeDadosException {
        if (AdminValidation.validar(login, senha)) {

            CompraItensDTO compraItensDTO =  objectMapper.convertValue(
                    compraRepository.findById(id),
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

        List<CartaoDTO> cartoes = cartaoService.listarPorNumeroConta(numeroConta, senha).stream()
                .filter(cartaoDTO -> cartaoDTO.getNumeroCartao().equals(numeroCartao))
                .toList();
        if(cartoes.size() == 0){
            throw new RegraDeNegocioException("Cartão não existente na conta informada!");
        }else{
            List<CompraItensDTO> compraItensDTO =  compraRepository.findAllByNumeroCartao(numeroCartao).stream()
                    .map(compra -> objectMapper.convertValue(compra, CompraItensDTO.class))
                    .toList();

            for (CompraItensDTO compra : compraItensDTO) {
                compra.setItens(itemService.listarItensPorIdCompra(compra.getIdCompra()));
            }

            return compraItensDTO;
        }
    }

    public CompraItensDTO adicionar(CompraCreateDTO compraCreateDTO, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException{
        contaService.validandoAcessoConta(numeroConta, senha);

        Double valorTotal = compraCreateDTO.getItens().stream()
                .mapToDouble(ItemCreateDTO::getValor).sum();

        CartaoPagarDTO cartaoPagarDTO = new CartaoPagarDTO();
        cartaoPagarDTO.setNumeroCartao(compraCreateDTO.getNumeroCartao());
        cartaoPagarDTO.setCodigoSeguranca(compraCreateDTO.getCodigoSeguranca());

        cartaoService.pagar(cartaoPagarDTO, valorTotal, numeroConta, senha);

        CompraEntity compra = compraRepository.save(objectMapper.convertValue(compraCreateDTO, CompraEntity.class));
        CompraItensDTO compraItensDTO = objectMapper.convertValue(compra, CompraItensDTO.class);

        for (ItemCreateDTO item : compraCreateDTO.getItens()) {
            item.setIdCompra(compra.getIdCompra());
        }
        List<ItemDTO> listItemDTO = itemService.adicionar(compraCreateDTO.getItens(), numeroConta, senha);

        compraItensDTO.setItens(listItemDTO);

        return compraItensDTO;
    }
}
