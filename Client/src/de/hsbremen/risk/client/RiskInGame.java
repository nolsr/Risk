package de.hsbremen.risk.client;

import de.hsbremen.risk.common.ServerRemote;
import de.hsbremen.risk.common.events.GameActionEvent;
import de.hsbremen.risk.common.events.GameControlEvent;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.common.events.GameLobbyEvent;
import de.hsbremen.risk.server.RiskServer;
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
    private final Player player;
    private Turn currentTurn;
    private de.hsbremen.risk.client.ShowCardsFrame showCardsFrame;

    private Attack attack;
    private Movement movement;

    public RiskInGame(ServerRemote riskServer, ArrayList<Player> players, ArrayList<Country> countries, Player player, Turn turn) throws RemoteException {
        this.riskServer = riskServer;
        this.player = player;
        this.map = new RiskMap(players, countries);
        this.infoPanel = new InGameInfoPanel(players, player);
        this.controlPanel = new InGameControlPanel(turn);
        this.currentTurnPanel = new CurrentTurnPanel(turn);
        updateTurn(turn);
        this.listenToCountryClicked = false;
        this.movement = new Movement();
        this.attack = new Attack();
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
            try {
                this.showCardsFrame = new ShowCardsFrame(riskServer.getCurrentTurn().getPlayer(), riskServer, true);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
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
                    case REINFORCEMENT_PHASE -> JOptionPane.showMessageDialog(null,
                            "Please select a country to place units in.");

//                case LIBERATION_PHASE -> {
//                    this.attack.reset();
//                    JOptionPane.showMessageDialog(null,
//                            "Please select an origin country for your attack.");
//                }
//                case MOVEMENT_PHASE -> {
//                    this.movement.reset();
//                    JOptionPane.showMessageDialog(null,
//                            "Please select an origin country for your movement.");
//                }
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

    private void selectCountry(int countryId) throws RemoteException {
        switch (riskServer.getCurrentTurn().getPhase()) {
            case REINFORCEMENT_PHASE -> {
                try {
                    int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                            null,
                            "How many units do you want to place?"));
                    riskServer.distributeArmy(countryId, amountOfUnits);
                } catch (NotEnoughArmiesException | DoNotOccupyCountryException | RemoteException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for amount of armies");
                }
            }
//            case LIBERATION_PHASE -> {
//                if (!this.attack.hasOriginCountry()) {
//                    this.attack.setOriginCountry(countryId);
//                    JOptionPane.showMessageDialog(null,
//                            "Please select a target country for your attack.");
//                    this.listenToCountryClicked = true;
//                } else {
//                    this.attack.setTargetCountry(countryId);
//                    try {
//                        int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
//                                null,
//                                "How many armies do you want to attack with?"));
//                        this.attack.setAmount(amountOfUnits);
//                        if (this.riskServer.isAttackLegal(this.attack)) {
//                            this.riskServer.removeAttackingForcesFromOriginCountry();
//                            this.openLiberationCycle();
//                            this.attack.reset();
//                        }
//                        this.updateGUI();
//                    } catch (NumberFormatException e) {
//                        JOptionPane.showMessageDialog(null, "Invalid input for amount of armies");
//                    } catch (Exception e) {
//                        JOptionPane.showMessageDialog(null, e.getMessage());
//                    }
//                }
//            }
//            case MOVEMENT_PHASE -> {
//                if (!this.movement.hasOriginCountry()) {
//                    this.movement.setOriginCountry(countryId);
//                    JOptionPane.showMessageDialog(null,
//                            "Please select a target country for your movement.");
//                    this.listenToCountryClicked = true;
//                } else {
//                    this.movement.setTargetCountry(countryId);
//                    try {
//                        int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
//                                null,
//                                "How many armies do you want to move?"));
//                        riskServer.moveForces(this.movement.getOriginCountry(), this.movement.getTargetCountry(), amountOfUnits);
//                        this.updateGUI();
//                    } catch (MovementException e) {
//                        JOptionPane.showMessageDialog(null, e.getMessage());
//                    } catch (NumberFormatException e) {
//                        JOptionPane.showMessageDialog(null, "Invalid input for amount of armies");
//                    }
//                }
//            }
//        }
    }
}

    private void openLiberationCycle() {
//        AttackResult result;
//        do {
//            Country attackingCountry = this.riskServer.getCountry(this.riskServer.getCurrentAttack().getOriginCountry());
//            Country defendingCountry = this.riskServer.getCountry(this.riskServer.getCurrentAttack().getTargetCountry());
//
//            int attackingDice = this.riskServer.getCurrentAttack().getAmount();
//            attackingDice = Math.min(attackingDice, 3);
//            int defendingDice = 0;
//            while (defendingDice < 1 || defendingDice > 2 || defendingDice > defendingCountry.getArmies()) {
//                try {
//                    defendingDice = Integer.parseInt(JOptionPane.showInputDialog(
//                            null,
//                            defendingCountry.getOccupiedBy() + " is defending. " +
//                                    attackingCountry.getOccupiedBy() + " is attacking with " +
//                                    attackingDice + " dice. How many dice do you want to defend with?"));
//                } catch (NumberFormatException e) {
//                    JOptionPane.showMessageDialog(null, "Invalid input for amount of armies");
//                }
//            }
//
//            result = this.riskServer.attack(attackingDice, defendingDice);
//            if (!result.hasAttackerWon()) {
//                JOptionPane.showMessageDialog(null,
//                        "Attacker rolled: " + result.getAttackingRolls().toString() + "\n" +
//                                "Defender rolled: " + result.getDefendingRolls().toString() + "\n\n" +
//                                "Defender won");
//            } else {
//                JOptionPane.showMessageDialog(null,
//                        "Attacker rolled: " + result.getAttackingRolls().toString() + "\n" +
//                                "Defender rolled: " + result.getDefendingRolls().toString() + "\n\n" +
//                                "Attacker won with " + result.getWinningAttackingDice() + " dice\n" +
//                                "Defender won with " + result.getWinningDefendingDice() + " dice\n" +
//                                (!result.hasBeenResolved() ? "The attack continues" : ""));
//            }
//        } while (!result.hasBeenResolved());
//
//        if (result.hasAttackerWon()) {
//            JOptionPane.showMessageDialog(null, "The attack was successful");
//        } else {
//            JOptionPane.showMessageDialog(null, "The country was defended");
//        }
    }

    private void onClickNextPhase() {
        this.movement.reset();
        try {
            this.riskServer.nextTurn();
        } catch (GameEndedException | UnplacedArmiesException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateGUI() throws RemoteException {
        this.map.updateCountryInfo(this.riskServer.getPlayerList(), this.riskServer.getCountries());
        this.currentTurnPanel.updateTurnDisplay(this.currentTurn);
        this.controlPanel.setNewPhaseContent(this.currentTurn);
        this.redrawMap();
    }

    public void updateTurn(Turn turn) throws RemoteException {
        this.currentTurn = turn;
        this.updateGUI();

        if (turn.getPlayer().getUsername().equals(this.player.getUsername())) {
            this.controlPanel.enableControls();
        } else {
            this.controlPanel.disableControls();
        }
    }

    public void redrawMap() {
        this.map.repaint();
    }

    public DarkButton getSaveGameButton() {
        return this.controlPanel.getBtnSave();
    }

    public void handleGameEvent(GameEvent event) {
        // Return if event is meant for different game state
        if (event instanceof GameLobbyEvent) {
            return;
        }

        if (event instanceof GameControlEvent) {
            System.out.println(((GameControlEvent) event).getType());
        } else if (event instanceof GameActionEvent) {
            System.out.println(((GameActionEvent) event).getType());
        }
    }
}
