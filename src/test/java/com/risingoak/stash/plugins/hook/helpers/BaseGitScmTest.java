package com.risingoak.stash.plugins.hook.helpers;

import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.scm.git.GitCommand;
import com.atlassian.stash.scm.git.GitCommandBuilderFactory;
import com.atlassian.stash.scm.git.GitScm;
import com.atlassian.stash.scm.git.GitScmCommandBuilder;
import org.junit.Before;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.when;

public class BaseGitScmTest {
    @Mock protected GitScm gitScm;
    @Mock protected Repository repository;
    @Mock protected GitScmCommandBuilder commandBuilder;
    @Mock protected GitCommandBuilderFactory commandBuilderFactory;
    @Mock protected GitCommand<List<String>> gitCommand;

    @Before
    public void setUp() throws Exception {
        when(gitScm.getCommandBuilderFactory()).thenReturn(commandBuilderFactory);
        when(commandBuilderFactory.builder(repository)).thenReturn(commandBuilder);
    }

}
