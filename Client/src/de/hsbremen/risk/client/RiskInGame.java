package de.hsbremen.risk.client;

import de.hsbremen.risk.client.components.*;
import de.hsbremen.risk.common.ServerRemote;
import de.hsbremen.risk.common.entities.*;
import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.events.GameActionEvent;
import de.hsbremen.risk.common.exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class RiskInGame extends JPanel {
    private final ServerRemote riskServer;
    private final RiskMap map;
    private final CurrentTurnPanel currentTurnPanel;
    private final InGameControlPanel controlPanel;
    private final InGameInfoPanel infoPanel;
    private boolean listenToCountryClicked;
    private Player player;
    private Turn currentTurn;
    private ShowCardsFrame showCardsFrame;

    private final Attack attack;
    private final Movement movement;

    public RiskInGame(ServerRemote riskServer, ArrayList<Player> players, ArrayList<Country> countries, Player player, Turn turn) {
        this.riskServer = riskServer;
        this.player = player;
        this.map = new RiskMap(players, countries);
        this.infoPanel = new InGameInfoPanel(players, player);
        this.controlPanel = new InGameControlPanel(turn, player);
        this.currentTurnPanel = new CurrentTurnPanel(turn);
        updateTurn(turn);
        this.listenToCountryClicked = false;
        this.movement = new Movement();
        this.attack = new Attack(player);
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(this.currentTurnPanel, BorderLayout.NORTH);
        centerPanel.add(this.map, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(this.infoPanel, BorderLayout.EAST);
        this.add(this.controlPanel, BorderLayout.SOUTH);

        map.repaint();
        this.addEventListeners();
    }

    /**
     * Adds the event listeners to the different control elements.
     */
    private void addEventListeners() {
        this.map.addCountryClickedListener(countryId -> {
            this.infoPanel.updateInfoPanel(riskServer.getCountry(countryId));
            if (listenToCountryClicked) {
                this.listenToCountryClicked = false;
                this.selectCountry(countryId);
                for (Card card : player.getCards()) {
                    System.out.println("Player cards: " + card.getId());
                }
            }
        });

        this.controlPanel.getBtnCardStack().addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            JFrame frame = new JFrame(this.player.getUsername() + "'s Card Stack");
            frame.setBounds(parentFrame.getX() + 50, parentFrame.getY() + 50, 1500, 600);
            this.showCardsFrame = new ShowCardsFrame(this.player, riskServer, true);
            frame.add(showCardsFrame);
            frame.setVisible(true);
        });

        this.controlPanel.getBtnTradeCards().addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            JFrame frame = new JFrame(this.player.getUsername() + "'s Card Stack");
            frame.setBounds(parentFrame.getX() + 50, parentFrame.getY() + 50, 1500, 600);
            JOptionPane.showMessageDialog(this, "Choose 3 cards you want to trade");
            this.showCardsFrame = new ShowCardsFrame(this.player, riskServer, false);
            frame.add(showCardsFrame);
            frame.setVisible(true);
        });

        this.controlPanel.getBtnNextPhase().addActionListener(e -> onClickNextPhase());
        this.controlPanel.getBtnAction().addActionListener(e -> {
            this.listenToCountryClicked = true;
            try {
                switch (riskServer.getCurrentTurn().getPhase()) {
                    case REINFORCEMENT_PHASE -> JOptionPane.showMessageDialog(this,
                            "Please select a country to place units in.");

                    case LIBERATION_PHASE -> {
                        this.attack.reset();
                        JOptionPane.showMessageDialog(this,
                                "Please select an origin country for your attack.");
                    }
                    case MOVEMENT_PHASE -> {
                        this.movement.reset();
                        JOptionPane.showMessageDialog(this,
                                "Please select an origin country for your movement.");
                    }
                    case DRAWING_PHASE -> {

                        riskServer.playerDrawsCard();
                        riskServer.getCurrentTurn().getPlayer().setEntitledToDraw(false);
                    }
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (NotEntitledToDrawCardException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    /**
     * Handles GameActionEvents sent from the server and passed through the RiskClientGUI.
     *
     * @param e GameActionEvent object of the event that should be handled.
     */
    public void handleAttackResultEvent(GameActionEvent e) {
        AttackResult result = e.getAttackResult();
        Attack attack = e.getAttack();

        if (attack.getAttackingPlayer().getUsername().equals(this.player.getUsername())) {
            if (result.hasBeenResolved()) {
                if (result.hasAttackerWon()) {
                    JOptionPane.showMessageDialog(this, "You successfully liberated "
                            + e.getCountries().get(attack.getTargetCountry()).getName() + "!\n"
                            + "You rolled: " + result.getAttackingRolls().toString() + "\n"
                            + attack.getDefendingPlayer().getUsername() + " rolled: "
                            + result.getDefendingRolls().toString());
                } else {
                    JOptionPane.showMessageDialog(this, attack.getDefendingPlayer().getUsername() +
                            " successfully defended " + e.getCountries().get(attack.getTargetCountry()).getName() + "!\n"
                            + "You rolled: " + result.getAttackingRolls().toString() + "\n"
                            + attack.getDefendingPlayer().getUsername() + " rolled: "
                            + result.getDefendingRolls().toString());
                }
            } else {
                JOptionPane.showMessageDialog(this, "The battle over "
                        + e.getCountries().get(attack.getTargetCountry()).getName() + " continues!\n"
                        + "You rolled: " + result.getAttackingRolls().toString() + "\n"
                        + attack.getDefendingPlayer().getUsername() + " rolled: "
                        + result.getDefendingRolls().toString() + "\n"
                        + "The attack continues...");
            }
        } else if (attack.getDefendingPlayer().getUsername().equals(this.player.getUsername())) {
            if (result.hasBeenResolved()) {
                if (result.hasAttackerWon()) {
                    JOptionPane.showMessageDialog(this, "Your country "
                            + e.getCountries().get(attack.getTargetCountry()).getName()
                            + " has been liberated!\n"
                            + "You rolled: " + result.getDefendingRolls().toString() + "\n"
                            + attack.getAttackingPlayer().getUsername() + " rolled: "
                            + result.getAttackingRolls().toString());
                } else {
                    JOptionPane.showMessageDialog(this, "You successfully defended "
                            + e.getCountries().get(attack.getTargetCountry()).getName() + "!\n"
                            + "You rolled: " + result.getDefendingRolls().toString() + "\n"
                            + attack.getAttackingPlayer().getUsername() + " rolled: "
                            + result.getAttackingRolls().toString());
                }
            } else {
                JOptionPane.showMessageDialog(this, "The battle over "
                        + e.getCountries().get(attack.getTargetCountry()).getName() + " continues!\n"
                        + "You rolled: " + result.getDefendingRolls().toString() + "\n"
                        + attack.getAttackingPlayer().getUsername() + " rolled: "
                        + result.getAttackingRolls().toString());
                getDefendingDice(e);
            }
        }
    }

    /**
     * Handles a country being clicked depending on what phase of the players turn it is.
     *
     * @param countryId ID of the country that was clicked.
     * @throws RemoteException When having trouble communicating with the server.
     */
    private void selectCountry(int countryId) throws RemoteException {
        switch (riskServer.getCurrentTurn().getPhase()) {
            case REINFORCEMENT_PHASE -> {
                try {
                    int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                            this,
                            "How many units do you want to place?"));
                    riskServer.distributeArmy(countryId, amountOfUnits);
                } catch (NotEnoughArmiesException | DoNotOccupyCountryException | RemoteException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid input for amount of armies");
                }
            }
            case LIBERATION_PHASE -> {
                if (!this.attack.hasOriginCountry()) {          // Wenn noch kein Urpsrungsland dann setze Urpsrungsland
                    this.attack.setOriginCountry(countryId);
                    JOptionPane.showMessageDialog(this,
                            "Please select a target country for your attack.");
                    this.listenToCountryClicked = true;
                } else {                                        // Wenn Urpsrungsland dann setze Zielland
                    this.attack.setTargetCountry(countryId);
                    try {
                        int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                                this,
                                "How many armies do you want to attack with?"));
                        this.attack.setAmount(amountOfUnits);
                        this.riskServer.startAttack(this.attack); // Angriff starten und Verteidiger benachrichtigen
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid input for amount of armies");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    }
                }
            }
            case MOVEMENT_PHASE -> {
                if (!this.movement.hasOriginCountry()) {
                    this.movement.setOriginCountry(countryId);
                    JOptionPane.showMessageDialog(this,
                            "Please select a target country for your movement.");
                    this.listenToCountryClicked = true;
                } else {
                    this.movement.setTargetCountry(countryId);
                    try {
                        int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                                this,
                                "How many armies do you want to move?"));
                        riskServer.moveForces(this.movement.getOriginCountry(), this.movement.getTargetCountry(), amountOfUnits);
                    } catch (MovementException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid input for amount of armies");
                    }
                }
            }
        }
    }

    /**
     * Gets the amount of dice a player defends an attack with via InputDialog.
     *
     * @param event GameActionEvent object containing the information of the Attack.
     */
    public void getDefendingDice(GameActionEvent event) {
        int amountOfUnits = -1;
        while (amountOfUnits == -1) {
            try {
                amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                        this,
                        "You country " + event.getCountries().get(event.getAttack().getTargetCountry()).getName() +
                                " is being attacked with " + event.getAttack().getAmount()
                                + " units, currently using "
                                + Math.min(event.getAttack().getAmount(), 3)
                                + " dice! How many armies do you want to defend with?"));
                this.riskServer.defendAttack(amountOfUnits);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for amount of armies");
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotEnoughArmiesException | IllegalDefendingDiceException e) {
                amountOfUnits = -1;
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    /**
     * Notifies the server that the player wants to end the current phase and enter the next one or end the turn.
     */
    private void onClickNextPhase() {
        this.movement.reset();
        try {
            this.riskServer.nextTurn();
        } catch (GameEndedException | UnplacedArmiesException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the UI with the latest information about players, map data, and the current turn.
     *
     * @param event GameActionEvent object containing information about the current map state.
     * @throws RemoteException When having trouble communicating with the server.
     */
    public void updateGUI(GameActionEvent event) throws RemoteException {
        this.updateTurn(this.currentTurn);
        this.map.updateCountryInfo(event.getPlayers(), event.getCountries());
        this.controlPanel.setNewPhaseContent(this.riskServer.getCurrentTurn());
        this.redrawMap();
    }

    /**
     * Updates the current turn object and the respective UI elements.
     *
     * @param turn Turn object of the current turn.
     */
    public void updateTurn(Turn turn) {
        this.currentTurn = turn;
        this.currentTurnPanel.updateTurnDisplay(this.currentTurn);
        this.controlPanel.setNewPhaseContent(this.currentTurn);

        if (turn.getPlayer().getUsername().equals(this.player.getUsername())) {
            this.controlPanel.enableControls();
        } else {
            this.controlPanel.disableControls();
        }
    }

    /**
     * Updates the player information, especially the amount of reinforcements left to place.
     *
     * @param player Latest Player object from the server.
     */
    public void updatePlayer(Player player) {
        this.player = player;
        this.currentTurn.setPlayer(player);
    }

    /**
     * Displays the winner of the game and the mission he accomplished.
     *
     * @param winner String of the username of the winner.
     * @param missionString String of the mission that the player accomplished.
     */
    public void displayWinner(String winner, String missionString) {
        JOptionPane.showMessageDialog(this, winner + " won the game by completing his mission! Congratulations!"
        + "\n Mission was: " + missionString);
    }

    public void peaceMesage(String peacePlayer) {
        JOptionPane.showMessageDialog(this, peacePlayer + " drew the peace card. All parties signed a peace treaty!");
    }

    /**
     * Tells the map component to redraw based on the latest information.
     */
    public void redrawMap() {
        this.map.repaint();
    }

    /**
     * Retrieves the save game button object.
     *
     * @return DarkButton object of the save game button.
     */
    public DarkButton getSaveGameButton() {
        return this.controlPanel.getBtnSave();
    }
}
