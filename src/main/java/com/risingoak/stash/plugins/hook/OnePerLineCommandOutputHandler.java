package com.risingoak.stash.plugins.hook;

import com.atlassian.stash.scm.CommandOutputHandler;
import com.atlassian.utils.process.ProcessException;
import com.atlassian.utils.process.StringOutputHandler;
import com.atlassian.utils.process.Watchdog;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class OnePerLineCommandOutputHandler implements CommandOutputHandler<List<String>> {
    private StringOutputHandler outputHandler;

    public OnePerLineCommandOutputHandler() {
        outputHandler = new StringOutputHandler();
    }

    @Override
    public void process(InputStream output) throws ProcessException {
        outputHandler.process(output);
    }

    @Override
    public void complete() throws ProcessException {
        outputHandler.complete();
    }

    @Override
    public void setWatchdog(Watchdog watchdog) {
        outputHandler.setWatchdog(watchdog);
    }

    @Override
    public List<String> getOutput() {
        String output = outputHandler.getOutput();

        // trim to null
        if (output != null && output.trim().isEmpty()) {
            output = null;
        }

        if (output != null) {
            return Arrays.asList(output.split("\n"));
        } else {
            return null;
        }
    }


}
