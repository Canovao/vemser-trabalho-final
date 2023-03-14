package br.com.dbc.vemser.financeiro.service;

import br.com.dbc.vemser.financeiro.dto.CartaoDTO;
import br.com.dbc.vemser.financeiro.dto.CompraItensDTO;
import br.com.dbc.vemser.financeiro.dto.ItemCreateDTO;
import br.com.dbc.vemser.financeiro.dto.ItemDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.entity.ItemEntity;
import br.com.dbc.vemser.financeiro.repository.ItemRepository;
import br.com.dbc.vemser.financeiro.utils.AdminValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService extends Servico {

    private final ItemRepository itemRepository;
    private final CartaoService cartaoService;
    private final ContaService contaService;
    private final CompraService compraService;

    public ItemService(ItemRepository itemRepository, ObjectMapper objectMapper, CartaoService cartaoService, ContaService contaService, CompraService compraService) {
        super(objectMapper);
        this.itemRepository = itemRepository;
        this.cartaoService = cartaoService;
        this.contaService = contaService;
        this.compraService = compraService;
    }

    public List<ItemDTO> adicionar(List<ItemCreateDTO> itensCreateDTO, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        contaService.validandoAcessoConta(numeroConta, senha);
        List<ItemEntity> itens = itensCreateDTO.stream()
                .map(itemCreateDTO -> objectMapper.convertValue(itemCreateDTO, ItemEntity.class))
                .toList();
        List<ItemDTO> itensDTO = new ArrayList<>();
        for(ItemEntity item : itens) {
            ItemEntity itemCreated = itemRepository.save(item);
            itensDTO.add(objectMapper.convertValue(itemCreated, ItemDTO.class));
        }
        return itensDTO;
    }

    public List<ItemDTO> listar(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        if (AdminValidation.validar(login, senha)) {
            return itemRepository.findAll().stream()
                    .map(item -> objectMapper.convertValue(item, ItemDTO.class))
                    .toList();
        }else{
            throw new RegraDeNegocioException("Credenciais de Administrador inválidas!");
        }
    }
    public List<ItemDTO> listarItensPorIdCompra(Integer idCompra) throws BancoDeDadosException, RegraDeNegocioException {
        return itemRepository.findAllByIdCompra(idCompra).stream()
                .map(item -> objectMapper.convertValue(item, ItemDTO.class))
                .toList();
    }

    public List<ItemDTO> listarItensPorIdCompra(Integer idCompra, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        List<ItemDTO> itens = itemRepository.findAllByIdCompra(idCompra).stream()
                .map(item -> objectMapper.convertValue(item, ItemDTO.class))
                .toList();
        if(itens.size() == 0){
            throw new RegraDeNegocioException("Id da compra não existe!");
        }else{
            boolean exibir = false;
            for(CartaoDTO cartao : cartaoService.listarPorNumeroConta(numeroConta, senha)){
                for(CompraItensDTO compra: compraService.retornarComprasCartao(cartao.getNumeroCartao(), numeroConta, senha)){
                    if(compra.getIdCompra().equals(idCompra)){
                        exibir = true;
                        break;
                    }
                }
            }
            if(exibir){
                return itens;
            }else{
                throw new RegraDeNegocioException("Essa compra não te pertence!");
            }
        }
    }
}
