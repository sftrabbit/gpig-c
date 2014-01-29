package com.gpigc.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gpigc.analysis.engines.MeanAnalysis;
import com.gpigc.analysis.Result;
import com.gpigc.database.SystemData;
import com.gpigc.database.SystemDataGateway;

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
	public void meanAnalysisRealNumbers() {
		givenOneSystem();
		andSensorValues(3);
		whenTheMeanOfTheTenValuesAreCalculated();
		thenTheMeanIsCalulcatedAs(2.0);
		
	}
	
	@Test
	public void meanAnalysisFloatNumbers() {
		givenOneSystem();
		andSensorValues(2);
		whenTheMeanOfTheTenValuesAreCalculated();
		thenTheMeanIsCalulcatedAs(1.5);
	}

	private void givenOneSystem() {
		maTest = new MeanAnalysis(database);
	}
	
	private void andSensorValues(int numberOfRecords) {
		Mockito.when(database.readSystemData("1", 10)).thenReturn(createSystemData(numberOfRecords));
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
	
	private List<SystemData> createSystemData(int systemData) {
		List<SystemData> systemDatas = new ArrayList<SystemData>();
		Map<String, Integer> payload = new HashMap<String, Integer>();	
		for(Integer i = 1; i < (systemData + 1); i ++) {
			payload.put(i.toString(), i);		
		}
		systemDatas.add(new SystemData(payload, new Date(), "1234"));
		
		return systemDatas;
	}
	
}