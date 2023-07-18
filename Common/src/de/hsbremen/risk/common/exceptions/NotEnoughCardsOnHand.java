package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Player;

public class NotEnoughCardsOnHand extends CardExceptions
{
    public NotEnoughCardsOnHand(Player player, int minAmount)
    {
        super(player,
                String.format("%s only has %s cards on hand but it needs to be at least 3!", player.getUsername(), minAmount));
    }
}
