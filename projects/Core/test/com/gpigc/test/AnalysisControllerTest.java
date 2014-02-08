package com.gpigc.test;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gpigc.core.analysis.AnalysisController;
import com.gpigc.core.analysis.Result;
import com.gpigc.core.analysis.engine.MeanAnalysis;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class AnalysisControllerTest {
	
	private AnalysisController analysisController;
	private MeanAnalysis meanAnalysis;
	private Result result;
	
	@Mock
	private SystemDataGateway database;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void analysisControllerMeanAnalysisTest() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, FailedToReadFromDatastoreException {
		givenAnalysisController();
		whenAnalysisIsPerformed(2);
	}

	private void whenAnalysisIsPerformed(int numberOfRecords)
			throws FailedToReadFromDatastoreException {
		Mockito.when(database.readMostRecent("1", 10)).thenReturn(createQueryResult(numberOfRecords));
	}

	private void givenAnalysisController() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,	NoSuchMethodException {
		analysisController = new AnalysisController(database);
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
