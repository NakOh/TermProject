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

import com.termproject.termproject.main.MainActivity;
import com.termproject.termproject.menu.MenuActivity;

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
    private String recvInput;
    private String myIpAddress;
    private String serverIpAddress;
    private int port = 8050;

    private Thread connect;
    private Thread recvSocket;
    private Thread checkMessage;

    private String map = "";



    public static TCPManager getInstance() {
        return instance;
    }

    public static TCPManager getFirstInstance(Context context) {
        instance = new TCPManager(context);
        return instance;
    }

    private TCPManager(Context context) {
        this.setmContext(context);
        cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        gameManager = GameManager.getInstance();
    }

    private void setToast(String msg) {
        Toast.makeText(getmContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void connect() {
        setConnect(new Connect());
        getConnect().start();
    }

    private void end() {
        ((MainActivity) MainActivity.mContext).dialogSimple();
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

    public void sendMessage(String message) {
        try {
            byte[] buffer = new byte[1000];
            buffer = message.getBytes();
            writeSocket.write(buffer);
        } catch (Exception e) {
            final String recvInput = "메시지 전송에 실패하였습니다.";
            Log.d("sendMessage", e.getMessage());
            getmHandler().post(new Runnable() {
                @Override
                public void run() {
                    setToast(recvInput);
                }
            });
        }
    }


    private void collectMine(int index) {
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                    continue;
                }
                if (gameManager.getTile()[i][j].isMine()) {
                    map += String.valueOf(i) + "," + String.valueOf(j) + ",";
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void setInfo(String serverIpAddress) {
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            if (serverIpAddress.length() == 0 || serverIpAddress.length() > 15 || serverIpAddress.equals("IP주소를 입력")) {
                WifiManager wManager = (WifiManager) getmContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wManager.getConnectionInfo();
                myIpAddress = Formatter.formatIpAddress(info.getIpAddress());
                this.serverIpAddress = myIpAddress;
                setToast("현재 나 자신이 서버 입니다");
                setServer();
            } else {
                this.serverIpAddress = serverIpAddress;
                setToast("현재 클라이언트입니다. 서버로 접근 시도 합니다");
                connect();
            }
        } else {
            setToast("Wifi에 연결이 되어 있지 않습니다");
        }
    }


    public Thread getConnect() {
        return connect;
    }

    public void setConnect(Thread connect) {
        this.connect = connect;
    }

    public Thread getRecvSocket() {
        return recvSocket;
    }

    public void setRecvSocket(Thread recvSocket) {
        this.recvSocket = recvSocket;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public Thread getCheckMessage() {
        return checkMessage;
    }

    public void setCheckMessage(Thread checkMessage) {
        this.checkMessage = checkMessage;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    class CheckMessage extends Thread {
        public void run() {
            String[] result = recvInput.split(",");
            switch (result[0]) {
                case "wantMap":
                    //client에서 Map 정보를 원한다면
                    collectMine(gameManager.getIndex());
                    sendMessage("giveMap," + map);
                    break;
                case "giveMap":
                    System.out.println(recvInput);
                    for (int i = 1; i < result.length; i = i + 2) {
                        gameManager.getTile()[Integer.valueOf(result[i])][Integer.valueOf(result[i + 1])].setIsMine(true);
                    }
                    gameManager.setWait(false);
                    break;
                case "touch":
                    for (int i = 1; i < result.length; i = i + 2) {
                        gameManager.checkUpdate(Integer.valueOf(result[i]), Integer.valueOf(result[i + 1]));
                    }
                    gameManager.setMyTurn(true);
                    break;
                case "noTouch":
                    for (int i = 1; i < result.length; i = i + 2) {
                        gameManager.checkUpdate(Integer.valueOf(result[i]), Integer.valueOf(result[i + 1]));
                    }
                    break;
                case "end":
                    end();
                    break;
                case "wantDifficulty":
                    sendMessage("giveDifficulty,"+gameManager.getDifficulty());
                    break;
                case "giveDifficulty":
                    gameManager.setDifficulty(Integer.valueOf(result[1]));
                    gameManager.setWait(false);
                    break;
                case "combo" :
                    gameManager.setOtherCombo(Integer.valueOf(result[1]));
                    gameManager.attack();
                    break;
                case "scoreChange" :
                    if(gameManager.getDefenseScoreNumber()>0) {
                        sendMessage("defenseScoreChange");
                        gameManager.setDefenseScoreNumber(gameManager.getDefenseScoreNumber() - 1);
                        //gameManager.deviceService.getDefense();
                    } else {
                        int tmp;
                        tmp = gameManager.getFindMine();
                        gameManager.setFindMine(gameManager.getFindOtherMine());
                        gameManager.setFindOtherMine(tmp);
                        //gameManager.deviceService.getAttack();
                    }
                    break;
                case "defenseScoreChange" :
                    //gameManager.deviceService.getDefense();
                    break;
                case "timeAttack" :
                    if(gameManager.getDefenseTimeNumber() > 0) {
                        sendMessage("defenseTimeAttack");
                        gameManager.setDefenseTimeNumber(gameManager.getDefenseTimeNumber() - 1);
                        //gameManager.deviceService.getDefense();
                    } else {
                        gameManager.setTimeAttackActivated(true);
                        //gameManager.deviceService.getAttack();
                    }
                    break;
                case "defenseTimeAttack" :
                    //gameManager.deviceService.getDefense();
                    break;
                default:
                    Log.d("checkMessage", result[0]);
                    break;
            }
        }
    }

    class Connect extends Thread {
        public void run() {
            Log.d("Connect", "Run Connect");
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIpAddress, port));
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                getmHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        setToast("연결에 성공하였습니다.");
                    }
                });
                gameManager.setServer(false);
                gameManager.setMyTurn(false);
                recvSocket = new recvSocket();
            } catch (Exception e) {
                final String recvInput = "연결에 실패하였습니다. 서버를 만듭니다";
                Log.d("Connect", e.getMessage());
                getmHandler().post(new Runnable() {
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

    class recvSocket extends Thread {
        public void run() {
            try {
                readSocket = new DataInputStream(socket.getInputStream());
                while (true) {
                    byte[] b = new byte[100];
                    int ac = readSocket.read(b, 0, b.length);
                    String input = new String(b, 0, b.length);
                    recvInput = input.trim();
                    setCheckMessage(new CheckMessage());
                    checkMessage.start();
                    checkMessage.join();
                    if (ac == -1) {
                        break;
                    }
                    getmHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            setToast(recvInput);
                        }
                    });
                }
            } catch (Exception e) {
                final String recvInput = "연결에 문제가 발생하여 종료되었습니다..";
                Log.d("SetServer", e.getMessage());
                getmHandler().post(new Runnable() {
                    @Override
                    public void run() {
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
                    getmHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            setToast("연결이 종료되었습니다.");
                        }
                    });
                }
            } catch (Exception e) {
                final String recvInput = "연결에 실패하였습니다.";
                Log.d("Connect", e.getMessage());
                getmHandler().post(new Runnable() {
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
                getmHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        setToast(result);
                    }
                });
                gameManager.setServer(true);
                gameManager.setMyTurn(true);
                gameManager.setFirst(true);
                socket = serverSocket.accept();
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                //서버가 만들어지면 맵을 만들고 일단 상대방이 접속할 때 까지 기다린다.
                while (true) {
                    byte[] b = new byte[1000];
                    int ac = readSocket.read(b, 0, b.length);
                    String input = new String(b, 0, b.length);
                    recvInput = input.trim();
                    if (ac == -1)
                        break;
                    checkMessage = new CheckMessage();
                    checkMessage.start();
                    checkMessage.join();
                    gameManager.setFirst(false);
                    getmHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            setToast(recvInput);
                        }
                    });
                }
                getmHandler().post(new Runnable() {
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
                getmHandler().post(new Runnable() {
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
                    getmHandler().post(new Runnable() {

                        @Override
                        public void run() {
                            setToast("서버가 종료되었습니다..");
                        }
                    });
                }
            } catch (Exception e) {
                final String recvInput = "서버 준비에 실패하였습니다.";
                Log.d("SetServer", e.getMessage());
                getmHandler().post(new Runnable() {

                    @Override
                    public void run() {
                        setToast(recvInput);
                    }

                });

            }

        }
    }


}
