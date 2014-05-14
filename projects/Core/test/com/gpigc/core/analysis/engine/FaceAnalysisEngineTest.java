package com.gpigc.core.analysis.engine;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.opencv.core.Mat;

import com.gpigc.core.analysis.engine.FaceAnalysisEngine;

public class FaceAnalysisEngineTest {

	@Test
	public void testParseFace() {
		double a = 0d;
		double b = 0.8672d;
		double c = 0.2d;
		Mat faceMat = FaceAnalysisEngine.parseFace(a+","+b+","+c);
		assertEquals(a, faceMat.get(0, 0)[0], 0.000005d);
		assertEquals(b, faceMat.get(0, 1)[0], 0.000005d);
		assertEquals(c, faceMat.get(0, 2)[0], 0.000005d);
	}
	
	@Test
	public void testParseFaces() {
		double a = 0d;
		double b = 0.8672d;
		double c = 0.2d;
		double d = 0.7d;
		double e = 0.672d;
		double f = 0.001d;
		List<Mat> faces = 
				FaceAnalysisEngine.parseFaces(a+","+b+","+c+"\n"+d+","+e+","+f);
		assertEquals(a, faces.get(0).get(0, 0)[0], 0.000005d);
		assertEquals(b, faces.get(0).get(0, 1)[0], 0.000005d);
		assertEquals(c, faces.get(0).get(0, 2)[0], 0.000005d);
		assertEquals(d, faces.get(1).get(0, 0)[0], 0.000005d);
		assertEquals(e, faces.get(1).get(0, 1)[0], 0.000005d);
		assertEquals(f, faces.get(1).get(0, 2)[0], 0.000005d);
	}

	@Test
	public void testIsAuthorisedFace() {
		fail("Not yet implemented");
	}

}
