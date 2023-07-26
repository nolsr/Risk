package de.hsbremen.risk.server.persistence;

import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.entities.cards.PeaceCard;
import de.hsbremen.risk.common.entities.cards.UnitCard;
import de.hsbremen.risk.common.entities.cards.WildCard;
import de.hsbremen.risk.server.CardManager;
import de.hsbremen.risk.common.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class FilePersistenceManager implements PersistenceManager {

    /**
     * This method stores all the data using the variables below into a JSONObject. Afterwards they'll be stored into JSONArrays and each JSONArray
     * will be stored into the rootObject.
     *
     * @param playerList ArrayList of all current players.
     * @param continentList ArrayList of Continents in which each country of each continent is stored as well.
     * @param turn includes the current turn, current player as well as the current phase.
     * @param cardManager class needed to get all the data including cards.
     * @return
     */
    @Override
    public JSONObject saveGame(ArrayList<Player> playerList, ArrayList<Continent> continentList, Turn turn, CardManager cardManager) {
        try {
            JSONObject rootObject = new JSONObject();

            JSONArray playerArray = new JSONArray();
            for (Player player : playerList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", player.getUsername());
                jsonObject.put("armies", player.getArmies());
                jsonObject.put("rndomNmbr", player.getRandomNumber());
                jsonObject.put("mission", player.getMissionString());

                JSONArray jsonCardOnHand = new JSONArray();
                for (Card card : player.getCards()) {
                    JSONObject jsonCardObject = new JSONObject();
                    if (card instanceof UnitCard) {
                        jsonCardObject.put("country", ((UnitCard) card).getCountry());
                    }
                    jsonCardObject.put("kind", card.getKind());
                    jsonCardObject.put("units", card.getUnit());
                    jsonCardObject.put("id", card.getId());
                    jsonCardOnHand.put(jsonCardObject);
                }
                jsonObject.put("cards", jsonCardOnHand);
                playerArray.put(jsonObject);
            }
            rootObject.put("players", playerArray);

            JSONArray continentHeaderArray = new JSONArray();
            for (int i = 0; i < continentList.size(); i++) {
                JSONArray continentBodyArray = new JSONArray();
                JSONObject jsonHeader = new JSONObject();
                switch (i) {
                    case 0 -> jsonHeader.put("name", "North America");
                    case 1 -> jsonHeader.put("name", "South America");
                    case 2 -> jsonHeader.put("name", "Europe");
                    case 3 -> jsonHeader.put("name", "Africa");
                    case 4 -> jsonHeader.put("name", "Asia");
                    case 5 -> jsonHeader.put("name", "Australia");
                }
                jsonHeader.put("ownedBy", continentList.get(i).getOwnedBy());
                for (int j = 0; j < continentList.get(i).getCountriesWithin().size(); j++) {

                    JSONObject jsonBody = new JSONObject();

                    jsonBody.put("countryId", continentList.get(i).getCountriesWithin().get(j).getAdjacencyId());
                    jsonBody.put("armies", continentList.get(i).getCountriesWithin().get(j).getArmies());
                    jsonBody.put("occupiedBy", continentList.get(i).getCountriesWithin().get(j).getOccupiedBy());
                    continentBodyArray.put(jsonBody);
                }
                jsonHeader.put("countries", continentBodyArray);
                continentHeaderArray.put(jsonHeader);
            }
            rootObject.put("continents", continentHeaderArray);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("player", turn.getPlayer().getUsername());
            System.out.println(turn.getPhase());
            jsonObject.put("phase", turn.getPhase());
            rootObject.put("turn", jsonObject);

            JSONArray jsonCardArray = new JSONArray();
            for (Card card : cardManager.getCardList()) {
                JSONObject jsonCardObject = new JSONObject();
                if (card instanceof UnitCard) {
                    jsonCardObject.put("country", ((UnitCard) card).getCountry());
                }
                jsonCardObject.put("kind", card.getKind());
                jsonCardObject.put("units", card.getUnit());
                jsonCardObject.put("id", card.getId());
                jsonCardArray.put(jsonCardObject);
            }
            rootObject.put("cardList", jsonCardArray);

            JSONObject jsonCardManagerObject = new JSONObject();
            jsonCardManagerObject.put("nthTrade", cardManager.getNthTrade());
            jsonCardManagerObject.put("deckPosition", cardManager.getDeckPosition());
            rootObject.put("cardManager", jsonCardManagerObject);

            return rootObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads the file, meaning it will append all the lines of the .json file into one string.
     *
     * @param file Filename of the file to be loaded.
     * @return JSONObject of the contents of the file.
     * @throws IOException When having trouble reading or finding the file.
     */
    @Override
    public JSONObject loadFile(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file + ".json"));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }
        br.close();
        return new JSONObject(builder.toString());
    }

    /**
     * All the card data are being retrieved and stored into the card ArrayList. Different kinds of cards are differentiated.
     *
     * @param jsonObject is the loaded file.
     * @return the card deck ArrayList which is filled with the retrieved cards from the saved game.
     */
    public ArrayList<Card> retrieveCardsData(JSONObject jsonObject) {
        ArrayList<Card> cardList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("cardList");
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getJSONObject(i).getString("kind").equals("Unit-Card")) {
                cardList.add(new UnitCard(jsonArray.getJSONObject(i).getString("units"), jsonArray.getJSONObject(i).getString("country"), jsonArray.getJSONObject(i).getInt("id")));
            }
            if (jsonArray.getJSONObject(i).getString("kind").equals("Peace-Card")) {
                cardList.add(new PeaceCard(jsonArray.getJSONObject(i).getInt("id")));
            }
            if (jsonArray.getJSONObject(i).getString("kind").equals("Wild-Card")) {
                cardList.add(new WildCard(jsonArray.getJSONObject(i).getInt("id")));
            }
        }
        return cardList;
    }

    /**
     * Retrieves all the playerdata from the given JSONObject (file) and stores them into a new player ArrayList.
     *
     * @param jsonObject is the loaded file.
     * @return the player ArrayList which is filled with the retrieved player including all their attributes from the saved game.
     */
    public ArrayList<Player> retrievePlayerData(JSONObject jsonObject) {
        ArrayList<Player> playerList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("players");
        for (int i = 0; i < jsonArray.length(); i++) {

            Player player = new Player(jsonArray.getJSONObject(i).getString("username"), jsonArray.getJSONObject(i).getInt("armies"));
            double randomNumber = jsonArray.getJSONObject(i).getDouble("rndomNmbr");
            player.setRandomNumber(randomNumber);

            JSONArray cardJsonArray = jsonArray.getJSONObject(i).getJSONArray("cards");

            for (int j = 0; j < cardJsonArray.length(); j++) {
                if (cardJsonArray.getJSONObject(j).getString("kind").equals("Unit-Card")) {
                    player.insertCardToHand(new UnitCard(cardJsonArray.getJSONObject(j).getString("units"), cardJsonArray.getJSONObject(j).getString("country"), cardJsonArray.getJSONObject(j).getInt("id")));
                }
                if (cardJsonArray.getJSONObject(j).getString("kind").equals("Peace-Card")) {
                    player.insertCardToHand(new WildCard(cardJsonArray.getJSONObject(j).getInt("id")));
                }
                if (cardJsonArray.getJSONObject(j).getString("kind").equals("Wild-Card")) {
                    player.insertCardToHand(new PeaceCard(cardJsonArray.getJSONObject(j).getInt("id")));
                }
            }

            playerList.add(player);
        }
        return playerList;
    }

    /**
     * It gets the player username and mission.
     * It'll be checked if the handed missionplayer is the player of the loaded file, meaning if the player has a mission to defeat a specific target.
     *
     * @param jsonObject is the loaded file.
     * @param missionPlayer Player that this mission gets assigned to.
     * @param playerList player ArrayList from the loaded JSONObject.
     * @return the targetplayer which the missionPlayer needs to defeat.
     */
    public Player retrieveDefeatPlayerMission(JSONObject jsonObject, Player missionPlayer, ArrayList<Player> playerList) {
        JSONArray jsonArray = jsonObject.getJSONArray("players");
        for (int i = 0; i < jsonArray.length(); i++) {
            String username = jsonArray.getJSONObject(i).getString("username");
            String mission = jsonArray.getJSONObject(i).getString("mission");
            if (username.equals(missionPlayer.getUsername())) {
                for (Player searchedTargetPlayer : playerList) {
                    if (mission.contains(searchedTargetPlayer.getUsername())) {
                        return searchedTargetPlayer;
                    }
                }
            }
        }
        return null;
    }

    /**
     * It gets the player username and mission. If the missionplayer is the player of the loaded file,
     * it will then check which continents he needs to obtain.
     *
     * @param jsonObject is the loaded file.
     * @param missionPlayer Player that this mission gets assigned to.
     * @param continentList ArrayList of all the continents including their countries.
     * @return a continentList which the player needs to own for his mission.
     */
    public ArrayList<Continent> retrieveContinentMission(JSONObject jsonObject, Player missionPlayer, ArrayList<Continent> continentList) {
        ArrayList<Continent> trimmedContinentList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("players");
        for (int i = 0; i < jsonArray.length(); i++) {
            String username = jsonArray.getJSONObject(i).getString("username");
            String mission = jsonArray.getJSONObject(i).getString("mission");
            if (username.equals(missionPlayer.getUsername())) {
                if (mission.contains("North America")) {
                    trimmedContinentList.add(continentList.get(0));
                }
                if (mission.contains("South America")) {
                    trimmedContinentList.add(continentList.get(1));
                }
                if (mission.contains("Europe")) {
                    trimmedContinentList.add(continentList.get(2));
                }
                if (mission.contains("Africa")) {
                    trimmedContinentList.add(continentList.get(3));
                }
                if (mission.contains("Asia")) {
                    trimmedContinentList.add(continentList.get(4));
                }
                if (mission.contains("Australia")) {
                    trimmedContinentList.add(continentList.get(5));
                }
            }
        }
        return trimmedContinentList;
    }

    /**
     * Gets and sets the cardManagers nthTrade count and deck position of the loaded file.
     *
     * @param jsonObject is the loaded file.
     * @param cardManager class needed to set its attributes.
     */
    public void retrieveCardManagerInfo(JSONObject jsonObject, CardManager cardManager) {
        JSONObject jObject = jsonObject.getJSONObject("cardManager");
        cardManager.setNthTrade(jObject.getInt("nthTrade"));
        cardManager.setDeckPosition(jObject.getInt("deckPosition"));
    }

    /**
     * Gets the current turn player of the loaded file and returns the player.
     *
     * @param jsonObject is the loaded file.
     * @return the loaded turn player.
     */
    public String retrieveTurnPlayer(JSONObject jsonObject) {
        JSONObject jObject = jsonObject.getJSONObject("turn");
        return jObject.getString("player");
    }

    /**
     * Gets the current turn of the loaded file and returns the phase.
     *
     * @param jsonObject is the loaded file.
     * @return the loaded phase from the file.
     */
    public Turn.Phase retrieveTurnPhase(JSONObject jsonObject) {
        JSONObject jObject = jsonObject.getJSONObject("turn");
        return Turn.Phase.getPhaseFromString(jObject.getString("phase"));
    }

    /**
     * Retrieves the index continent data from the given JSONObject (file) and sets the ownedBy attribute of the continent as well
     * as the armies and occupiedBy attributes of each country within the continent of the given index.
     *
     * @param jsonObject is the loaded file.
     * @param index are the continents: 0 = North America, 1 = South America, 2 = Europe, 3 =  Africa,
     * 4 = Asia, 5 = Australia
     * @param continentList ArrayList of all the continents including their countries.
     */
    public void retrieveContinentData(JSONObject jsonObject, int index, ArrayList<Continent> continentList) {
        JSONArray jsonArray = jsonObject.getJSONArray("continents");
        String continentOwnedBy = jsonArray.getJSONObject(index).getString("ownedBy");
        continentList.get(index).setOwnedBy(continentOwnedBy);
        jsonArray = jsonArray.getJSONObject(index).getJSONArray("countries");
        for (int i = 0; i < jsonArray.length(); i++) {
            int armies = jsonArray.getJSONObject(i).getInt("armies");
            String occupiedBy = jsonArray.getJSONObject(i).getString("occupiedBy");
            continentList.get(index).getCountriesWithin().get(i).setArmies(armies);
            continentList.get(index).getCountriesWithin().get(i).setOccupiedBy(occupiedBy);
        }
    }

    /**
     * Writes all the data of the rootObject into a new file.json using a FileWriter.
     *
     * @param playerList ArrayList of all current players.
     * @param continentList ArrayList of Continents in which each country of each continent is stored as well.
     * @param turn includes the current turn, current player as well as the current phase.
     * @param cardManager class needed to get all the data including cards.
     * @param file name of file that will be saved as a  .json file.
     * @throws IOException When there is a problem reading the file.
     */
    public void writeGameIntoFile(ArrayList<Player> playerList, ArrayList<Continent> continentList, Turn turn, CardManager cardManager, String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file + ".json");
        fileWriter.write(saveGame(playerList, continentList, turn, cardManager).toString(4));
        fileWriter.close();
    }
}
