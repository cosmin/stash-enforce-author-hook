package com.risingoak.stash.plugins.hook;

import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.user.Person;
import com.atlassian.stash.user.StashUser;

import java.util.Map;

public interface RejectedResponsePrinter {
    void printRejectedMessage(StashUser currentUser, HookResponse hookResponse, Map<String, Person> rejectedRevs);
}
