package com.advnote.server.model.pessoa;

import java.io.Serializable;

import com.advnote.server.common.GenericDAO;
import com.advnote.server.model.pessoaFisica.PessoaFisica;
import com.advnote.server.model.pessoaJuridica.PessoaJuridica;

public abstract class Pessoa extends GenericDAO<Pessoa> implements Serializable{

	private static final long serialVersionUID = -2385472165233560627L;
	
	private String nomePessoa;
	private PessoaFisica pessoaFisica;
	private PessoaJuridica pessoaJuridica;
//	private List<Endereco> enderecos;
//	private List<Telefone> telefone;
	
	
	public String getNomePessoa() {
		return nomePessoa;
	}
	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}
	public PessoaFisica getPessoaFisica() {
		return pessoaFisica;
	}
	public void setPessoaFisica(PessoaFisica pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}
	public PessoaJuridica getPessoaJuridica() {
		return pessoaJuridica;
	}
	public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
		this.pessoaJuridica = pessoaJuridica;
	}

}
