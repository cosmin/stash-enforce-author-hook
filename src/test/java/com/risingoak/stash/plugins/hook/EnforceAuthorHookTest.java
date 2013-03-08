package com.risingoak.stash.plugins.hook;

import com.atlassian.stash.history.HistoryService;
import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.repository.RefChange;
import com.atlassian.stash.repository.RefChangeType;
import com.atlassian.stash.user.Person;
import com.atlassian.stash.user.StashAuthenticationContext;
import com.atlassian.stash.user.StashUser;
import com.risingoak.stash.plugins.hook.helpers.BaseGitScmTest;
import com.risingoak.stash.plugins.hook.helpers.TestRefChange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnforceAuthorHookTest extends BaseGitScmTest {
    @Mock
    HistoryService historyService;
    @Mock
    StashAuthenticationContext stashAuthenticationContext;
    @Mock
    RefService refService;
    @Mock
    RevListService revListService;
    @Mock
    RejectedResponsePrinter rejectedResponsePrinter;
    @Mock
    StashUser stashUser;
    @Mock
    HookResponse hookResponse;
    private EnforceAuthorHook hook;


    @Before
    public void setUp() throws Exception {
        when(stashAuthenticationContext.getCurrentUser()).thenReturn(stashUser);
        hook = new EnforceAuthorHook(historyService, stashAuthenticationContext, rejectedResponsePrinter, refService, revListService);
    }

    @Test
    public void shouldBuildPushedRefsForUpdate() throws Exception {
        List<RefChange> refChanges = new ArrayList<RefChange>();
        refChanges.add(new TestRefChange("foobar", "123", "456", RefChangeType.UPDATE));

        List<String> pushedRefs = hook.getPushedRefs(refChanges);

        assertArrayEquals(new String[]{"123..456"}, pushedRefs.toArray());
    }

    @Test
    public void shouldBuildPushedRefsForAdd() throws Exception {
        List<RefChange> refChanges = new ArrayList<RefChange>();
        refChanges.add(new TestRefChange("foobar", "000", "456", RefChangeType.ADD));

        List<String> pushedRefs = hook.getPushedRefs(refChanges);

        assertArrayEquals(new String[]{"456"}, pushedRefs.toArray());
    }

    @Test
    public void shouldIgnorePushedRefsForDelete() throws Exception {
        List<RefChange> refChanges = new ArrayList<RefChange>();
        refChanges.add(new TestRefChange("foobar", "456", "000", RefChangeType.DELETE));

        List<String> pushedRefs = hook.getPushedRefs(refChanges);

        assertEquals(new ArrayList<String>(), pushedRefs);
    }

    @Test
    public void shouldPassIfNoRejections() throws Exception {
        assertTrue(hook.handleRejections(hookResponse, new HashMap<String, Person>(), stashUser));
    }

    @Test
    public void shouldReturnFalseIfThereAreRejections() throws Exception {
        HashMap<String, Person> rejectedRevs = new HashMap<String, Person>();
        rejectedRevs.put("123", mock(Person.class));
        assertFalse(hook.handleRejections(hookResponse, rejectedRevs, stashUser));
    }

    @Test
    public void shouldCallRejectedResponsePrinterWithRejections() throws Exception {
        HashMap<String, Person> rejectedRevs = new HashMap<String, Person>();
        rejectedRevs.put("123", mock(Person.class));

        hook.handleRejections(hookResponse, rejectedRevs, stashUser);

        verify(rejectedResponsePrinter).printRejectedMessage(stashUser, hookResponse, rejectedRevs);
    }

}
