package org.manitou.daemon.application.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.Serializable;

/**
 * Created 1/29/12 @ 3:37 AM by aleksey.
 */
@Parameters(resourceBundle = "org.manitou.daemon.application.cli.CliParameters")
public class CliParameters implements Serializable
{
    @Parameter(names = {"-c", "--config"}, descriptionKey = "config")
    private String configurationFile;

    @Parameter(names = {"-u", "--usage", "--help"}, descriptionKey = "usage")
    private boolean needHelp;

    public String getConfigurationFile()
    {
        return configurationFile;
    }

    public boolean isNeedHelp()
    {
        return needHelp;
    }
}
