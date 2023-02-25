package entities.service;

import entities.exception.BancoDeDadosException;
import entities.model.*;
import entities.repository.CartaoRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CartaoService extends Service {

    private CartaoRepository cartaoRepository;

    public CartaoService() {
        this.cartaoRepository = new CartaoRepository();
    }

    public void exibirExtrato(Conta conta, TipoCartao tipo) {
        List<Cartao> cartoes = this.returnCartoes(conta);
        int cartao = -1;

        if (cartoes != null) {
            for (int i = 0; i < cartoes.size(); i++) {
                if (cartoes.get(i).getTipo() == tipo) {
                    cartao = i;
                    System.err.println("\t\t\nExibindo dados do cartão [" + (i + 1) + "]:");
                    System.out.print(
                            "\tTipo do cartão: " + ((tipo == TipoCartao.DEBITO) ? "DÉBITO" : "CRÉDITO") + "\n" +
                                    "\tNúmero da conta do cartão: " + cartoes.get(i).getConta().getNumeroConta() + "\n" +
                                    "\tNúmero do cartão: " + cartoes.get(i).getNumeroCartao() + "\n" +
                                    "\tVencimento do cartão: " + cartoes.get(i).getVencimento() + "\n" +
                                    "\tCódigo de segurança do cartão: " + cartoes.get(i).getCodigoSeguranca() + "\n" +
                                    "\tData de expedição: " + cartoes.get(i).getDataExpedicao());
                    CompraService compraService = new CompraService();
                    compraService.exibirComprasCartao(cartoes.get(i));
                    break;
                }
            }
            if (cartao == -1) {
                System.err.println("\tVocê não possui nenhum cartão de " + ((tipo == TipoCartao.DEBITO) ? "débito!" : "crédito!") + "\n");
            }
        }
    }

    public void cadastrarCartao(Conta conta, TipoCartao tipoCartao) {
        List<Cartao> cartoes = this.returnCartoes(conta);
        if (cartoes != null && cartoes.size() == 2) {
            System.err.println("Você não pode adicionar mais cartões, só é possível ter no máximo 2x cartões!\n");
            return;
        } else if(tipoCartao == null){

            //Lendo e passando o valor referente ao tipo do cartão
            int valor = askInt("\nInsira o tipo do cartão:\n[1] DÉBITO\n[2] CRÉDITO\n[3] CANCELAR");
            if (valor > 0 && valor < 3) {
                tipoCartao = TipoCartao.getTipoCartao(valor);
                System.out.println();
            } else {
                System.err.println("Operação cancelada!\n");
                return;
            }

        } else {
            tipoCartao = TipoCartao.DEBITO;
        }
            switch (tipoCartao) {
                case CREDITO -> {
                    CartaoDeCredito cartaoDeCredito = new CartaoDeCredito();
                    cartaoDeCredito.setConta(conta);
                    cartaoDeCredito.setLimite(1000);
                    cartaoDeCredito.setTipo(TipoCartao.CREDITO);
                    cartaoDeCredito.setVencimento(LocalDate.now().plusYears(4));
                    cartaoDeCredito.setCodigoSeguranca(ThreadLocalRandom.current().nextInt(100, 999));
                    cartaoDeCredito.setDataExpedicao(LocalDate.now());
                    try {
                        cartaoDeCredito = (CartaoDeCredito) this.cartaoRepository.adicionar(cartaoDeCredito);
                        if (cartaoDeCredito != null) {
                            System.out.println("Novo CARTÃO de CRÉDITO adicionado com sucesso!");
                            System.out.println("\tDados do CARTÃO: ");
                            System.out.println("\t\tNúmero da conta que possui o cartão: " + cartaoDeCredito.getConta().getNumeroConta());
                            System.out.println("\t\tLimite: " + cartaoDeCredito.getLimite());
                            System.out.println("\t\tTipo: " + cartaoDeCredito.getTipo());
                            System.out.println("\t\tVencimento: " + cartaoDeCredito.getVencimento());
                            System.out.println("\t\tData de expedição: " + cartaoDeCredito.getDataExpedicao());
                            System.out.println("\t\tCódigo de segurança: " + cartaoDeCredito.getCodigoSeguranca() + "\n");
                        }
                    } catch (BancoDeDadosException e) {
                        e.printStackTrace();
                    }
                }
                case DEBITO -> {
                    CartaoDeDebito cartaoDeDebito = new CartaoDeDebito();
                    cartaoDeDebito.setConta(conta);
                    cartaoDeDebito.setTipo(TipoCartao.DEBITO);
                    cartaoDeDebito.setCodigoSeguranca(ThreadLocalRandom.current().nextInt(100, 999));
                    cartaoDeDebito.setDataExpedicao(LocalDate.now());
                    cartaoDeDebito.setVencimento(LocalDate.now().plusYears(4));
                    try {
                        cartaoDeDebito = (CartaoDeDebito) this.cartaoRepository.adicionar(cartaoDeDebito);
                        if (cartaoDeDebito != null) {
                            System.out.println("Novo CARTÃO de DÉBITO adicionado com sucesso!");
                            System.out.println("\tDados do CARTÃO: ");
                            System.out.println("\t\tNúmero da conta que possui o cartão: " + cartaoDeDebito.getConta().getNumeroConta());
                            System.out.println("\t\tTipo: " + cartaoDeDebito.getTipo());
                            System.out.println("\t\tVencimento: " + cartaoDeDebito.getVencimento());
                            System.out.println("\t\tData de expedição: " + cartaoDeDebito.getDataExpedicao());
                            System.out.println("\t\tCódigo de segurança: " + cartaoDeDebito.getCodigoSeguranca() + "\n");
                        }
                    } catch (BancoDeDadosException e) {
                        e.printStackTrace();
                    }
                }
                default -> System.err.println("Erro bizarro!");
            }
    }

    public void deletarCartao(Conta conta) {
        List<Cartao> cartoes = this.returnCartoes(conta);
        if (cartoes.size() == 1) {
            System.err.println("Você não pode remover mais cartões, é necessário ter no mínimo 1x cartão!\n");
        } else {
            int cartao;

            StringBuilder message = new StringBuilder("\nSelecione o cartão para remover:\n");
            for (int i = 0; i < cartoes.size(); i++) {
                if (cartoes.get(i) != null) {
                    message.append("Cartão [").append(i + 1).append("] -> ").append(cartoes.get(i).getTipo() == TipoCartao.DEBITO ? "Débito" : "Crédito");
                    //pular uma linha para exibição
                    if(i == 0){
                        message.append("\n");
                    }
                }
            }

            cartao = askInt(String.valueOf(message)) - 1;
            if (cartao > -1 && cartao < cartoes.size()) {
                try {
                    if (cartoes.get(cartao).getTipo() == TipoCartao.CREDITO) {
                        CartaoDeCredito cartaoDeCredito = (CartaoDeCredito) cartoes.get(cartao);
                        if (cartaoDeCredito.getLimite() == 1000 && this.cartaoRepository.remover(cartoes.get(cartao).getNumeroCartao())) {
                            System.err.println("Cartão removido com sucesso!\n");
                        } else {
                            System.err.println("Não é possível deletar o cartão!\n");
                        }
                    } else {
                        if (this.cartaoRepository.remover(cartoes.get(cartao).getNumeroCartao())) {
                            System.err.println("Cartão removido com sucesso!\n");
                        } else {
                            System.err.println("Não é possível deletar o cartão!\n");
                        }
                    }

                } catch (BancoDeDadosException e) {
                    e.printStackTrace();
                }
            } else if (cartao > 1){
                System.err.println("Opção inválida!\n");
            }
        }
    }

    public void deletarCartao(Cartao cartao) {
        try {
            if (cartao.getTipo() == TipoCartao.CREDITO) {
                CartaoDeCredito cartaoDeCredito = (CartaoDeCredito) cartao;
                if (cartaoDeCredito.getLimite() != 1000 && this.cartaoRepository.remover(cartaoDeCredito.getNumeroCartao())) {
                    System.out.println("CARTÃO removido com sucesso!");
                } else {
                    System.err.println("Problemas na deleção do CARTÃO");
                }
            } else {
                if (this.cartaoRepository.remover(cartao.getNumeroCartao())) {
                    System.out.println("CARTÃO removido com sucesso!");
                } else {
                    System.err.println("Problemas na deleção do CARTÃO");
                }
            }
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
    }

    public List<Cartao> returnCartoes(Conta conta) {
        List<Cartao> cartoes = new ArrayList<>();
        try {
            cartoes = this.cartaoRepository.listarCartoesPorNumeroConta(conta);
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
        if (cartoes.size() == 0) {
            return null;
        } else {
            return cartoes;
        }
    }

    public boolean editarCartao(String idCartao, Cartao cartao) {
        try {
            return this.cartaoRepository.editar(idCartao, cartao);
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
        return false;
    }
}
