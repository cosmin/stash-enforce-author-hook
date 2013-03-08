package com.risingoak.stash.plugins.hook.internal;

import com.risingoak.stash.plugins.hook.internal.OnePerLineCommandOutputHandler;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class OnePerLineCommandOutputHandlerTest {

    public static final String LINE_1 = "line1";
    public static final String LINE_2 = "line2";

    @Test
    public void shouldReturnAddedLines() {
        OnePerLineCommandOutputHandler handler = new OnePerLineCommandOutputHandler();
        handler.processLine(0, LINE_1);
        handler.processLine(1, LINE_2);
        List<String> output = handler.getOutput();
        assertArrayEquals(new String[]{LINE_1, LINE_2}, output.toArray());
    }
}
