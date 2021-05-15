package io.github.catchyaintit.blockbattle.game;

import io.github.catchyaintit.blockbattle.game.card.CardManager;

public class BlockBattlePlayer {

    // TODO data about the player in the game
    private CardManager inventory = new CardManager();
    private int heldMana = 0;
    private int health = 100;



    public CardManager getInventory() {
        return inventory;
    }

    public void setInventory(CardManager inventory) {
        this.inventory = inventory;
    }

    public int getHeldMana() {
        return heldMana;
    }

    public void setHeldMana(int heldMana) {
        this.heldMana = heldMana;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

}
