package io.github.catchyaintit.blockbattle.game.battle;

import io.github.catchyaintit.blockbattle.game.card.CardManager;

import java.util.LinkedList;
import java.util.UUID;

public class Battle {
    private UUID playerOne;
    private UUID playerTwo;
    private LinkedList<CardManager> playerOnePlayedCards;
    private LinkedList<CardManager> playerTwoPlayedCards;

    public Battle(UUID playerOne, UUID playerTwo, LinkedList<CardManager> playerOnePlayedCards, LinkedList<CardManager> playerTwoPlayedCards) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.playerOnePlayedCards = playerOnePlayedCards;
        this.playerTwoPlayedCards = playerTwoPlayedCards;
    }

    public UUID getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(UUID playerOne) {
        this.playerOne = playerOne;
    }

    public UUID getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(UUID playerTwo) {
        this.playerTwo = playerTwo;
    }

    public LinkedList<CardManager> getPlayerOnePlayedCards() {
        return playerOnePlayedCards;
    }

    public void setPlayerOnePlayedCards(LinkedList<CardManager> playerOnePlayedCards) {
        this.playerOnePlayedCards = playerOnePlayedCards;
    }

    public LinkedList<CardManager> getPlayerTwoPlayedCards() {
        return playerTwoPlayedCards;
    }

    public void setPlayerTwoPlayedCards(LinkedList<CardManager> playerTwoPlayedCards) {
        this.playerTwoPlayedCards = playerTwoPlayedCards;
    }
}
