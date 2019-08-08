package com.pranavkapoorr.akka_network_scanner;

import akka.actor.*;

public class NetworkScannerActor extends AbstractActor {
	
	public static Props props() {
		return Props.create(NetworkScannerActor.class);
	}
	@Override
	public void preStart() throws Exception {
		context().actorOf(TcpWorker.props());
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(String.class, s->{
					System.out.println(s);
				})
				.build();
	}
}
