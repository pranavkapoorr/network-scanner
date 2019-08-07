package com.pranavkapoorr.akka_network_scanner;

import java.net.InetSocketAddress;

import akka.actor.*;
import akka.io.*;
import akka.io.Tcp.*;
import akka.util.ByteString;

public class TcpWorker extends AbstractActor{
	final InetSocketAddress remote;
	  final ActorRef listener;

	  public static Props props(InetSocketAddress remote, ActorRef listener) {
	    return Props.create(TcpWorker.class, remote, listener);
	  }

	  public TcpWorker(InetSocketAddress remote, ActorRef listener) {
	    this.remote = remote;
	    this.listener = listener;

	    final ActorRef tcp = Tcp.get(getContext().getSystem()).manager();
	    tcp.tell(TcpMessage.connect(remote), getSelf());
	  }

	  @Override
	  public Receive createReceive() {
	    return receiveBuilder()
	        .match(
	            CommandFailed.class,
	            msg -> {
	              listener.tell("failed", getSelf());
	              getContext().stop(getSelf());
	            })
	        .match(
	            Connected.class,
	            msg -> {
	              listener.tell(msg, getSelf());
	              getSender().tell(TcpMessage.register(getSelf()), getSelf());
	              getContext().become(connected(getSender()));
	            })
	        .build();
	  }

	  private Receive connected(final ActorRef connection) {
	    return receiveBuilder()
	        .match(
	            ByteString.class,
	            msg -> {
	              connection.tell(TcpMessage.write((ByteString) msg), getSelf());
	            })
	        .match(
	            CommandFailed.class,
	            msg -> {
	              // OS kernel socket buffer was full
	            })
	        .match(
	            Received.class,
	            msg -> {
	              listener.tell(msg.data(), getSelf());
	            })
	        .matchEquals(
	            "close",
	            msg -> {
	              connection.tell(TcpMessage.close(), getSelf());
	            })
	        .match(
	            ConnectionClosed.class,
	            msg -> {
	              getContext().stop(getSelf());
	            })
	        .build();
	  }
	}