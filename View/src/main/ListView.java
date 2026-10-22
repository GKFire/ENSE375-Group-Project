package ense375.personalpasswordmanager.view;

import ense375.personalpasswordmanager.presenter.ProgramPresenter;

import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

//ListView: the main screen, shown after a successful login.

public class ListView {
    private final ProgramPresenter presenter;
    private final Scanner scanner;
    private final PrintStream out;
    private final EntryView entryView;
    private boolean inputClosed;

    //Creates a ListView that uses the real console.
    
    public ListView(ProgramPresenter presenter) {
        this(presenter, new Scanner(System.in), System.out);
    }

    // Creates a ListView with injected input and output.
   
    public ListView(ProgramPresenter presenter, Scanner scanner, PrintStream out) 
    {
        this(presenter, scanner, out, new EntryView(presenter, scanner, out));
    }

    //Creates a ListView with every collaborator injected.
    
    public ListView(ProgramPresenter presenter, Scanner scanner, PrintStream out, EntryView entryView) 
    {
        this.presenter = presenter;
        this.scanner = scanner;
        this.out = out;
        this.entryView = entryView;
    }

    //Runs the main screen until the user exits.
    
    public void listLogins() 
    {
        boolean running = true;
        while (running) 
        {
            printLogins(presenter.getLogins());
            out.println("ID = open   N = new login   0 = save and exit");

            String input = promptUserInput("Choose: ");
            if (input.equals("0") || inputClosed) 
            {
                out.println(presenter.saveToDatabase()
                        ? "Saved. Goodbye."
                        : "Could not save the database!");
                running = false;
            } 
            else if (input.equalsIgnoreCase("n")) 
            {
                entryView.createNewLogin();
            } 
            else 
            {
                openEntry(input);
            }
        }
    }

    // Prints the list of logins as "id. description" lines.
     
    private void printLogins(Map<String, String> logins) 
    {
        out.println();
        out.println("---------CREDENTIALS ---------");
        if (logins == null || logins.isEmpty()) 
        {
            out.println(" (no logins yet)");
        } 
        else 
        {
            for (Map.Entry<String, String> login : logins.entrySet()) 
            {
                out.printf(" %s. %s%n", login.getKey(), login.getValue());
            }
        }
        out.println("------------------");
    }

    // Opens the entry whose id the user typed: shows it, then offers to edit or delete it 

    private void openEntry(String input) 
    {
        int entryId;
        try 
        {
            entryId = Integer.parseInt(input);
        } 
        catch (NumberFormatException e) 
        {
            out.println("Unknown option.");
            return;
        }

        // The id must belong to an existing entry.
        Map<String, String> logins = presenter.getLogins();

        if (logins == null || !logins.containsKey(String.valueOf(entryId))) 
        {
            out.println("No login with that ID.");
            return;
        }

        entryView.showLoginEntry(entryId);

        // Pressing Enter simply goes back to the list.
        String action = promptUserInput("E = edit   D = delete   Enter = back: ");

        if (action.equalsIgnoreCase("e")) 
        {
            entryView.editExistingLogin(entryId);
        } 
        else if (action.equalsIgnoreCase("d")) 
        {
            entryView.deleteLoginEntry(entryId);
        }
    }

    // Prints a prompt and reads one line of input.

    public String promptUserInput(String prompt) 
    {
        out.print(prompt);
        out.flush();
        if (!scanner.hasNextLine()) 
        {
            inputClosed = true;
            return "";
        }
        return scanner.nextLine().trim();
    }
}
