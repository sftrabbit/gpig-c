package com.gpigc.test;

import static org.junit.Assert.*;

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

import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;
import com.gpigc.core.analysis.Result;
import com.gpigc.core.analysis.engine.MeanAnalysis;

public class MeanAnalysisTest {
	
	private MeanAnalysis meanAnalysis;
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
		addSensorValues(3);
		whenTheMeanOfTheValuesAreCalculated();
		thenTheMeanIsCalulcatedAs("2.0");
		noNotificationIsTriggered();
	}
	
	@Test
	public void meanAnalysisFloatNumbers() throws FailedToReadFromDatastoreException {
		givenOneSystem();
		addSensorValues(2);
		whenTheMeanOfTheValuesAreCalculated();
		thenTheMeanIsCalulcatedAs("1.5");
		noNotificationIsTriggered();
	}

	@Test
	public void notificationWhenMeanViolatesBounds() throws FailedToReadFromDatastoreException {
		givenOneSystem();
		addSensorValues(1);
		whenTheMeanOfTheValuesAreCalculated();
		thenNotificationIsTriggered();		
	}
	
	@Test
	public void errorWhenSystemIdNotFound() throws FailedToReadFromDatastoreException {
		givenOneSystem();
		withNoSensorData();
		whenTheMeanOfTheValuesAreCalculated();
		thenAnErrorResultIsReturned();
	}
	
	private void noNotificationIsTriggered() {
		assertFalse(result.isNotify());
	}
	
	private void thenAnErrorResultIsReturned() {
		assertTrue(result.getDataToSave().containsKey((String) "Error"));
	}

	private void withNoSensorData() throws FailedToReadFromDatastoreException {
		Mockito.when(database.readMostRecent("1", 10)).thenThrow(new FailedToReadFromDatastoreException("Failed"));
	}

	private void thenNotificationIsTriggered() {
		assertTrue(result.isNotify());
	}

	private void givenOneSystem() {
		meanAnalysis = new MeanAnalysis(database);
	}
	
	private void addSensorValues(int numberOfRecords) throws FailedToReadFromDatastoreException {
		Mockito.when(database.readMostRecent("1", 10)).thenReturn(createQueryResult(numberOfRecords));
	}

	private void whenTheMeanOfTheValuesAreCalculated() {
		result = meanAnalysis.analyse();
	}
	
	private void thenTheMeanIsCalulcatedAs(String string) {
		Map<?, ?> data = result.getDataToSave();
		Iterator<?> dataIterator = data.keySet().iterator();
		
		while(dataIterator.hasNext()) {
			String key = dataIterator.next().toString();
			assertEquals(string, data.get(key));
		}
	}

	private QueryResult createQueryResult(int numberOfRecords) {
		List<SensorState> sensorStates = new ArrayList<SensorState>();	
		for(Integer i = 1; i < (numberOfRecords + 1); i ++) {
			sensorStates.add(new SensorState(i.toString(), new Date(), new Date(), i.toString()));
		}
		QueryResult queryResult = new QueryResult("1", sensorStates);
		return queryResult;
	}
}