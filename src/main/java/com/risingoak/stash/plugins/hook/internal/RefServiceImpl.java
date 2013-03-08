package com.risingoak.stash.plugins.hook.internal;

import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.scm.git.GitCommand;
import com.atlassian.stash.scm.git.GitScm;
import com.atlassian.stash.scm.git.GitScmCommandBuilder;
import com.risingoak.stash.plugins.hook.RefService;

import java.util.List;

public class RefServiceImpl implements RefService {
    private final GitScm gitScm;

    public RefServiceImpl(GitScm gitScm) {
        this.gitScm = gitScm;
    }

    @Override
    public List<String> getExistingRefs(Repository repository) {
        GitScmCommandBuilder builder = gitScm.getCommandBuilderFactory().builder(repository);
        GitCommand<List<String>> command = builder.forEachRef().format("%(refname:short)").pattern("refs/heads/").build(new OnePerLineCommandOutputHandler());
        return command.call();
    }
}
