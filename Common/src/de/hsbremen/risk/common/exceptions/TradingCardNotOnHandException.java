package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Player;

public class TradingCardNotOnHandException extends CardExceptions
{
    public TradingCardNotOnHandException(Player player, int cardId)
    {
        super(player,
                String.format("%s does not have that Card %i on hand!", player.getUsername(), cardId));
    }
}
