package com.advnote.server.model.pessoaJuridica;

import java.io.Serializable;

import com.advnote.server.model.pessoa.Pessoa;

public class PessoaJuridica extends Pessoa implements Serializable {

	private static final long serialVersionUID = 3196231896250878926L;
	
	private String razaoSocial;
	
	private Long cnpj;
	
	private Long inscricaoEstadual;

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public Long getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public void setInscricaoEstadual(Long inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}
	

}
