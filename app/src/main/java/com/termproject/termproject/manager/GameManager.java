package com.termproject.termproject.manager;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.os.Vibrator;

import com.termproject.termproject.main.DeviceService;
import com.termproject.termproject.model.Item;
import com.termproject.termproject.model.Tile;

/**
 * Created by kk070 on 2015-12-06.
 */
public class GameManager {
    private Item item;
    private static GameManager instance;
    private TCPManager tcpManager;
    private int difficulty;
    private int index;
    private int findMine;
    private int findOtherMine;
    private int totalMine = 0;
    private int myCombo;
    private int otherCombo;
    private boolean end = false;
    private boolean multi;
    private boolean server;
    private boolean myTurn ;
    private boolean first;
    private boolean wait;
    private Thread myThread;
    private Tile[][] tile;
    private int[][] queueTile;
    private int queueCounter = 0;
    private int queueSearcher = -1;

    private int defenseScoreNumber;
    private int defenseTimeNumber;
    private int onceMoreNumber;
    private int previewNumber;
    private int scoreChangeNumber;
    private int timeAttackNumber;

    private Vibrator mVibrator;
    private DeviceService deviceService;

    public static GameManager getInstance(){
        if(instance == null){
            instance  = new GameManager();
        }
        return instance;
    }

    public void attack() {
        //3번 연속으로 찾으면
        if (getOtherCombo() >= 3) {
        }
    }

    public void makeVibrator(Context context) {
        this.mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public Vibrator getVibrator(){
        return this.mVibrator;
    }

    public void checkUpdate(int i, int j){
        tile[i][j].setIsShow(true);
        if (tile[i][j].isMine()) {
            //   mVibrator.vibrate(10); // 몇 콤보인지 확인하여 그에 따라 진동이 세지게 설정해야함
            setOtherCombo(getOtherCombo() + 1);
            mVibrator.vibrate(10 * getOtherCombo());
           setFindOtherMine(getFindOtherMine() + 1);

            int segData = getLeftMine() * 10000;
            if(getFindMine() > getFindOtherMine()) segData += 100;
            else segData += 200;
            segData += getFindMine();
            deviceService.SegmentControl(segData);
        } else if (tile[i][j].getNumber() == 0) {
            if(getQueueCounter() > 20){
                setQueueCounter(0);
                setQueueSearcher(-1);
            } else {
                setQueueSearcher(getQueueCounter());
                setQueueCounter(getQueueCounter()+1);
            }
            getQueueTile()[getQueueCounter()][1] = i;
            getQueueTile()[getQueueCounter()][2] = j;
            checkSide(getIndex());
        }
    }

    public void checkSide(int index) { //클릭한 타일이 0일 때 상하좌우를 확인해서 오픈
        int i, j;
        while (getQueueCounter() != getQueueSearcher()) {
            setQueueSearcher(getQueueSearcher() + 1);
            if(getQueueSearcher() > 20) setQueueSearcher(0);
            i = getQueueTile()[getQueueSearcher()][1];
            j = getQueueTile()[getQueueSearcher()][2];
            if (i + 1 < index - 1 && !(tile[i + 1][j].isShow()) && !(tile[i + 1][j].isMine())) {
                tile[i + 1][j].setIsShow(true);
                if (tile[i + 1][j].getNumber() == 0) {
                    setQueueCounter(getQueueCounter() + 1);
                    if(getQueueCounter() > 20) setQueueCounter(0);
                    getQueueTile()[getQueueCounter()][1] = i + 1;
                    getQueueTile()[getQueueCounter()][2] = j;
                }
            }
            if (j + 1 < index - 1 && !(tile[i][j + 1].isShow()) && !(tile[i][j + 1].isMine())) {
                tile[i][j + 1].setIsShow(true);
                if (tile[i][j + 1].getNumber() == 0) {
                    setQueueCounter(getQueueCounter() + 1);
                    if(getQueueCounter() > 20) setQueueCounter(0);
                    getQueueTile()[getQueueCounter()][1] = i;
                    getQueueTile()[getQueueCounter()][2] = j + 1;
                }
            }
            if (i - 1 > 0 && !(tile[i - 1][j].isShow()) && !(tile[i - 1][j].isMine())) {
                tile[i - 1][j].setIsShow(true);
                if (tile[i - 1][j].getNumber() == 0) {
                    setQueueCounter(getQueueCounter() + 1);
                    if(getQueueCounter() > 20) setQueueCounter(0);
                    getQueueTile()[getQueueCounter()][1] = i - 1;
                    getQueueTile()[getQueueCounter()][2] = j;
                }
            }
            if (j - 1 > 0 && !(tile[i][j - 1].isShow()) && !(tile[i][j - 1].isMine())) {
                tile[i][j - 1].setIsShow(true);
                if (tile[i][j - 1].getNumber() == 0) {
                    setQueueCounter(getQueueCounter() + 1);
                    if(getQueueCounter() > 20) setQueueCounter(0);
                    getQueueTile()[getQueueCounter()][1] = i;
                    getQueueTile()[getQueueCounter()][2] = j - 1;
                }
            }
        }
    }

    public void sendMessage(String message) {
        setWait(true);
        tcpManager.sendMessage(message);
        while (true) {
            if (isWait())
                break;
        }
    }

    private GameManager() {
        tcpManager = TCPManager.getInstance();
        deviceService = new DeviceService();
    }

    /*
    public void useItem(int index, float i, float j, int itemNum) {
        //아이템 버튼을 눌렀을 때
        if(itemNum == 1) { // preview
            item.preview(index, i, j);
        } else if(itemNum == 2) { // Once More
            item.onceMore();
            // 게임 로직에 mine 이면 한 번 더 클릭, 아니면 상대방에게 넘기는 로직 추가
        } else if (itemNum == 3) { //scoreChange
            item.scoreChange();
            int tmp;
            tmp = getFindMine();
            setFindMine(getFindOtherMine());
            setFindOtherMine(tmp);
            // 상대방에게 scoreChange 아이템 공격 보내기
            // 만일 상대방이 defenseScoreChange()를 사용하면 무효화
        } else if(itemNum == 4) { // timeAttack
            item.timeAttack();
            // 상대방에게 timeAttack 아이템 공격 보내기
            // 제한 시간을 8초에서 4초로 변경
            // 만일 상대방이 defenseTimeAttack()을 사용하면 무효화
        }
    }
    */

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getFindMine() {
        return findMine;
    }

    public void setFindMine(int findMine) {
        this.findMine = findMine;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getTotalMine() {
        return totalMine;
    }

    public void setTotalMine(int totalMine) {
        this.totalMine = totalMine;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public Tile[][] getTile() {
        return tile;
    }

    public void setTile(Tile[][] tile) {
        this.tile = tile;
    }

    public Thread getMyThread() {
        return myThread;
    }

    public void setMyThread(Thread myThread) {
        this.myThread = myThread;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int[][] getQueueTile() {
        return queueTile;
    }

    public void setQueueTile(int[][] queueTile) {
        this.queueTile = queueTile;
    }

    public int getQueueCounter() {
        return queueCounter;
    }

    public void setQueueCounter(int queueCounter) {
        this.queueCounter = queueCounter;
    }

    public int getQueueSearcher() {
        return queueSearcher;
    }

    public void setQueueSearcher(int queueSearcher) {
        this.queueSearcher = queueSearcher;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getFindOtherMine() {
        return findOtherMine;
    }

    public void setFindOtherMine(int findOtherMine) {
        this.findOtherMine = findOtherMine;
    }

    public int getMyCombo() {
        return myCombo;
    }

    public void setMyCombo(int myCombo) {
        this.myCombo = myCombo;
    }

    public int getOtherCombo() {
        return otherCombo;
    }

    public void setOtherCombo(int otherCombo) {
        this.otherCombo = otherCombo;
    }

    public int getDefenseScoreNumber() {
        return defenseScoreNumber;
    }

    public void setDefenseScoreNumber(int defenseScoreNumber) {
        this.defenseScoreNumber = defenseScoreNumber;
    }

    public int getDefenseTimeNumber() {
        return defenseTimeNumber;
    }

    public void setDefenseTimeNumber(int defenseTimeNumber) {
        this.defenseTimeNumber = defenseTimeNumber;
    }

    public int getOnceMoreNumber() {
        return onceMoreNumber;
    }

    public void setOnceMoreNumber(int onceMoreNumber) {
        this.onceMoreNumber = onceMoreNumber;
    }

    public int getPreviewNumber() {
        return previewNumber;
    }

    public void setPreviewNumber(int previewNumber) {
        this.previewNumber = previewNumber;
    }

    public int getScoreChangeNumber() {
        return scoreChangeNumber;
    }

    public void setScoreChangeNumber(int scoreChangeNumber) {
        this.scoreChangeNumber = scoreChangeNumber;
    }

    public int getTimeAttackNumber() {
        return timeAttackNumber;
    }

    public void setTimeAttackNumber(int timeAttackNumber) {
        this.timeAttackNumber = timeAttackNumber;
    }

    public int getLeftMine() {
        return totalMine - findMine - findOtherMine;
    }

    public Item getItem(){
        return this.item;
    }

    public DeviceService getDeviceService() {
        return this.deviceService;
    }
}
