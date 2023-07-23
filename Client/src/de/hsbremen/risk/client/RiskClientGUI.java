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
        gamestateManager = new GameStateManager();
        riskServer = new RiskServer();
        window = new JFrame();
        riskLobby = new RiskLobby();
        startScreen = new RiskStartScreen();

        addStartScreenButtonListeners();
        addLobbyButtonListeners();
    }

    public void createGameWindow() {
        window.setTitle("Risk");
        window.setBackground(new Color(18, 20, 24));
        window.setVisible(true);
//        window.setSize(1600, 900);
        window.setSize(600, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        setView();
    }

    public void setView() {
        switch (gamestateManager.getGameState()) {
            case MAIN_MENU -> {
                changePanel(window, startScreen);
            }
            case LOBBY -> {
                changePanel(window, riskLobby);
            }
            case IN_GAME -> {
                changePanel(window, inGame);
            }
        }
    }

    private void addStartScreenButtonListeners() {
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
                        riskLobby.updatePlayerList(riskServer.updatePlayerModel());
                        gamestateManager.enterLobby();
                        setView();
                    }
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), name + " is already taken");
                }
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        });
        startScreen.getQuitGameButton().addActionListener(e -> {
            System.exit(0);
        });
    }

    private void addLobbyButtonListeners() {
        riskLobby.getStartGameButton().addActionListener(e -> {
            try {
                riskServer.startGame();
            } catch (NotEnoughPlayersException notEnoughPlayersException) {
                JOptionPane.showMessageDialog(null, notEnoughPlayersException.getMessage());
            } catch (RemoteException remoteException) {
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
            setView();
        });

        riskLobby.getLoadGameButton().addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Please type in the file you want to load.");
            try {
                if (riskServer.loadGame(name)) {
                    gamestateManager.enterGame();
                } else if (name != null) {
                    JOptionPane.showMessageDialog(new JFrame(), "Couldn't find the file " + name + ".json");
                }
                setView();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void addInGameButtonListeners() {
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

    public void changePanel(JFrame frame, JPanel panel) {
        frame.setContentPane(panel);
        frame.repaint();
        frame.revalidate();
    }

    public Player getPlayer() {
        return player;
    }

    public RiskClientGUI getGameEventListener() {
        return this;
    }

    Thread defendingThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("Defending Thread running...");
                defending();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    });

    private void defending() throws RemoteException {
        synchronized (inGame) {
            System.out.println("You're under attack, defend!");
            //JOptionPane.showMessageDialog(null, "I'm " + event.getPlayer().getUsername() + " and i'm under attack!");
            int dice = Integer.parseInt(JOptionPane.showInputDialog("With how many dices would you like to defend?"));
            riskServer.setDefendingDice(dice);
            // Notify but it doesn't reach Attack
            inGame.notify();
        }
    }

    @Override
    public void handleGameEvent(GameEvent event) throws RemoteException {
        if (event instanceof GameActionEvent) {
            if (((GameActionEvent) event).getType() == GameActionEvent.GameControlEventType.DEFEND) {
                System.out.println("Event player "+ event.getPlayer().getUsername());
                System.out.println("Current player " + player.getUsername());
                if (event.getPlayer().getUsername().equals(player.getUsername())) {
                  defendingThread.start();
                }

            } else if (((GameActionEvent) event).getType() == GameActionEvent.GameControlEventType.ATTACK) {
                JOptionPane.showMessageDialog(null, event.getPlayer().getUsername() + " is attacking!");
            }
            System.out.println("Something happened, update GUI");
            inGame.updateGUI();
        } else if (event instanceof GameControlEvent) {
            if (((GameControlEvent) event).getType() == GameControlEvent.GameControlEventType.GAME_STARTED) {
                inGame = new RiskInGame(this.riskServer, this.riskServer.getPlayerList(),
                        this.riskServer.getCountries(), this.player, ((GameControlEvent) event).getTurn());
                addInGameButtonListeners();
                gamestateManager.enterGame();
                setView();
            } else if (((GameControlEvent) event).getType() == GameControlEvent.GameControlEventType.NEXT_PHASE) {
                inGame.updateTurn(((GameControlEvent) event).getTurn());
            } else if (((GameControlEvent) event).getType() == GameControlEvent.GameControlEventType.GAME_OVER) {
                inGame.updateTurn(((GameControlEvent) event).getTurn());
            }
        } else if (event instanceof GameLobbyEvent) {
            if (((GameLobbyEvent) event).getType() == GameLobbyEvent.GameLobbyEventType.PLAYER_ENTERED) {
                riskLobby.updateLobbyLog(event.getPlayer().getUsername() +" joined the lobby\n");
            } else {
                riskLobby.updateLobbyLog(event.getPlayer().getUsername() +" left the lobby\n");
            }
            riskLobby.updatePlayerList(riskServer.updatePlayerModel());
        }
    }

    public static void main(String[] args) throws RemoteException {
        RiskClientGUI riskClientGUI = new RiskClientGUI();
        riskClientGUI.createGameWindow();
    }
}
