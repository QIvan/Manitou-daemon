package org.manitou.daemon.application;

import java.io.Serializable;

/**
 * Created 1/29/12 @ 4:32 AM by aleksey.
 */
public enum ExitCode implements Serializable
{
    normal(0);

    private final int code;

    public int getCode()
    {
        return code;
    }

    private ExitCode(final int code)
    {
        this.code = code;
    }
}
