package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Player;

public class WrongAmountOfUnitsException extends Exception{

    public WrongAmountOfUnitsException(Player player) {
        super(String.format("You can't distribute more than %s units", player.getArmies()));
    }
}
