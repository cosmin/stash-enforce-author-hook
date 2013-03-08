package com.risingoak.stash.plugins.hook;

import com.atlassian.stash.repository.Repository;

import java.util.List;

public interface RefService {
    List<String> getExistingRefs(Repository repository);
}
