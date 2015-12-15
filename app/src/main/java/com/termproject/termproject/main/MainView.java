package com.termproject.termproject.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.termproject.termproject.manager.TCPManager;
import com.termproject.termproject.manager.GameManager;
import com.termproject.termproject.model.Tile;
import com.termproject.termproject.main.DeviceService;

/**
 * Created by kk070 on 2015-12-06.
 */


public class MainView extends View {
    private Context mContext = null;
    private Tile[][] tile;
    private int queueTile[][];
    private int w, h;
    //0 쉬움(5*5), 1 중간(7*7), 2 어려움(10*10)
    //로직 상 +2 한 값을 입력해야한다.
    private final static int mask = 2;
    private final static int easy = 5 + mask;
    private final static int normal = 7 + mask;
    private final static int hard = 10 + mask;
    private Canvas canvas;
    private GameManager gameManager;
    private TCPManager tcpManager;
    private Thread myThread;
    private int counter = 0;
    private int difficulty = 0;
    private int queueCounter = 0;
    private int queueSearcher = -1;

    public int myCombo = 0;
    public int countDown = 0;
    public int flag = 100;

    public int totalMine = 0;
    public int foundMine = 0;

    Vibrator mVibrator;
    protected static final int MY_TURN = 100;

    public MainView(Context context) {
        super(context);
        this.mContext = context;
        myThread = Thread.currentThread();
        gameManager = GameManager.getInstance();
        tcpManager = TCPManager.getInstance();
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.difficulty = gameManager.getDifficulty();
        gameManager.setMyThread(myThread);
        //0 쉬움(5*5), 1 중간(7*7), 2 어려움(10*10)
        if (difficulty == 0) {
            gameManager.setIndex(easy);
        } else if (difficulty == 1) {
            gameManager.setIndex(normal);
        } else if (difficulty == 2) {
            gameManager.setIndex(hard);
        } else {
            Log.d("MainView", "Error No Difficulty");
        }

        makeTile(gameManager.getIndex());

        //서버에서 받아오기 위해 Tile을 넘김
        gameManager.setTile(tile);

        if (gameManager.isServer() || !gameManager.isMulti()) {
            setMine(gameManager.getIndex());
            setNumber(gameManager.getIndex());
            setTileImage(gameManager.getIndex());
        } else {
            setTileAgain(gameManager.getIndex());
        }
        this.setFocusableInTouchMode(true);
        totalMine = gameManager.getTotalMine();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        if (difficulty == 0) {
            updateTile(easy, canvas);
        } else if (difficulty == 1) {
            updateTile(normal, canvas);
        } else if (difficulty == 2) {
            updateTile(hard, canvas);
        } else {
            //난이도 설정이 이상하게 된 경우
            Log.d("MainView", "Error");
        }
        queueTile = new int[20][3];
        gameManager.setQueueTile(queueTile);
        invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        this.w = w;
        this.h = h;
        if (difficulty == 0) {
            setTileSize(easy);
        } else if (difficulty == 1) {
            setTileSize(normal);
        } else if (difficulty == 2) {
            setTileSize(hard);
        } else {
            //난이도 설정이 이상하게 된 경우
            Log.d("MainView", "Error");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameManager.isMyTurn() && gameManager.isFirst() == false) {
            float currentX = event.getX();
            float currentY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_DOWN:
                    checkTouch(gameManager.getIndex(), currentX, currentY);
                    checkEnd();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        } else if (!gameManager.isMulti()) {
            float currentX = event.getX();
            float currentY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_DOWN:
                    checkTouch(gameManager.getIndex(), currentX, currentY);
                    checkEnd();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return true;
    }

    private void makeTile(int index) {
        tile = new Tile[index][index];
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                tile[i][j] = new Tile(mContext);
            }
        }
    }

    private void setMine(int index) {
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                //마스크는 패스(왼쪽 끝과, 오른쪽 끝은 빈 곳이다. 즉 지뢰가 설치될 수 있는 공간이 아님)
                if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                    continue;
                }
                //25%의 확률로 마인을 배치한다.
                if (randomRange(1, 4) == 1) {
                    tile[i][j].setIsMine(true);
                    gameManager.setTotalMine(gameManager.getTotalMine() + 1);
                }
            }
        }
    }

    private void checkEnd() {
        System.out.println(gameManager.getTotalMine() + "" + gameManager.getFindMine() + "" + gameManager.getFindOtherMine());
        if (gameManager.getTotalMine() == gameManager.getFindMine() + gameManager.getFindOtherMine()) {
            gameManager.setEnd(true);
            if (gameManager.isMulti()) {
                tcpManager.sendMessage("end");
            }
            Log.d("GameView", "GameEnd");
            ((MainActivity) mContext).dialogSimple();
        }
    }

    private void setNumber(int index) {
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                    continue;
                }
                counter = 0;
                for (int l = -1; l < 2; l++) {
                    for (int m = -1; m < 2; m++) {
                        if (tile[i + l][j + m].isMine()) {
                            //현재 tile의 주변의 Mine 갯수를 샘.
                            counter++;
                        }
                    }
                }
                //주변의 Mine 갯수를 새서 입력한다.
                tile[i][j].setNumber(counter);
            }
        }
    }

    private void setTileImage(int index) {
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                //맨끝과 오른쪽 끝은 Image를 셋팅할 필요가 없다.
                if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                    continue;
                }
                //TIle 이미지를 셋팅한다.(숫자 0~8, Mine)
                tile[i][j].setImage(mContext);
            }
        }
    }

    private void setTileSize(int index) {
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                    continue;
                }
                //이미지 타일 이미지 크기를 결정한다.
                tile[i][j].setSize(w / (index - mask), h / (index - mask));
            }
        }
    }

    private void setTileAgain(int index) {
        //클라이언트일 경우
        //서버로 맵 정보를 요구하자
        tcpManager.sendMessage("wantMap");
        while (true) {
            if (!gameManager.isWait())
                break;
        }
        //Mine설치 후 숫자 셋팅
        setNumber(index);
        //숫자 셋팅 및 Mine 셋팅 후 Tile마다 이미지 설정
        setTileImage(index);
        setMineNumber(index);
    }

    private void setMineNumber(int index) {
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (tile[i][j].isMine()) {
                    gameManager.setTotalMine(gameManager.getTotalMine() + 1);
                }
            }
        }
    }

    private void updateTile(int index, Canvas canvas) {
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                    continue;
                }
                //이미지 위치를 지정한다. 테스트 해본 뒤 조정할 예정
                tile[i][j].update(canvas, w / (index - mask) * i - w / (index - mask), h / (index - mask) * j - h / (index - mask));
            }
        }
    }

    private void checkTouch(int index, float currentX, float currentY) {
        gameManager.setQueueCounter(0);
        gameManager.setQueueSearcher(-1);
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                    continue;
                }
                //tile[i][j]의 안에 클릭했을 때 작동
                if (tile[i][j].getX() + tile[i][j].getW() > currentX && tile[i][j].getX() < currentX && tile[i][j].getY() < currentY && tile[i][j].getY() + tile[i][j].getH() > currentY) {
                    //tile이 원래는 보이지 않기 때문에 보이도록 수정한다. 그리고 그것이 마인일 경우 마인 찾은 갯수를 증가!
                    //기존 지뢰찾기 처럼 0인 경우에는 주변의 타일이 전부 Show 되어야 한다.(하지 말자) (하지 말자 뭐냐)
                    //여기에 로직 추가하면 됩니다.
                    if (gameManager.isMulti()) {
                        tcpManager.sendMessage("touch," + i + "," + j);
                    }
                    updateTouch(i, j, index);
                    gameManager.setMyTurn(false);
                    break;
                }
            }
        }
    }

    private void updateTouch(int i, int j, int index) {
        //나 자신이 눌렀을 때 (즉 내가 마인을 찾은거)
        tile[i][j].setIsShow(true);
        if (tile[i][j].isMine()) {
            mVibrator.vibrate(10);
            //   mVibrator.vibrate(10); // 몇 콤보인지 확인하여 그에 따라 진동이 세지게 설정해야함
            gameManager.setFindMine(gameManager.getFindMine() + 1);
            foundMine = gameManager.getFindMine();
        } else if (tile[i][j].getNumber() == 0) {
            gameManager.getQueueTile()[queueCounter][1] = i;
            gameManager.getQueueTile()[queueCounter][2] = j;
            gameManager.checkSide(index);
        }
    }


    private int randomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }


}
