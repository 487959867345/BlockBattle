package io.github.catchyaintit.blockbattle.game.card;

import java.util.LinkedList;

public class CardManager {
    LinkedList<Card> cards = new LinkedList<>();
    public CardManager() {

    }

    public LinkedList<Card> getCards() {
        return cards;
    }

    public Card getCardByName(String id) {
        for (Card card : cards) {
            if (card.getName() == id) {
                return card;
            }
        }
        return null;
    }
    public void removeCardByName(String id) {
        for (Card card : cards) {
            if (card.getName() == id) {
                cards.remove(card);
            }
        }
    }
}
