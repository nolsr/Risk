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

    /**
     * Create a player with their name.
     *
     * @param username String of their username.
     */
    public Player(String username) {
        this.username = username;
        this.armies = 0;
    }

    /**
     * To recreate a player after loading set their name and the amount of armies.
     *
     * @param username String of their username.
     * @param armyAmount Integer of their army amount.
     */
    public Player(String username, int armyAmount) {
        this.username = username;
        this.armies = armyAmount;
    }

    /**
     *  Get the String of the Player username.
     *
     * @return String of the Players username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the amount of armies the Player has.
     *
     * @return Integer amount of armies.
     */
    public int getArmies() {
        return armies;
    }

    /**
     * Set the amount of armies the Player has.
     *
     * @param armies Integer amount the Player has.
     */
    public void setArmies(int armies) {
        this.armies = armies;
    }

    /**
     * Set the mission the Player should complete.
     *
     * @param mission Mission the Player receives.
     */
    public void setMission(Mission mission) {
        this.mission = mission;
    }

    /**
     * Returns the missions instructions as a String.
     *
     * @return String of mission instructions.
     */
    public String getMissionString() {
        return this.mission.getPrintMissionString();
    }

    /**
     * Check if the Player has met the requirements to complete the mission and return true if they did.
     *
     * @param countries ArrayList of all countries.
     * @param players ArrayList of all Players.
     * @return True if the Player has met the missions requirements.
     */
    public boolean hasCompletedMission(ArrayList<Country> countries, ArrayList<Player> players) {
        return this.mission.isCompleted(this, countries, players);
    }

    /**
     * Set the random number the Player received to acquirer the mission.
     * Important for loading the Player with the right mission.
     *
     * @param randomNumber Double the random number had to receive the mission.
     */
    public void setRandomNumber(double randomNumber) {
        this.randomNumber = randomNumber;
    }

    /**
     * Get the random number the Player had to reload the Player correct mission.
     *
     * @return Integer random number.
     */
    public double getRandomNumber() {
        return this.randomNumber;
    }

    /**
     * Increase the Players army by the integer amount.
     *
     * @param amount Integer amount the army should be increased.
     */
    public void increaseArmies(int amount) {
        this.armies = this.armies + amount;
    }

    /**
     * Insert a new Card to a Players hand.
     *
     * @param card the card that needs to be inserted into the players hand.
     */
    public void insertCardToHand(Card card) {
        cards.add(card);
    }



    /**
     * Get all cards on the player hand.
     *
     * @return ArrayList of the current card hand of the player.
     */
    public ArrayList<Card> getCards()
    {
        return this.cards;
    }

    /**
     * Remove the three card form the players hand.
     *
     * @param firstCard removes the card from the player hand.
     * @param secondCard removes the card from the player hand.
     * @param thirdCard removes the card from the player hand.
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

    /**
     * Get the boolean that determent's if the Player is entitled to draw a card.
     *
     * @return True if the Player is entitled to draw a card.
     */
    public boolean getEntitledToDraw()
    {
        return entitledToDraw;
    }

    /**
     * Set that the Player is entitled to draw a card to true if the Player has defeated a country.
     * Otherwise, set it to false if the Player has drawn a card.
     *
     * @param isEntitledTo Boolean true if the player has defeated a country, false if the player has drawn a card.
     */
    public void setEntitledToDraw(boolean isEntitledTo)
    {
        this.entitledToDraw = isEntitledTo;
    }
}
