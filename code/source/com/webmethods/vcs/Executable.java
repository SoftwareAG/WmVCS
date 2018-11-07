package com.webmethods.vcs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Wraps a command line executable.
 */
public class Executable extends Command
{
    private List _command;
    
    public Executable(List command, List input)
    {
        super(input);
        _command = command;
    }

    public Executable(List command)
    {
        this(command, null);
    }

    public Executable(String fileName, List args, List input)
    {
        super(input);

        _command = new ArrayList();
        _command.add(fileName);
        if (args != null) {
            _command.addAll(args);
        }
    }

    public Executable(String fileName, String arg)
    {
        this(fileName, Arrays.asList(new String[] { arg }));
    }

    public Executable(String fileName, List args)
    {
        this(fileName, args, null);
    }
    
    public Executable(String fileName)
    {
        this(fileName, null, null);
    }
    
    public List getCommand()
    {
        return _command;
    }

}
