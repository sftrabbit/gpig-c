package com.gpigc.dataemitter.emitters;

import java.util.List;

import com.gpigc.proto.Protos.SystemData;

public class _StubEmitter_ extends Emitter {

	public _StubEmitter_() {
		super(0);
		System.out.println("StubEmitter");
	}

	@Override
	public void setup() throws Exception {
	}

	@Override
	public List<SystemData> collectData() throws Exception {
		return null;
	}

}
