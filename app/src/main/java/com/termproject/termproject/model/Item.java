package com.termproject.termproject.model;

import com.termproject.termproject.manager.GameManager;
import com.termproject.termproject.model.Tile;

/**
 * Created by kk070 on 2015-12-02.
 */
public class Item {
    private boolean previewUsed = false;
    private boolean scorechangeUsed = false;
    private boolean oncemoreUsed = false;
    private boolean timeattackUsed = false;

    private Tile[][] tile;

    public void preview(int index, float currentX, float currentY) {
        if(!previewUsed){
            for (int i = 0; i < index; i++) {
                for (int j = 0; j < index; j++) {
                    if (i == 0 || j == 0 || i == index - 1 || j == index - 1) {
                        continue;
                    }
                    //tile[i][j]의 안에 클릭했을 때 작동
                    if(tile[i][j].getX()+tile[i][j].getW() > currentX && tile[i][j].getX() < currentX && tile[i][j].getY() < currentY && tile[i][j].getY()+tile[i][j].getH() > currentY){
                        //선택한 tile과 상하좌우 4개 타일을 잠시 보여줌
                        //여기에 로직 추가하면 됩니다.
                        tile[i][j].setIsShow(true);
                        if(i + 1 < index - 1 && !tile[i+1][j].isShow()) tile[i+1][j].setIsShow(true);
                        if(j + 1 < index - 1 && !tile[i][j+1].isShow()) tile[i][j+1].setIsShow(true);
                        if(i - 1 > 0 && !tile[i-1][j].isShow()) tile[i-1][j].setIsShow(true);
                        if(j - 1 > 0 && !tile[i][j-1].isShow()) tile[i][j-1].setIsShow(true);
                        //약간의 시간이 지난 후 다시 setIsShow(false) 적용
                    }
                }
            }
            previewUsed = true;
        }
    }

    public void scoreChange() {
        //근데 플레이어가 한 번씩 모두 쓸 수 있으니까 둘 다 한 번씩 쓰면 무쓸모 아닌가싶음
        //가 아니고 tile에 아이템을 랜덤배치해줘야하니까 있어도 될듯
    }

    public boolean onceMore() {
        if(!oncemoreUsed) {
            //MainView에 아이템 쓴 상태인지 체크하는 boolean값을 추가해서
            //아이템을 사용하면 true로 변경
            //만약 tile을 눌렀을 때 boolean 값이 true면 false로 변경한 후 한 번 더 선택하도록 설정
            oncemoreUsed = true;
            return true;
        }
        return false;
    }

    public boolean timeAttack() {
        if(!timeattackUsed){
            //소켓통신을 할 때 player1의 상태와 함께 timeLimit 아이템을 썼는지 여부를 보내서
            //true면 시간제한이 절반으로 감소
            timeattackUsed = true;
            return true;
        }
        return false;
    }

    public boolean defenceTimeAttack() {
        return false;
    }

    public boolean defenceScoreChange() {
        return false;
    }
}
