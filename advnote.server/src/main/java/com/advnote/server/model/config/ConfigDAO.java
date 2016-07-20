package com.advnote.server.model.config;

import org.springframework.stereotype.Repository;

import com.advnote.server.common.GenericDAO;

@Repository
public class ConfigDAO extends GenericDAO<Config> {
	
	public static final Integer MAIL_ACCOUNT_NAME = 0;
	public static final Integer MAIL_ACCOUNT_PASSWORD = 1;
	public static final Integer MAIL_ACCOUNT_SMTP_URL = 2;
	public static final Integer MAIL_ACCOUNT_SMTP_PORT = 3;
	public static final Integer MAIL_CONTACT_ACCOUNT = 4;
	public static final Integer MAIL_SEND_ACCOUNT = 5;

	public ConfigDAO() {
		super(Config.class);
	}

}
