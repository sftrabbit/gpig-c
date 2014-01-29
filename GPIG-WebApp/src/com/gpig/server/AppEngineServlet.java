package com.gpig.server;

import javax.servlet.http.HttpServlet;

public class AppEngineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String databaseURL;

	public AppEngineServlet(String dataBaseURL) {
		this.databaseURL = databaseURL;
	}
}
