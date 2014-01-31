package com.gpigc.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gpig.client.FailedToReadFromDatastoreException;
import com.gpig.client.QueryResult;
import com.gpig.client.SensorState;
import com.gpig.client.SystemDataGateway;
import com.gpigc.core.analysis.Result;
import com.gpigc.core.analysis.engines.MeanAnalysis;

public class MeanAnalysisTest {
	
	private MeanAnalysis maTest;
	private Result result;
	
	@Mock
	private SystemDataGateway database;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void meanAnalysisRealNumbers() throws FailedToReadFromDatastoreException {
		givenOneSystem();
		andSensorValues(3);
		whenTheMeanOfTheTenValuesAreCalculated();
		thenTheMeanIsCalulcatedAs(2.0);
		
	}
	
	@Test
	public void meanAnalysisFloatNumbers() throws FailedToReadFromDatastoreException {
		givenOneSystem();
		andSensorValues(2);
		whenTheMeanOfTheTenValuesAreCalculated();
		thenTheMeanIsCalulcatedAs(1.5);
	}

	private void givenOneSystem() {
		maTest = new MeanAnalysis(database);
	}
	
	private void andSensorValues(int numberOfRecords) throws FailedToReadFromDatastoreException {
		Mockito.when(database.readMostRecent("1", 10)).thenReturn(createSystemData(numberOfRecords));
	}

	private void whenTheMeanOfTheTenValuesAreCalculated() {
		result = maTest.analyse();
	}
	
	private void thenTheMeanIsCalulcatedAs(double meanValue) {
		Map<?, ?> data = result.getDataToSave();
		Iterator<?> dataIterator = data.keySet().iterator();
		
		while(dataIterator.hasNext()) {
			String key = dataIterator.next().toString();
			assertEquals(meanValue, data.get(key));
		}
	}

	private QueryResult createSystemData(int systemData) {
		List<SensorState> systemDatas = new ArrayList<SensorState>();	
		for(Integer i = 1; i < (systemData + 1); i ++) {
			systemDatas.add(new SensorState("1234", new Date(), new Date(), i.toString()));
		}
		
		QueryResult result = new QueryResult("1", systemDatas);
		return result;
	}
	
}