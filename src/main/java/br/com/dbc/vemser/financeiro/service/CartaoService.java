package br.com.dbc.vemser.financeiro.service;

import br.com.dbc.vemser.financeiro.dto.CartaoCreateDTO;
import br.com.dbc.vemser.financeiro.dto.CartaoDTO;
import br.com.dbc.vemser.financeiro.dto.CartaoPagarDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.entity.CartaoEntity;
import br.com.dbc.vemser.financeiro.entity.CartaoDeCreditoEntity;
import br.com.dbc.vemser.financeiro.entity.CartaoDeDebitoEntity;
import br.com.dbc.vemser.financeiro.entity.TipoCartao;
import br.com.dbc.vemser.financeiro.repository.CartaoRepository;
import br.com.dbc.vemser.financeiro.utils.AdminValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class CartaoService extends Servico {

    private final CartaoRepository cartaoRepository;
    private final ContaService contaService;

    public CartaoService(@Lazy ContaService contaService, CartaoRepository cartaoRepository, ObjectMapper objectMapper) {
        super(objectMapper);
        this.cartaoRepository = cartaoRepository;
        this.contaService = contaService;
    }

    public List<CartaoDTO> listarPorNumeroConta(Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        contaService.validandoAcessoConta(numeroConta, senha);
        List<CartaoEntity> cartoes = cartaoRepository.findByNumeroConta(numeroConta);
        return cartoes.stream()
                .map(cartao -> objectMapper.convertValue(cartao, CartaoDTO.class))
                .toList();
    }

    private List<CartaoDTO> listarPorNumeroConta(Integer numeroConta) throws BancoDeDadosException, RegraDeNegocioException {
        List<CartaoEntity> cartoes = cartaoRepository.findByNumeroConta(numeroConta);
        return cartoes.stream()
                .map(cartao -> objectMapper.convertValue(cartao, CartaoDTO.class))
                .toList();
    }

    public CartaoDTO criar(Integer numeroConta, String senha, TipoCartao tipo) throws BancoDeDadosException, RegraDeNegocioException {
        contaService.validandoAcessoConta(numeroConta, senha);
        List<CartaoEntity> cartoes = cartaoRepository.findByNumeroConta(numeroConta);
        if (cartoes.size() == 2) {
            throw new RegraDeNegocioException("Usuário já possui dois cartões");
        } else {
            CartaoEntity cartao;
            if (tipo.equals(TipoCartao.DEBITO)) {
                cartao = new CartaoDeDebitoEntity();
            } else {
                cartao = new CartaoDeCreditoEntity();
            }
            cartao.setNumeroConta(numeroConta);
            cartao.setDataExpedicao(LocalDate.now());
            cartao.setCodigoSeguranca(ThreadLocalRandom.current().nextInt(100, 999));
            cartao.setTipo(tipo);
            cartao.setVencimento(cartao.getDataExpedicao().plusYears(4));

            return objectMapper.convertValue(cartaoRepository.save(cartao), CartaoDTO.class);
        }
    }

    public CartaoDTO pagar(CartaoPagarDTO cartaoPagarDTO, Double valor, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        //Validando acesso a conta
        contaService.validandoAcessoConta(numeroConta, senha);

        //Validando e retornando cartões da conta.
        CartaoEntity cartao = validarCartao(cartaoPagarDTO, numeroConta);

        /* ********* CARTÃO DE CRÉDITO ********* */

        //Validando e pegando cartão de crédito
        if (cartao instanceof CartaoDeCreditoEntity cartaoDeCredito){

            //Verificando o limite
            if (cartaoDeCredito.getLimite() < valor) {
                throw new RegraDeNegocioException("Cartão de crédito não possui limite suficiente!");
            }
            //Pagando com o cartão de crédito
            cartaoDeCredito.setLimite(cartaoDeCredito.getLimite() - valor);
            CartaoEntity cartaoAtualizado = cartaoRepository.editar(cartaoDeCredito.getNumeroCartao(), cartaoDeCredito);
            CartaoDTO cartaoDTOAtualizado = objectMapper.convertValue(cartaoAtualizado, CartaoDTO.class);
            cartaoDTOAtualizado.setarLimite(cartaoDeCredito.getLimite());
            return cartaoDTOAtualizado;
        }

        /* ********** CARTÃO DE DÉBITO ********* */

        //Validando e pagando com cartão de débito
        if (cartao instanceof CartaoDeDebitoEntity cartaoDeDebito){
            contaService.sacar(valor, numeroConta, senha);
            return objectMapper.convertValue(cartaoDeDebito, CartaoDTO.class);
        }

        return null;
    }

    public CartaoDTO atualizar(Long numeroCartao, CartaoCreateDTO cartaoCreateDTO, Integer numeroConta, String senha) throws RegraDeNegocioException, BancoDeDadosException {
        contaService.validandoAcessoConta(numeroConta, senha);
        CartaoEntity cartao = this.getPeloNumeroCartao(numeroCartao);

        CartaoEntity cartaoEditado;
        if(cartao.getTipo().equals(TipoCartao.DEBITO)) {
            cartaoEditado = cartaoRepository.editar(numeroCartao, objectMapper.convertValue(cartaoCreateDTO, CartaoDeDebitoEntity.class));
        } else {
            cartaoEditado = cartaoRepository.editar(numeroCartao, objectMapper.convertValue(cartaoCreateDTO, CartaoDeCreditoEntity.class));
        }
        return objectMapper.convertValue(cartaoEditado, CartaoDTO.class);
    }

    public void deletar(Long numeroCartao, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        contaService.validandoAcessoConta(numeroConta, senha);
        log.info("Buscando cartão...");
        CartaoEntity cartao = this.getPeloNumeroCartao(numeroCartao);

        List<CartaoEntity> cartoes = cartaoRepository.findByNumeroConta(cartao.getNumeroConta());

        if (cartoes.size() == 1) {
            throw new RegraDeNegocioException("Cliente possui apenas um cartão");
        }

        //VALIDANDO CARTAO DE CRÉDITO
        if(cartao.getTipo().equals(TipoCartao.CREDITO)){
            CartaoDeCreditoEntity cartaoDeCredito = (CartaoDeCreditoEntity) cartao;
            if(cartaoDeCredito.getLimite() < 1000){
                throw new RegraDeNegocioException("Não é possível remover o cartão com limite em aberto!");
            }
        }

        cartaoRepository.softDelete(cartao.getNumeroCartao());
    }

    public List<CartaoDTO> listar(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        if (AdminValidation.validar(login, senha)) {
            return cartaoRepository.findAll().stream()
                    .map(cartao -> objectMapper.convertValue(cartao, CartaoDTO.class))
                    .toList();
        } else {
            throw new RegraDeNegocioException("Credenciais inválidas!");
        }
    }

    private CartaoEntity validarCartao(CartaoPagarDTO cartaoPagarDTO, Integer numeroConta) throws BancoDeDadosException, RegraDeNegocioException {
        return cartaoRepository
                .findByNumeroConta(
                        numeroConta)
                .stream()
                .filter(cartao -> cartao.getNumeroCartao().equals(cartaoPagarDTO.getNumeroCartao()))
                .filter(cartao -> cartao.getCodigoSeguranca().equals(cartaoPagarDTO.getCodigoSeguranca()))
                .findFirst()
                .orElseThrow(()-> new RegraDeNegocioException("Dados do cartão inválido!"));
    }

    void deletarTodosCartoes(Integer numeroConta) throws BancoDeDadosException, RegraDeNegocioException {
        List<CartaoDTO> cartoes = listarPorNumeroConta(numeroConta);

        for(CartaoDTO cartao: cartoes){
            cartaoRepository.softDelete(cartao.getNumeroCartao());
        }
    }

    CartaoEntity getPeloNumeroCartao(Long numeroCartao) throws RegraDeNegocioException {
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new RegraDeNegocioException("Cartão não encontrado!"));
    }

}