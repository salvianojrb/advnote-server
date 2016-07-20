package com.advnote.server.model.pessoaFisica;

import java.io.Serializable;
import java.util.Date;

import com.advnote.server.model.pessoa.Pessoa;

public class PessoaFisica extends Pessoa implements Serializable {
	
	private static final long serialVersionUID = -5634141239472513279L;
	
	private String nome;
	private Long cpf;
	private Long rg;
	private Date dtNascimento;
	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Long getCpf() {
		return cpf;
	}
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	public Long getRg() {
		return rg;
	}
	public void setRg(Long rg) {
		this.rg = rg;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	
	

}
