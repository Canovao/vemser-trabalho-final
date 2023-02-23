package entities.view;

import entities.model.Conta;
import entities.service.TransferenciaService;

public class TelaTransferencias extends Tela {
    public static void exibirTransferencias(){
        System.out.println("Você está na Tela de Transferências");
        TelaTransferencias.tratarInput();
    }

    public static void tratarInput() {
        int input = pedirInput("[1] -> Insira seus dados de login para VISUALIZAR suas TRANSFERÊNCIAS\n[2] -> Voltar para a Tela Principal");
        Conta login;
        switch(input){
            case 1 ->{
                login = Tela.login();
                if(login != null){
                    TransferenciaService.exibirTransferencias(login);
                }else{
                    System.err.println("Login mal-sucedido");
                }
                exibirTransferencias();
            }
            case 2 -> Tela.redirecionarParaTela(1);
            default -> {
                System.err.println("Opção inválida!");
                exibirTransferencias();
            }
        }
    }
}
