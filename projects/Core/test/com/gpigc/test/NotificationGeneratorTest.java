package com.gpigc.test;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import com.gpigc.core.analysis.Result;
import com.gpigc.core.event.Event;
import com.gpigc.core.notification.NotificationEngine;
import com.gpigc.core.notification.NotificationGenerator;

public class NotificationGeneratorTest {

	private NotificationGenerator notificationGenerator;

	@Mock
	private NotificationEngine notificationEngine;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void notificationIsSentOnce() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		givenNotificationGenerator();
		andMockedEngine();
		withRecentlySentSetTo(false);
		whenAnEventIsGenerated();
		thenANotificationIsSent();
	}
	
	@Test
	public void notificationIsNotRepeatedlySent() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		givenNotificationGenerator();
		andMockedEngine();
		withRecentlySentSetTo(true);
		whenAnEventIsGenerated();
		thenANotificationIsNotSent();
	}

	private void givenNotificationGenerator() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		notificationGenerator = new NotificationGenerator();
		List<NotificationEngine> engines = new ArrayList<NotificationEngine>();
		engines.add(notificationEngine);
		notificationGenerator.setEngines(engines);
	}
	
	private void andMockedEngine() {
		List<String> associatedSystems = new ArrayList<String>();
		associatedSystems.add("1");
		when(notificationEngine.getAssociatedSystems()).thenReturn(associatedSystems);
	}
	
	private void withRecentlySentSetTo(boolean recentlySent) {
		when(notificationEngine.getRecentlySent()).thenReturn(recentlySent);
	}
	
	private void whenAnEventIsGenerated() {
		notificationGenerator.generate(createEvent());
	}
	
	private void thenANotificationIsSent() {
		verify(notificationEngine, times(1)).send(anyString(), anyString(), anyString());
	}
	
	private void thenANotificationIsNotSent() {
		verify(notificationEngine, times(0)).send(anyString(), anyString(), anyString());
	}
	
	private Event createEvent() {
		Map<String,String> map = new HashMap<String, String>();
		map.put("mean", "Some mean value");
		return new Event(new Result(map, true), "", "1");
	}

}
