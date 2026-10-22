package ense375.personalpasswordmanager;

import ense375.personalpasswordmanager.presenter.ProgramPresenter;
import ense375.personalpasswordmanager.view.ProgramView;

// Entry point of the Personal Password Manager.

public class Main {

    //To start the application 
    public static void main(String[] args) 
    {
        ProgramPresenter presenter = new ProgramPresenter();
        ProgramView view = new ProgramView(presenter);

        view.init(); 
        view.run();  
    }
}