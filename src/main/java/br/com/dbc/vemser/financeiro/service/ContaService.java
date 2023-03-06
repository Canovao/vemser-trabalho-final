package br.com.dbc.vemser.financeiro.service;

import br.com.dbc.vemser.financeiro.dto.*;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.model.Cliente;
import br.com.dbc.vemser.financeiro.model.Conta;
import br.com.dbc.vemser.financeiro.model.Status;
import br.com.dbc.vemser.financeiro.repository.ContaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

@Service
public class ContaService extends Servico {
    private final ContaRepository contaRepository;
    private final ClienteService clienteService;
    private final CartaoService cartaoService;
    private final TransferenciaService transferenciaService;

    public ContaService(ContaRepository contaRepository, ClienteService clienteService,
                        CartaoService cartaoService, TransferenciaService transferenciaService,
                        ObjectMapper objectMapper) {
        super(objectMapper);
        this.contaRepository = contaRepository;
        this.clienteService = clienteService;
        this.cartaoService = cartaoService;
        this.transferenciaService = transferenciaService;
    }

    public List<ContaDTO> listar() throws BancoDeDadosException, RegraDeNegocioException {
        return contaRepository.listar().stream()
                .map(conta -> objectMapper.convertValue(conta, ContaDTO.class))
                .collect(Collectors.toList());
    }

    public ContaDTO retornarContaCliente(Integer idCliente, ContaAcessDTO contaAcessDTO) throws BancoDeDadosException, RegraDeNegocioException{
        //Validando cliente
        clienteService.visualizarCliente(idCliente);

        //Verificando senha e recuperando conta
        ContaDTO contaDTO = validandoAcessoConta(contaAcessDTO,idCliente);

        return contaDTO;
    }

    public ContaDTO criar(ContaCreateDTO contaCreateDTO) throws BancoDeDadosException, RegraDeNegocioException {
        //Validando se o CPF já possui uma conta no banco.
        validarCriacaoConta(contaCreateDTO);
        //Criando entidade
        Conta conta = criandoEntidade(contaCreateDTO);
        return objectMapper.convertValue(contaRepository.adicionar(conta), ContaDTO.class);
    }

    public ContaDTO alterarSenha(Integer idCliente,
                              ContaUpdateDTO contaUpdateDTO) throws BancoDeDadosException, RegraDeNegocioException {

        //Vericando acesso a conta que irá ser atualizada
        ContaAcessDTO contaAcessDTO = new ContaAcessDTO();
        contaAcessDTO.setNumeroConta(contaUpdateDTO.getNumeroConta());
        contaAcessDTO.setSenha(contaUpdateDTO.getSenha());
        ContaDTO contaDTO = validandoAcessoConta(contaAcessDTO, idCliente);

        //Alterando senha
        Conta conta = objectMapper.convertValue(contaDTO, Conta.class);
        conta.setSenha(contaUpdateDTO.getSenhaNova());

        //Executando edição
        return objectMapper.convertValue(contaRepository
                .editar(conta.getNumeroConta(), conta), ContaDTO.class);
    }

    public ContaDTO sacar(Integer idCliente, ContaTransfDTO contaTransfDTO) throws BancoDeDadosException, RegraDeNegocioException {
        //Validando conta
        ContaDTO contaDTO = retornarContaCliente(idCliente, contaTransfDTO);

        if(!(contaDTO.getSaldo() - contaTransfDTO.getValor() >= 0)){
            throw new RegraDeNegocioException("Operação não realizada, saldo insuficiente! Seu saldo atual: R$" + contaDTO.getSaldo());
        }

        //Sacando valor
        Conta conta = objectMapper.convertValue(contaDTO, Conta.class);
        conta.setSaldo(conta.getSaldo() - contaTransfDTO.getValor());
        return objectMapper.convertValue(contaRepository.editar(conta.getNumeroConta(), conta), ContaDTO.class);
    }

    public ContaDTO depositar(Integer idCliente, ContaTransfDTO contaTransfDTO) throws BancoDeDadosException, RegraDeNegocioException {
        //Validando conta
        ContaDTO contaDTO = retornarContaCliente(idCliente, contaTransfDTO);

        //Depositando valor
        Conta conta = objectMapper.convertValue(contaDTO, Conta.class);
        conta.setSaldo(conta.getSaldo() + contaTransfDTO.getValor());
        return objectMapper.convertValue(contaRepository.editar(conta.getNumeroConta(), conta), ContaDTO.class);
    }

    public void removerConta(Integer idCliente, Integer numeroConta) throws BancoDeDadosException, RegraDeNegocioException {
        //Validando e recuperando conta
        ContaDTO contaDTO = objectMapper.convertValue(contaRepository.consultarPorNumeroConta(numeroConta, idCliente), ContaDTO.class);
        if(!(contaDTO != null)){
            throw new RegraDeNegocioException("Conta inválida!");
        }

        //Deletando cliente
        clienteService.deletarCliente(idCliente);

        //Deletando conta
        contaRepository.remover(numeroConta);
    }

    private Conta criandoEntidade(ContaCreateDTO contaCreateDTO){
        Random random = new Random();
        Cliente cliente = new Cliente();
        cliente.setIdCliente(contaCreateDTO.getIdCliente());
        Conta conta = new Conta();
        conta.setCliente(cliente);
        conta.setSenha(contaCreateDTO.getSenha());
        conta.setSaldo(contaCreateDTO.getSaldo());
        conta.setAgencia(random.nextInt(9000) + 1000);
        return conta;
    }

    private void validarCriacaoConta(ContaCreateDTO contaCreateDTO) throws BancoDeDadosException, RegraDeNegocioException {
        ClienteDTO clienteDTO = clienteService.visualizarCliente(contaCreateDTO.getIdCliente());
        if(listar().stream()
                .anyMatch(conta -> conta.getCliente().getCpf().equals(clienteDTO.getCpf()))){
            throw new RegraDeNegocioException("Este cliente já tem uma conta cadastrada!");
        }

        if(!contaCreateDTO.getCpf().equals(clienteDTO.getCpf())){
            throw new RegraDeNegocioException("CPF inválido!");
        }
    }

    private ContaDTO validandoAcessoConta(ContaAcessDTO contaAcessDTO, Integer idCliente) throws RegraDeNegocioException, BancoDeDadosException {

        Conta conta = contaRepository.consultarPorNumeroConta(contaAcessDTO.getNumeroConta(), idCliente);
        if(!(conta != null)){
            throw new RegraDeNegocioException("Conta inválida!");
        }

        if(!conta.getSenha().equals(contaAcessDTO.getSenha())){
            throw new RegraDeNegocioException("Senha errada!");
        }

        return objectMapper.convertValue(conta, ContaDTO.class);
    }

    /*
     public boolean transferir(Integer numeroContaEnvia, Integer numeroContaRecebe, Double valor) throws BancoDeDadosException, RegraDeNegocioException {
        Conta recebe = retornarContaCliente(numeroContaRecebe),
                envia = retornarContaCliente(numeroContaEnvia);
        if(recebe != null && envia != null){
            if(recebe.getStatus() == Status.ATIVO && envia.getStatus() == Status.ATIVO){
                if(valor > 0){
                    if(envia.getSaldo()-valor >= 0){
                        envia.setSaldo(envia.getSaldo()-valor);
                        recebe.setSaldo(recebe.getSaldo()+valor);
                        editar(numeroContaEnvia, objectMapper.convertValue(envia, ContaCreateDTO.class));
                        editar(numeroContaRecebe, objectMapper.convertValue(recebe, ContaCreateDTO.class));
                        return true;
                    }else{
                        throw new RegraDeNegocioException("Saldo da conta que envia insuficiente!");
                    }
                }else{
                    throw new RegraDeNegocioException("Valor de transferência inválido!");
                }
            }else{
                if(recebe.getStatus() == Status.INATIVO){
                    throw new RegraDeNegocioException("Conta que recebe inativa!");
                }else{
                    throw new RegraDeNegocioException("Conta que envia inativa!");
                }
            }
        }else{
            if(recebe == null){
                throw new RegraDeNegocioException("Número da conta que recebe incorreto!");
            }else{
                throw new RegraDeNegocioException("Número da conta que envia incorreto!");
            }
        }
    }

    public ContaDTO reativarConta(Integer numeroConta, String senhaAdmin) throws BancoDeDadosException, RegraDeNegocioException {
        Conta conta = retornarContaCliente(numeroConta);
        if(senhaAdmin.equals("ABACAXI")){
            if(conta.getStatus()==Status.ATIVO){
                throw new RegraDeNegocioException("Conta já está ativa!");
            }else{
                conta.setStatus(Status.ATIVO);
                return editar(numeroConta, objectMapper.convertValue(conta, ContaCreateDTO.class));
            }
        }else{
            throw new RegraDeNegocioException("Senha administrativa incorreta!");
        }
    }
*/
}

