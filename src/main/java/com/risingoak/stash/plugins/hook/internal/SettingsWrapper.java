package com.risingoak.stash.plugins.hook.internal;

import com.atlassian.stash.setting.Settings;
import com.atlassian.stash.user.StashUser;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class SettingsWrapper {
    public static final String ENFORCE_EMAIL = "enforceEmail";
    public static final String ALLOW_USERNAME_AT = "allowUsernameAt";
    public static final String ENFORCE_NAME = "enforceName";

    private final Settings settings;

    public SettingsWrapper(Settings settings) {
        this.settings = settings;
    }

    public boolean isEnforceEmail() {
        //Boolean value = settings.getBoolean(ENFORCE_EMAIL);
        return true;
    }

    public boolean isEnforceName() {
        Boolean value = settings.getBoolean(ENFORCE_NAME);
        return value != null ? value : false;
    }

    public boolean isAllowUsernameAt() {
        Boolean value = settings.getBoolean(ALLOW_USERNAME_AT);
        return value != null ? value : false;
    }

    public List<String> getAllowedEmailAddresses(StashUser currentUser) {
        List<String> allowedEmails = new ArrayList<String>();
        allowedEmails.add(currentUser.getEmailAddress());
        if (isAllowUsernameAt() && isNotBlank(currentUser.getEmailAddress())) {
            allowedEmails.add(getAlternativeAddress(currentUser));
        }
        return allowedEmails;
    }

    public String getAlternativeAddress(StashUser currentUser) {
        return currentUser.getName() + "@" + currentUser.getEmailAddress().split("@")[1];
    }
}
