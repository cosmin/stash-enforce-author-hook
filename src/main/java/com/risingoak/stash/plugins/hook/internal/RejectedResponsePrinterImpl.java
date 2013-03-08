package com.risingoak.stash.plugins.hook.internal;

import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.setting.Settings;
import com.atlassian.stash.user.Person;
import com.atlassian.stash.user.StashUser;
import com.risingoak.stash.plugins.hook.RejectedResponsePrinter;

import java.util.Map;

public class RejectedResponsePrinterImpl implements RejectedResponsePrinter {
    public RejectedResponsePrinterImpl() {
    }

    @Override
    public void printRejectedMessage(StashUser currentUser, HookResponse hookResponse, Map<String, Person> rejectedRevs, Settings settings) {
        hookResponse.err().println();
        SettingsWrapper settingsWrapper = new SettingsWrapper(settings);

        printBanner(hookResponse);
        printPushingAsInfo(currentUser, hookResponse, settingsWrapper);
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

    void printPushingAsInfo(StashUser currentUser, HookResponse hookResponse, SettingsWrapper settingsWrapper) {
        if (settingsWrapper.isEnforceName()) {
            hookResponse.err().format("  required name: %s\n", currentUser.getDisplayName());
        }
        if (settingsWrapper.isEnforceEmail()) {
            hookResponse.err().format(" required email: %s\n", currentUser.getEmailAddress());
        }
        if (settingsWrapper.isAllowUsernameAt()) {
            String alternativeAddress = settingsWrapper.getAlternativeAddress(currentUser);
            if (!currentUser.getEmailAddress().equalsIgnoreCase(alternativeAddress)) {
                hookResponse.err().format("alternate email: %s\n", alternativeAddress);
            }
        }
        hookResponse.err().println();
    }


    void printCommitListHeader(HookResponse hookResponse) {
        hookResponse.err().println("The following commits do not match:");
        hookResponse.err().println();
    }

    void printIndividualCommit(HookResponse hookResponse, String refId, Person author) {
        hookResponse.err().format("%s - %s <%s>\n", refId, author.getName(), author.getEmailAddress());
    }
}