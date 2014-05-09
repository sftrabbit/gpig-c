package com.gpigc.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParameterTest {

	@Test
	public void testisValid() {
		assertTrue(Parameter.isValid("LOWER_BOUND"));
		assertTrue(Parameter.isValid("UPPER_BOUND"));
		assertTrue(Parameter.isValid("NUM_RECORDS"));
		assertTrue(Parameter.isValid("EXPRESSION"));
	}

}
