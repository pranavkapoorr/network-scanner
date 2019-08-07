package com.pranavkapoorr.akka_network_scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class App {
    public static void main( String[] args ) {
        ActorSystem system = ActorSystem.create("Network-Scanning-System");
        ActorRef networkScanner = system.actorOf(NetworkScannerActor.props(40001));
    }
}
