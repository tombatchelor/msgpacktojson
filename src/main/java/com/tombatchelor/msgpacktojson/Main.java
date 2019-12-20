/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tombatchelor.msgpacktojson;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tombatchelor
 */
public class Main {
    
    
    public static void main(String [] args) {
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(4541), 0);
            server.createContext("/v0.4/traces", new Handler());
            server.createContext("/v0.3/traces", new Handler());
            server.start();
            while(true) {
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
