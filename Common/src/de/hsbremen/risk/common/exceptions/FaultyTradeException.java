package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Player;

public class FaultyTradeException extends CardExceptions
{
    public FaultyTradeException(Player player)
    {
        super(player,
                String.format("%s tried to make a Faulty Trade, make sure to trade three different Cards!", player.getUsername()));
    }
}
