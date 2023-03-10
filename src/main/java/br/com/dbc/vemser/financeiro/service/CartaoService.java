package br.com.dbc.vemser.financeiro.service;

import br.com.dbc.vemser.financeiro.controller.ControleListarPorID;
import br.com.dbc.vemser.financeiro.dto.CartaoCreateDTO;
import br.com.dbc.vemser.financeiro.dto.CartaoDTO;
import br.com.dbc.vemser.financeiro.dto.CartaoPagarDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.model.Cartao;
import br.com.dbc.vemser.financeiro.model.CartaoDeCredito;
import br.com.dbc.vemser.financeiro.model.CartaoDeDebito;
import br.com.dbc.vemser.financeiro.model.TipoCartao;
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
        List<Cartao> cartoes = cartaoRepository.listarPorNumeroConta(numeroConta);
        return cartoes.stream()
                .map(cartao -> objectMapper.convertValue(cartao, CartaoDTO.class))
                .toList();
    }

    private List<CartaoDTO> listarPorNumeroConta(Integer numeroConta) throws BancoDeDadosException, RegraDeNegocioException {
        List<Cartao> cartoes = cartaoRepository.listarPorNumeroConta(numeroConta);
        return cartoes.stream()
                .map(cartao -> objectMapper.convertValue(cartao, CartaoDTO.class))
                .toList();
    }

    public CartaoDTO criar(Integer numeroConta, String senha, TipoCartao tipo) throws BancoDeDadosException, RegraDeNegocioException {
        contaService.validandoAcessoConta(numeroConta, senha);
        List<Cartao> cartoes = cartaoRepository.listarPorNumeroConta(numeroConta);
        if (cartoes.size() == 2) {
            throw new RegraDeNegocioException("Usu??rio j?? possui dois cart??es");
        } else {
            Cartao cartao;
            if (tipo.equals(TipoCartao.DEBITO)) {
                cartao = new CartaoDeDebito();
            } else {
                cartao = new CartaoDeCredito();
            }
            cartao.setNumeroConta(numeroConta);
            cartao.setDataExpedicao(LocalDate.now());
            cartao.setCodigoSeguranca(ThreadLocalRandom.current().nextInt(100, 999));
            cartao.setTipo(tipo);
            cartao.setVencimento(cartao.getDataExpedicao().plusYears(4));

            return objectMapper.convertValue(cartaoRepository.adicionar(cartao), CartaoDTO.class);
        }
    }

    public CartaoDTO pagar(CartaoPagarDTO cartaoPagarDTO, Double valor, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        //Validando acesso a conta
        contaService.validandoAcessoConta(numeroConta, senha);

        //Validando e retornando cart??es da conta.
        Cartao cartao = validarCartao(cartaoPagarDTO, numeroConta);

        /* ********* CART??O DE CR??DITO ********* */

        //Validando e pegando cart??o de cr??dito
        if (cartao instanceof CartaoDeCredito cartaoDeCredito){

            //Verificando o limite
            if (cartaoDeCredito.getLimite() < valor) {
                throw new RegraDeNegocioException("Cart??o de cr??dito n??o possui limite suficiente!");
            }
            //Pagando com o cart??o de cr??dito
            cartaoDeCredito.setLimite(cartaoDeCredito.getLimite() - valor);
            Cartao cartaoAtualizado = cartaoRepository.editar(cartaoDeCredito.getNumeroCartao(), cartaoDeCredito);
            CartaoDTO cartaoDTOAtualizado = objectMapper.convertValue(cartaoAtualizado, CartaoDTO.class);
            cartaoDTOAtualizado.setarLimite(cartaoDeCredito.getLimite());
            return cartaoDTOAtualizado;
        }

        /* ********** CART??O DE D??BITO ********* */

        //Validando e pagando com cart??o de d??bito
        if (cartao instanceof CartaoDeDebito cartaoDeDebito){
            contaService.sacar(valor, numeroConta, senha);
            return objectMapper.convertValue(cartaoDeDebito, CartaoDTO.class);
        }

        return null;
    }

    public CartaoDTO atualizar(Long numeroCartao, CartaoCreateDTO cartaoCreateDTO, Integer numeroConta, String senha) throws RegraDeNegocioException, BancoDeDadosException {
        contaService.validandoAcessoConta(numeroConta, senha);
        Cartao cartao = cartaoRepository.getPorNumeroCartao(numeroCartao);
        Cartao cartaoEditado;
        if(cartao.getTipo().equals(TipoCartao.DEBITO)) {
            cartaoEditado = cartaoRepository.editar(numeroCartao, objectMapper.convertValue(cartaoCreateDTO, CartaoDeDebito.class));
        } else {
            cartaoEditado = cartaoRepository.editar(numeroCartao, objectMapper.convertValue(cartaoCreateDTO, CartaoDeCredito.class));
        }
        return objectMapper.convertValue(cartaoEditado, CartaoDTO.class);
    }

    public void deletar(Long numeroCartao, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        contaService.validandoAcessoConta(numeroConta, senha);
        log.info("Buscando cart??o...");
        Cartao cartao = cartaoRepository.getPorNumeroCartao(numeroCartao);
        List<Cartao> cartoes = cartaoRepository.listarPorNumeroConta(cartao.getNumeroConta());

        if (cartoes.size() == 1) {
            throw new RegraDeNegocioException("Cliente possui apenas um cart??o");
        }

        //VALIDANDO CARTAO DE CR??DITO
        if(cartao.getTipo().equals(TipoCartao.CREDITO)){
            CartaoDeCredito cartaoDeCredito = (CartaoDeCredito) cartao;
            if(cartaoDeCredito.getLimite() < 1000){
                throw new RegraDeNegocioException("N??o ?? poss??vel remover o cart??o com limite em aberto!");
            }
        }

        cartaoRepository.remover(cartao.getNumeroCartao());
    }

    public List<CartaoDTO> listar(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        if (AdminValidation.validar(login, senha)) {
            return cartaoRepository.listar().stream()
                    .map(cartao -> objectMapper.convertValue(cartao, CartaoDTO.class))
                    .toList();
        } else {
            throw new RegraDeNegocioException("Credenciais inv??lidas!");
        }
    }

    private Cartao validarCartao(CartaoPagarDTO cartaoPagarDTO, Integer numeroConta) throws BancoDeDadosException, RegraDeNegocioException {
        return cartaoRepository
                .listarPorNumeroConta(
                        numeroConta)
                .stream()
                .filter(cartao -> cartao.getNumeroCartao().equals(cartaoPagarDTO.getNumeroCartao()))
                .filter(cartao -> cartao.getCodigoSeguranca().equals(cartaoPagarDTO.getCodigoSeguranca()))
                .findFirst()
                .orElseThrow(()-> new RegraDeNegocioException("Dados do cart??o inv??lido!"));
    }

    void deletarTodosCartoes(Integer numeroConta) throws BancoDeDadosException, RegraDeNegocioException {
        List<CartaoDTO> cartoes = listarPorNumeroConta(numeroConta);

        for(CartaoDTO cartao: cartoes){
            cartaoRepository.remover(cartao.getNumeroCartao());
        }
    }

}