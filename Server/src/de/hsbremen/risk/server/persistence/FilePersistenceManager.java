package de.hsbremen.risk.server.persistence;

import de.hsbremen.risk.common.entities.cards.Card;
import de.hsbremen.risk.common.entities.cards.PeaceCard;
import de.hsbremen.risk.common.entities.cards.UnitCard;
import de.hsbremen.risk.common.entities.cards.WildCard;
import de.hsbremen.risk.common.exceptions.NotEntitledToDrawCardException;
import de.hsbremen.risk.server.CardManager;
import de.hsbremen.risk.common.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class FilePersistenceManager implements PersistenceManager{

    @Override
    public JSONObject saveGame(ArrayList<Player> playerList, ArrayList<Continent> continentList, Turn turn, ArrayList<Card> cardList, CardManager cardManager) {
        try{
            JSONObject rootObject = new JSONObject();

            JSONArray playerArray = new JSONArray();
            for (Player player : playerList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", player.getUsername());
                jsonObject.put("armies", player.getArmies());
                jsonObject.put("rndomNmbr", player.getRandomNumber());
                jsonObject.put("mission", player.getMissionString());

                JSONArray jsonCardOnHand = new JSONArray();
                for (Card card: player.getCards())
                {
                    JSONObject jsonCardObject = new JSONObject();
                    if(card instanceof UnitCard)
                    {
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
        //    System.out.println("JSON Object" + rootObject.toString(4));

            JSONArray jsonCardArray = new JSONArray();
            for (Card card: cardList)
            {
                JSONObject jsonCardObject = new JSONObject();
                if(card instanceof UnitCard)
                {
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

    @Override
    public JSONObject loadFile(String file) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file + ".json"));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
          //  System.out.println("JSON: " + builder);
            br.close();
            return new JSONObject(builder.toString());
        } catch (FileNotFoundException ignored) {

        }
        return null;
    }
    public ArrayList<Card> retrieveCardsData(JSONObject jsonObject)
    {
        ArrayList<Card> cardList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("cardList");
        for(int i = 0; i < jsonArray.length(); i++)
        {
            if(jsonArray.getJSONObject(i).getString("kind").equals("Unit-Card"))
            {
                cardList.add(new UnitCard(jsonArray.getJSONObject(i).getString("units"), jsonArray.getJSONObject(i).getString("country"),jsonArray.getJSONObject(i).getInt("id")));
            }
            if(jsonArray.getJSONObject(i).getString("kind").equals("Peace-Card"))
            {
                cardList.add(new PeaceCard(jsonArray.getJSONObject(i).getInt("id")));
            }
            if(jsonArray.getJSONObject(i).getString("kind").equals("Wild-Card"))
            {
                cardList.add(new WildCard(jsonArray.getJSONObject(i).getInt("id")));
            }
        }
        return cardList;
    }

    public ArrayList<Player> retrievePlayerData(JSONObject jsonObject) throws NotEntitledToDrawCardException {
            ArrayList<Player> playerList = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("players");
            for (int i = 0; i < jsonArray.length(); i++) {

                Player player = new Player(jsonArray.getJSONObject(i).getString("username"), jsonArray.getJSONObject(i).getInt("armies"));
                double randomNumber = jsonArray.getJSONObject(i).getDouble("rndomNmbr");
                player.setRandomNumber(randomNumber);

                JSONArray cardjsonArray = jsonArray.getJSONObject(i).getJSONArray("cards");

                for (int j = 0; j < cardjsonArray.length(); j++)
                {
                    if(cardjsonArray.getJSONObject(j).getString("kind").equals("Unit-Card"))
                    {
                        player.insertCardToHand(new UnitCard(cardjsonArray.getJSONObject(j).getString("units"), cardjsonArray.getJSONObject(j).getString("country"), cardjsonArray.getJSONObject(j).getInt("id")));
                    }
                    if(cardjsonArray.getJSONObject(j).getString("kind").equals("Peace-Card"))
                    {
                        player.insertCardToHand(new WildCard(cardjsonArray.getJSONObject(j).getInt("id")));
                    }
                    if(cardjsonArray.getJSONObject(j).getString("kind").equals("Wild-Card"))
                    {
                        player.insertCardToHand(new PeaceCard(cardjsonArray.getJSONObject(j).getInt("id")));
                    }
                }

                playerList.add(player);
            }
            return playerList;
    }

    public Player retrieveDefeatPlayerMission(JSONObject jsonObject, Player player, ArrayList<Player> playerList) {
        JSONArray jsonArray = jsonObject.getJSONArray("players");
        for (int i = 0; i < jsonArray.length(); i++) {
            String username = jsonArray.getJSONObject(i).getString("username");
            String mission = jsonArray.getJSONObject(i).getString("mission");
            if (username.equals(player.getUsername())) {
                for (Player searchPlayer : playerList) {
                    if (mission.contains(searchPlayer.getUsername())) {
                  //      System.out.println("Mission " + mission);
                    //    System.out.println("Mission contains " + searchPlayer.getUsername() + " " + mission.contains(searchPlayer.getUsername()));
                        return searchPlayer;
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<Continent> retrieveContinentMission(JSONObject jsonObject, Player player, ArrayList<Continent> continentList) {
        ArrayList<Continent> trimmedContinentList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("players");
        for (int i = 0; i < jsonArray.length(); i++) {
            String username = jsonArray.getJSONObject(i).getString("username");
            String mission = jsonArray.getJSONObject(i).getString("mission");
            if (username.equals(player.getUsername())) {
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

    public void retrieveCardManagerInfo(JSONObject jsonObject, CardManager cardManager)
    {
        JSONObject jObject = jsonObject.getJSONObject("cardManager");
        cardManager.setNthTrade(jObject.getInt("nthTrade"));
        cardManager.setDeckPosition(jObject.getInt("deckPosition"));

    }

    public String retrieveTurnPlayer(JSONObject jsonObject) {
        JSONObject jObject = jsonObject.getJSONObject("turn");
        return jObject.getString("player");
    }

    public Turn.Phase retrieveTurnPhase(JSONObject jsonObject) {
        JSONObject jObject = jsonObject.getJSONObject("turn");
        return Turn.Phase.getPhaseFromString(jObject.getString("phase"));
    }

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

    public void writeGameIntoFile(ArrayList<Player> playerList, ArrayList<Continent> continentList, Turn turn, ArrayList<Card> card, CardManager cardManager, String datei) throws IOException {
        FileWriter fileWriter = new FileWriter(datei + ".json");
        fileWriter.write(saveGame(playerList, continentList, turn, card, cardManager).toString(4));
        fileWriter.close();
    }
}
