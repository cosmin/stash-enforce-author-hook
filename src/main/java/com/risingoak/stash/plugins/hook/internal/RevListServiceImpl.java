package com.risingoak.stash.plugins.hook.internal;

import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.scm.git.GitCommand;
import com.atlassian.stash.scm.git.GitScm;
import com.atlassian.stash.scm.git.GitScmCommandBuilder;
import com.risingoak.stash.plugins.hook.RevListService;

import java.util.ArrayList;
import java.util.List;

public class RevListServiceImpl implements RevListService {
    private final GitScm gitScm;

    public RevListServiceImpl(GitScm gitScm) {
        this.gitScm = gitScm;
    }

    @Override
    public List<String> revList(Repository repository, List<String> refs, List<String> ignoreReachableFrom) {
        GitScmCommandBuilder builder = gitScm.getCommandBuilderFactory().builder(repository);
        List<String> revListArgs = new ArrayList<String>();
        revListArgs.addAll(refs);
        for (String ignore : ignoreReachableFrom) {
            revListArgs.add("^" + ignore);
        }
        GitCommand<List<String>> revList = builder.revList().revs(revListArgs).build(new OnePerLineCommandOutputHandler());
        return revList.call();
    }
}
