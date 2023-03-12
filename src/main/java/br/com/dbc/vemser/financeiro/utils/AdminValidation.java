package br.com.dbc.vemser.financeiro.utils;

public class AdminValidation {

    private static final String LOGIN = "admin";
    private static final String SENHA = "abacaxi";

    public static boolean validar(String login, String senha) {
        if (login.equals(LOGIN) && senha.equals(SENHA)) {
            return true;
        }
        return false;
    }
}
