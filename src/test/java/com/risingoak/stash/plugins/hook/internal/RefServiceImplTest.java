package com.risingoak.stash.plugins.hook.internal;

import com.atlassian.stash.scm.CommandOutputHandler;
import com.atlassian.stash.scm.git.foreachref.GitForEachRefBuilder;
import com.risingoak.stash.plugins.hook.helpers.BaseGitScmTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RefServiceImplTest extends BaseGitScmTest {
    public static final String REFNAME_SHORT = "%(refname:short)";
    public static final String REFS_HEADS = "refs/heads/";

    @Mock private GitForEachRefBuilder forEachRefBuilder;
    private RefServiceImpl refService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(commandBuilder.forEachRef()).thenReturn(forEachRefBuilder);
        when(forEachRefBuilder.pattern(anyString())).thenReturn(forEachRefBuilder);
        when(forEachRefBuilder.format(anyString())).thenReturn(forEachRefBuilder);
        when(forEachRefBuilder.build(Matchers.<CommandOutputHandler<List<String>>>anyObject())).thenReturn(gitCommand);
        refService = new RefServiceImpl(gitScm);
    }

    @Test
    public void shouldReturnOutputOfCommand() throws Exception {
        List<String> expected = Arrays.asList("1", "2");
        when(gitCommand.call()).thenReturn(expected);

        List<String> actual = refService.getExistingRefs(repository);

        Assert.assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void shouldFormatAppropriately() throws Exception {
        refService.getExistingRefs(repository);
        verify(forEachRefBuilder).format(REFNAME_SHORT);
    }

    @Test
    public void shouldPatternRefsHeads() throws Exception {
        refService.getExistingRefs(repository);
        verify(forEachRefBuilder).pattern(REFS_HEADS);
    }
}
