package com.termproject.termproject.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.termproject.termproject.R;
import com.termproject.termproject.manager.TCPManager;
import com.termproject.termproject.manager.GameManager;
import com.termproject.termproject.model.Tile;

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
    private int queueCounter = 0;
    public int foundMine = 0;
    private Vibrator mVibrator;
    private Bitmap change;
    private Bitmap anti_change;
    private Bitmap time;
    private Bitmap anti_time;
    private Bitmap glass;
    private Bitmap onemore;
    private int queueSearcher = -1;
    private String firstLine, secondLine;
    private int retTextLCD;
    private int segData = 0;
    private DeviceService deviceService;
    protected static final int MY_TURN = 100;

    public MainView(Context context) {
        super(context);
        this.mContext = context;

        change = BitmapFactory.decodeResource(context.getResources(), R.drawable.change);
        anti_change = BitmapFactory.decodeResource(context.getResources(), R.drawable.anti_change);
        time = BitmapFactory.decodeResource(context.getResources(), R.drawable.time);
        anti_time = BitmapFactory.decodeResource(context.getResources(), R.drawable.anti_time);
        glass = BitmapFactory.decodeResource(context.getResources(), R.drawable.glass);
        onemore = BitmapFactory.decodeResource(context.getResources(), R.drawable.onemore);

        myThread = Thread.currentThread();
        gameManager = GameManager.getInstance();
        tcpManager = TCPManager.getInstance();
        gameManager.makeVibrator(context);
        mVibrator = gameManager.getVibrator();
        gameManager.setMyThread(myThread);

        /*
        deviceService = new DeviceService();
        deviceService.IOCtlClear();
        deviceService.IOCtlReturnHome();
        deviceService.IOCtlDisplay(true);
        deviceService.IOCtlCursor(false);
        deviceService.IOCtlBlink(false);
        deviceService.SegmentIOControl(0);
*/

        //시작할 때 데이터 초기화
        gameManager.resetScore();

        if (!gameManager.isServer() && gameManager.isMulti()) {
            //클라면 서버의 난이도를 받아온다.
            gameManager.sendMessage("wantDifficulty");
        }

        if (gameManager.getDifficulty() == 0) {
            gameManager.setIndex(easy);
        } else if (gameManager.getDifficulty() == 1) {
            gameManager.setIndex(normal);
        } else {
            gameManager.setIndex(hard);
        }
        //0 쉬움(5*5), 1 중간(7*7), 2 어려움(10*10)
        makeTile(gameManager.getIndex());

        if (gameManager.isServer() || !gameManager.isMulti()) {
            setMine(gameManager.getIndex());
            setNumber(gameManager.getIndex());
            setTileImage(gameManager.getIndex());

        } else {
            setTileAgain(gameManager.getIndex());
        }

        this.setFocusableInTouchMode(true);

        /*
        if (!gameManager.isMulti()) {
            firstLine = "single Play";
        } else if (gameManager.isMulti() && gameManager.isServer()) {
            firstLine = "I'm Server";
        } else if (gameManager.isMulti() && !gameManager.isServer()) {
            firstLine = "I'm Client";
        }

        secondLine = gameManager.getDifficulty() + "/" + gameManager.getTotalMine() + "/" + (gameManager.getFindMine() + gameManager.getFindOtherMine());
        retTextLCD = deviceService.TextLCDOut(firstLine, secondLine);
        deviceService.DotMatrixControl("" + gameManager.getMyCombo());
        segData = gameManager.getTotalMine() * 10000;
        deviceService.SegmentControl(segData);
        deviceService.LEDControl();*/
    }


    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        updateTile(gameManager.getIndex(), canvas);
        canvas.drawBitmap(change, 0, 3 * h / 4, null);
        canvas.drawBitmap(anti_change, w / 6, 3 * h / 4, null);
        canvas.drawBitmap(time, (2 * w / 6), 3 * h / 4, null);
        canvas.drawBitmap(anti_time, (3 * w / 6), 3 * h / 4, null);
        canvas.drawBitmap(glass, (4 * w / 6), 3 * h / 4, null);
        canvas.drawBitmap(onemore, (5 * w / 6), 3 * h / 4, null);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        paint.setTextSize(30);

        canvas.drawText(String.valueOf(gameManager.getScoreChangeNumber()), 0 + w / 12, h - 0, paint);
        canvas.drawText(String.valueOf(gameManager.getDefenseScoreNumber()), w / 6 + w / 12, h - 0, paint);
        canvas.drawText(String.valueOf(gameManager.getTimeAttackNumber()), (2 * w / 6) + w / 12, h - 0, paint);
        canvas.drawText(String.valueOf(gameManager.getDefenseTimeNumber()), (3 * w / 6) + w / 12, h - 0, paint);
        canvas.drawText(String.valueOf(gameManager.getPreviewNumber()), (4 * w / 6) + w / 12, h - 0, paint);
        canvas.drawText(String.valueOf(gameManager.getOnceMoreNumber()), (5 * w / 6) + w / 12, h - 0, paint);

        queueTile = new int[20][3];
        gameManager.setQueueTile(queueTile);
        invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        this.w = w;
        this.h = h;
        setTileSize(gameManager.getIndex());
        change = Bitmap.createScaledBitmap(change, w / 6, h / 4 - 60, true);
        anti_change = Bitmap.createScaledBitmap(anti_change, w / 6, h / 4 - 60, true);
        time = Bitmap.createScaledBitmap(time, w / 6, h / 4 - 60, true);
        anti_time = Bitmap.createScaledBitmap(anti_time, w / 6, h / 4 - 60, true);
        glass = Bitmap.createScaledBitmap(glass, w / 6, h / 4 - 60, true);
        onemore = Bitmap.createScaledBitmap(onemore, w / 6, h / 4 - 60, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameManager.isMyTurn() && !gameManager.isFirst()) {
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
        //서버에서 받아오기 위해 Tile을 넘김
        gameManager.setTile(tile);
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
                } else if (randomRange(1, 6) == 1) { // 1/6의 확률로 아이템을 배치
                    int randomNum = randomRange(1, 6); // 6종류의 아이템 중 랜덤하게 골라진 아이템이 이미 2개 이상 배치되었다면 패스
                    if (gameManager.getItemMadeCounter(randomNum) < 2) {
                        tile[i][j].setIndex(randomNum);
                        gameManager.setItemMadeCounter(randomNum);
                        tile[i][j].setIsItem(true);
                    }
                }

            }
        }
    }

    private void checkEnd() {
        if (gameManager.getTotalMine() == gameManager.getFindMine() + gameManager.getFindOtherMine()) {
            gameManager.setEnd(true);
            if (gameManager.isMulti()) {
                tcpManager.sendMessage("end");
            }
            /*
            if (gameManager.getFindMine() > gameManager.getFindOtherMine())
                deviceService.DotMatrixControl("WIN");
            else deviceService.DotMatrixControl("LOSE");
            */
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
                if (tile[i][j].isItem()) tile[i][j].setNumber(10 + tile[i][j].getIndex());
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
                tile[i][j].setSize(w / (index - mask), (3 * h / 4) / (index - mask));
            }
        }
    }

    private void setTileAgain(int index) {
        //클라이언트일 경우
        //서버로 맵 정보를 요구하자
        gameManager.sendMessage("wantMap");
        setNumber(index);
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
                tile[i][j].update(canvas, w / (index - mask) * i - w / (index - mask), (3 * h / 4) / (index - mask) * j - (3 * h / 4) / (index - mask));
            }
        }
    }

    private void checkTouch(int index, float currentX, float currentY) {
        gameManager.setQueueCounter(0);
        gameManager.setQueueSearcher(-1);
        int changeLeft = 0;
        int changeRight = 0 + change.getWidth();
        int anti_changeRight = changeRight + anti_change.getWidth();
        int timeRight = anti_changeRight + time.getWidth();
        int anti_timeRight = timeRight + anti_time.getWidth();
        int glassRight = anti_timeRight + glass.getWidth();
        int onemoreRight = glassRight + onemore.getWidth();

        if (changeRight > currentX && changeLeft < currentX && 3 * h / 4 < currentY && 3 * h / 4 + change.getHeight() > currentY) {
            if (gameManager.getScoreChangeNumber() > 0) {
                gameManager.scoreChange();
                gameManager.setScoreChangeNumber(gameManager.getScoreChangeNumber() - 1);
            }
        } else if (anti_changeRight > currentX && changeRight < currentX && 3 * h / 4 < currentY && 3 * h / 4 + anti_change.getHeight() > currentY) {
            if (gameManager.getDefenseScoreNumber() > 0) {
                gameManager.defenseScore(); //눌러서 작동하는건 아니다;
                gameManager.setDefenseScoreNumber(gameManager.getDefenseScoreNumber() - 1);
            }
        } else if (timeRight > currentX && anti_changeRight < currentX && 3 * h / 4 < currentY && 3 * h / 4 + time.getHeight() > currentY) {
            if (gameManager.getTimeAttackNumber() > 0) {
                gameManager.timeAttack();
                gameManager.setTimeAttackNumber(gameManager.getTimeAttackNumber() - 1);
            }
        } else if (anti_timeRight > currentX && timeRight < currentX && 3 * h / 4 < currentY && 3 * h / 4 + anti_time.getHeight() > currentY) {
            if (gameManager.getDefenseTimeNumber() > 0) {
                gameManager.defenseTime(); //눌러서 작동하는건 아니다;
                gameManager.setDefenseTimeNumber(gameManager.getDefenseTimeNumber() - 1);
            }
        } else if (glassRight > currentX && anti_timeRight < currentX && 3 * h / 4 < currentY && 3 * h / 4 + glass.getHeight() > currentY) {
            if (gameManager.getPreviewNumber() > 0) {
                //gameManager.preview();
                gameManager.setPreviewActivated(true);
                gameManager.setPreviewNumber(gameManager.getPreviewNumber() - 1);
            }
        } else if (onemoreRight > currentX && glassRight < currentX && 3 * h / 4 < currentY && 3 * h / 4 + onemore.getHeight() > currentY) {
            if (gameManager.getOnceMoreNumber() > 0) {
                gameManager.onceMore();
                gameManager.setOnceMoreActivated(true);
                gameManager.setOnceMoreNumber(gameManager.getOnceMoreNumber() - 1);
            }
        } else {
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
                        if (gameManager.getPreviewActivated()) {
                            gameManager.preview(i, j);
                            gameManager.setPreviewActivated(false);
                        } else if (!tile[i][j].getIsClicked()) {
                            updateTouch(i, j, index);
                        }
                    }
                }
            }
        }
    }

    private void updateTouch(int i, int j, int index) {
        //나 자신이 눌렀을 때 (즉 내가 마인을 찾은거)
        tile[i][j].setIsShow(true);
        tile[i][j].setIsClicked();

        if (tile[i][j].isMine()) {
            if (gameManager.isMulti()) {
                tcpManager.sendMessage("noTouch," + i + "," + j);
            }

            if(gameManager.isYesCombo()) {
                gameManager.setMyCombo(gameManager.getMyCombo() + 1);
                mVibrator.vibrate(20 * gameManager.getMyCombo());
            }else{
                gameManager.setMyCombo(0);
            }

            if (gameManager.isMulti()) {
                tcpManager.sendMessage("combo" + gameManager.getMyCombo());
            }
            gameManager.setFindMine(gameManager.getFindMine() + 1);
            segData = gameManager.getLeftMine() * 10000;
        /*    if (gameManager.getFindMine() > gameManager.getFindOtherMine()) segData += 100;
            else segData += 200;
            segData += gameManager.getFindMine();
            deviceService.SegmentControl(segData);*/
        } else if (tile[i][j].isItem()) {
            if (tile[i][j].getIndex() == 1) {
                gameManager.setDefenseScoreNumber(gameManager.getDefenseScoreNumber() + 1);
            } else if (tile[i][j].getIndex() == 2) {
                gameManager.setDefenseTimeNumber(gameManager.getDefenseTimeNumber() + 1);
            } else if (tile[i][j].getIndex() == 3) {
                gameManager.setOnceMoreNumber(gameManager.getOnceMoreNumber() + 1);
            } else if (tile[i][j].getIndex() == 4) {
                gameManager.setPreviewNumber(gameManager.getPreviewNumber() + 1);
            } else if (tile[i][j].getIndex() == 5) {
                gameManager.setScoreChangeNumber(gameManager.getScoreChangeNumber() + 1);
            } else if (tile[i][j].getIndex() == 6) {
                gameManager.setTimeAttackNumber(gameManager.getTimeAttackNumber() + 1);
            }

            gameManager.setMyCombo(0);

            if (gameManager.isMulti()) {
                tcpManager.sendMessage("touch," + i + "," + j);
            }

            gameManager.setMyTurn(false);

        } else if (tile[i][j].getNumber() == 0) {
            if (gameManager.isMulti()) {
                tcpManager.sendMessage("touch," + i + "," + j);
            }
            gameManager.getQueueTile()[queueCounter][1] = i;
            gameManager.getQueueTile()[queueCounter][2] = j;
            gameManager.checkSide(index);
            gameManager.setMyCombo(0);
            if (gameManager.isMulti()) {
                tcpManager.sendMessage("combo" + gameManager.getMyCombo());
            }
            if (!gameManager.getOnceMoreActivated()) { // onceMore 쓰고있으면 그냥 진행
                gameManager.setMyTurn(false);
            }
        } else {
            if (gameManager.isMulti()) {
                tcpManager.sendMessage("touch," + i + "," + j);
            }
            gameManager.setMyCombo(0);
            if (gameManager.isMulti()) {
                tcpManager.sendMessage("combo" + gameManager.getMyCombo());
            }
            if (!gameManager.getOnceMoreActivated()) { // onceMore 쓰고있으면 그냥 진행
                gameManager.setMyTurn(false);
            }
        }
        /*
        //TextLCD 정보 업데이트
        secondLine = gameManager.getDifficulty() + "/" + gameManager.getTotalMine() + "/" + (gameManager.getFindMine() + gameManager.getFindOtherMine());
        retTextLCD = deviceService.TextLCDOut(firstLine, secondLine);
        //DotMatrix 정보 업데이트
        deviceService.DotMatrixControl("" + gameManager.getMyCombo());
        */
    }


    private int randomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }


}
