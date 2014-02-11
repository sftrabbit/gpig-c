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
import com.gpigc.core.analysis.AnalysisEngine;
import com.gpigc.core.analysis.Result;
import com.gpigc.core.analysis.engine.MeanAnalysis;
import com.gpigc.core.event.Event;
import com.gpigc.core.notification.NotificationGenerator;
import com.gpigc.dataabstractionlayer.client.FailedToReadFromDatastoreException;
import com.gpigc.dataabstractionlayer.client.QueryResult;
import com.gpigc.dataabstractionlayer.client.SensorState;
import com.gpigc.dataabstractionlayer.client.SystemDataGateway;

public class AnalysisControllerTest {
	
	private AnalysisController analysisController;
	
	@Mock
	private SystemDataGateway database;
	
	@Mock
	private NotificationGenerator notificationGenerator;
	
	@Mock
	private MeanAnalysis meanAnalysis;
	@Mock 
	private Result result;
	
	private List<AnalysisEngine> engines;
	
	@Before
	public void setUp() {
		engines = new ArrayList<AnalysisEngine>();
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void analysisControllerNotificationRequired() throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
		givenAnAnalysisController();
		whenTheAnalysisControllerSearchesForId("1");
		whenSystemUpdateIsCalledOnId("1", true);
		thenNotificationIsTriggered();
	}
	
	@Test
	public void analysisControllerNotificationNotRequired() throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
		givenAnAnalysisController();
		whenTheAnalysisControllerSearchesForId("1");
		whenSystemUpdateIsCalledOnId("1", false);
		thenNotificationIsNotTriggered();
	}

	private void givenAnAnalysisController() throws 	NoSuchMethodException,
														ClassNotFoundException, InstantiationException,
														IllegalAccessException, InvocationTargetException, MalformedURLException, IllegalArgumentException, SecurityException {
		analysisController = new AnalysisController(database, notificationGenerator);
		engines = new ArrayList<AnalysisEngine>();
		engines.add(meanAnalysis);
		analysisController.setAnalysisEngines(engines);
	}
	
	private void whenTheAnalysisControllerSearchesForId(String systemId) {
		List<String> associatedSystemIds = new ArrayList<String>();
		associatedSystemIds.add(systemId);
		Mockito.when(meanAnalysis.getAssociatedSystems()).thenReturn(associatedSystemIds);
	}
	

	private void whenSystemUpdateIsCalledOnId(String systemId, boolean isNotify) {
		Mockito.when(meanAnalysis.analyse()).thenReturn(result);
		Mockito.when(result.isNotify()).thenReturn(isNotify);
		analysisController.systemUpdate(systemId);
	}
	
	private void thenNotificationIsTriggered() {
		Mockito.verify(notificationGenerator, Mockito.times(1)).generate(Mockito.any(Event.class));
	}
	
	private void thenNotificationIsNotTriggered() {
		Mockito.verify(notificationGenerator, Mockito.times(0)).generate(Mockito.any(Event.class));
	}
}
