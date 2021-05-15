package io.github.catchyaintit.blockbattle.game.battle;

import java.util.LinkedList;

public class BattleManager {
    private int battles = 0;
    private LinkedList<Battle> battleList = new LinkedList();

    public LinkedList<Battle> getBattles() {
        return battleList;
    }

    public int getBattleCount() {
        return battles;
    }
}
