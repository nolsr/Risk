package de.hsbremen.risk.client;

import de.hsbremen.risk.common.ServerRemote;
import de.hsbremen.risk.common.entities.cards.PeaceCard;
import de.hsbremen.risk.server.RiskServer;
import de.hsbremen.risk.common.exceptions.InvalidCardCombinationException;
import de.hsbremen.risk.common.entities.Player;
import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.entities.cards.UnitCard;
import de.hsbremen.risk.common.entities.cards.WildCard;
import de.hsbremen.risk.client.components.DarkButton;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

/**
 * @author Raphael Tam-Dao
 */

public class ShowCardsFrame extends JPanel {

    private final int[] chosenCard = new int[3];
    private int clickCounter;

    public ShowCardsFrame(Player currentPlayer, ServerRemote riskServer, boolean stack) {
        super();

        this.setLayout(new BorderLayout());
        JPanel jPanel = new JPanel();
        this.setBackground(new Color(18, 20, 24));
        for (Card card : currentPlayer.getCards()) {

            System.out.println("Card: " + card);
            DarkButton darkButton = new DarkButton("");
            if (card instanceof UnitCard) {
                darkButton.setText("<html>ID: " + card.getId() + "<br/><br/><br/><br/>Country: " + ((UnitCard) card).getCountry() +  "<br/><br/><br/><br/>Kind: " + card.getKind() + "<br/><br/><br/><br/>Unit: " + card.getUnit() + "</html>");
            } else  if (card instanceof WildCard){

                darkButton.setText("<html>ID: " + card.getId() + "<br/><br/><br/><br/>Kind: " + card.getKind() + "<br/><br/><br/><br/>Unit: " + card.getUnit() + "</html>");
            } else if (card instanceof PeaceCard){
                darkButton.setText("<html>ID: " + card.getId() + "<br/><br/><br/><br/>Kind: " + card.getKind() + "<br/><br/><br/><br/>Unit: " + card.getUnit() + "</html>");
            }
            this.add(jPanel);
            if (stack) {
                darkButton.setEnabled(false);
            }

            darkButton.setFont(new Font("Arial", Font.BOLD, 14));
            darkButton.setPreferredSize(new Dimension(175, 300));

            jPanel.add(darkButton);
            jPanel.setOpaque(false);

            darkButton.addActionListener(e -> {
                chosenCard[clickCounter] = card.getId();

                System.out.println("Clicked Card: " + chosenCard[clickCounter]);
                darkButton.setEnabled(false);
                clickCounter++;
                if (clickCounter == 3) {
                    try {
                        JFrame thisFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        thisFrame.dispose();
                        riskServer.tradeCards(chosenCard);
                        JOptionPane.showMessageDialog(this, "You have successfully traded your cards!");
                    } catch (InvalidCardCombinationException | RemoteException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }
                }
            });
        }
    }

}
