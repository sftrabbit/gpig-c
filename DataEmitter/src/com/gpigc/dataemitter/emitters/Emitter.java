package com.gpigc.dataemitter.emitters;

import java.util.concurrent.Callable;

public interface Emitter extends Callable<Void> {
	public Void call() throws Exception;
	public void stop();
}
