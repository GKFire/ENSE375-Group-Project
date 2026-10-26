package ense375.personalpasswordmanager;

// Entry point of the Personal Password Manager.

public class Main {

    //To start the application 
    public static void main(String[] args) 
    {
        ProgramPresenter presenter = new ProgramPresenter();
        ProgramView view = new ProgramView(presenter);

        view.run();  
    }
}