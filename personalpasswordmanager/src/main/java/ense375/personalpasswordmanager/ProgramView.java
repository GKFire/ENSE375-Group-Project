package ense375.personalpasswordmanager;

import java.io.PrintStream;
import java.util.Scanner;

import ense375.personalpasswordmanager.view.AuthView;
import ense375.personalpasswordmanager.view.ListView;

//ProgramView: the top of the view layer.

public class ProgramView {
    private final ProgramPresenter presenter;
    private final ListView listview;
    private final AuthView authview;
    private final PrintStream out;
    private boolean initialized;

    //Creates a ProgramView with a brand-new presenter and the real console
    public ProgramView() 
    {
        this(new ProgramPresenter());
    }

    //Creates a ProgramView that uses the real console.
   
    public ProgramView(ProgramPresenter presenter) 
    {
        this(presenter, new Scanner(System.in), System.out);
    }

    // Creates a ProgramView whose screens share one input source and one
  
    public ProgramView(ProgramPresenter presenter, Scanner scanner, PrintStream out) 
    {
        this(presenter,
                new AuthView(presenter, scanner, out),
                new ListView(presenter, scanner, out),
                out);
    }

    //Creates a ProgramView with every collaborator injected 
    public ProgramView(ProgramPresenter presenter, AuthView authview, ListView listview, PrintStream out) 
    {
        this.presenter = presenter;
        this.authview = authview;
        this.listview = listview;
        this.out = out;
    }

    //Prints the welcome banner 
    
    public void init() 
    {
        if (initialized) 
        {
            return;
        }
        out.println(" PERSONAL PASSWORD MANAGER ");
    
        initialized = true;
    }

    //Runs the program: Banner, Login and then the list screen
   
    public void run() 
    {
        init(); 

        if (authview.authenticate()) 
        {
            listview.listLogins();
        } 
        else 
        {
            out.println("Goodbye.");
        }
    }
}
