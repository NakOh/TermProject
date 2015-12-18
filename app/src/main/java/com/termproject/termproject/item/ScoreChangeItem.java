package com.termproject.termproject.item;

import com.termproject.termproject.model.Items;
import com.termproject.termproject.model.Tile;

/**
 * Created by kk070 on 2015-12-18.
 */
public class ScoreChangeItem extends Items {
    public void useItem(Tile[][] tile, int index, float currentX, float currentY){
        //근데 플레이어가 한 번씩 모두 쓸 수 있으니까 둘 다 한 번씩 쓰면 무쓸모 아닌가싶음
        //가 아니고 tile에 아이템을 랜덤배치해줘야하니까 있어도 될듯
    }
}
