package com.gpigc.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class SensorParameterTest {

	
	@Test
	public void testKeys() {
		assertEquals("LowerBound",SensorParameter.LOWER_BOUND.getKey());
		assertEquals("UpperBound",SensorParameter.UPPER_BOUND.getKey());
	}
	
	@Test
	public void testToString() {
		assertEquals(SensorParameter.LOWER_BOUND.getKey(),SensorParameter.LOWER_BOUND.toString());
	}

}
