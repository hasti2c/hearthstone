package server;

import client.*;
import com.google.gson.stream.JsonWriter;
import commands.*;
import commands.types.*;
import elements.heros.Deck;
import elements.heros.Hero;
import shared.GameData;
import system.Configable;
import system.Configor;
import system.player.Player;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Server extends NetworkMember<ServerCommandType> {
    public Server() {
        super(ServerController.getInstance());
    }

    public void setClient(Client client) {
        this.target = client;
        runner = new ServerCommandRunner((ServerController) controller, client);
        parser = new CommandParser<>(controller, ServerCommandType.class);
    }
}
