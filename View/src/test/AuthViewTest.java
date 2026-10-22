package ense375.personalpasswordmanager.view;

import ense375.personalpasswordmanager.presenter.ProgramPresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Test for AuthView
class AuthViewTest {

    private ByteArrayOutputStream output;
    private PrintStream out;
    private List<String> keysTried;
    private ProgramPresenter presenter;

    @BeforeEach //Before each command 
    void setUp() 
    {
        output = new ByteArrayOutputStream();
        out = new PrintStream(output);
        keysTried = new ArrayList<>();
        presenter = new ProgramPresenter() 
        {
            @Override
            public boolean initDatabase(String key) 
            {
                keysTried.add(key);
                return key.equals("secret123");
            }
        };
    }

    //Builds an AuthView that reads the given text 
    private AuthView viewWithInput(String input) 
    {
        return new AuthView(presenter, new Scanner(input), out);
    }

    @Test
    void authenticate_succeedsWithCorrectPassword() 
    {
        assertTrue(viewWithInput("secret123\n").authenticate());
        assertEquals(List.of("secret123"), keysTried);
    }

    @Test
    void authenticate_succeedsAfterOneWrongAttempt() 
    {
        assertTrue(viewWithInput("wrong\nsecret123\n").authenticate());
        assertEquals(List.of("wrong", "secret123"), keysTried);
    }

    @Test
    void authenticate_givesUpAfterThreeWrongAttempts()
     {
        assertFalse(viewWithInput("a\nb\nc\nd\n").authenticate());
        assertEquals(List.of("a", "b", "c"), keysTried);
        assertTrue(output.toString().contains("Too many failed attempts"));
    }

    @Test
    void authenticate_neverSendsAnEmptyPassword() 
    {
        assertFalse(viewWithInput("\n\n\n").authenticate());
        assertTrue(keysTried.isEmpty());
    }

    @Test
    void authenticate_endsCleanlyWhenInputRunsOut() 
    {
        assertFalse(viewWithInput("").authenticate());
    }

    @Test
    void promptUserInput_returnsTrimmedLine() 
    {
        assertEquals("hello", viewWithInput("  hello  \n").promptUserInput("> "));
    }
}
