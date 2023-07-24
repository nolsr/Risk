package de.hsbremen.risk.common.entities;

import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.entities.missions.Mission;
import de.hsbremen.risk.common.exceptions.NotEntitledToDrawCardException;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = -5630139682033064525L;

    private final String username;
    private int armies;
    private final ArrayList<Card> cards = new ArrayList<>();
    private Mission mission;
    private boolean entitledToDraw = false;

    private double randomNumber;

    public Player(String username) {
        this.username = username;
        this.armies = 0;
    }

    public Player(String username, int armyAmount) {
        this.username = username;
        this.armies = armyAmount;
    }

    public String getUsername() {
        return username;
    }

    public int getArmies() {
        return armies;
    }

    public void setArmies(int armies) {
        this.armies = armies;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public String getMissionString() {
        return this.mission.getPrintMissionString();
    }

    public boolean hasCompletedMission(ArrayList<Country> countries, ArrayList<Player> players) {
        return this.mission.isCompleted(this, countries, players);
    }

    public void setRandomNumber(double randomNumber) {
        this.randomNumber = randomNumber;
    }

    public double getRandomNumber() {
        return this.randomNumber;
    }
    public void increaseArmies(int amount)
    {
        this.armies = this.armies + amount;
    }
    /**
     *
     * @return how many cards a Player has on hand
     */
    public int cardsOnHand()
    {
        return cards.size();
    }

    /**
     * Insert a new Card to a Players hand
     * @param card the card that needs to be inserted into the players hand
     */
    public void insertCardToHand(Card card) throws NotEntitledToDrawCardException {
        if (entitledToDraw) {
            cards.add(card);
        } else {
            throw new NotEntitledToDrawCardException();
        }
    }



    /**
     * Get all cards on the player hand
     * @return ArrayList of the current card hand of the player
     */
    public ArrayList<Card> getCards()
    {
        return this.cards;
    }

    /**
     * Remove the three card form the players hand
     * @param firstCard removes the card from the player hand
     * @param secondCard removes the card from the player hand
     * @param thirdCard removes the card from the player hand
     */
    public void removeCards(Card firstCard, Card secondCard, Card thirdCard)
    {
        for (int i = 0; i < this.cards.size(); i++) {
            Card card = this.cards.get(i);
            if (card.getId() == firstCard.getId()) {
                this.cards.remove(i);
            }
        }
        for (int i = 0; i < this.cards.size(); i++) {
            Card card = this.cards.get(i);
            if (card.getId() == secondCard.getId()) {
                this.cards.remove(i);
            }
        }
        for (int i = 0; i < this.cards.size(); i++) {
            Card card = this.cards.get(i);
            if (card.getId() == thirdCard.getId()) {
                this.cards.remove(i);
            }
        }
    }

    public boolean getEntitledToDraw()
    {
        return entitledToDraw;
    }

    public void setEntitledToDraw(boolean isEntitledTo)
    {
        this.entitledToDraw = isEntitledTo;
    }
}
