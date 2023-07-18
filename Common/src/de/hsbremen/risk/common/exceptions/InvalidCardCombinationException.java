package de.hsbremen.risk.common.exceptions;

import de.hsbremen.risk.common.entities.Player;

import javax.swing.*;

public class InvalidCardCombinationException extends CardExceptions
{
    public InvalidCardCombinationException(Player player)
    {
        super(player,
                String.format("%s has given a invalid combination of Cards!", player.getUsername()));
        /*JOptionPane.showMessageDialog(new JFrame(), String.format("%s has given a invalid combination of Cards!\n" +
                "You need: \n" +
                "3 cards of the same kind\n" +
                "3 cards with a different unit\n" +
                "or 3 random cards and a wild card", player.getUsername()));

         */
        JOptionPane.showMessageDialog(new JFrame(), String.format("%s has given a invalid combination of Cards!\n" +
                "blablablablbal" , player.getUsername()));
    }
}
