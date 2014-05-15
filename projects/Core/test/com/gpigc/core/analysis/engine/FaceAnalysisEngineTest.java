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
		Mat faceMat = FaceAnalysisEngine.parseFace(a + "," + b + "," + c);
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
		List<Mat> faces = FaceAnalysisEngine.parseFaces(a + "," + b + "," + c
				+ "\n" + d + "," + e + "," + f);
		assertEquals(a, faces.get(0).get(0, 0)[0], 0.000005d);
		assertEquals(b, faces.get(0).get(0, 1)[0], 0.000005d);
		assertEquals(c, faces.get(0).get(0, 2)[0], 0.000005d);
		assertEquals(d, faces.get(1).get(0, 0)[0], 0.000005d);
		assertEquals(e, faces.get(1).get(0, 1)[0], 0.000005d);
		assertEquals(f, faces.get(1).get(0, 2)[0], 0.000005d);
	}

	@Test
	public void testIsAuthorisedFaceAccept() {
		// TODO Set some reasonable test values
		Mat testFace = null;
		List<Mat> exampleFaces = null;
		double threshold = 0.0;
		boolean isAuthorised = FaceAnalysisEngine.isAuthorisedFace(testFace,
				exampleFaces, threshold);
		assertTrue("Face should have been authorised, but it was not",
				isAuthorised);
	}

	@Test
	public void testIsAuthorisedFaceDecline() {
		// TODO Set some reasonable test values
		Mat testFace = null;
		List<Mat> exampleFaces = null;
		double threshold = 0.0;
		boolean isAuthorised = FaceAnalysisEngine.isAuthorisedFace(testFace,
				exampleFaces, threshold);
		assertFalse("Face should not have been authorised, but it was",
				isAuthorised);
	}

}
