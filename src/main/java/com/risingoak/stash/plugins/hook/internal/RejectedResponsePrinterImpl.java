package com.risingoak.stash.plugins.hook.internal;

import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.user.Person;
import com.atlassian.stash.user.StashUser;
import com.risingoak.stash.plugins.hook.RejectedResponsePrinter;

import java.util.Map;

public class RejectedResponsePrinterImpl implements RejectedResponsePrinter {
    public RejectedResponsePrinterImpl() {
    }

    @Override
    public void printRejectedMessage(StashUser currentUser, HookResponse hookResponse, Map<String, Person> rejectedRevs) {
        hookResponse.err().println();
        printBanner(hookResponse);
        printPushingAsInfo(currentUser, hookResponse);
        printCommitListHeader(hookResponse);
        for (String refId : rejectedRevs.keySet()) {
            Person author = rejectedRevs.get(refId);
            printIndividualCommit(hookResponse, refId, author);
        }
        hookResponse.err().println();
    }

    void printBanner(HookResponse hookResponse) {
        hookResponse.err().println("-----------------------------------------------------");
        hookResponse.err().println("REJECTED: you can only push commits you have authored");
        hookResponse.err().println("-----------------------------------------------------");
    }

    void printPushingAsInfo(StashUser currentUser, HookResponse hookResponse) {
        hookResponse.err().format("Pushing as: %s <%s>\n", currentUser.getDisplayName(), currentUser.getEmailAddress());
    }


    void printCommitListHeader(HookResponse hookResponse) {
        hookResponse.err().println("The following commits do not match your current information:");
        hookResponse.err().println();
    }

    void printIndividualCommit(HookResponse hookResponse, String refId, Person author) {
        hookResponse.err().format("%s - %s <%s>\n", refId, author.getName(), author.getEmailAddress());
    }
}