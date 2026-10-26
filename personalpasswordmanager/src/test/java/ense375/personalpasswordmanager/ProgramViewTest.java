package ense375.personalpasswordmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ense375.personalpasswordmanager.view.AuthView;
import ense375.personalpasswordmanager.view.ListView;

//Tests for ProgramView
class ProgramViewTest {

    private static final String BANNER = "PERSONAL PASSWORD MANAGER";

    private ByteArrayOutputStream output;
    private PrintStream out;
    private ProgramPresenter presenter;
    private List<String> events;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        out = new PrintStream(output);
        presenter = new ProgramPresenter();
        events = new ArrayList<>();
    }
    //Builds a ProgramView
    private ProgramView viewWhereLoginResultIs(boolean loginResult) {
        PrintStream quiet = new PrintStream(new ByteArrayOutputStream());
        AuthView authView = new AuthView(presenter, new Scanner(""), quiet) {
            @Override
            public boolean authenticate() {
                events.add("authenticate");
                return loginResult;
            }
        };
        ListView listView = new ListView(presenter, new Scanner(""), quiet) 
        {
            @Override
            public void listLogins() {
                events.add("list");
            }
        };
        return new ProgramView(presenter, authView, listView, out);
    }

    private int count(String text, String part) 
    {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }

    @Test
    void run_logsInBeforeShowingTheList() 
    {
        viewWhereLoginResultIs(true).run();
        assertEquals(List.of("authenticate", "list"), events);
    }

    @Test
    void run_doesNotShowTheListWhenLoginFails() 
    {
        viewWhereLoginResultIs(false).run();
        assertEquals(List.of("authenticate"), events);
        assertTrue(output.toString().contains("Goodbye"));
    }

    @Test
    void init_printsTheBannerOnlyOnce() 
    {
        ProgramView view = viewWhereLoginResultIs(true);
        view.init();
        view.run();
        assertEquals(1, count(output.toString(), BANNER));
    }

    @Test
    void endToEnd_loginThenExitSavesTheDatabase()
     {
        List<String> saved = new ArrayList<>();
        ProgramPresenter real = new ProgramPresenter() {
            @Override
            public boolean initDatabase(String key) {
                return key.equals("secret123");
            }

            @Override
            public boolean saveToDatabase() {
                saved.add("saved");
                return true;
            }
        };

        new ProgramView(real, new Scanner("secret123\n0\n"), out).run();
        assertEquals(List.of("saved"), saved);
    }
}
