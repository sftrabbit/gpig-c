/**
 * 
 */
package com.gpigc.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gpigc.core.analysis.Result;
import com.gpigc.core.analysis.engine.ExpressionAnalysis;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

/**
 * @author GPIG-C
 */
public class ExpressionAnalysisTest {

	private static final double THRESHOLD = 0.00001;
	private static String SYSTEM = "system";
	private static String SENSOR_A = "senA";
	private static Double SENSOR_A_VALUE = 4.0;
	private static String SENSOR_B = "senB";
	private static Double SENSOR_B_VALUE = 5.0;
	private static String EXPRESSION = "(POW(senA,2)-senB)+4";
	
	@Mock
	private SystemDataGateway database;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * Test method for 
	 * {@link com.gpigc.core.analysis.engine.ExpressionAnalysis#analyse()}.
	 * @throws FailedToReadFromDatastoreException 
	 */
	@Test
	public void testAnalyse() throws FailedToReadFromDatastoreException {
		initDatabase();
		ExpressionAnalysis analysis = new ExpressionAnalysis(
				"Test Engine", 
				EXPRESSION, 
				Arrays.asList(new String[]{SYSTEM}), 
				database, 
				false);
		Result result = analysis.analyse();
		String value = result.getDataToSave().get(
				result.getDataToSave().keySet().iterator().next()); // Get stored value
		System.out.println("Analysis value = " + value);
		double valueDouble = Double.parseDouble(value);
		double expectedValue = (Math.pow(SENSOR_A_VALUE, 2.0)-SENSOR_B_VALUE)+4.0;
		assertTrue(Math.abs(valueDouble-expectedValue) < THRESHOLD);
	}

	private void initDatabase() throws FailedToReadFromDatastoreException {
		List<SensorState> states = new ArrayList<>();
		states.add(new SensorState(SENSOR_A, new Date(14), new Date(14), SENSOR_A_VALUE.toString()));
		states.add(new SensorState(SENSOR_B, new Date(14), new Date(14), SENSOR_B_VALUE.toString()));
		Mockito.when(database.readMostRecent(SYSTEM, 1))
		.thenReturn(new QueryResult(SYSTEM, states));
	}


}
