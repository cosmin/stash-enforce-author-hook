package com.risingoak.stash.plugins.hook.helpers;

import com.atlassian.stash.repository.RefChange;
import com.atlassian.stash.repository.RefChangeType;

import javax.annotation.Nonnull;

public class TestRefChange implements RefChange {
    String refId;
    String fromHash;
    String toHash;
    RefChangeType type;

    public TestRefChange(String refId, String fromHash, String toHash, RefChangeType type) {
        this.refId = refId;
        this.fromHash = fromHash;
        this.toHash = toHash;
        this.type = type;
    }

    @Nonnull
    @Override
    public String getRefId() {
        return refId;
    }

    @Nonnull
    @Override
    public String getFromHash() {
        return fromHash;
    }

    @Nonnull
    @Override
    public String getToHash() {
        return toHash;
    }

    @Nonnull
    @Override
    public RefChangeType getType() {
        return type;
    }
}
