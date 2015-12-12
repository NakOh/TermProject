package com.termproject.termproject.manager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kk070 on 2015-12-12.
 */
public class ClientManager {
    public static ClientManager instance;
    private Socket socket;
    private PrintWriter socket_out;
    private BufferedReader socket_in;
    private String data;

    private ClientManager(){
        Thread client = new Thread() {
            public void run() {
                try {
                    socket = new Socket("222.104.147.163", 5555);
                    socket_out = new PrintWriter(socket.getOutputStream(), true);
                    socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    while (true) {
                        data = socket_in.readLine();
                        /*
                        output.post(new Runnable() {
                            public void run() {
                                output.setText(data);
                            }
                        });
                        */
                    }
                } catch (Exception e) {
                }
            }
        };
        client.start();
    }

    public static ClientManager getInstance(){
        if(instance == null){
            instance = new ClientManager();
        }
        return instance;
    }


}
