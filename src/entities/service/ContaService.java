package entities.service;

import entities.exception.BancoDeDadosException;
import entities.model.*;
import entities.repository.ContaRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class ContaService extends Service{

    private ContaRepository contaRepository;

    public ContaService() {
        this.contaRepository = new ContaRepository();
    }

    public void adicionar() {
        while (true) {
            int agencia = ThreadLocalRandom.current().nextInt(10, 99);
            String senha;

            try {
                Cliente cliente = new ClienteService().adicionarCliente();

                while (true) {
                    System.out.print("Insira a senha: ");
                    senha = SCANNER.nextLine().trim().replaceAll(" ", "");
                    if (senha.length() == 6 && !Pattern.matches("[a-zA-Z!@#$%^&*(),.?\":{}|<>]+", senha)) {
                        break;
                    }
                    System.out.println("A senha não possui 6 digitos ou não é composta apenas por números! Tente novamente.");
                }

                Conta conta = new Conta();
                conta.setAgencia(agencia);
                conta.setCliente(cliente);
                conta.setSenha(senha);

                contaRepository.adicionar(conta);

            } catch (BancoDeDadosException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void listar() {
        try {
            contaRepository.listar().forEach(System.out::println);
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
    }

    public void editar(Integer numeroConta, Conta conta) {
        try {
            contaRepository.editar(numeroConta, conta);
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }

    }

    public void removerConta() {
        try{
            this.listar();
            int numeroConta = askInt("Insira o número da CONTA que deseja DELETAR:");
            if(contaRepository.remover(numeroConta)) {
                System.out.println("Conta removida com sucesso!");
            }
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
    }


    public Conta retornarConta(String numeroConta, String senhaConta){
        Conta conta;
        Integer numero = Integer.parseInt(numeroConta);
        try{
            conta = this.contaRepository.consultarPorNumeroConta(numero);
            if(conta != null && conta.getSenha().equals(senhaConta)){
                return conta;
            }else{
                return null;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public void exibirConta(Conta conta){
        System.out.println("Número da CONTA: "+conta.getNumeroConta());
        System.out.println("Nome do CLIENTE da CONTA: "+conta.getCliente().getNome());
        System.out.println("Saldo: "+conta.getSaldo());
        System.out.println("Agência: "+conta.getAgencia());
        System.out.println("Cheque especial: "+conta.getChequeEspecial());
    }

    public void depositar(Conta conta){
        double valor = askDouble("Insira o valor do Depósito: ");
        conta.setSaldo(conta.getSaldo()+valor);
        this.editar(conta.getNumeroConta(), conta);
    }

    public void sacar(Conta conta){
        double valor = askDouble("Insira o valor do Saque: ");
        if(conta.getSaldo()-valor+conta.getChequeEspecial() < 0){
            System.err.println("Saldo insuficiente!");
        }else{
            conta.setSaldo(conta.getSaldo()-valor);
            this.editar(conta.getNumeroConta(), conta);
        }
    }

    public void transferir(Conta conta){
        double valor = askDouble("Insira o valor da transferência: ");

        int numeroConta = askInt("Insira o número da conta que receberá a transferência: ");

        try{
            Conta contaRecebeu = this.contaRepository.consultarPorNumeroConta(numeroConta);

            contaRecebeu.setSaldo(contaRecebeu.getSaldo()+valor);

            this.editar(numeroConta, contaRecebeu);

            if(conta.getSaldo()-valor < 0){
                System.err.println("Saldo insuficiente!");
            }else{
                conta.setSaldo(conta.getSaldo()-valor);

                this.editar(conta.getNumeroConta(), conta);

                System.err.println("Transferência concluída!");
                System.out.printf("Saldo atual: R$ %.2f\n", conta.getSaldo());
            }
        }catch(BancoDeDadosException e){
            e.printStackTrace();
        }
    }

    public void pagar(Conta conta){
        double valor = askDouble("Insira o valor do pagamento: ");

        CartaoService cartaoService = new CartaoService();
        List<Cartao> cartoes = cartaoService.returnCartoes(conta);
        Cartao cartao;

        StringBuilder message = new StringBuilder("Selecione o cartão para efetuar o pagamento:");
        for(int i=0;i<cartoes.size();i++){
            if(cartoes.get(i) != null){
                message.append("Cartão [").append(i + 1).append("] -> ").append(cartoes.get(i).getTipo() == TipoCartao.DEBITO ? "Débito" : "Crédito").append(":");
            }
        }
        cartao = cartoes.get(askInt(String.valueOf(message)) - 1);

        if(cartao.getTipo() == TipoCartao.CREDITO){
            if(((CartaoDeCredito) cartao).getLimite()-valor < 0){
                System.err.println("Limite insuficiente!");
            }else{
                ((CartaoDeCredito) cartao).setLimite(((CartaoDeCredito) cartao).getLimite()-valor);
                if(cartaoService.editarCartao(cartao.getNumeroCartao(), cartao)){
                    System.out.println("Limite do cartão de CRÉDITO ATUALIZADO!");
                    System.out.printf("Limite restante: R$%.2f", ((CartaoDeCredito) cartao).getLimite());
                }else{
                    System.err.println("Problemas ao atualizar o limite do cartão de crédito");
                }
            }
        }else{
            if(conta.getSaldo()-valor < 0){
                System.out.println("Saldo insuficiente!");
            }else{
                conta.setSaldo(conta.getSaldo()-valor);

                this.editar(conta.getNumeroConta(), conta);

                System.err.println("Pagamento concluído!");
                System.out.printf("Saldo atual: R$ %.2f\n", conta.getSaldo());
            }
        }
    }

    public void alterarSenha(Conta conta){
        String novaSenha;

        while (true) {
            System.out.print("Insira a nova senha: ");
            novaSenha = SCANNER.nextLine().trim().replaceAll(" ", "");
            if (novaSenha.length() == 6 && !Pattern.matches("[a-zA-Z!@#$%^&*(),.?\":{}|<>]+", novaSenha)) {
                conta.setSenha(novaSenha);
                break;
            } else if (novaSenha.length() == 0) {
                break;
            }
            System.out.println("A senha não possui 6 digitos ou não é composta apenas por números! Tente novamente.");
        }
        conta.setSenha(novaSenha);

        try{
            if(this.contaRepository.editar(conta.getNumeroConta(), conta)){
                System.out.println("Senha atualizada com sucesso!");
            }else{
                System.err.println("Problema ao atualizar senha!");
            }
        }catch(BancoDeDadosException e){
            e.printStackTrace();
        }
    }
}
