package de.hsbremen.risk.client;

import de.hsbremen.risk.common.GameEventListener;
import de.hsbremen.risk.common.ServerRemote;
import de.hsbremen.risk.common.entities.Player;
import de.hsbremen.risk.common.events.GameActionEvent;
import de.hsbremen.risk.common.events.GameControlEvent;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.common.events.GameLobbyEvent;
import de.hsbremen.risk.common.exceptions.NotEnoughPlayersException;
import de.hsbremen.risk.server.RiskServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

public class RiskClientGUI extends UnicastRemoteObject implements GameEventListener {
    // GameState Windows
    private final RiskLobby riskLobby;
    private RiskStartScreen startScreen;
    private RiskInGame inGame;

    private final JFrame window;

    private ServerRemote riskServer;

    private final GameStateManager gamestateManager;

    private final boolean quitGame = false;

    private Player player;

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
                changePanel(window, riskLobby);
                System.out.println("Game Manager Lobby Menu");
                lobbyMenu();
            }
            case IN_GAME -> {
//                inGame = new RiskInGame(this.riskServer);
//                changePanel(window, inGame);
//                turnMenu();
//
//                window.addComponentListener(new ComponentAdapter() {
//                    @Override
//                    public void componentResized(ComponentEvent e) {
//                        super.componentResized(e);
//                        window.setVisible(true);
//                        inGame.redrawMap();
//                        window.setVisible(true);
//                    }
//                });
//
//                window.addWindowStateListener(new WindowAdapter() {
//                    @Override
//                    public void windowStateChanged(WindowEvent e) {
//                        super.windowStateChanged(e);
//                        window.setVisible(true);
//                        inGame.redrawMap();
//                        window.setVisible(true);
//                    }
//                });
            }
        }
    }

    private void mainMenu() {
        System.out.println("Main Menu");
        startScreen.getNewGameButton().addActionListener(listener -> {
            String name = JOptionPane.showInputDialog("Please enter your username");


            if (name == null) {
                return;
            }

            // Server connection
            try {
                String serviceName = "RiskServer";
                Registry registry = LocateRegistry.getRegistry();
                riskServer = (ServerRemote) registry.lookup(serviceName);

                riskServer.addGameEventListener(this);


                // Enter lobby
                if (riskServer.getPlayer(name) == null) {
                    if (name.length() < 3) {
                        JOptionPane.showMessageDialog(new JFrame(), "Your username cannot have less than 3 letters");
                    } else {
                        this.player = new Player(name);
                        window.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                super.windowClosing(e);
                                try {
                                    riskServer.removePlayer(getPlayer());
                                    riskServer.removeGameEventListener(getGameEventListener());
                                } catch (RemoteException remoteException) {
                                    remoteException.printStackTrace();
                                }
                            }
                        });
                        try {
                            riskServer.addPlayer(this.player);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        riskLobby.getPlayerJList().setModel(riskServer.updatePlayerModel());
                        gamestateManager.enterLobby();

                        gameManager();
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), name + " is already taken");
                }
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }


        });
        startScreen.getLoadGameButton().addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Please type in the file you want to load.");
            try {
                if (riskServer.loadGame(name)) {
                    gamestateManager.enterGame();
                } else if (name != null) {
                    JOptionPane.showMessageDialog(new JFrame(), "Couldn't find the file " + name + ".json");
                }
                gameManager();
                // handleGameEvent();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        startScreen.getQuitGameButton().addActionListener(e -> System.exit(0));
    }

    private void lobbyMenu() {
        System.out.println("Lobby Menu");

        riskLobby.getStartGameButton().addActionListener(listener -> {
            try {
                riskServer.startGame();
            } catch (NotEnoughPlayersException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });

        riskLobby.getExitButton().addActionListener(e -> {
            try {
                riskServer.removePlayer(this.player);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
            gamestateManager.exitLobby();
            gameManager();
        });
    }

    public Player getPlayer() {
        return player;
    }

    public RiskClientGUI getGameEventListener() {
        return this;
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

    @Override
    public void handleGameEvent(GameEvent event) throws RemoteException {
        System.out.println("Received Game Event");

        if (event instanceof GameActionEvent) {

        } else if (event instanceof GameControlEvent) {

        } else if (event instanceof GameLobbyEvent) {
            System.out.println("Something happened, update model");
            riskLobby.getPlayerJList().setModel(riskServer.updatePlayerModel());
        }
    }

    public static void main(String[] args) throws RemoteException {
        RiskClientGUI riskClientGUI = new RiskClientGUI();
        riskClientGUI.createGameWindow();
    }
}
