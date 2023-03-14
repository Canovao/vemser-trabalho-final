package br.com.dbc.vemser.financeiro.service;

import br.com.dbc.vemser.financeiro.dto.ClienteCreateDTO;
import br.com.dbc.vemser.financeiro.dto.ClienteDTO;
import br.com.dbc.vemser.financeiro.exception.BancoDeDadosException;
import br.com.dbc.vemser.financeiro.exception.RegraDeNegocioException;
import br.com.dbc.vemser.financeiro.entity.ClienteEntity;
import br.com.dbc.vemser.financeiro.entity.Status;
import br.com.dbc.vemser.financeiro.repository.ClienteRepository;
import br.com.dbc.vemser.financeiro.utils.AdminValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService extends Servico {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository, ObjectMapper objectMapper) {
        super(objectMapper);
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteDTO> listarClientes(String login, String senha) throws RegraDeNegocioException {
        if (AdminValidation.validar(login, senha)) {
            return clienteRepository.findAll().stream()
                    .map(cliente -> objectMapper.convertValue(cliente, ClienteDTO.class))
                    .toList();
        }else{
            throw new RegraDeNegocioException("Credenciais de Administrador inválidas!");
        }
    }

    public ClienteDTO visualizarCliente(Integer idCliente) throws RegraDeNegocioException {
        ClienteDTO clienteDTO = objectMapper.convertValue(clienteRepository.findById(idCliente), ClienteDTO.class);
        validarClienteInativo(clienteDTO);
        return clienteDTO;
    }

    ClienteDTO retornandoCliente(Integer idCliente) {
        return objectMapper.convertValue(clienteRepository.findById(idCliente), ClienteDTO.class);
    }

    public ClienteDTO adicionarCliente(ClienteCreateDTO clienteCreateDTO) throws BancoDeDadosException, RegraDeNegocioException {
        validarClientePorCPF(clienteCreateDTO);
        ClienteEntity cliente = objectMapper.convertValue(clienteCreateDTO, ClienteEntity.class);
        return objectMapper.convertValue(clienteRepository.save(cliente), ClienteDTO.class);
    }

    public ClienteDTO alterarCliente(Integer idCliente, ClienteCreateDTO clienteCreateDTO) throws RegraDeNegocioException {
        ClienteDTO clienteDTO = visualizarCliente(idCliente);

        if(!(clienteDTO.getCpf().equals(clienteCreateDTO.getCpf()))){
            throw new RegraDeNegocioException("Digite o CPF do titular da conta!");
        }

        ClienteEntity cliente = objectMapper.convertValue(clienteCreateDTO, ClienteEntity.class);
        return objectMapper.convertValue(clienteRepository.editar(idCliente, cliente), ClienteDTO.class);
    }

    public void deletarCliente(Integer idCliente) throws RegraDeNegocioException{
        visualizarCliente(idCliente);
        this.clienteRepository.softDelete(idCliente);
    }

    void validarClienteInativo(ClienteDTO clienteDTO) throws RegraDeNegocioException {

        if (clienteDTO.getStatus() == Status.INATIVO) {
            throw new RegraDeNegocioException("Cliente Inativo!");
        }

        if(clienteDTO.getCpf() == null){
            throw new RegraDeNegocioException("Este cliente não existe!");
        }
    }

    void validarClientePorCPF(ClienteCreateDTO clienteCreateDTO) throws RegraDeNegocioException {

        Optional<ClienteEntity> cliente = clienteRepository.findByCpf(clienteCreateDTO.getCpf());

        if(cliente.isEmpty()) {
            return;
        }

        if(cliente.get().getStatus().getStatus() == 0) {
            throw new RegraDeNegocioException("Este CPF está inativo!");
        }

        if(cliente.get().getStatus().getStatus() == 1){
            throw new RegraDeNegocioException("Este cpf já está registrado e ativo");
        }
    }
}
