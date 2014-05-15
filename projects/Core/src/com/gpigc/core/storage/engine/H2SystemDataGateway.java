package com.gpigc.core.storage.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gpigc.core.ClientSystem;
import com.gpigc.core.storage.SystemDataGateway;
import com.gpigc.dataabstractionlayer.client.EmitterSystemState;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.FailedToWriteToDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class H2SystemDataGateway extends SystemDataGateway {

	private HikariDataSource connectionPool;

	private String writerEmitterSystemState = "INSERT INTO EMITTER_SYSTEM_STATE (SYSTEM_ID, TIMESTAMP) values(?, ?)";
	private String writeSensorReading = "INSERT INTO SENSOR_READINGS(emitter_system, sensor_id, value, database_timestamp) values(?, ?, ?, ?)";
	private String readMostRecentStatement = "SELECT * FROM EMITTER_SYSTEM_STATE RIGHT OUTER JOIN SENSOR_READINGS ON EMITTER_SYSTEM_STATE.EMITTER_SYSTEM_PK = SENSOR_READINGS.EMITTER_SYSTEM WHERE SYSTEM_ID = ? AND sensor_id = ? ORDER BY database_timestamp desc LIMIT ?;";
	private String readBetween = "SELECT * FROM EMITTER_SYSTEM_STATE RIGHT OUTER JOIN SENSOR_READINGS ON EMITTER_SYSTEM_STATE.EMITTER_SYSTEM_PK = SENSOR_READINGS.EMITTER_SYSTEM WHERE SYSTEM_ID = ? AND timestamp >= ? AND timestamp <= ? AND sensor_id = ?;";

	public H2SystemDataGateway(List<ClientSystem> registeredSystems) {
		super(registeredSystems);
		setupConnectionPool();
	}

	private void setupConnectionPool() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName("org.h2.Driver");
		config.setJdbcUrl("jdbc:h2:file:~\\GPIG-C-H2-DATASTORE");
		config.setConnectionTimeout(10000);
		connectionPool = new HikariDataSource(config);
		try {
			initialiseTables();
		} catch (FailedToWriteToDatastoreException e) {
			e.printStackTrace();
		}
	}

	public void initialiseTables() throws FailedToWriteToDatastoreException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = connectionPool.getConnection();
			statement = connection.createStatement();
			statement.execute("DROP INDEX IF EXISTS emitter_system_idx");
			statement.execute("DROP INDEX IF EXISTS sensor_reading_idx");
			statement.execute("DROP TABLE IF EXISTS SENSOR_READINGS CASCADE");
			statement
					.execute("DROP TABLE IF EXISTS EMITTER_SYSTEM_STATE CASCADE");
			statement
					.execute("CREATE TABLE EMITTER_SYSTEM_STATE (EMITTER_SYSTEM_PK identity PRIMARY KEY, SYSTEM_ID varchar(255), TIMESTAMP timestamp);");
			statement
					.execute("CREATE TABLE SENSOR_READINGS (EMITTER_SYSTEM bigint references EMITTER_SYSTEM_STATE (EMITTER_SYSTEM_PK), SENSOR_ID varchar(255), VALUE varchar(255), CREATION_TIMESTAMP timestamp, DATABASE_TIMESTAMP timestamp);");
			statement
					.execute("CREATE INDEX emitter_system_idx ON EMITTER_SYSTEM_STATE(EMITTER_SYSTEM_PK, timestamp);");
			statement
					.execute("CREATE INDEX sensor_reading_idx ON SENSOR_READINGS(EMITTER_SYSTEM);");
		} catch (SQLException e) {
			throw new FailedToWriteToDatastoreException(e.toString());
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
		}
	}

	public QueryResult readMostRecent(String systemID, String sensorID,
			int numRecords) throws FailedToReadFromDatastoreException {
		Connection connection = null;
		PreparedStatement readMostRecent = null;
		ResultSet resultSet = null;
		QueryResult result = null;
		try {
			connection = connectionPool.getConnection();
			readMostRecent = connection
					.prepareStatement(readMostRecentStatement);
			readMostRecent.setString(1, systemID);
			readMostRecent.setString(2, sensorID);
			readMostRecent.setInt(3, numRecords);

			resultSet = readMostRecent.executeQuery();

			result = constructResult(systemID, resultSet);
		} catch (SQLException e) {
			System.out
					.println("Failed to read most recent records from H2 datastore");
			e.printStackTrace();
		} finally {
			try {
				if (readMostRecent != null) {
					readMostRecent.close();
				}
			} catch (SQLException e) {
				throw new FailedToReadFromDatastoreException(e.toString());

			}
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				throw new FailedToReadFromDatastoreException(e.toString());

			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new FailedToReadFromDatastoreException(e.toString());
			}
		}
		return result;
	}

	public QueryResult readBetween(String systemID, String sensorID,
			Date start, Date end) throws FailedToReadFromDatastoreException {
		Connection connection = null;
		PreparedStatement readBetweenQuery = null;
		ResultSet resultSet = null;
		QueryResult result = null;
		try {
			connection = connectionPool.getConnection();
			readBetweenQuery = connection.prepareStatement(readBetween);
			readBetweenQuery.setString(1, systemID);
			readBetweenQuery.setTimestamp(2, toSQLTimestamp(start));
			readBetweenQuery.setTimestamp(3, toSQLTimestamp(end));
			readBetweenQuery.setString(4, sensorID);
			resultSet = readBetweenQuery.executeQuery();

			result = constructResult(systemID, resultSet);
		} catch (SQLException e) {
			System.out.println("Failed to execute query:");
			e.printStackTrace();
		} finally {
			try {
				if (readBetweenQuery != null) {
					readBetweenQuery.close();
				}
			} catch (SQLException e) {
				throw new FailedToReadFromDatastoreException(e.toString());

			}
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				throw new FailedToReadFromDatastoreException(e.toString());

			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new FailedToReadFromDatastoreException(e.toString());
			}
		}
		return result;
	}

	public void write(EmitterSystemState data)
			throws FailedToWriteToDatastoreException {
		Connection connection = null;
		PreparedStatement emitterInsert = null;
		PreparedStatement sensorReadingsInsert = null;
		ResultSet resultSet = null;
		int autoGeneratedPrimaryKey = 0;
		try {
			connection = connectionPool.getConnection();
			emitterInsert = connection.prepareStatement(
					writerEmitterSystemState, Statement.RETURN_GENERATED_KEYS);
			emitterInsert.setString(1, data.getSystemID());
			emitterInsert.setTimestamp(2, toSQLTimestamp(data.getTimeStamp()));
			emitterInsert.execute();

			resultSet = emitterInsert.getGeneratedKeys();
			while (resultSet.next()) {
				autoGeneratedPrimaryKey = resultSet.getInt(1);
			}

			Map<String, String> sensorReadings = data.getSensorReadings();

			for (Map.Entry<String, String> sensorReading : sensorReadings
					.entrySet()) {
				sensorReadingsInsert = connection
						.prepareStatement(writeSensorReading);
				sensorReadingsInsert.setLong(1, autoGeneratedPrimaryKey);
				sensorReadingsInsert.setString(2, sensorReading.getKey());
				sensorReadingsInsert.setString(3, sensorReading.getValue());
				sensorReadingsInsert
						.setTimestamp(4, toSQLTimestamp(new Date()));
				sensorReadingsInsert.execute();
			}
		} catch (SQLException e) {
			System.out.println("Failed to write data to H2 datastore");
			e.printStackTrace();
		} finally {
			try {
				if (emitterInsert != null) {
					emitterInsert.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
			try {
				if (sensorReadingsInsert != null) {
					sensorReadingsInsert.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
		}
	}

	public void write(List<EmitterSystemState> data)
			throws FailedToWriteToDatastoreException {
		Connection connection = null;
		PreparedStatement emitterInsert = null;
		PreparedStatement sensorReadingsInsert = null;
		ResultSet resultSet = null;
		try {
			int primaryKey = 0;
			connection = connectionPool.getConnection();
			emitterInsert = connection.prepareStatement(
					writerEmitterSystemState, Statement.RETURN_GENERATED_KEYS);
			sensorReadingsInsert = connection
					.prepareStatement(writeSensorReading);
			for (EmitterSystemState systemState : data) {
				emitterInsert.setString(1, systemState.getSystemID());
				emitterInsert.setTimestamp(2,
						toSQLTimestamp(systemState.getTimeStamp()));
				emitterInsert.execute();
				emitterInsert.clearParameters();
				resultSet = emitterInsert.getGeneratedKeys();

				while (resultSet.next()) {
					primaryKey = resultSet.getInt(1);
				}

				for (Map.Entry<String, String> sensorReading : systemState
						.getSensorReadings().entrySet()) {
					sensorReadingsInsert.setLong(1, primaryKey);
					sensorReadingsInsert.setString(2, sensorReading.getKey());
					sensorReadingsInsert.setString(3, sensorReading.getValue());
					sensorReadingsInsert.setTimestamp(4,
							toSQLTimestamp(new Date()));
					sensorReadingsInsert.execute();
					sensorReadingsInsert.clearParameters();
				}
			}
		} catch (SQLException e) {
			System.out.println("Failed to write data to H2 datastore");
			e.printStackTrace();
		} finally {
			try {
				if (emitterInsert != null) {
					emitterInsert.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
			try {
				if (sensorReadingsInsert != null) {
					sensorReadingsInsert.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new FailedToWriteToDatastoreException(e.toString());
			}
		}
	}
}