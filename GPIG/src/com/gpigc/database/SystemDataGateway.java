package com.gpigc.database;

import java.util.List;
import java.util.Date;

public interface SystemDataGateway {
	public List<SystemData> readSystemData(String systemId, int numberOfRecords);
	
	public List<SystemData> readSystemBetween(String systemId, Date startDate, Date endDate);
}
