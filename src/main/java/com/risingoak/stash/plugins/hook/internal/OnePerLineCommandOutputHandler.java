package com.risingoak.stash.plugins.hook.internal;

import com.atlassian.stash.scm.CommandOutputHandler;
import com.atlassian.utils.process.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnePerLineCommandOutputHandler extends LineOutputHandler implements CommandOutputHandler<List<String>> {
    List<String> output = new ArrayList<String>();

    @Override
    public List<String> getOutput() {
        return output;
    }

    @Override
    protected void processLine(int i, String s) {
        output.add(s);
    }
}
