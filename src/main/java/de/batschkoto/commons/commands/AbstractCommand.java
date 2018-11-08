package de.batschkoto.commons.commands;

import lombok.Getter;

@Getter
public abstract class AbstractCommand {

    private final String command;

    public AbstractCommand( String command ) {
        this.command = command.toLowerCase();
    }

    public abstract void execute( String[] args );

}
