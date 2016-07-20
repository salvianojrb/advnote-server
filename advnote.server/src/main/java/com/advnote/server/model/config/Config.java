package com.advnote.server.model.config;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "CONFIG")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Config implements Serializable {

	private static final long serialVersionUID = 3135498872457843178L;

	@Id
	@Column(name = "KEY")
	@NotNull
	private Integer key;

	@NotNull
	@Column(name = "VALUE")
	private String value;

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

