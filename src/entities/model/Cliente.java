package entities.model;

import entities.controller.BancoDeDados;
import entities.interfaces.Exibicao;

import java.util.ArrayList;

public class Cliente implements Exibicao {
    private String nome;
    private String cpf;
    private ArrayList<Endereco> enderecos;
    private ArrayList<Contato> contatos;
    private String login;
    private String senha;
    private ArrayList<Conta> contas;

    public Cliente(String nome, String cpf, ArrayList<Endereco> enderecos, ArrayList<Contato> contatos, String login, String senha, ArrayList<Conta> contas) {
        this.nome = nome;
        this.cpf = cpf;
        this.enderecos = enderecos;
        this.contatos = contatos;
        this.login = login;
        this.senha = senha;
        this.contas = contas;
    }

    public boolean criarConta() {
        return true;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getSenha() {
        return senha;
    }

    @Override
    public void exibir() {

    }
}
