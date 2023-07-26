package de.hsbremen.risk.server.persistence;

import de.hsbremen.risk.server.CardManager;
import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.entities.Continent;
import de.hsbremen.risk.common.entities.Player;
import de.hsbremen.risk.common.entities.Turn;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public interface PersistenceManager {

    JSONObject saveGame(ArrayList<Player> player, ArrayList<Continent> continentList, Turn turn, ArrayList<Card> card, CardManager cardManager) throws IOException;

    JSONObject loadFile(String file) throws IOException;
}
