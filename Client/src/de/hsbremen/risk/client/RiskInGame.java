package de.hsbremen.risk.client;

import de.hsbremen.risk.server.Risk;
import de.hsbremen.risk.common.exceptions.*;
import de.hsbremen.risk.common.entities.*;
import de.hsbremen.risk.client.components.*;

import javax.swing.*;
import java.awt.*;

public class RiskInGame extends JPanel {
    private final Risk risk;
    private final RiskMap map;
    private final CurrentTurnPanel currentTurnPanel;
    private final InGameControlPanel controlPanel;
    private final InGameInfoPanel infoPanel;
    private boolean listenToCountryClicked;
    private de.hsbremen.risk.client.ShowCardsFrame showCardsFrame;

    private Attack attack;
    private Movement movement;

    public RiskInGame(Risk risk) {
        this.risk = risk;
        this.map = new RiskMap(risk.getPlayerList(), risk.getCountries());
        this.controlPanel = new InGameControlPanel(this.risk.getCurrentTurn());
        this.infoPanel = new InGameInfoPanel(risk.getPlayerList());
        this.currentTurnPanel = new CurrentTurnPanel(this.risk.getCurrentTurn());
        this.listenToCountryClicked = false;
        this.movement = new Movement();
        this.attack = new Attack();
        // this.showCardsFrame = new ShowCardsFrame(risk.getCurrentTurn().getPlayer());
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
            this.infoPanel.updateInfoPanel(risk.getCountry(countryId));
            if (listenToCountryClicked) {
                this.listenToCountryClicked = false;
                this.selectCountry(countryId);
            }
        });

        this.controlPanel.getBtnCardStack().addActionListener(e -> {
            JFrame frame = new JFrame();
            frame.setSize(1500, 600);
            this.showCardsFrame = new ShowCardsFrame(risk.getCurrentTurn().getPlayer(), risk, true);
            frame.add(showCardsFrame);
            frame.setVisible(true);

        });

        this.controlPanel.getBtnTradeCards().addActionListener(e -> {
            JFrame frame = new JFrame();
            frame.setSize(1500, 600);

            JOptionPane.showMessageDialog(new JFrame(), "Choose 3 cards you want to trade");
            this.showCardsFrame = new ShowCardsFrame(risk.getCurrentTurn().getPlayer(), risk, false);
            frame.add(showCardsFrame);
            frame.setVisible(true);

        });


        this.controlPanel.getBtnNextPhase().addActionListener(e -> onClickNextPhase());
        this.controlPanel.getBtnAction().addActionListener(e -> {
            this.listenToCountryClicked = true;
            switch (risk.getCurrentTurn().getPhase()) {
                case REINFORCEMENT_PHASE -> JOptionPane.showMessageDialog(null,
                        "Please select a country to place units in.");

                case LIBERATION_PHASE -> {
                    this.attack.reset();
                    JOptionPane.showMessageDialog(null,
                            "Please select an origin country for your attack.");
                }
                case MOVEMENT_PHASE -> {
                    this.movement.reset();
                    JOptionPane.showMessageDialog(null,
                            "Please select an origin country for your movement.");
                }
                case DRAWING_PHASE -> {
                    if (risk.getCurrentTurn().getPlayer().getEntitledToDraw()) {
                        risk.playerDrawsCard(risk.getCurrentTurn().getPlayer());
                        risk.getCurrentTurn().getPlayer().setEntitledToDraw(false);
                        JOptionPane.showMessageDialog(null,
                                "You drew a card.");
                        System.out.println("You Drew a Card");
                    }
                }
            }
        });
    }

    private void selectCountry(int countryId) {
        switch (risk.getCurrentTurn().getPhase()) {
            case REINFORCEMENT_PHASE -> {
                try {
                    int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                            null,
                            "How many units do you want to place?"));
                    risk.distributeArmy(countryId, amountOfUnits);
                    this.updateGUI();
                } catch (NotEnoughArmiesException | DoNotOccupyCountryException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for amount of armies");
                }
            }
            case LIBERATION_PHASE -> {
                if (!this.attack.hasOriginCountry()) {
                    this.attack.setOriginCountry(countryId);
                    JOptionPane.showMessageDialog(null,
                            "Please select a target country for your attack.");
                    this.listenToCountryClicked = true;
                } else {
                    this.attack.setTargetCountry(countryId);
                    try {
                        int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                                null,
                                "How many armies do you want to attack with?"));
                        this.attack.setAmount(amountOfUnits);
                        if (this.risk.isAttackLegal(this.attack)) {
                            this.risk.removeAttackingForcesFromOriginCountry();
                            this.openLiberationCycle();
                            this.attack.reset();
                        }
                        this.updateGUI();
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid input for amount of armies");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
            case MOVEMENT_PHASE -> {
                if (!this.movement.hasOriginCountry()) {
                    this.movement.setOriginCountry(countryId);
                    JOptionPane.showMessageDialog(null,
                            "Please select a target country for your movement.");
                    this.listenToCountryClicked = true;
                } else {
                    this.movement.setTargetCountry(countryId);
                    try {
                        int amountOfUnits = Integer.parseInt(JOptionPane.showInputDialog(
                                null,
                                "How many armies do you want to move?"));
                        risk.moveForces(this.movement.getOriginCountry(), this.movement.getTargetCountry(), amountOfUnits);
                        this.updateGUI();
                    } catch (MovementException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid input for amount of armies");
                    }
                }

            }
        }
    }

    private void openLiberationCycle() {
        AttackResult result;
        do {
            Country attackingCountry = this.risk.getCountry(this.risk.getCurrentAttack().getOriginCountry());
            Country defendingCountry = this.risk.getCountry(this.risk.getCurrentAttack().getTargetCountry());

            int attackingDice = this.risk.getCurrentAttack().getAmount();
            attackingDice = Math.min(attackingDice, 3);
            int defendingDice = 0;
            while (defendingDice < 1 || defendingDice > 2 || defendingDice > defendingCountry.getArmies()) {
                try {
                    defendingDice = Integer.parseInt(JOptionPane.showInputDialog(
                            null,
                            defendingCountry.getOccupiedBy() + " is defending. " +
                                    attackingCountry.getOccupiedBy() + " is attacking with " +
                                    attackingDice + " dice. How many dice do you want to defend with?"));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input for amount of armies");
                }
            }

            result = this.risk.attack(attackingDice, defendingDice);
            if (!result.hasAttackerWon()) {
                JOptionPane.showMessageDialog(null,
                        "Attacker rolled: " + result.getAttackingRolls().toString() + "\n" +
                                "Defender rolled: " + result.getDefendingRolls().toString() + "\n\n" +
                                "Defender won");
            } else {
                JOptionPane.showMessageDialog(null,
                        "Attacker rolled: " + result.getAttackingRolls().toString() + "\n" +
                                "Defender rolled: " + result.getDefendingRolls().toString() + "\n\n" +
                                "Attacker won with " + result.getWinningAttackingDice() + " dice\n" +
                                "Defender won with " + result.getWinningDefendingDice() + " dice\n" +
                                (!result.hasBeenResolved() ? "The attack continues" : ""));
            }
        } while (!result.hasBeenResolved());

        if (result.hasAttackerWon()) {
            JOptionPane.showMessageDialog(null, "The attack was successful");
        } else {
            JOptionPane.showMessageDialog(null, "The country was defended");
        }
    }

    private void onClickNextPhase() {
        this.movement.reset();
        try {
            this.risk.nextTurn();
            if (risk.getCurrentTurn().getPhase().equals(Turn.Phase.REINFORCEMENT_PHASE)) {
            }
        } catch (GameEndedException | UnplacedArmiesException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        this.updateGUI();
    }

    private void updateGUI() {
        this.map.updateCountryInfo(this.risk.getPlayerList(), this.risk.getCountries());
        this.currentTurnPanel.updateTurnDisplay(this.risk.getCurrentTurn());
        this.controlPanel.setNewPhaseContent(this.risk.getCurrentTurn());
        this.redrawMap();
    }

    public void redrawMap() {
        this.map.repaint();
    }

    public DarkButton getSaveGameButton() {
        return this.controlPanel.getBtnSave();
    }

    /*private void tradeCardsMenu()
        {
            int[] tradingCards = new int[3];
            for (Card card: risk.getCurrentTurn().getPlayer().getCards())
            {
                System.out.println(card);
            }
            System.out.println("Choose 3 Cards to trade with by Id");

            for(int i = 0; i < 3; i++)
            {
                System.out.println("Choose your " +(i+1)+" Card to Trade with");
                tradingCards[i] = readIntegerInput();
            }
            try
            {
                if(tradingCards[0] == tradingCards[1] || tradingCards[0] == tradingCards[2] || tradingCards[1] == tradingCards[2] )
                {
                    throw new FaultyTradeException(risk.getCurrentTurn().getPlayer());
                }
                for(int compId: tradingCards)
                {
                    boolean cardIsInHand = false;
                    for(Card cardId: risk.getCurrentTurn().getPlayer().getCards())
                    {
                        if(compId == cardId.getId())
                        {
                            cardIsInHand = true;
                        }
                    }
                    try {
                        if(!cardIsInHand)
                        {
                            throw new TradingCardNotOnHandException(risk.getCurrentTurn().getPlayer(), compId);
                        }
                    } catch (TradingCardNotOnHandException e) {
                        System.out.println(e.getMessage());
                        tradeCardsMenu();
                    }
                }
            } catch (FaultyTradeException e)
            {
                System.out.println(e.getMessage());
                tradeCardsMenu();
            }
            try
            {
                risk.tradeCards(tradingCards);
            } catch (InvalidCardCombinationException e) {
                System.out.println(e.getMessage());
            }


    }
      */
}
