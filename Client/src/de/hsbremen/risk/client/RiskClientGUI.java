package de.hsbremen.risk.client;

import de.hsbremen.risk.common.GameEventListener;
import de.hsbremen.risk.common.ServerRemote;
import de.hsbremen.risk.common.entities.Player;
import de.hsbremen.risk.common.events.GameActionEvent;
import de.hsbremen.risk.common.events.GameControlEvent;
import de.hsbremen.risk.common.events.GameEvent;
import de.hsbremen.risk.common.events.GameLobbyEvent;
import de.hsbremen.risk.common.exceptions.IllegalPlayerCountException;
import de.hsbremen.risk.common.exceptions.LoadGameWrongPlayerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RiskClientGUI extends UnicastRemoteObject implements GameEventListener {
    @Serial
    private static final long serialVersionUID = 8358370513053391721L;

    // Client view states
    private final RiskLobby riskLobby;
    private final RiskStartScreen startScreen;
    private RiskInGame inGame;
    private final GameStateManager gamestateManager;
    private final JFrame window;
    private ServerRemote riskServer;
    private Player player;

    public RiskClientGUI() throws RemoteException {
        window = new JFrame();
        gamestateManager = new GameStateManager();
        riskLobby = new RiskLobby();
        startScreen = new RiskStartScreen();

        addStartScreenButtonListeners();
        addLobbyButtonListeners();
    }

    /**
     * Creates the client window.
     */
    public void createGameWindow() {
        window.setTitle("Risk");
        window.setBackground(new Color(18, 20, 24));
        window.setVisible(true);
        window.setSize(1600, 900);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        setView();
    }

    /**
     * Sets the display view of the game window.
     */
    public void setView() {
        switch (gamestateManager.getGameState()) {
            case MAIN_MENU -> changePanel(window, startScreen);
            case LOBBY -> changePanel(window, riskLobby);
            case IN_GAME -> changePanel(window, inGame);
        }
    }

    /**
     * Adds the click listeners to the UI buttons on the main menu view.
     */
    private void addStartScreenButtonListeners() {
        startScreen.getNewGameButton().addActionListener(listener -> {
            String name = JOptionPane.showInputDialog(window, "Please enter your username");

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
                        JOptionPane.showMessageDialog(window, "Your username cannot have less than 3 letters");
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
                        riskLobby.updatePlayerList(riskServer.updatePlayerModel());
                        gamestateManager.enterLobby();
                        setView();
                    }
                } else {
                    JOptionPane.showMessageDialog(window, name + " is already taken");
                }
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        });
        startScreen.getQuitGameButton().addActionListener(e -> System.exit(0));
    }

    /**
     * Adds the click listeners to the UI buttons of the lobby view.
     */
    private void addLobbyButtonListeners() {
        riskLobby.getStartGameButton().addActionListener(e -> {
            try {
                riskServer.startGame();
            } catch (IllegalPlayerCountException illegalPlayerCountException) {
                JOptionPane.showMessageDialog(window, illegalPlayerCountException.getMessage());
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });

        riskLobby.getExitButton().addActionListener(e -> {
            try {
                riskServer.removePlayer(this.player);
                riskServer.removeGameEventListener(getGameEventListener());
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
            gamestateManager.exitLobby();
            setView();
        });

        riskLobby.getLoadGameButton().addActionListener(e -> {
            String file = JOptionPane.showInputDialog(window, "Please type in the file you want to load.");
            File f = new File(file + ".json");
            try {
                System.out.println("File: " + f);
                if (f.isFile()) {
                    riskServer.loadGame(file);
                } else {
                    JOptionPane.showMessageDialog(window, "Couldn't find the file " + file + ".json");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (LoadGameWrongPlayerException ex) {
                JOptionPane.showMessageDialog(window, ex.getMessage());
            }
        });
    }

    /**
     * Adds the save game button click listener and resize options for the in game view.
     */
    private void addInGameButtonListeners() {
        inGame.getSaveGameButton().addActionListener(e -> {
            String name = JOptionPane.showInputDialog(window, "Please assign a file name for your saved game.");
            try {
                if (name == null || name.isEmpty()) {
                    JOptionPane.showMessageDialog(window, "Please use at least one character as your file name");
                } else {
                    riskServer.saveGame(name);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
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

    /**
     * Changes the view of the window to a different panel.
     *
     * @param frame JFrame object of the game window.
     * @param panel JPanel object to be displayed in the window. Either MainMenu, Lobby or InGame.
     */
    public void changePanel(JFrame frame, JPanel panel) {
        frame.setContentPane(panel);
        frame.repaint();
        frame.revalidate();
    }

    /**
     * Retrieves the clients player object.
     *
     * @return A Player object of the clients player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retrieves this client.
     *
     * @return A RiskClientGUI object representing this client as a game listener.
     */
    public RiskClientGUI getGameEventListener() {
        return this;
    }

    /**
     * Receives all events sent by the server and handles them or passes them to the respective view for handling.
     *
     * @param event GameEvent object sent by the server to be handled.
     * @throws RemoteException When having trouble communicating with the server.
     */
    @Override
    public void handleGameEvent(GameEvent event) throws RemoteException {
        if (event instanceof GameActionEvent) { // Handle GameActionEvents
            GameActionEvent e = (GameActionEvent) event;
            if (e.getPlayer().getUsername().equals(this.getPlayer().getUsername())) {
                inGame.updatePlayer(e.getPlayer());
            }
            inGame.updateGUI(e);
            switch (e.getType()) {
                case ATTACK -> {
                    if (e.getAttack().getDefendingPlayer().getUsername().equals(this.getPlayer().getUsername())) {
                        inGame.getDefendingDice(e);
                    }
                }
                case ATTACK_RESULT -> inGame.handleAttackResultEvent(e);
                case DRAW -> {
                    if (e.getPlayer().getUsername().equals(player.getUsername())) {
                        inGame.updatePlayer(e.getPlayer());
                        JOptionPane.showMessageDialog(window, "You drew a card");
                    }
                }
                case TRADE -> {
                    if (e.getPlayer().getUsername().equals(player.getUsername())) {
                        inGame.updatePlayer(e.getPlayer());
                    }
                }
            }
        } else if (event instanceof GameControlEvent) { // Handle GameControlEvents
            GameControlEvent e = (GameControlEvent) event;
            if (e.getType() == GameControlEvent.GameControlEventType.GAME_STARTED) {
                this.player = riskServer.getPlayer(this.player.getUsername());
                inGame = new RiskInGame(this.riskServer, this.riskServer.getPlayerList(),
                        e.getCountries(), this.player, e.getTurn());
                addInGameButtonListeners();
                gamestateManager.enterGame();
                setView();
            } else if (e.getType() == GameControlEvent.GameControlEventType.NEXT_PHASE) {
                inGame.updateTurn(e.getTurn());
            } else if (e.getType() == GameControlEvent.GameControlEventType.GAME_OVER) {
                inGame.updateTurn(e.getTurn());
                if (e.isPeaceCardDrawn()) {
                    inGame.peaceMesage(e.getPlayer().getUsername());
                } else {
                    inGame.displayWinner(e.getPlayer().getUsername(), e.getPlayer().getMissionString());
                }
            }
        } else if (event instanceof GameLobbyEvent) { // Handle GameLobbyEvents
            if (((GameLobbyEvent) event).getType() == GameLobbyEvent.GameLobbyEventType.PLAYER_ENTERED) {
                riskLobby.updateLobbyLog(event.getPlayer().getUsername() + " joined the lobby\n");
            } else {
                riskLobby.updateLobbyLog(event.getPlayer().getUsername() + " left the lobby\n");
            }
            riskLobby.updatePlayerList(riskServer.updatePlayerModel());
        }
    }

    /**
     * Entry point of the client application.
     */
    public static void main(String[] args) {
        try {
            RiskClientGUI riskClientGUI = new RiskClientGUI();
            riskClientGUI.createGameWindow();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "Could not establish connection to the risk server.");
        }
    }
}
