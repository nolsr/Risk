package de.hsbremen.risk.client;

public class GameStateManager {
    public enum GameState {
        MAIN_MENU,
        LOBBY,
        IN_GAME
    }

    private GameState state;

    /**
     * Initializes the manager for the game state.
     */
    public GameStateManager() {
        state = GameState.MAIN_MENU;
    }

    /**
     * Retrieves the current game state.
     *
     * @return A GameState object of the current game state.
     */
    public GameState getGameState() {
        return state;
    }

    /**
     * Sets the game state to lobby when entering the lobby.
     */
    public void enterLobby() {
        this.state = GameState.LOBBY;
    }


    /**
     * Sets the game state to main menu when exiting the lobby.
     */
    public void exitLobby() {
        this.state = GameState.MAIN_MENU;
    }


    /**
     * Sets the game state to in game when starting or loading a game.
     */
    public void enterGame() {
        this.state = GameState.IN_GAME;
    }
}
