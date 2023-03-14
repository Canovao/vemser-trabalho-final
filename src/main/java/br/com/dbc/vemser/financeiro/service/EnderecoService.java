package br.com.dbc.vemser.financeiro.service;


import br.com.dbc.vemser.financeiro.dto.ClienteDTO;
import br.com.dbc.vemser.financeiro.dto.ContaDTO;
import br.com.dbc.vemser.financeiro.dto.EnderecoCreateDTO;
import br.com.dbc.vemser.financeiro.dto.EnderecoDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.entity.EnderecoEntity;
import br.com.dbc.vemser.financeiro.repository.EnderecoRepository;
import br.com.dbc.vemser.financeiro.utils.AdminValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnderecoService extends Servico {

    private final EnderecoRepository enderecoRepository;
    private final ClienteService clienteService;
    private final ContaService contaService;

    public EnderecoService(EnderecoRepository enderecoRepository, ClienteService clienteService, ObjectMapper objectMapper, ContaService contaService) {
        super(objectMapper);
        this.enderecoRepository = enderecoRepository;
        this.clienteService = clienteService;
        this.contaService = contaService;
    }

    public List<EnderecoDTO> listarEnderecos(String login, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        if (AdminValidation.validar(login, senha)) {
            return enderecoRepository.findAll().stream()
                    .map(endereco -> objectMapper.convertValue(endereco, EnderecoDTO.class))
                    .toList();
        }else{
            throw new RegraDeNegocioException("Credenciais de Administrador inválidas!");
        }
    }

    public List<EnderecoDTO> listarEnderecosDoCliente(Integer numeroConta, String senha) throws RegraDeNegocioException {
        ContaDTO conta = contaService.validandoAcessoConta(numeroConta, senha);
        clienteService.visualizarCliente(conta.getCliente().getIdCliente());
        return this.enderecoRepository.listarEnderecosPorPessoa(conta.getCliente().getIdCliente()).stream()
                .map(endereco -> objectMapper.convertValue(endereco, EnderecoDTO.class))
                .collect(Collectors.toList());
    }

    public EnderecoDTO adicionar(EnderecoCreateDTO enderecoCreateDTO, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        ClienteDTO cliente = contaService.validandoAcessoConta(numeroConta, senha).getCliente();

        clienteService.visualizarCliente(cliente.getIdCliente());

        enderecoCreateDTO.setIdCliente(cliente.getIdCliente());
        validarCEPEndereco(enderecoCreateDTO);

        EnderecoEntity endereco = objectMapper.convertValue(enderecoCreateDTO, EnderecoEntity.class);
        return objectMapper.convertValue(enderecoRepository.save(endereco), EnderecoDTO.class);
    }

    public EnderecoDTO atualizar(Integer idEndereco, EnderecoCreateDTO enderecoCreateDTO, Integer numeroConta, String senha) throws BancoDeDadosException, RegraDeNegocioException {
        ClienteDTO cliente = contaService.validandoAcessoConta(numeroConta, senha).getCliente();

        clienteService.visualizarCliente(cliente.getIdCliente());

        enderecoCreateDTO.setIdCliente(cliente.getIdCliente());
        validarEndereco(idEndereco);
        validarCEPEndereco(enderecoCreateDTO);

        EnderecoEntity endereco = objectMapper.convertValue(enderecoCreateDTO, EnderecoEntity.class);
        return objectMapper.convertValue(enderecoRepository.editar(idEndereco, endereco), EnderecoDTO.class);
    }

    public boolean deletar(Integer idEndereco, Integer numeroConta, String senha) throws RegraDeNegocioException {
        List<EnderecoDTO> enderecoDTOS = listarEnderecosDoCliente(numeroConta, senha);

        if(enderecoDTOS.size() == 1){
            throw new RegraDeNegocioException("É necessário ter ao menos um endereço!");
        }
        this.enderecoRepository.deleteById(idEndereco);

        //mesma coisa, rever o return desse método (transformar em void?)

        return true;
    }

    private void validarEndereco(Integer idEndereco) throws RegraDeNegocioException {
        enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new RegraDeNegocioException("Endereço não encontrado!"));

    }

    private void validarCEPEndereco(EnderecoCreateDTO enderecoCreateDTO) throws RegraDeNegocioException {

        if(enderecoRepository.listar().stream()
                .filter(enderecoDTO -> enderecoDTO.getIdCliente().equals(enderecoCreateDTO.getIdCliente()))
                .anyMatch(enderecoDTO -> enderecoDTO.getCep().equals(enderecoCreateDTO.getCep()) && enderecoDTO.getNumero().equals(enderecoCreateDTO.getNumero()))){
            throw new RegraDeNegocioException("Este Endereço já existe!");
        }
    }
}