package com.gpigc.core.storage;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {

	private HikariDataSource connectionPool;

	private ConnectionPool(Builder builder) {
		HikariConfig config = new HikariConfig();
		config.setMaximumPoolSize(builder.maximumPoolSize);
		config.setMinimumIdle(builder.minimumPoolSize);
		config.setDataSourceClassName(builder.dataSourceClassName);
		config.setConnectionTimeout(builder.connectionTimeOut);
		config.addDataSourceProperty("portNumber", builder.portNumber);
		config.addDataSourceProperty("serverName", builder.serverName);
		config.addDataSourceProperty("user", builder.user);
		config.addDataSourceProperty("password", builder.password);
		config.addDataSourceProperty("databaseName", builder.databaseName);
		config.addDataSourceProperty("ssl", builder.ssl);
		config.addDataSourceProperty("sslfactory", builder.sslFactory);

		connectionPool = new HikariDataSource(config);
	}

	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}

	public static class Builder {
		private int portNumber;
		private int minimumPoolSize;
		private int maximumPoolSize;
		private int connectionTimeOut;

		private String dataSourceClassName;
		private String serverName;
		private String user;
		private String password;
		private String databaseName;
		private String ssl;
		private String sslFactory;

		public Builder portNumber(int value) {
			portNumber = value;
			return this;
		}

		public Builder minimumPoolSize(int value) {
			minimumPoolSize = value;
			return this;
		}

		public Builder maximumPoolSize(int value) {
			maximumPoolSize = value;
			return this;
		}
		
		public Builder connectionTimeOut(int value) {
			connectionTimeOut = value;
			return this;
		}

		public Builder dataSourceClassName(String value) {
			dataSourceClassName = value;
			return this;
		}

		public Builder serverName(String value) {
			serverName = value;
			return this;
		}

		public Builder user(String value) {
			user = value;
			return this;
		}

		public Builder password(String value) {
			password = value;
			return this;
		}

		public Builder databaseName(String value) {
			databaseName = value;
			return this;
		}

		public Builder ssl(boolean value) {
			ssl = Boolean.toString(value);
			return this;
		}

		public Builder sslFactory(String value) {
			sslFactory = value;
			return this;
		}

		public ConnectionPool build() {
			return new ConnectionPool(this);
		}
	}
}
