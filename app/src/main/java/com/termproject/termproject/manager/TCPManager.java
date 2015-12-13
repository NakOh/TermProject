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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kk070 on 2015-12-12.
 */
public class TCPManager {
    public static TCPManager instance;
    private GameManager gameManager;
    private Context mContext = null;
    private ConnectivityManager cManager;
    private Socket socket;
    private DataOutputStream writeSocket;
    private DataInputStream readSocket;
    private Handler mHandler = new Handler();
    private NetworkInfo wifi;
    private ServerSocket serverSocket;

    private String myIpAddress;
    private String serverIpAddress;
    private int port = 8050;

    public static TCPManager getInstance() {
        return instance;
    }

    public static TCPManager getFirstInstance(Context context) {
        instance = new TCPManager(context);
        return instance;
    }

    private TCPManager(Context context) {
        this.mContext = context;
        cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        gameManager = GameManager.getInstance();
    }

    private void setToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void connet() {
        (new Connect()).start();
    }

    public void disconnet() {
        (new Disconnect()).start();
    }

    public void setServer() {
        (new SetServer()).start();
    }

    public void closeServer() {
        (new CloseServer()).start();
    }

    public void sendMessage(String message){
        try {
            byte[] buffer = new byte[1000];
            buffer = message.getBytes();
            writeSocket.write(buffer);
        } catch (Exception e) {
            final String recvInput = "메시지 전송에 실패하였습니다.";
            Log.d("SetServer", e.getMessage());
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    setToast(recvInput);
                }

            });
        }
    }

    @SuppressWarnings("deprecation")
    public void setInfo(String serverIpAddress) {
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            if (serverIpAddress.length() == 0 || serverIpAddress.length() > 15 || serverIpAddress.equals("IP주소를 입력")) {
                WifiManager wManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wManager.getConnectionInfo();
                myIpAddress = Formatter.formatIpAddress(info.getIpAddress());
                this.serverIpAddress = myIpAddress;
                setToast("현재 나 자신이 서버 입니다");
                setServer();
            } else {
                this.serverIpAddress = serverIpAddress;
                setToast("현재 클라이언트입니다. 서버로 접근 시도 합니다");
                connet();
            }
        } else {
            setToast("Wifi에 연결이 되어 있지 않습니다");
        }
    }

    class Connect extends Thread {
        public void run() {
            Log.d("Connect", "Run Connect");
            try {
                socket = new Socket();
                //socket.setSoTimeout(5000);
                socket.connect(new InetSocketAddress(serverIpAddress, port));
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setToast("연결에 성공하였습니다.");
                    }

                });
                gameManager.setServer(false);
                (new recvSocket()).start();

            } catch (Exception e) {
                final String recvInput = "연결에 실패하였습니다. 서버를 만듭니다";
                Log.d("Connect", e.getMessage());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //연결에 실패한 경우 Server가 없는 것으로 판단하여 자신이 서버가 된다.
                        setToast(recvInput);
                        setInfo("");
                        setServer();
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
                final String result = "서버 IP" + serverIpAddress + "서버 포트 " + port + " 가 준비되었습니다.";
                Log.d("SetServer", "IPAddress" + serverIpAddress);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setToast(result);
                    }
                });
                gameManager.setServer(true);
                socket = serverSocket.accept();
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                while (true) {
                    byte[] b = new byte[1000];
                    int ac = readSocket.read(b, 0, b.length);
                    String input = new String(b, 0, b.length);
                    final String recvInput = input.trim();
                    if (ac == -1)
                        break;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setToast(recvInput);
                        }
                    });
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
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
                    if (ac == -1)
                        break;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setToast(recvInput);
                        }

                    });
                }
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        setToast("연결이 종료되었습니다.");
                    }

                });
            } catch (Exception e) {
                final String recvInput = "연결에 문제가 발생하여 종료되었습니다..";
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

    class CloseServer extends Thread {
        public void run() {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                    socket.close();
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
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
                        setToast(recvInput);
                    }

                });

            }

        }
    }



}
