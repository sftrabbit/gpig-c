package com.gpigc.database;

import java.util.Date;
import java.util.List;

public class Concrete implements SystemDataGateway {

	@Override
	public List<SystemData> readSystemData(String systemId, int numberOfRecords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SystemData> readSystemBetween(String systemId, Date startDate,
			Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
