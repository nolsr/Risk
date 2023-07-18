package de.hsbremen.risk.server;

import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.entities.cards.PeaceCard;
import de.hsbremen.risk.common.entities.cards.UnitCard;
import de.hsbremen.risk.common.entities.cards.WildCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CardManager {
    private final String wild = "Wild";
    private final String peace = "Peace";
    private int nthTrade = 0;
    private int deckPosition = 0;

    private ArrayList<Card> cardList;


    public CardManager()
    {
        creatUnitCardList();
    }

    public void updateCardManager(ArrayList<Card> cardList)
    {
        this.cardList = cardList;
    }

    public ArrayList<Card> getCardList()
    {
        return cardList;
    }

    /**
     * initializes cardList by filling it with all available card
     * except for the peace card
     *
     */
    private void creatUnitCardList()
    {
        cardList = new ArrayList<>();
        String infantry = "Infantry";
        cardList.add(new UnitCard(infantry, "Alaska"));
        String artillery = "Artillery";
        cardList.add(new UnitCard(artillery, "Northwest Territory"));
        String cavalry = "Cavalry";
        cardList.add(new UnitCard(cavalry, "Greenland"));
        cardList.add(new UnitCard(cavalry, "Alberta"));
        cardList.add(new UnitCard(cavalry, "Ontario"));
        cardList.add(new UnitCard(cavalry, "Quebec"));
        cardList.add(new UnitCard(artillery, "Western US"));
        cardList.add(new UnitCard(artillery, "Eastern US"));
        cardList.add(new UnitCard(artillery, "Central America"));

        cardList.add(new UnitCard(infantry, "Venezuela"));
        cardList.add(new UnitCard(artillery, "Brazil"));
        cardList.add(new UnitCard(infantry, "Peru"));
        cardList.add(new UnitCard(infantry, "Argentina"));

        cardList.add(new UnitCard(infantry, "Iceland"));
        cardList.add(new UnitCard(cavalry, "Scandinavia"));
        cardList.add(new UnitCard(artillery, "Great Britain"));
        cardList.add(new UnitCard(artillery, "Northern Europe"));
        cardList.add(new UnitCard(cavalry, "Ukraine"));
        cardList.add(new UnitCard(artillery, "Western Europe"));
        cardList.add(new UnitCard(artillery, "Southern Europe"));

        cardList.add(new UnitCard(cavalry, "North Africa"));
        cardList.add(new UnitCard(infantry, "Egypt"));
        cardList.add(new UnitCard(infantry, "Congo"));
        cardList.add(new UnitCard(infantry, "East Africa"));
        cardList.add(new UnitCard(artillery, "South Africa"));
        cardList.add(new UnitCard(cavalry, "Madagascar"));

        cardList.add(new UnitCard(cavalry, "Yakutsk"));
        cardList.add(new UnitCard(cavalry, "Ural"));
        cardList.add(new UnitCard(cavalry, "Siberia"));
        cardList.add(new UnitCard(cavalry, "Irkutsk"));
        cardList.add(new UnitCard(infantry, "Kamchatka"));
        cardList.add(new UnitCard(cavalry, "Afghanistan"));
        cardList.add(new UnitCard(infantry, "China"));
        cardList.add(new UnitCard(infantry, "Mongolia"));
        cardList.add(new UnitCard(artillery, "Japan"));
        cardList.add(new UnitCard(infantry, "Middle East"));
        cardList.add(new UnitCard(cavalry, "India"));
        cardList.add(new UnitCard(infantry, "Siam"));

        cardList.add(new UnitCard(artillery, "Indonesia"));
        cardList.add(new UnitCard(infantry, "New Guinea"));
        cardList.add(new UnitCard(artillery, "Western Australia"));
        cardList.add(new UnitCard(artillery, "Eastern Australia"));
        cardList.add(new WildCard());
        cardList.add(new WildCard());
        Collections.shuffle(cardList);
    }

    public Card getCardById(int cardId)
    {
        for (Card card: cardList)
        {
            if(card.getId() == cardId)
            {
                return card;
            }
        }
        return null;
    }

    /**
     * This Methode decides if and where within the card deck the peace card will fall,
     * depending on the Amount of active players.
     * This Methode should be called only once at the initialization of a game
     *
     * @param playerAmount number of active players in a game
     * @return PeaceCard has been inserted if true otherwise, it hasn't been inserted
     */
    public void insertPeaceCard(int playerAmount)
    {
        int randomOffSet = new Random().nextInt(5) - 2;
        switch (playerAmount)
        {
            case 2 -> cardList.add(14 + randomOffSet, new PeaceCard());
            case 3 -> cardList.add(21 + randomOffSet, new PeaceCard());
            case 4 -> cardList.add(28 + randomOffSet, new PeaceCard());
            case 5 -> cardList.add(35 + randomOffSet, new PeaceCard());
            default -> { cardList.add(new PeaceCard()); }
        }
    }

    /**
     * return the first cardList and removes it from the list
     * @return the first card from the deck
     */
    public Card drawCard()
    {
        Card tempCard = cardList.get(deckPosition);
        deckPosition++;
        //cardList.remove(0);
        return tempCard;
    }

    public int tradeCards(int[] cardIds)
    {

        Card card1 = getCardById(cardIds[0]);
        Card card2 = getCardById(cardIds[1]);
        Card card3 = getCardById(cardIds[2]);
        System.out.println("Card 1 " + card1);
        System.out.println("Card 2 " + card2);
        System.out.println("Card 3 " + card3);

        boolean allCardsAreValid = true;
        boolean atLeast1WildCard = false;

        boolean card1And2Equal = card1.getUnit().equals(card2.getUnit());
        boolean card1And3Equal = card1.getUnit().equals(card3.getUnit());
        boolean card2And3Equal = card2.getUnit().equals(card3.getUnit());

        for (int cardId: cardIds)
        {
            if(!(getCardById(cardId) instanceof UnitCard || getCardById(cardId) instanceof WildCard))
            {
                allCardsAreValid = false;
            }
            if(getCardById(cardId) instanceof WildCard)
            {
                atLeast1WildCard = true;
            }
        }
        if(allCardsAreValid)
        {
            if(card1And2Equal && card1And3Equal)
            {
                nthTrade++;
                if(nthTrade < 6)
                {
                    return nthTrade*2+2;
                }
                else
                {
                    return (nthTrade-3) * 5;
                }
            }
            else if(!card1And2Equal && !card1And3Equal && !card2And3Equal)
            {
                nthTrade++;
                if (nthTrade < 6) {
                    return nthTrade * 2 + 2;
                } else {
                    return (nthTrade - 3) * 5;
                }
            } else if(atLeast1WildCard)
            {
                nthTrade++;
                if (nthTrade < 6) {
                    return nthTrade * 2 + 2;
                } else {
                    return (nthTrade - 3) * 5;
                }
            }
        }
        return 0;
    }

    public int getNthTrade() {
        return nthTrade;
    }

    public void setNthTrade(int nthTrade) {
        this.nthTrade = nthTrade;
    }

    public int getDeckPosition() {
        return deckPosition;
    }

    public void setDeckPosition(int deckPosition) {
        this.deckPosition = deckPosition;
    }
}