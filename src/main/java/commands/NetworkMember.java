package commands;

import commands.types.*;
import shared.Controller;

import java.util.ArrayList;

public abstract class NetworkMember <T extends CommandType> {
    protected Controller<T> controller;
    protected CommandRunner<T> runner;
    protected CommandParser<T> parser;
    protected NetworkMember<?> target;

    protected NetworkMember(Controller<T> controller) {
        this.controller = controller;
    }

    protected NetworkMember(NetworkMember<?> target) {
        this.target = target;
    }

    protected NetworkMember(Controller<T> controller, NetworkMember<?> target) {
        this.controller = controller;
        this.target = target;
    }

    public boolean receive(String message) {
        return runner.run(parser.parse(message));
    }

    public boolean request(Command<? extends CommandType> command) {
        return target.receive(command.toString());
    }

    public NetworkMember<?> getTarget() {
        return target;
    }

    public Controller<T> getController() {
        return controller;
    }
}
