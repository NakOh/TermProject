package com.termproject.termproject.item;

import com.termproject.termproject.model.Items;
import com.termproject.termproject.model.Tile;

/**
 * Created by kk070 on 2015-12-18.
 */
public class PreviewItem extends Items {
    public void useItem(Tile[][] tile, int index, float currentX, float currentY){
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
                    //약간의 시간이 지난 후 다시 setIsShow(false) 적용//
                    ////////////////////////////////////////////////////
                    if(i + 1 < index - 1 && tile[i+1][j].isShow()) tile[i+1][j].setIsShow(false);
                    if(j + 1 < index - 1 && tile[i][j+1].isShow()) tile[i][j+1].setIsShow(false);
                    if(i - 1 > 0 && tile[i-1][j].isShow()) tile[i-1][j].setIsShow(false);
                    if(j - 1 > 0 && tile[i][j-1].isShow()) tile[i][j-1].setIsShow(false);
                }
            }
        }
    }
}
