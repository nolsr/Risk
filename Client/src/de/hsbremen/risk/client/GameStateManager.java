package de.hsbremen.risk.client;

public class GameStateManager {
    public enum GameState {
        MAIN_MENU,
        LOBBY,
        IN_GAME
    }

    private GameState state;

    public GameStateManager() {
        state = GameState.MAIN_MENU;
    }

    public GameState getGameState() {
        return state;
    }

    public void enterLobby() {
        this.state = GameState.LOBBY;
    }

    public void exitLobby() {
        this.state = GameState.MAIN_MENU;
    }

    public void enterGame() { this.state = GameState.IN_GAME; }
}
