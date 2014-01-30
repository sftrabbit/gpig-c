package com.gpig.server;

import java.util.Date;

/**
 * The state of a sensor at a point in time
 * 
 * @author GPIG-C
 */
public class SensorState {
	
	private final String sensorID;
	private final Date creationTimestamp;
	private final Date databaseTimestamp;
	private final String value;

	/**
	 * @param sensorID A sensor ID
	 * @param creationTimestamp The time this sensor value was first read
	 * @param databaseTimestamp The time this sensor value was stored
	 * @param value The sensor value
	 */
	public SensorState(
			String sensorID, 
			Date creationTimestamp, 
			Date databaseTimestamp, 
			String value){
		this.sensorID = sensorID;
		this.creationTimestamp = creationTimestamp;
		this.databaseTimestamp = databaseTimestamp;
		this.value = value;
	}

	/**
	 * @return The ID of the sensor that this record refers to
	 */
	public String getSensorID() {
		return sensorID;
	}

	/**
	 * @return The time that this sensor value was recived by the HUMS
	 */
	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	/**
	 * @return The time that this sensor value was written to the datastore
	 */
	public Date getDatabaseTimestamp() {
		return databaseTimestamp;
	}

	/**
	 * @return The value of the sensor at approximately the creation time
	 */
	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((creationTimestamp == null) ? 0 : creationTimestamp
						.hashCode());
		result = prime
				* result
				+ ((databaseTimestamp == null) ? 0 : databaseTimestamp
						.hashCode());
		result = prime * result
				+ ((sensorID == null) ? 0 : sensorID.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorState other = (SensorState) obj;
		if (creationTimestamp == null) {
			if (other.creationTimestamp != null)
				return false;
		} else if (!creationTimestamp.equals(other.creationTimestamp))
			return false;
		if (databaseTimestamp == null) {
			if (other.databaseTimestamp != null)
				return false;
		} else if (!databaseTimestamp.equals(other.databaseTimestamp))
			return false;
		if (sensorID == null) {
			if (other.sensorID != null)
				return false;
		} else if (!sensorID.equals(other.sensorID))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
