package com.risingoak.stash.plugins.hook.internal;

import com.atlassian.stash.scm.CommandOutputHandler;
import com.atlassian.stash.scm.git.revlist.GitRevListBuilder;
import com.risingoak.stash.plugins.hook.helpers.BaseGitScmTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RevListServiceImplTest extends BaseGitScmTest {
    @Mock private GitRevListBuilder gitRevListBuilder;
    private RevListServiceImpl revListService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(commandBuilder.revList()).thenReturn(gitRevListBuilder);
        when(gitRevListBuilder.build(Matchers.<CommandOutputHandler<List<String>>>anyObject())).thenReturn(gitCommand);
        when(gitRevListBuilder.revs(anyList())).thenReturn(gitRevListBuilder);
        revListService = new RevListServiceImpl(gitScm);
    }

    @Test
    public void shouldReturnOutputOfCommand() throws Exception {
        List<String> expected = Arrays.asList("1", "2");
        when(gitCommand.call()).thenReturn(expected);

        List<String> result = revListService.revList(repository, new ArrayList<String>(), new ArrayList<String>());

        Assert.assertArrayEquals(expected.toArray(), result.toArray());
    }

    @Test
    public void shouldPassAlongRefsToIgnore() throws Exception {
        revListService.revList(repository, Arrays.asList("startHere"), Arrays.asList("ignoreMe", "anotherIgnore"));
        verify(gitRevListBuilder).revs(eq(Arrays.asList("startHere", "^ignoreMe", "^anotherIgnore")));
    }
}
