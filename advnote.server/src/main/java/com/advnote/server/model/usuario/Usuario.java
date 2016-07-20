package com.advnote.server.model.usuario;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "USUARIO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario implements Serializable {

	private static final long serialVersionUID = 2815963825149160687L;

	@Id
	@Column(name = "ID_USUARIO")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long idUsuario;

	@Column(name = "NM_USUARIO")
	private String nmUsuario;
	
	@Column(name = "DS_EMAIL")
	private String dsEmail;
	
	@Column(name = "DS_SENHA")
	private String dsSenha;
	
	@Column(name = "DS_TOKEN")
	private String dsToken;
	
	@Column(name = "DT_CRIACAO")
	private Date dtCriacao;
	
	@Column(name = "DT_ULTIMO_ACESSO")
	private Date dtUltimoAcesso;
	
	@Column(name = "SN_ATIVO")
	private String snAtivo;

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNmUsuario() {
		return nmUsuario;
	}

	public void setNmUsuario(String nmUsuario) {
		this.nmUsuario = nmUsuario;
	}

	public String getDsEmail() {
		return dsEmail;
	}

	public void setDsEmail(String dsEmail) {
		this.dsEmail = dsEmail;
	}

	public String getDsSenha() {
		return dsSenha;
	}

	public void setDsSenha(String dsSenha) {
		this.dsSenha = dsSenha;
	}

	public String getDsToken() {
		return dsToken;
	}

	public void setDsToken(String dsToken) {
		this.dsToken = dsToken;
	}

	public Date getDtCriacao() {
		return dtCriacao;
	}

	public void setDtCriacao(Date dtCriacao) {
		this.dtCriacao = dtCriacao;
	}

	public Date getDtUltimoAcesso() {
		return dtUltimoAcesso;
	}

	public void setDtUltimoAcesso(Date dtUltimoAcesso) {
		this.dtUltimoAcesso = dtUltimoAcesso;
	}

	public String getSnAtivo() {
		return snAtivo;
	}

	public void setSnAtivo(String snAtivo) {
		this.snAtivo = snAtivo;
	}

}
