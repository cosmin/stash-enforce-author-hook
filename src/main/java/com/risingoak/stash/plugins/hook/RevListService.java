package com.risingoak.stash.plugins.hook;

import com.atlassian.stash.repository.Repository;

import java.util.List;

public interface RevListService {
    List<String> revList(Repository repository, List<String> refs, List<String> ignoreReachableFrom);
}
