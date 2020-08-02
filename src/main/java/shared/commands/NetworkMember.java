package shared.commands;

import shared.commands.types.CommandType;

public class NetworkMember <T extends CommandType> {
    protected CommandRunner<T> runner;
    protected CommandParser<T> parser;
    protected NetworkMember<?> target;

    public NetworkMember() {}

    public NetworkMember(NetworkMember<?> target) {
        this.target = target;
    }

    public boolean receive(String message) {
        return runner.run(parser.parse(message));
    }

    public boolean request(Command<? extends CommandType> command) {
        return target.receive(command.toString());
    }
}
