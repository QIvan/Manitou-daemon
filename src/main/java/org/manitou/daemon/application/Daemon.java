package org.manitou.daemon.application;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.manitou.daemon.application.cli.CliParameters;

import java.io.Serializable;

/**
 * this is the main starter class for daemon
 *
 * so how will it work
 * 1. dbms -- we can have embeddable OR service instance
 *      1-a. sqlite: each user should have an instance of DB file --
 *                          so it'll be located @ ${user.home}
 *      1-b. postgresql: I. parameter string with connection options
 *                          should be given with cli -- or
 *                       II. configuration file @ ${user.home} --
 *                          which seems like more secure to me
 * 2. mail server OR list of servers
 *          cli arguments
 *      OR
 *          configuration file
 * 3. there should be a limitation of one instance of daemon per user --
 *      so should be some lock file @ ${user.home}
 * 4. Signals
 *      4-a: send mail
 *      4-b: check remote mail
 *      4-c: reread configuration
 *      4-d: we've got new items @ db
 * 5. Exit codes
 *      0 -- normal exit
 *          etc
 * @see org.manitou.daemon.application.ExitCode
 */
public class Daemon implements Serializable
{
    private static Daemon ourInstance = new Daemon();

    public static Daemon getInstance()
    {
        return ourInstance;
    }

    public final static String PROGRAM_NAME = "maintoud";

    private Daemon()
    {
    }

    public static void main(String[] args)
    {
        final CliParameters parameters = new CliParameters();
        JCommander jCommander = null;
        try
        {
            jCommander = new JCommander(parameters, args);
        }
        catch (ParameterException e)
        {
            helpUser(new JCommander(parameters));
        }

        if (parameters.isNeedHelp())
        {
            helpUser(jCommander);
        }

        // looks like we will just delegating logic for now
        net.Daemon.main(args);

        // todo init application
        // todo process lifecycle
    }

    private static void helpUser(final JCommander jCommander)
    {
        jCommander.setProgramName(PROGRAM_NAME);
        jCommander.usage();
        Daemon.exit(ExitCode.normal);
    }

    public static void exit(ExitCode code)
    {
        System.exit(code.getCode());
    }
}
