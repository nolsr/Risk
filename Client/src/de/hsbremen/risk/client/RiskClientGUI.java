package de.hsbremen.risk.client;

import de.hsbremen.risk.common.GameEventListener;
import de.hsbremen.risk.common.events.GameControlEvent;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.server.RiskServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RiskClientGUI extends UnicastRemoteObject// implements GameEventListener
 {

    // GameState Windows
    private RiskLobby riskLobby;
    private RiskStartScreen startScreen;
    private RiskInGame inGame;

    private final JFrame window;

    private final RiskServer riskServer;

    private final GameStateManager gamestateManager;

    private final boolean quitGame = false;

    public RiskClientGUI() throws RemoteException {
        gamestateManager = new GameStateManager();
        riskServer = new RiskServer();
        window = new JFrame();
        riskLobby = new RiskLobby();
    }

    public void createGameWindow() {
        window.setTitle("Risk");
        window.setBackground(new Color(18, 20, 24));
        window.setVisible(true);
        window.setSize(1600, 900);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());

        gameManager();
        //handleGameEvent();
    }

    public void gameManager() {
        GameStateManager.GameState state = gamestateManager.getGameState();
        switch (state) {
            case MAIN_MENU -> {
                startScreen = new RiskStartScreen();
                changePanel(window, startScreen);
                System.out.println("Game Manager Main Menu");
                mainMenu();
            }
            case LOBBY -> {
                // riskLobby = new RiskLobby();
                changePanel(window, riskLobby);
                System.out.println("Game Manager Lobby Menu");
                lobbyMenu();
            }
            case IN_GAME -> {
                inGame = new RiskInGame(this.riskServer);
                changePanel(window, inGame);
                turnMenu();

                window.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        super.componentResized(e);
                        window.setVisible(true);
                        inGame.redrawMap();
                        window.setVisible(true);
                    }
                });

                window.addWindowStateListener(new WindowAdapter() {
                    @Override
                    public void windowStateChanged(WindowEvent e) {
                        super.windowStateChanged(e);
                        window.setVisible(true);
                        inGame.redrawMap();
                        window.setVisible(true);
                    }
                });
            }
        }
    }

    /*
    @Override
    public void handleGameEvent(GameEvent event) {
        GameStateManager.GameState state = gamestateManager.getGameState();
        GameControlEvent gce = (GameControlEvent) event;
        //switch(gce.getType())
        switch (state) {
            case MAIN_MENU -> {
                startScreen = new RiskStartScreen();
                changePanel(window, startScreen);
                System.out.println("Game Manager Main Menu");
                mainMenu();
            }
            case LOBBY -> {
               // riskLobby = new RiskLobby();
                changePanel(window, riskLobby);
                System.out.println("Game Manager Lobby Menu");
                lobbyMenu();
            }
            case IN_GAME -> {

                inGame = new RiskInGame(this.riskServer);
                JOptionPane.showMessageDialog(new JFrame(),
                        "The game has just begun... It's player " + gce.getPlayer().getUsername() + "'s turn.",
                        "Game Started",
                        JOptionPane.INFORMATION_MESSAGE);
                changePanel(window, inGame);
                turnMenu();

                window.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        super.componentResized(e);
                        window.setVisible(true);
                        inGame.redrawMap();
                        window.setVisible(true);
                    }
                });

                window.addWindowStateListener(new WindowAdapter() {
                    @Override
                    public void windowStateChanged(WindowEvent e) {
                        super.windowStateChanged(e);
                        window.setVisible(true);
                        inGame.redrawMap();
                        window.setVisible(true);
                    }
                });
            }
        }
    }

     */

    private void mainMenu() {
        System.out.println("Main Menu");
        startScreen.getNewGameButton().addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Please enter your username");

            if (riskServer.getPlayer(name) == null) {
                if (name.length() < 3) {
                    JOptionPane.showMessageDialog(new JFrame(), "Your username cannot have less than 3 letters");
                } else {
                    try {
                        riskServer.addPlayer(name);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    riskLobby.getPlayerJList().setModel(riskServer.addPlayerToModel(name));
                    gamestateManager.enterLobby();

                    gameManager();
                    //handleGameEvent();
                }
            } else {
                JOptionPane.showMessageDialog(new JFrame(), name + " is already taken");
            }
        });
        startScreen.getLoadGameButton().addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Please type in the file you want to load.");
            try {
                if (riskServer.loadGame(name)) {
                    gamestateManager.enterGame();
                } else if (name != null){
                    JOptionPane.showMessageDialog(new JFrame(), "Couldn't find the file " + name + ".json");
                }
                gameManager();
                // handleGameEvent();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        startScreen.getQuitGameButton().addActionListener(e -> System.exit(0)) ;
    }

    private void lobbyMenu() {
        System.out.println("Lobby Menu");

        riskLobby.getStartGameButton().addActionListener(e -> {
            if (riskServer.getModel().size() >= 3 && riskServer.getModel().size() <= 6) {
                gamestateManager.enterGame();
                riskServer.startGame();
                System.out.println("Game gestartet");
                gameManager();
                //handleGameEvent();
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "You can only start the game if you have a minimum of 3 players and a maximum of 6 players");
            }
        });
        riskLobby.getAddPlayerButton().addActionListener(e -> {
                String name = JOptionPane.showInputDialog("Please enter your username");

                if (riskServer.getPlayer(name) == null) {
                    if (name.length() < 3) {
                        JOptionPane.showMessageDialog(new JFrame(), "Your username cannot have less than 3 letters");
                    } else {
                        try {
                            riskServer.addPlayer(name);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                        riskLobby.getPlayerJList().setModel(riskServer.addPlayerToModel(name));
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), name + " is already taken");
                }
        });
        riskLobby.getRemovePlayerButton().addActionListener(e -> {
            if (riskServer.getModel().size() == 0) {
                JOptionPane.showMessageDialog(new JFrame(), "There are no players signed up for the game yet");
            } else {
                String name = JOptionPane.showInputDialog("Please enter the player you want to remove");
                if (!name.isEmpty()){
                    try {
                        riskServer.removePlayer(name);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    riskLobby.getPlayerJList().setModel(riskServer.removePlayerFromModel(name));
                }
            }
        });
        riskLobby.getExitButton().addActionListener(e -> {
            riskServer.getModel().clear();
            riskServer.getPlayerList().clear();
            gamestateManager.exitLobby();
            gameManager();
            //handleGameEvent();
        });
    }

    private void turnMenu() {
        System.out.println("Turn Menu");
        inGame.getSaveGameButton().addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Please assign a file name for your saved game.");
                try {
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please use at least one character as your file name");
                    } else {
                        riskServer.saveGame(name);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
    }

    public void changePanel(JFrame frame, JPanel panel) {
        frame.setContentPane(panel);
        frame.repaint();
        frame.revalidate();
    }


    public static void main(String[] args) throws RemoteException {
        RiskClientGUI riskClientGUI = new RiskClientGUI();
        riskClientGUI.createGameWindow();
    }
}
