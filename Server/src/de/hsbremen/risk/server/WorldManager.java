package de.hsbremen.risk.server;

import de.hsbremen.risk.common.exceptions.*;
import de.hsbremen.risk.common.entities.Continent;
import de.hsbremen.risk.common.entities.Country;
import de.hsbremen.risk.common.entities.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class WorldManager {
    private ArrayList<Country> countryList;
    private ArrayList<Continent> continentList;
    private boolean[][] adjacencyMatrix;

    public WorldManager() {
        fillWorldList();
        buildAdjacencyMatrix();
    }

    public Country getCountry(int countryId) {
        for (Country country : countryList) {
            if (country.getAdjacencyId() == countryId) {
                return country;
            }
        }
        return null;
    }

    public ArrayList<Country> getCountries() {
        return countryList;
    }

    public ArrayList<Continent> getContinentList() {
        return continentList;
    }

    public ArrayList<Integer> getNeighbourCountries(int countryId) {
        ArrayList<Integer> neighbourCountryId = new ArrayList<>();
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix[i].length; j++) {
                if (adjacencyMatrix[i][j] && i == countryId) {
                    neighbourCountryId.add(j);
                }
            }
        }
        return neighbourCountryId;
    }


    private void fillWorldList() {
        countryList = new ArrayList<>();
        continentList = new ArrayList<>();
        ArrayList<Country> northAmericaList = new ArrayList<>();
        ArrayList<Country> southAmericaList = new ArrayList<>();
        ArrayList<Country> europeList = new ArrayList<>();
        ArrayList<Country> africaList = new ArrayList<>();
        ArrayList<Country> asiaList = new ArrayList<>();
        ArrayList<Country> australiaList = new ArrayList<>();

        /* North America */
        northAmericaList.add(new Country(0, "Alaska", "alaska", "North America"));
        northAmericaList.add(new Country(1, "Northwest Territory", "northwestTerritory", "North America"));
        northAmericaList.add(new Country(2, "Greenland", "greenland", "North America"));
        northAmericaList.add(new Country(3, "Alberta", "alberta", "North America"));
        northAmericaList.add(new Country(4, "Ontario", "ontario", "North America"));
        northAmericaList.add(new Country(5, "Quebec", "quebec", "North America"));
        northAmericaList.add(new Country(6, "Western US", "westernUS", "North America"));
        northAmericaList.add(new Country(7, "Eastern US", "easternUS", "North America"));
        northAmericaList.add(new Country(8, "Central America", "centralAmerica", "North America"));
        continentList.add(new Continent("North America", northAmericaList));

        /* South America */
        southAmericaList.add(new Country(9, "Venezuela", "venezuela", "South America"));
        southAmericaList.add(new Country(10, "Brazil", "brazil", "South America"));
        southAmericaList.add(new Country(11, "Peru", "peru", "South America"));
        southAmericaList.add(new Country(12, "Argentina", "argentina", "South America"));
        continentList.add(new Continent("South America", southAmericaList));

        /* Europe */
        europeList.add(new Country(13, "Iceland", "iceland", "Europe"));
        europeList.add(new Country(14, "Scandinavia", "scandinavia", "Europe"));
        europeList.add(new Country(15, "Great Britain", "greatBritain", "Europe"));
        europeList.add(new Country(16, "Northern Europe", "northernEurope", "Europe"));
        europeList.add(new Country(17, "Ukraine", "ukraine", "Europe"));
        europeList.add(new Country(18, "Western Europe", "westernEurope", "Europe"));
        europeList.add(new Country(19, "Southern Europe", "southernEurope", "Europe"));
        continentList.add(new Continent("Europe", europeList));

        /* Africa */
        africaList.add(new Country(20, "North Africa", "northAfrica", "Africa"));
        africaList.add(new Country(21, "Egypt", "egypt", "Africa"));
        africaList.add(new Country(22, "Congo", "congo", "Africa"));
        africaList.add(new Country(23, "East Africa", "eastAfrica", "Africa"));
        africaList.add(new Country(24, "South Africa", "southAfrica", "Africa"));
        africaList.add(new Country(25, "Madagascar", "madagascar", "Africa"));
        continentList.add(new Continent("Africa", africaList));

        /* Asia */
        asiaList.add(new Country(26, "Yakutsk", "yakutsk", "Asia"));
        asiaList.add(new Country(27, "Ural", "ural", "Asia"));
        asiaList.add(new Country(28, "Siberia", "siberia", "Asia"));
        asiaList.add(new Country(29, "Irkutsk", "irkutsk", "Asia"));
        asiaList.add(new Country(30, "Kamchatka", "kamchatka", "Asia"));
        asiaList.add(new Country(31, "Afghanistan", "afghanistan", "Asia"));
        asiaList.add(new Country(32, "China", "china", "Asia"));
        asiaList.add(new Country(33, "Mongolia", "mongolia", "Asia"));
        asiaList.add(new Country(34, "Japan", "japan", "Asia"));
        asiaList.add(new Country(35, "Middle East", "middleEast", "Asia"));
        asiaList.add(new Country(36, "India", "india", "Asia"));
        asiaList.add(new Country(37, "Siam", "siam", "Asia"));
        continentList.add(new Continent("Asia", asiaList));

        /* Australia */
        australiaList.add(new Country(38, "Indonesia", "indonesia", "Australia"));
        australiaList.add(new Country(39, "New Guinea", "newGuinea", "Australia"));
        australiaList.add(new Country(40, "Western Australia", "westernAustralia", "Australia"));
        australiaList.add(new Country(41, "Eastern Australia", "easternAustralia", "Australia"));
        continentList.add(new Continent("Australia", australiaList));

        countryList.addAll(northAmericaList);
        countryList.addAll(southAmericaList);
        countryList.addAll(europeList);
        countryList.addAll(africaList);
        countryList.addAll(asiaList);
        countryList.addAll(australiaList);
    }

    private void buildAdjacencyMatrix() {
        adjacencyMatrix = new boolean[42][42];
        for (int i = 0; i < 42; i++) {
            Arrays.fill(adjacencyMatrix[i], false);
        }

        /* North America */
        setAdjacencyBetween(0, 1);
        setAdjacencyBetween(0, 3);
        setAdjacencyBetween(1, 2);
        setAdjacencyBetween(1, 3);
        setAdjacencyBetween(1, 4);
        setAdjacencyBetween(2, 4);
        setAdjacencyBetween(2, 5);
        setAdjacencyBetween(3, 4);
        setAdjacencyBetween(4, 5);
        setAdjacencyBetween(3, 6);
        setAdjacencyBetween(4, 6);
        setAdjacencyBetween(4, 7);
        setAdjacencyBetween(5, 7);
        setAdjacencyBetween(6, 7);
        setAdjacencyBetween(6, 8);
        setAdjacencyBetween(7, 8);

        /* South America */
        setAdjacencyBetween(9, 10);
        setAdjacencyBetween(9, 11);
        setAdjacencyBetween(10, 11);
        setAdjacencyBetween(10, 12);
        setAdjacencyBetween(11, 12);

        /* Europe */
        setAdjacencyBetween(13, 14);
        setAdjacencyBetween(13, 15);
        setAdjacencyBetween(14, 15);
        setAdjacencyBetween(14, 16);
        setAdjacencyBetween(14, 17);
        setAdjacencyBetween(15, 16);
        setAdjacencyBetween(15, 18);
        setAdjacencyBetween(16, 17);
        setAdjacencyBetween(16, 18);
        setAdjacencyBetween(16, 19);
        setAdjacencyBetween(17, 19);
        setAdjacencyBetween(18, 19);

        /* Africa */
        setAdjacencyBetween(20, 21);
        setAdjacencyBetween(20, 22);
        setAdjacencyBetween(20, 23);
        setAdjacencyBetween(21, 23);
        setAdjacencyBetween(22, 23);
        setAdjacencyBetween(22, 24);
        setAdjacencyBetween(23, 24);
        setAdjacencyBetween(23, 25);
        setAdjacencyBetween(24, 25);

        /* Asia */
        setAdjacencyBetween(26, 27);
        setAdjacencyBetween(26, 28);
        setAdjacencyBetween(26, 29);
        setAdjacencyBetween(26, 30);
        setAdjacencyBetween(27, 28);
        setAdjacencyBetween(27, 31);
        setAdjacencyBetween(27, 32);
        setAdjacencyBetween(28, 29);
        setAdjacencyBetween(28, 32);
        setAdjacencyBetween(28, 33);
        setAdjacencyBetween(29, 30);
        setAdjacencyBetween(29, 33);
        setAdjacencyBetween(30, 33);
        setAdjacencyBetween(30, 34);
        setAdjacencyBetween(31, 32);
        setAdjacencyBetween(31, 35);
        setAdjacencyBetween(31, 36);
        setAdjacencyBetween(32, 33);
        setAdjacencyBetween(32, 36);
        setAdjacencyBetween(32, 37);
        setAdjacencyBetween(33, 34);
        setAdjacencyBetween(35, 36);
        setAdjacencyBetween(36, 37);

        /* Australia */
        setAdjacencyBetween(38, 39);
        setAdjacencyBetween(38, 40);
        setAdjacencyBetween(39, 40);
        setAdjacencyBetween(39, 41);
        setAdjacencyBetween(40, 41);

        /* Intercontinental */
        setAdjacencyBetween(0, 30);
        setAdjacencyBetween(2, 13);
        setAdjacencyBetween(8, 9);
        setAdjacencyBetween(10, 20);
        setAdjacencyBetween(17, 27);
        setAdjacencyBetween(17, 31);
        setAdjacencyBetween(17, 35);
        setAdjacencyBetween(18, 20);
        setAdjacencyBetween(19, 20);
        setAdjacencyBetween(19, 21);
        setAdjacencyBetween(19, 35);
        setAdjacencyBetween(21, 35);
        setAdjacencyBetween(23, 35);
        setAdjacencyBetween(37, 38);
    }

    private void setAdjacencyBetween(int countryOneId, int countryTwoId) {
        adjacencyMatrix[countryOneId][countryTwoId] = true;
        adjacencyMatrix[countryTwoId][countryOneId] = true;
    }

    public int getAmountOfCountriesOwnedBy(String username) {
        int amount = 0;
        for (Country country : countryList) {
            if (country.getOccupiedBy().equals(username)) amount++;
        }
        return amount;
    }

    public void moveForces(int originCountryId, int targetCountryId, int amount, Player currentPlayer) throws MovementException {
        Country originCountry = getCountry(originCountryId);
        Country targetCountry = getCountry(targetCountryId);
        if (originCountryId == targetCountryId) {
            throw (new MovementBetweenSameCountriesException(originCountry, targetCountry));
        } else if (!originCountry.getOccupiedBy().equals(currentPlayer.getUsername()) ||
                !targetCountry.getOccupiedBy().equals(currentPlayer.getUsername())) {
            throw (new DoNotOccupyBothCountriesException(originCountry, targetCountry));
        } else if (originCountry.getUnitsMoved()) {
            throw (new ArmiesAlreadyMovedException(originCountry, targetCountry));
        } else if (originCountry.getArmies() - amount < 1) {
            throw (new NoArmiesLeftException(originCountry, targetCountry));
        } else if (!hasConnection(originCountryId, targetCountryId, currentPlayer)) {
            throw (new CountriesNotConnectedException(originCountry, targetCountry));
        }
        originCountry.setArmies(originCountry.getArmies() - amount);
        targetCountry.setArmies(targetCountry.getArmies() + amount);
        targetCountry.setUnitsMoved(true);
    }

    public void resetUnitsMoved() {
        countryList.forEach(country -> country.setUnitsMoved(false));
    }

    private ArrayList<Country> getCountriesOccupiedBy(Player player) {
        ArrayList<Country> countries = new ArrayList<>();
        countryList.forEach(country -> {
            if (country.getOccupiedBy().equals(player.getUsername())) {
                countries.add(country);
            }
        });
        return countries;
    }

    private boolean hasConnection(int originCountryId, int targetCountryId, Player currentPlayer) {
        ArrayList<Country> playersCountries = getCountriesOccupiedBy(currentPlayer);
        boolean[] checked = new boolean[42];
        return recurseFindConnection(originCountryId, targetCountryId, playersCountries, checked);
    }

    private boolean recurseFindConnection(int currentCountryId, int targetCountryId, ArrayList<Country> playersCountries, boolean[] checked) {
        if (currentCountryId == targetCountryId) {
            return true;
        }

        checked[currentCountryId] = true;
        for (Country country : playersCountries) {
            if (adjacencyMatrix[currentCountryId][country.getAdjacencyId()] && !checked[country.getAdjacencyId()]) {
                boolean isConnected = recurseFindConnection(country.getAdjacencyId(), targetCountryId, playersCountries, checked);
                if (isConnected) return true;
            }
        }

        return false;
    }

    public void assignCountriesToPlayers(ArrayList<Player> playerList) {
        int defaultArmyStartSize = 1;
        try {
            ArrayList<Country> tempCountryList = new ArrayList<>(countryList);
            int n = 0;
            int numberOfCountriesPerPlayer = countryList.size() / playerList.size();
            int remainder = countryList.size() % playerList.size();
            Collections.shuffle(tempCountryList);

            for (Player player : playerList) {
                for (int j = 0; j < numberOfCountriesPerPlayer; j++) {
                    Country countryOfList = tempCountryList.get(n);
                    int index = countryList.indexOf(countryOfList);
                    countryList.get(index).setOccupiedBy(player.getUsername());
                    countryList.get(index).setArmies(defaultArmyStartSize);
                    n++;
                }
            }
            if (remainder != 0) {
                for (int i = 0; i < remainder; i++) {
                    Country countryOfList = tempCountryList.get(n);
                    int index = countryList.indexOf(countryOfList);
                    countryList.get(index).setOccupiedBy(playerList.get(i).getUsername());
                    countryList.get(index).setArmies(defaultArmyStartSize);
                    n++;
                }
            }
        } catch (Exception e) {
            System.out.println("Not enough players have been added to the game yet.");
        }
    }

    public void checkIfPlayerOwnsContinentAndSet(String continentName, String occupant) {
        for (Continent continent1 : continentList) {
            if (continent1.getName().equals(continentName)) {
                for (int i = 0; i < continent1.getCountriesWithin().size(); i++) {
                    if (!(continent1.getCountriesWithin().get(i).getOccupiedBy().equals(occupant))) {
                        return;
                    }
                }
                continent1.setOwnedBy(occupant);
                return;
            }
        }
    }

    public boolean isPlayerOccupantOfGivenCountry(Player player, int countryId) {
        return player.getUsername().equals(getCountry(countryId).getOccupiedBy());
    }

    // ReinforcementPhase:
    //  1-11: 3
    // 12-14: 4
    // 15-17: 5
    // 18-20: 6
    // 21-23: 7
    // 24-26: 8
    // 27-29: 9
    // 30-32: 10
    // 33-35: 11
    // 36-38: 12
    // 39-41: 13

    // Additional units per occupied continent:
    // North America: 5
    // South America: 2
    // Europe:        5
    // Africa:        3
    // Asia:          7
    // Australia:     2

    public int getAdditionalUnitsPerContinent(Player player) {
        int additionalUnits = 0;
        for (Continent continent : continentList) {
            if (continent.getOwnedBy().equals(player.getUsername())) {
                switch (continent.getName()) {
                    case "North America", "Europe" -> additionalUnits += 5;
                    case "South America", "Australia" -> additionalUnits += 2;
                    case "Africa" -> additionalUnits += 3;
                    case "Asia" -> additionalUnits += 7;
                }
            }
        }
        return additionalUnits;
    }

    public void getReinforcementUnits(Player player) {
        int additionalUnits = getAdditionalUnitsPerContinent(player);
        int occupiedCountries = 0;
        int reinforcementUnits = 3;
        for (Country country : countryList) {
            if (player.getUsername().equals(country.getOccupiedBy())) {
                occupiedCountries++;
            }
        }
        if (occupiedCountries > 11) {
            for (int i = 0; i < (occupiedCountries - 11); i++) {
                if (i % 3 == 0) {
                    reinforcementUnits++;
                }
            }
        }
        //System.out.println("Reinforcement: " + reinforcementUnits + " Additional: " + additionalUnits);
        player.setArmies(player.getArmies() + additionalUnits + reinforcementUnits);
    }

    public void distributeArmy(Player player, int countryId, int amount) {
        countryList.get(countryId).setArmies(countryList.get(countryId).getArmies() + amount);
        player.setArmies(player.getArmies() - amount);
    }
}

