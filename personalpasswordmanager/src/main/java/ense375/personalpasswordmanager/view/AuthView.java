package ense375.personalpasswordmanager.view;

import java.io.PrintStream;
import java.util.Scanner;

import ense375.personalpasswordmanager.ProgramPresenter;

//AuthView: the login screen.

public class AuthView {

    static final int MAX_ATTEMPTS = 3; //Maximum no of attempts 
    private final ProgramPresenter presenter;
    private final Scanner scanner;
    private final PrintStream out;

    //Creates an AuthView that uses the real console.
  
    public AuthView(ProgramPresenter presenter) 
    {
        this(presenter, new Scanner(System.in), System.out);
    }

    // Creates an AuthView with injected input and output.

    public AuthView(ProgramPresenter presenter, Scanner scanner, PrintStream out) 
    {
        this.presenter = presenter;
        this.scanner = scanner;
        this.out = out;
    }

    //Asks for the master password until it works or the attempts run out.
    
    public boolean authenticate() 
    {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) 
        {
            String key = promptUserInput("Master password: ");

            if (key.isEmpty()) 
            {
                out.println("The master password field cannot be empty.");
                continue;
            }

            if (presenter.initDatabase(key) && presenter.readDatabase()) {
                out.println("Database ready.");
                return true;
            }
            out.printf("Could not open the database (%d/%d).%n", attempt, MAX_ATTEMPTS);
        }
        out.println("Too many failed attempts.");
        return false;
    }

    //Prints a prompt and reads one line of input.
    
    public String promptUserInput(String prompt) 
    {
        out.print(prompt);
        out.flush(); 
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }
}
