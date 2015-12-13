package com.termproject.termproject.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.format.Formatter;
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
    private Context mContext = null;
    private ConnectivityManager cManager;
    private Socket socket;
    private DataOutputStream writeSocket;
    private DataInputStream readSocket;
    private Handler mHandler = new Handler();
    private NetworkInfo wifi;
    private ServerSocket serverSocket;

    private String ipAddress;
    private int port = 1010;

    public static ClientManager getInstance(){
        return instance;
    }
    public static ClientManager getFirstInstance(Context context){
         instance = new ClientManager(context);
        return instance;
    }

    private ClientManager(Context context){
        this.mContext = context;
        cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void setToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void connet(){
        (new Connect()).start();
    }

    public void disconnet(){
        (new Disconnect()).start();
    }

    public void setServer(){
        (new SetServer()).start();
    }

    public void closeServer(){
        (new CloseServer()).start();
    }

    @SuppressWarnings("deprecation")
    public void setInfo(){
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            WifiManager wManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wManager.getConnectionInfo();
            ipAddress = Formatter.formatIpAddress(info.getIpAddress());
        } else {
            setToast("Wifi에 연결이 되어 있지 않습니다");
        }
    }

    class Connect extends Thread {
        public void run() {
            Log.d("Connect", "Run Connect");
            try {
                socket = new Socket(ipAddress, port);
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast("연결에 성공하였습니다.");
                    }

                });
                (new recvSocket()).start();
            } catch (Exception e) {
                final String recvInput = "연결에 실패하였습니다.";
                Log.d("Connect", e.getMessage());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(recvInput);
                    }

                });

            }

        }
    }

    class Disconnect extends Thread {
        public void run() {
            try {
                if (socket != null) {
                    socket.close();
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setToast("연결이 종료되었습니다.");
                        }
                    });

                }

            } catch (Exception e) {
                final String recvInput = "연결에 실패하였습니다.";
                Log.d("Connect", e.getMessage());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(recvInput);
                    }

                });

            }

        }
    }

    class SetServer extends Thread {
        public void run() {
            try {
                 serverSocket = new ServerSocket(port);
                 final String result = "서버 포트 " + port + " 가 준비되었습니다.";
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                          setToast(result);
                    }
                });
                socket = serverSocket.accept();
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                while (true) {
                    byte[] b = new byte[100];
                    int ac = readSocket.read(b, 0, b.length);
                    String input = new String(b, 0, b.length);
                    final String recvInput = input.trim();
                    if(ac==-1)
                        break;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setToast(recvInput);
                        }
                    });
                }
                mHandler.post(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast("연결이 종료되었습니다.");
                    }
                });
                serverSocket.close();
                socket.close();
            } catch (Exception e) {
                final String recvInput = "서버 준비에 실패하였습니다.";
                Log.d("SetServer", e.getMessage());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setToast(recvInput);
                    }
                });
            }
        }
    }

    class recvSocket extends Thread {

        public void run() {
            try {
                readSocket = new DataInputStream(socket.getInputStream());

                while (true) {
                    byte[] b = new byte[100];
                    int ac = readSocket.read(b, 0, b.length);
                    String input = new String(b, 0, b.length);
                    final String recvInput = input.trim();

                    if(ac==-1)
                        break;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setToast(recvInput);
                        }

                    });
                }
                mHandler.post(new Runnable(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast("연결이 종료되었습니다.");
                    }

                });
            } catch (Exception e) {
                final String recvInput = "연결에 문제가 발생하여 종료되었습니다..";
                Log.d("SetServer", e.getMessage());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(recvInput);
                    }

                });

            }

        }
    }

    class CloseServer extends Thread {
        public void run() {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                    socket.close();

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setToast("서버가 종료되었습니다..");
                        }
                    });
                }
            } catch (Exception e) {
                final String recvInput = "서버 준비에 실패하였습니다.";
                Log.d("SetServer", e.getMessage());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(recvInput);
                    }

                });

            }

        }
    }

    class sendMessage extends Thread {
        public void run() {
            try {
                byte[] b = new byte[100];
                b = "Hello, World!".getBytes();
                writeSocket.write(b);

            } catch (Exception e) {
                final String recvInput = "메시지 전송에 실패하였습니다.";
                Log.d("SetServer", e.getMessage());
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setToast(recvInput);
                    }

                });

            }

        }
    }


}
