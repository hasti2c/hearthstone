package commands;

import shared.*;
import commands.types.*;

public class NetworkMember <T extends CommandType> {
    protected Controller<T> controller;
    protected CommandRunner<T> runner;
    protected CommandParser<T> parser;
    protected NetworkMember<?> target;

    public NetworkMember(Controller<T> controller) {
        this.controller = controller;
    }

    public NetworkMember(Controller<T> controller, NetworkMember<?> target) {
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
