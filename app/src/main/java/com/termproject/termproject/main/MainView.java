package com.termproject.termproject.main;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.termproject.termproject.R;
import com.termproject.termproject.manager.GameManager;
import com.termproject.termproject.model.Tile;

/**
 * Created by kk070 on 2015-12-06.
 */
public class MainView extends View {
    private Context mContext = null;
    private Tile[][] tile;
    private int w, h;
    //0 쉬움(5*5), 1 중간(7*7), 2 어려움(10*10)
    //로직 상 +2 한 값을 입력해야한다.
    private final static int mask = 2;
    private final static int easy = 5 + mask;
    private final static int normal = 7 + mask;
    private final static int hard = 10 + mask;
    private Canvas canvas;
    private int counter = 0;
    private int difficulty = 0;

    public MainView(Context context) {
        super(context);
        this.mContext = context;
        this.difficulty  = GameManager.getInstance().getDifficulty();
        //여기서 difficulty 값만 바꿔주면 난이도가 바뀜
        this.difficulty = 0;
        //0 쉬움(5*5), 1 중간(7*7), 2 어려움(10*10)
        if(difficulty == 0) {
            makeTile(easy);
        }else if(difficulty ==1){
            makeTile(normal);
        }else if(difficulty == 2){
            makeTile(hard);
        }else{
            //난이도 설정이 이상하게 된 경우
            System.out.println("잘못된 접근입니다");
        }
        this.setFocusableInTouchMode(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        if(difficulty == 0) {
            setTileSize(easy);
            updateTile(easy, canvas);
        }else if(difficulty ==1){
            setTileSize(normal);
            updateTile(normal, canvas);
        }else if(difficulty == 2){
            setTileSize(hard);
            updateTile(hard, canvas);
        }else{
            //난이도 설정이 이상하게 된 경우
            System.out.println("잘못된 접근입니다");
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        this.w = w;
        this.h = h;
        if(canvas != null){
            if(difficulty == 0) {
                setTileSize(easy);
                updateTile(easy, canvas);
            }else if(difficulty ==1){
                setTileSize(normal);
                updateTile(normal, canvas);
            }else if(difficulty == 2){
                setTileSize(hard);
                updateTile(hard, canvas);
            }else{
                //난이도 설정이 이상하게 된 경우
                System.out.println("잘못된 접근입니다");
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                if(difficulty == 0) {
                    checkTouch(easy, currentX, currentY);
                }else if(difficulty ==1){
                    checkTouch(normal, currentX, currentY);
                }else if(difficulty == 2){
                    checkTouch(hard, currentX, currentY);
                }else{
                    System.out.println("잘못된 접근입니다");
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private void makeTile(int index){
        //Mine설치
        setMine(index);
        //Mine설치 후 숫자 셋팅
        setNumber(index);
        //숫자 셋팅 및 Mine 셋팅 후 Tile마다 이미지 설정
        setTileImage(index);
    }

    private void setMine(int index){
        tile= new Tile[index][index];
        for (int i = 0; i < index; i++) {
            for(int j=0; j < index; j++){
                tile[i][j] = new Tile(mContext);
                //마스크는 패스(왼쪽 끝과, 오른쪽 끝은 빈 곳이다. 즉 지뢰가 설치될 수 있는 공간이 아님)
                if(i==0 || j==0 || i==index-1 || j==index-1){
                    continue;
                }
                //25%의 확률로 마인을 배치한다.
                if(randomRange(1,4) == 1) {
                    tile[i][j].setIsMine(true);
                }
            }
        }
    }

    private void setNumber(int index){
        for (int i = 0; i < index; i++) {
            for(int j=0; j < index; j++) {
                if(i==0 || j==0 || i==index-1 || j==index-1){
                    continue;
                }
                counter = 0;
                for(int l=-1; l<2; l++){
                    for(int m=-1; m<2; m++){
                        if(tile[i+l][j+m].isMine()){
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

    private void setTileImage(int index){
        for (int i = 0; i < index; i++) {
            for(int j=0; j < index; j++){
                //맨끝과 오른쪽 끝은 Image를 셋팅할 필요가 없다.
                if(i==0 || j==0 || i==index - 1 || j==index-1){
                    continue;
                }
                //TIle 이미지를 셋팅한다.(숫자 0~8, Mine)
                tile[i][j].setImage(mContext);
            }
        }
    }

    private void setTileSize(int index){
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if(i==0 || j==0 || i==index-1 || j==index-1){
                    continue;
                }
                //이미지 타일 이미지 크기를 결정한다.
                tile[i][j].setSize(w / (index - mask), h / (index - mask));
            }
        }
    }

    private void updateTile(int index, Canvas canvas){
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if(i==0 || j==0 || i==index-1 || j==index-1){
                    continue;
                }
                //이미지 위치를 지정한다. 테스트 해본 뒤 조정할 예정
                tile[i][j].update(canvas, j * w /index, i*h/index);
            }
        }
    }

    private void checkTouch(int index, float currentX, float currentY){
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                    continue;
                }
                //tile[i][j]의 안에 클릭했을 때 작동
                if(tile[i][j].getX()+tile[i][j].getW() > currentX && tile[i][j].getX() < currentX && tile[i][j].getY() < currentY && tile[i][j].getY()+tile[i][j].getH() > currentY){
                    //tile이 원래는 보이지 않기 때문에 보이도록 수정한다. 그리고 그것이 마인일 경우 마인 찾은 갯수를 증가!
                    //기존 지뢰찾기 처럼 0인 경우에는 주변의 타일이 전부 Show 되어야 한다.
                    //여기에 로직 추가하면 됩니다.
                    tile[i][j].setIsShow(true);
                    GameManager.getInstance().setFindMine(GameManager.getInstance().getFindMine() + 1);
                }
            }
        }
    }

    private int randomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }
}
