package com.pranavkapoorr.akka_network_scanner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import akka.actor.*;
import akka.io.*;
import akka.io.Tcp.*;
import akka.util.ByteString;
import scala.concurrent.duration.Duration;

public class TcpWorker extends AbstractActor {
      private String myIp;
      
	  public static Props props() {
	    return Props.create(TcpWorker.class);
	  }
	  
	  @Override
	public void preStart() throws Exception {
		resolveMyIp();
	}
	  
	/*
	 * @Override public void preStart() throws Exception {
	 * context().system().scheduler().scheduleOnce(Duration.create(1,
	 * TimeUnit.SECONDS), new Runnable() {
	 * 
	 * @Override public void run() { getSelf().tell(PoisonPill.getInstance(),
	 * ActorRef.noSender()); } }, context().system().dispatcher()); }
	 */

	  @Override
	  public Receive createReceive() {
	    return receiveBuilder()
	        .match(String.class, s -> {
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
					Socket mySocket = new Socket();
					try {
						mySocket.connect(new InetSocketAddress(myIp.replaceAll("(.*\\.)\\d+$", "$1")+String.valueOf(i),j),200);
						mySocket.close();
						System.out.println(mySocket.getInetAddress());
					} catch (IOException e) {
					}
						
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