package de.hsbremen.risk.client;

import de.hsbremen.risk.common.ServerRemote;
import de.hsbremen.risk.common.events.GameActionEvent;
import de.hsbremen.risk.common.events.GameControlEvent;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.common.events.GameLobbyEvent;
import de.hsbremen.risk.common.exceptions.*;
import de.hsbremen.risk.common.entities.*;
import de.hsbremen.risk.client.components.*;

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
    private de.hsbremen.risk.client.ShowCardsFrame showCardsFrame;

    private final Attack attack;
    private Movement movement;

    public RiskInGame(ServerRemote riskServer, ArrayList<Player> players, ArrayList<Country> countries, Player player, Turn turn) {
        this.riskServer = riskServer;
        this.player = player;
        this.map = new RiskMap(players, countries);
        this.infoPanel = new InGameInfoPanel(players, player);
        this.controlPanel = new InGameControlPanel(turn);
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

    private void addEventListeners() {
        this.map.addCountryClickedListener(countryId -> {
            this.infoPanel.updateInfoPanel(riskServer.getCountry(countryId));
            if (listenToCountryClicked) {
                this.listenToCountryClicked = false;
                this.selectCountry(countryId);
            }
        });

        this.controlPanel.getBtnCardStack().addActionListener(e -> {
            JFrame frame = new JFrame();
            frame.setSize(1500, 600);
            this.showCardsFrame = new ShowCardsFrame(this.currentTurn.getPlayer(), riskServer, true);
            frame.add(showCardsFrame);
            frame.setVisible(true);

        });
//
//        this.controlPanel.getBtnTradeCards().addActionListener(e -> {
//            JFrame frame = new JFrame();
//            frame.setSize(1500, 600);
//
//            JOptionPane.showMessageDialog(new JFrame(), "Choose 3 cards you want to trade");
//            this.showCardsFrame = new ShowCardsFrame(riskServer.getCurrentTurn().getPlayer(), riskServer, false);
//            frame.add(showCardsFrame);
//            frame.setVisible(true);
//        });
//
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
//                case DRAWING_PHASE -> {
//                    if (riskServer.getCurrentTurn().getPlayer().getEntitledToDraw()) {
//                        riskServer.playerDrawsCard(riskServer.getCurrentTurn().getPlayer());
//                        riskServer.getCurrentTurn().getPlayer().setEntitledToDraw(false);
//                        JOptionPane.showMessageDialog(null,
//                                "You drew a card.");
//                        System.out.println("You Drew a Card");
//                    }
//                }
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });
    }

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

    public void getDefendingDice(GameActionEvent event) {
        int amountOfUnits = -1;
        while (amountOfUnits == -1) {
            try {
                amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                        this,
                        "You country " + event.getCountries().get(event.getAttack().getTargetCountry()).getName() +
                                " is being attacked with " + Math.max(event.getAttack().getAmount(), 3)
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

    public void updateGUI(GameActionEvent event) throws RemoteException {
        this.updateTurn(this.currentTurn);
        this.map.updateCountryInfo(event.getPlayers(), event.getCountries());
        this.controlPanel.setNewPhaseContent(this.riskServer.getCurrentTurn());
        this.redrawMap();
    }

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

    public void updatePlayer(Player player) {
        this.player = player;
    }

    public void redrawMap() {
        this.map.repaint();
    }

    public DarkButton getSaveGameButton() {
        return this.controlPanel.getBtnSave();
    }
}
