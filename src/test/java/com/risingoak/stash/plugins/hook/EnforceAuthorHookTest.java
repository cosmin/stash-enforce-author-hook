package com.risingoak.stash.plugins.hook;

import com.atlassian.stash.commit.CommitService;
import com.atlassian.stash.hook.HookResponse;
import com.atlassian.stash.repository.RefChange;
import com.atlassian.stash.repository.RefChangeType;
import com.atlassian.stash.setting.Settings;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class EnforceAuthorHookTest extends BaseGitScmTest {
    @Mock
    CommitService commitService;
    @Mock StashAuthenticationContext stashAuthenticationContext;
    @Mock RefService refService;
    @Mock RevListService revListService;
    @Mock RejectedResponsePrinter rejectedResponsePrinter;
    @Mock StashUser stashUser;
    @Mock HookResponse hookResponse;
    @Mock Settings settings;
    private EnforceAuthorHook hook;


    @Before
    public void setUp() throws Exception {
        when(stashAuthenticationContext.getCurrentUser()).thenReturn(stashUser);
        hook = new EnforceAuthorHook(commitService, stashAuthenticationContext, rejectedResponsePrinter, refService, revListService);
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
        assertTrue(hook.handleRejections(hookResponse, new HashMap<String, Person>(), stashUser, settings));
    }

    @Test
    public void shouldReturnFalseIfThereAreRejections() throws Exception {
        HashMap<String, Person> rejectedRevs = new HashMap<String, Person>();
        rejectedRevs.put("123", mock(Person.class));
        assertFalse(hook.handleRejections(hookResponse, rejectedRevs, stashUser, settings));
    }

    @Test
    public void shouldCallRejectedResponsePrinterWithRejections() throws Exception {
        HashMap<String, Person> rejectedRevs = new HashMap<String, Person>();
        rejectedRevs.put("123", mock(Person.class));

        hook.handleRejections(hookResponse, rejectedRevs, stashUser, settings);

        verify(rejectedResponsePrinter).printRejectedMessage(stashUser, hookResponse, rejectedRevs, settings);
    }

    @Test
    public void shouldAllowCommitWhenEmailAddressMatches() throws Exception {
        Settings settings = getSettingsWith(true, false, false);
        Person author = getAuthorWithNameAndEmail("John Doe", "jdoe@example.com");
        StashUser pusher = getCurrentUserWithIdNameAndEmail("1234", "John L. Doe", "jdoe@example.com");

        assertTrue(hook.hasValidAuthor(settings, author, pusher));
    }

    @Test
    public void shouldPreventCommitWhenEmailAddressDoesNotMatch() throws Exception {
        Settings settings = getSettingsWith(true, false, false);
        Person author = getAuthorWithNameAndEmail("John Doe", "jdoe@example.com");
        StashUser pusher = getCurrentUserWithIdNameAndEmail("1234", "John L. Doe", "jdoe@bar.com");

        assertFalse(hook.hasValidAuthor(settings, author, pusher));
    }

    @Test
    public void shouldRejectIfNameDoesNotMatchButNameEnforcementRequired() throws Exception {
        Settings settings = getSettingsWith(true, true, false);
        Person author = getAuthorWithNameAndEmail("John Doe", "jdoe@example.com");
        StashUser pusher = getCurrentUserWithIdNameAndEmail("1234", "John L. Doe", "jdoe@example.com");

        assertFalse(hook.hasValidAuthor(settings, author, pusher));
    }

    @Test
    public void shouldAllowIfNameMatches() throws Exception {
        Settings settings = getSettingsWith(true, true, false);
        Person author = getAuthorWithNameAndEmail("John Doe", "jdoe@example.com");
        StashUser pusher = getCurrentUserWithIdNameAndEmail("1234", "John Doe", "jdoe@example.com");

        assertTrue(hook.hasValidAuthor(settings, author, pusher));
    }

    @Test
    public void shouldAllowUsernameAtEmailIfSPecified() throws Exception {
        Settings settings = getSettingsWith(true, false, true);
        Person author = getAuthorWithNameAndEmail("John Doe", "1234@example.com");
        StashUser pusher = getCurrentUserWithIdNameAndEmail("1234", "John L. Doe", "jdoe@example.com");

        assertTrue(hook.hasValidAuthor(settings, author, pusher));
    }

    @Test
    public void shouldFailIfNeitherEmailAddressMatches() throws Exception {
        Settings settings = getSettingsWith(true, false, true);
        Person author = getAuthorWithNameAndEmail("John Doe", "bar@example.com");
        StashUser pusher = getCurrentUserWithIdNameAndEmail("1234", "John L. Doe", "jdoe@example.com");

        assertFalse(hook.hasValidAuthor(settings, author, pusher));
    }

    private StashUser getCurrentUserWithIdNameAndEmail(String id, String name, String email) {
        StashUser stashUser = mock(StashUser.class);
        when(stashUser.getName()).thenReturn(id);
        when(stashUser.getDisplayName()).thenReturn(name);
        when(stashUser.getEmailAddress()).thenReturn(email);
        return stashUser;
    }

    private Person getAuthorWithNameAndEmail(String name, String email) {
        Person author = mock(Person.class);
        when(author.getEmailAddress()).thenReturn(email);
        when(author.getName()).thenReturn(name);
        return author;
    }

    private Settings getSettingsWith(boolean enforceEmail, boolean enforceName, boolean allowUsernameAt) {
        Settings settings = mock(Settings.class);
        when(settings.getBoolean("allowUsernameAt")).thenReturn(allowUsernameAt);
        when(settings.getBoolean("enforceEmail")).thenReturn(enforceEmail);
        when(settings.getBoolean("enforceName")).thenReturn(enforceName);
        return settings;
    }

}
