package com.advnote.server.common;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.ws.rs.InternalServerErrorException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionImpl;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
	
	private static final String DATABASE_PROPERTIES_FILE = "db.properties";
	private static final String HIBERNATE_CONFIG_FILE = "hibernate.cfg.xml";
	
	
	private static final synchronized SessionFactory buildSessionFactory() {
		if (sessionFactory == null) {
			Properties dbConnectionProperties = new Properties();

			InputStream propertiesFileStream = null;

			try {
				// db.properties contains properties there are assigned by "Maven profiles" inside pom.xml.
				propertiesFileStream = ConfigHelper.getResourceAsStream(DATABASE_PROPERTIES_FILE);
				dbConnectionProperties.load(propertiesFileStream);
			} catch (IOException e) {
				throw new InternalServerErrorException(e);
			} finally {
				if (propertiesFileStream != null) {
					try {
						propertiesFileStream.close();
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}

			Configuration configuration = new Configuration().mergeProperties(dbConnectionProperties).configure(HIBERNATE_CONFIG_FILE);

			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
					.buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}

		return sessionFactory;
	}

	public static final Session getSessionFactory() {
		return buildSessionFactory().openSession();
	}

	public static final void closeSession(Session session) {
		if (session.isOpen()) {
			session.close();
		}
	}

	public static final void closeResources(Connection connection, Statement statement, ResultSet resultSet) {

		if (resultSet != null) {

			try {
				resultSet.close();
			} catch (SQLException e) {
				throw new InternalServerErrorException(e);
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				throw new InternalServerErrorException(e);
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new InternalServerErrorException(e);
			}
		}

	}

	public static final Connection getConnectionFromSession(Session session) {
		return ((SessionImpl) session).connection();
	}
}