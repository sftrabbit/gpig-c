package com.gpigc.core.analysis.engine;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.opencv.core.Mat;

import com.gpigc.core.analysis.engine.FaceAnalysisEngine;
import com.gpigc.core.analysis.engine.FaceAnalysisEngine.FaceClass;

public class FaceAnalysisEngineTest {

	@Test
	public void testParseFace() throws ParseException {
		double a = 0d;
		double b = 0.8672d;
		double c = 0.2d;
		Mat faceMat = FaceAnalysisEngine.parseFace(a + "," + b + "," + c);
		assertEquals(a, faceMat.get(0, 0)[0], 0.000005d);
		assertEquals(b, faceMat.get(0, 1)[0], 0.000005d);
		assertEquals(c, faceMat.get(0, 2)[0], 0.000005d);
	}

	@Test
	public void testIsAuthorisedFaceAccept() {
		// TODO Set some reasonable test values
		Mat testFace = null;
		List<FaceAnalysisEngine.FaceExample> exampleFaces = null;
		double threshold = 0.0;
		FaceAnalysisEngine.FaceClass faceClass = FaceAnalysisEngine.getMostLikelyClass(testFace,
				exampleFaces, threshold);
		assertTrue("Face should have been authorised, but it was not",
				faceClass == FaceClass.ROSY);
	}

	@Test
	public void testIsAuthorisedFaceDecline() {
		// TODO Set some reasonable test values
		Mat testFace = null;
		List<FaceAnalysisEngine.FaceExample> exampleFaces = null;
		double threshold = 0.0;
		FaceAnalysisEngine.FaceClass faceClass = FaceAnalysisEngine.getMostLikelyClass(testFace,
				exampleFaces, threshold);
		assertFalse("Face should not have been authorised, but it was",
				faceClass != FaceClass.ROSY);
	}

}
