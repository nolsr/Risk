package de.hsbremen.risk.common.events;

import de.hsbremen.risk.common.entities.Player;
import de.hsbremen.risk.common.entities.Turn;

import java.io.Serial;

public class GameLobbyEvent extends GameEvent {
    @Serial
    private static final long serialVersionUID = 1182863096463390804L;

    public enum GameLobbyEventType { PLAYER_ENTERED, PLAYER_LEFT }

    private final GameLobbyEventType type;

    public GameLobbyEvent(Player player, GameLobbyEventType type) {
        super(player);

        this.type = type;
    }

    public GameLobbyEventType getType() {
        return type;
    }
}
