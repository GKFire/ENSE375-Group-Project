package ense375.personalpasswordmanager.view;

import ense375.personalpasswordmanager.presenter.ProgramPresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Tests for ListView
class ListViewTest {

    //Entry view record 
    private static class RecordingEntryView extends EntryView {
        final List<String> calls = new ArrayList<>();

        RecordingEntryView(ProgramPresenter presenter) {
            super(presenter, new Scanner(""), new PrintStream(new ByteArrayOutputStream()));
        }

        @Override
        public void createNewLogin() {
            calls.add("create");
        }

        @Override
        public void showLoginEntry(int entryId) {
            calls.add("show:" + entryId);
        }

        @Override
        public boolean editExistingLogin(int entryId) {
            calls.add("edit:" + entryId);
            return true;
        }

        @Override
        public boolean deleteLoginEntry(int entryId) {
            calls.add("delete:" + entryId);
            return true;
        }
    }

    private ByteArrayOutputStream output;
    private PrintStream out;
    private Map<String, String> logins;
    private boolean saveResult;
    private int saveCalls;
    private RecordingEntryView entryView;
    private ProgramPresenter presenter;

    @BeforeEach
    void setUp() 
    {
        output = new ByteArrayOutputStream();
        out = new PrintStream(output);
        logins = new LinkedHashMap<>();
        logins.put("1", "alice - example.com");
        saveResult = true;
        saveCalls = 0;
        presenter = new ProgramPresenter() 
        {
            @Override
            public Map<String, String> getLogins() 
            {
                return logins;
            }

            @Override
            public boolean saveToDatabase() 
            {
                saveCalls++;
                return saveResult;
            }
        };
        entryView = new RecordingEntryView(presenter);
    }

    //Builds a Listview that reads the value
    private ListView viewWithInput(String input) 
    {
        return new ListView(presenter, new Scanner(input), out, entryView);
    }

    private String printed() 
    {
        return output.toString();
    }

    @Test
    void listLogins_showsEveryLoginWithItsId() 
    {
        viewWithInput("0\n").listLogins();
        assertTrue(printed().contains("1.Steve - example.com"));
    }

    @Test

    void listLogins_zeroSavesAndExits() 
    {
        viewWithInput("0\n").listLogins();
        assertEquals(1, saveCalls);
        assertTrue(printed().contains("Saved"));
    }

    @Test

    void listLogins_reportsSaveFailure() 
    {
        saveResult = false;
        viewWithInput("0\n").listLogins();
        assertTrue(printed().contains("Could not save the report"));
    }

    @Test
    //to list logins also to edit or delete logins
    void listLogins_openThenEditOrDeleteUsesThatId() 
    {
        viewWithInput("1\ne\n1\nd\n1\n\n0\n").listLogins();
        assertEquals(List.of("show:1", "edit:1", "show:1", "delete:1", "show:1"), entryView.calls);
    }

    @Test
    void listLogins_unknownIdOrTextOpensNothing() 
    {
        viewWithInput("99\nabc\n0\n").listLogins()
        assertTrue(entryView.calls.isEmpty());
        assertTrue(printed().contains("No login with that ID"));
        assertTrue(printed().contains("Unknown option"));
    }

    @Test
    void listLogins_nOpensTheCreateScreen() 
    {
        viewWithInput("n\n0\n").listLogins();
        assertEquals(List.of("create"), entryView.calls);
    }

    @Test
    void listLogins_savesAndExitsWhenInputRunsOut() 
    {
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> viewWithInput("").listLogins());
        assertEquals(1, saveCalls);
    }
}
