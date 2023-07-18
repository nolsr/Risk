package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Player;

public class ToManyCardOnHand extends CardExceptions
{
    public ToManyCardOnHand(Player player)
    {
        super(player,
                String.format("%s has to many Cards on hand, you'll need to at least trade them ones!", player.getUsername()));
    }
}

