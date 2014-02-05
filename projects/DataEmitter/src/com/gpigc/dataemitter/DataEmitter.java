package com.gpigc.dataemitter;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import com.gpigc.dataemitter.emitters.*;
import com.gpigc.dataemitter.monitors.JavaVirtualMachineMonitor.AppNotRunningException;

public class DataEmitter implements Runnable {
	// _StubEmitter_ is replaced by the appropriate Emitter by the build system
	private static final Emitter emitter = new _StubEmitter_();

	public static void main(String[] args) throws InterruptedException,
			ExecutionException, TimeoutException {
		ExecutorService threadExecutor = null;
		try {
			threadExecutor = Executors.newSingleThreadExecutor();

			DataEmitter dataEmitter = new DataEmitter();
			Thread dataEmitterThread = new Thread(dataEmitter);
			dataEmitterThread.setDaemon(true);
			dataEmitterThread.start();

			Future<Void> futureResult = threadExecutor.submit(emitter);

			futureResult.get();

			emitter.stop();
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof ConnectException) {
				System.out.println("GPIG-C server is not running.");
			} else if (cause instanceof AppNotRunningException) {
				System.out.println("Application to monitor is not running.");
			} else if (cause instanceof SocketException) {
				System.out.println("GPIG-C server stopped running.");
			} else {
				throw e;
			}
		} finally {
			threadExecutor.shutdown();
		}
	}

	@Override
	public void run() {
		try {
			System.out.println("Press enter to stop the emitter.");
			System.in.read();
			System.out.println("Emitter has been stopped.");

			emitter.stop();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
