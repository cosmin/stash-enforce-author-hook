package com.risingoak.stash.plugins.hook;


import com.atlassian.stash.content.Changeset;
import com.atlassian.stash.history.HistoryService;
import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.hook.repository.PreReceiveRepositoryHook;
import com.atlassian.stash.hook.repository.RepositoryHookContext;
import com.atlassian.stash.repository.RefChange;
import com.atlassian.stash.repository.RefChangeType;
import com.atlassian.stash.scm.git.GitCommand;
import com.atlassian.stash.scm.git.GitScm;
import com.atlassian.stash.scm.git.GitScmCommandBuilder;
import com.atlassian.stash.user.Person;
import com.atlassian.stash.user.StashAuthenticationContext;
import com.atlassian.stash.user.StashUser;

import javax.annotation.Nonnull;
import java.util.*;

public class EnforceAuthorHook implements PreReceiveRepositoryHook {
    private final GitScm gitScm;
    private final HistoryService historyService;
    private final StashAuthenticationContext stashAuthenticationContext;

    public EnforceAuthorHook(GitScm gitScm, HistoryService historyService, StashAuthenticationContext stashAuthenticationContext) {
        this.gitScm = gitScm;
        this.historyService = historyService;
        this.stashAuthenticationContext = stashAuthenticationContext;
    }

    @Override
    public boolean onReceive(@Nonnull RepositoryHookContext context, @Nonnull Collection<RefChange> refChanges, @Nonnull HookResponse hookResponse) {
        GitScmCommandBuilder builder = gitScm.getCommandBuilderFactory().builder(context.getRepository());
        List<String> pushedRefs = getPushedRefs(refChanges);

        Map<String, Person> rejectedRevs = new HashMap<String, Person>();
        StashUser currentUser = stashAuthenticationContext.getCurrentUser();


        if (!pushedRefs.isEmpty()) {
            List<String> ignoreRefs = getExistingBranches(builder);
            List<String> brandNewRevs = getAllPushedRevisions(builder, pushedRefs, ignoreRefs);

            for (String refId : brandNewRevs) {
                Changeset changeset = historyService.getChangeset(context.getRepository(), refId);
                Person author = changeset.getAuthor();
                if (author.getEmailAddress().equalsIgnoreCase(currentUser.getEmailAddress())) {
                } else {
                    rejectedRevs.put(refId, author);
                }
            }
        }

        if (!rejectedRevs.isEmpty()) {
            displayErrorMessage(currentUser, hookResponse, rejectedRevs);
            return false;
        } else {
            return true;
        }

    }

    private void displayErrorMessage(StashUser currentUser, HookResponse hookResponse, Map<String, Person> rejectedRevs) {
        hookResponse.err().println();
        hookResponse.err().println("-----------------------------------------------------");
        hookResponse.err().println("REJECTED: you can only push commits you have authored");
        hookResponse.err().println("-----------------------------------------------------");
        hookResponse.err().format("Pushing as: %s <%s>\n", currentUser.getDisplayName(), currentUser.getEmailAddress());
        hookResponse.err().println("The following commits do not match your current information:");
        hookResponse.err().println();
        for (String refId : rejectedRevs.keySet()) {
            Person author = rejectedRevs.get(refId);
            hookResponse.err().format("%s - %s <%s>\n", refId, author.getName(), author.getEmailAddress());
        }
        hookResponse.err().println();
    }

    private List<String> getAllPushedRevisions(GitScmCommandBuilder builder, List<String> pushedRefs, List<String> ignoreRefs) {
        List<String> revListArgs = new ArrayList<String>();
        revListArgs.addAll(pushedRefs);
        revListArgs.addAll(ignoreRefs);
        GitCommand<List<String>> revList = builder.revList().revs(revListArgs).build(new OnePerLineCommandOutputHandler());
        return revList.call();
    }

    private List<String> getExistingBranches(GitScmCommandBuilder builder) {
        GitCommand<List<String>> command = builder.forEachRef().format("^%(refname:short)").pattern("refs/heads/").build(new OnePerLineCommandOutputHandler());
        return command.call();
    }

    private List<String> getPushedRefs(Collection<RefChange> refChanges) {
        List<String> toList = new ArrayList<String>();

        for (RefChange refChange : refChanges) {
            if (refChange.getType().equals(RefChangeType.UPDATE)) {
                toList.add(refChange.getFromHash() + ".." + refChange.getToHash());
            } else if (refChange.getType().equals(RefChangeType.ADD)) {
                toList.add(refChange.getToHash());
            }
        }
        return toList;
    }
}
