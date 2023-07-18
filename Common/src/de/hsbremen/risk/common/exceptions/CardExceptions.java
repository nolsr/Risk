package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.entities.Player;

public class CardExceptions extends Exception
{
    private final Player player;

    public CardExceptions(Player player, String message)
    {
        super(message);
        this.player = player;
    }
}
