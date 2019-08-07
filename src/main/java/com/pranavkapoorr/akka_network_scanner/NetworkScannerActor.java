package com.pranavkapoorr.akka_network_scanner;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import akka.actor.*;
import akka.io.Tcp.Connected;

public class NetworkScannerActor extends AbstractActor{
	private final int port2Scan;
	private String myIp;
	
	public static Props props(int port2Scan) {
		return Props.create(NetworkScannerActor.class, port2Scan);
	}
	public NetworkScannerActor(int port2Scan){
		this.port2Scan = port2Scan;
	}
	@Override
	public void preStart() throws Exception {
		resolveMyIp();
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Connected.class,c -> System.out.print(c))
				.match(String.class, s->{
					if(s.equals("scan")) {
						scanNetwork();
					}
				})
				.build();
	}
	
	private void scanNetwork() {
		System.out.println("scanning network");
		for(int i = 1; i< 255; i++) {
			for(int j = 1; j< 64000; j++) {
				System.out.println("scanning: "+ myIp.replaceAll("(.*\\.)\\d+$", "$1")+String.valueOf(i)+":"+j);
				context().actorOf(TcpWorker.props(
					new InetSocketAddress(myIp.replaceAll("(.*\\.)\\d+$", "$1")+String.valueOf(i),j),
					getSelf()));
			}
		}
	}
	private void resolveMyIp() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			myIp = inetAddress.getHostAddress();
			System.out.println("resolved Ip!");
			getSelf().tell("scan", getSelf());
		} catch (UnknownHostException e) {
			myIp = null;
			System.out.println("Failed to resolve Ip!");
		}
		
	}
	
}
