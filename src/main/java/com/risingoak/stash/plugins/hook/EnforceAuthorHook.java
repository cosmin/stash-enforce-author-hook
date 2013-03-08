package com.risingoak.stash.plugins.hook;


import com.atlassian.stash.content.Changeset;
import com.atlassian.stash.history.HistoryService;
import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.hook.repository.PreReceiveRepositoryHook;
import com.atlassian.stash.hook.repository.RepositoryHookContext;
import com.atlassian.stash.repository.RefChange;
import com.atlassian.stash.repository.RefChangeType;
import com.atlassian.stash.setting.Settings;
import com.atlassian.stash.user.Person;
import com.atlassian.stash.user.StashAuthenticationContext;
import com.atlassian.stash.user.StashUser;

import javax.annotation.Nonnull;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class EnforceAuthorHook implements PreReceiveRepositoryHook {
    private final HistoryService historyService;
    private final StashAuthenticationContext stashAuthenticationContext;
    private final RejectedResponsePrinter rejectedResponsePrinter;
    private final RefService refService;
    private final RevListService revListService;

    public EnforceAuthorHook(HistoryService historyService, StashAuthenticationContext stashAuthenticationContext, RejectedResponsePrinter rejectedResponsePrinter, RefService refService, RevListService revListService) {
        this.historyService = historyService;
        this.stashAuthenticationContext = stashAuthenticationContext;
        this.rejectedResponsePrinter = rejectedResponsePrinter;
        this.refService = refService;
        this.revListService = revListService;
    }

    @Override
    public boolean onReceive(@Nonnull RepositoryHookContext context, @Nonnull Collection<RefChange> refChanges, @Nonnull HookResponse hookResponse) {
        Map<String, Person> rejectedRevs = new HashMap<String, Person>();
        StashUser currentUser = stashAuthenticationContext.getCurrentUser();

        List<String> pushedRefs = getPushedRefs(refChanges);
        if (!pushedRefs.isEmpty()) {
            List<String> ignoreRefs = refService.getExistingRefs(context.getRepository());
            List<String> brandNewRevs = revListService.revList(context.getRepository(), pushedRefs, ignoreRefs);

            for (String refId : brandNewRevs) {
                Changeset changeset = historyService.getChangeset(context.getRepository(), refId);
                Person author = changeset.getAuthor();
                if (!hasValidAuthor(context.getSettings(), author, currentUser)) {
                    rejectedRevs.put(refId, author);
                }
            }
        }

        return handleRejections(hookResponse, rejectedRevs, currentUser);
    }

    boolean handleRejections(HookResponse hookResponse, Map<String, Person> rejectedRevs, StashUser currentUser) {
        if (!rejectedRevs.isEmpty()) {
            rejectedResponsePrinter.printRejectedMessage(currentUser, hookResponse, rejectedRevs);
            return false;
        } else {
            return true;
        }
    }

    boolean hasValidAuthor(Settings settings, Person author, StashUser currentUser) {
        Boolean allowUsernameAt = settings.getBoolean("allowUsernameAt");
        Boolean enforceEmail = settings.getBoolean("enforceEmail") || true;
        Boolean enforceName = settings.getBoolean("enforceName");

        boolean valid = true;
        if (enforceEmail) {
            valid = valid && author.getEmailAddress().equalsIgnoreCase(currentUser.getEmailAddress());
        }
        if (enforceName) {
            valid = valid && author.getName().equalsIgnoreCase(currentUser.getName());
        }

        return valid;
    }

    List<String> getPushedRefs(Collection<RefChange> refChanges) {
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
