package de.hsbremen.risk.common.exceptions;

public class LoadGameWrongPlayerException extends Exception{
    public LoadGameWrongPlayerException(String file) {
        super("All the players in the " + file + ".json file have to be in the lobby in order to start");
    }
}
