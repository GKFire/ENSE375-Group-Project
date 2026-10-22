package ense375.personalpasswordmanager.view;

import ense375.personalpasswordmanager.model.LoginEntry;
import ense375.personalpasswordmanager.model.TextLogin;
import ense375.personalpasswordmanager.presenter.ProgramPresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


// Tests for Entry View to create, show, edit and delete one login
class EntryViewTest {
    
    // A presenter that records what the view asked for and returns whatever
    private static class RecordingPresenter extends ProgramPresenter 
    {
        // what the test sets
        LoginEntry entryToReturn;                
        Map<String, String> loginToShow;        
        boolean editResult = true;
        boolean deleteResult = true;

        // what the view asked for
        String textLogin;                       
        String totpLogin;                         
        Integer editedId;
        Map<String, String> editChanges;
        Integer deletedId;

        @Override
        public LoginEntry createNewTextLogin(String username, String password, String website) 
        {
            textLogin = username + "|" + password + "|" + website;
            return entryToReturn;
        }

        @Override
        public LoginEntry createNewTOTPLogin(String username, String secretKey, String website, List<String> recoveryKeys) 
        {
            totpLogin = username + "|" + secretKey + "|" + website + "|" + recoveryKeys;
            return entryToReturn;
        }

        @Override
        public String getOneTimePassword(LoginEntry entry) 
        {
            return "123456";
        }

        @Override
        public Map<String, String> getLogin(int entryId) 
        {
            return loginToShow;
        }

        @Override
        public boolean editExistingLogin(int entryId, Map<String, String> changes) 
        {
            editedId = entryId;
            editChanges = changes;
            return editResult;
        }

        @Override
        public boolean deleteLogin(int entryId) 
        {
            deletedId = entryId;
            return deleteResult;
        }
    }

    private ByteArrayOutputStream output;
    private PrintStream out;
    private RecordingPresenter presenter;

    @BeforeEach
    void setUp() 
    {
        output = new ByteArrayOutputStream();
        out = new PrintStream(output);
        presenter = new RecordingPresenter();
        presenter.entryToReturn = new TextLogin("alice", "example.com", "hunter2");
    }

    // Builds an EntryView that reads the given text 
    private EntryView viewWithInput(String input)
    {
        return new EntryView(presenter, new Scanner(input), out);
    }

    private String printed() 
    {
        return output.toString();
    }

    private Map<String, String> loginWithSecret(String key, String value) 
    {
        Map<String, String> login = new LinkedHashMap<>();
        login.put("username", "alice");
        login.put(key, value);
        return login;
    }

    //To test Create new login

    @Test
    void createNewLogin_textLoginIsSentToPresenter() 
    {
        viewWithInput("1\nalice\nexample.com\nhunter2\n").createNewLogin();

        assertEquals("alice|hunter2|example.com", presenter.textLogin);
        assertTrue(printed().contains("Login created"));
    }

    @Test
    void createNewLogin_totpLoginParsesRecoveryKeysAndShowsCode() 
    {
        viewWithInput("2\nalice\nexample.com\nJBSWY3DPEHPK3PXP\nr1, r2\n").createNewLogin();

        assertEquals("alice|JBSWY3DPEHPK3PXP|example.com|[r1, r2]", presenter.totpLogin);
        assertTrue(printed().contains("123456"));
    }

    @Test
    void createNewLogin_missingFieldsAreRejected() 
    {
        viewWithInput("1\n\nexample.com\n").createNewLogin();   // no username
        viewWithInput("1\nalice\nexample.com\n\n").createNewLogin(); // no password

        assertNull(presenter.textLogin);
    }

    @Test
    void createNewLogin_reportsFailureWhenPresenterReturnsNull() 
    {
        presenter.entryToReturn = null;

        viewWithInput("1\nalice\nexample.com\nhunter2\n").createNewLogin();
        assertTrue(printed().contains("Could not create"));
    }

    //To show the login Entry

    @Test
    void showLoginEntry_hidesPasswordUnlessRevealed() 
    {
        presenter.loginToShow = loginWithSecret("password", "hunter2");

        viewWithInput("n\n").showLoginEntry(0);
        assertTrue(printed().contains("********"));
        assertFalse(printed().contains("hunter2"));
    }

    @Test
    void showLoginEntry_showsPasswordWhenRevealed()
    {
        presenter.loginToShow = loginWithSecret("password", "hunter2");

        viewWithInput("y\n").showLoginEntry(0);
        assertTrue(printed().contains("hunter2"));
    }

    @Test
    void showLoginEntry_handlesMissingEntry() 
    {
        presenter.loginToShow = null;

        viewWithInput("").showLoginEntry(5);
        assertTrue(printed().contains("not found"));
    }
 
    //To edit the existing Login
    @Test
    void editExistingLogin_sendsOnlyTheChangedFields() 
    {
        assertTrue(viewWithInput("bob\n\n\n").editExistingLogin(3));

        assertEquals(3, presenter.editedId);
        assertEquals(Map.of("username", "bob"), presenter.editChanges);
    }

    @Test
    void editExistingLogin_withNoChangesDoesNotCallPresenter() 
    {
        assertFalse(viewWithInput("\n\n\n").editExistingLogin(3));
        assertNull(presenter.editedId);
    }

    //To test delete login entry 

    @Test
    void deleteLoginEntry_deletesOnlyWhenConfirmed() 
    {
        assertFalse(viewWithInput("no\n").deleteLoginEntry(2));
        assertNull(presenter.deletedId);

        assertTrue(viewWithInput("yes\n").deleteLoginEntry(2));
        assertEquals(2, presenter.deletedId);
    }
}
