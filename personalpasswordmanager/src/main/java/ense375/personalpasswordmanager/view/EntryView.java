package ense375.personalpasswordmanager.view;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ense375.personalpasswordmanager.ProgramPresenter;

//To create a EntryView

public class EntryView {
    private final ProgramPresenter presenter;
    private final Scanner scanner;
    private final PrintStream out;

    //Creates an EntryView that uses the real console.
    
    public EntryView(ProgramPresenter presenter) 
    {
        this(presenter, new Scanner(System.in), System.out);
    }

    //Creates an EntryView with injected input and output.
    
    public EntryView(ProgramPresenter presenter, Scanner scanner, PrintStream out) 
    {
        this.presenter = presenter;
        this.scanner = scanner;
        this.out = out;
    }

    // Helps the user through creating a new login.
    
    public void createNewLogin() {
        out.println();
        out.println("1. Text login (username and password)");
        out.println("2. TOTP login (authenticator app)");
        out.println("0. Cancel");
        String type = promptUserInput("Type: ");

        if (!type.equals("1") && !type.equals("2")) 
        {
            out.println("Cancelled.");
            return;
        }

        String username = promptUserInput("Username: ");
        String website = promptUserInput("Website: ");

        
        if (username.isEmpty() || website.isEmpty()) 
        {
            out.println("Username and website are required.");
            return;
        }

        int entry;
        if (type.equals("1")) 
        {
            // Text login: needs a password.
            String password = promptUserInput("Password: ");
            if (password.isEmpty()) 
            {
                out.println("The password cannot be empty.");
                return;
            }
            entry = presenter.createNewTextLogin(username, password, website);
        }
        else 
        {
            // TOTP login: needs the shared secret, recovery keys are optional.
            String secretKey = promptUserInput("Secret key (Base32): ");
            if (secretKey.isEmpty()) 
            {
                out.println("The secret key cannot be empty.");
                return;
            }
            if (!ProgramPresenter.validBase32(secretKey)) {
            	out.println("The secret key must be of Base32 format.");
            	return;
            }

            List<String> recoveryKeys = parseRecoveryKeys(
                    promptUserInput("Recovery keys, comma separated (optional): "));
            entry = presenter.createNewTOTPLogin(username, secretKey, website, recoveryKeys);
        }

       
        if (entry == -1) 
        {
            out.println("Could not create the login.");
            return;
        }
        out.println("Login created.");


        if (type.equals("2")) 
        {
            out.println("Current OTP: " + presenter.getOneTimePassword(entry));
        }
    }

    //Prints all the login fields
    
    public void showLoginEntry(int entryId) 
    {
        Map<String, String> login = presenter.getLogin(entryId);
        if (login == null || login.isEmpty()) {
            out.println("Login not found.");
            return;
        }

        // Only ask about revealing if there is actually something secret to hide.
        boolean reveal = false;
        if (hasSecretField(login)) 
        {
            reveal = promptUserInput("Reveal secret fields? (y/n): ").equalsIgnoreCase("y");
        }

        out.println();
        for (Map.Entry<String, String> field : login.entrySet()) 
        {
            String value = isSecretField(field.getKey()) && !reveal ? "********" : field.getValue();
            out.printf(" %-16s %s%n", field.getKey() + ":", value);
        }
    }

    //To edit existing login details 
    public boolean editExistingLogin(int entryId) {
    	
    	Map<String, String> entry = presenter.getLogin(entryId);
    	
    	Map<String, String> changes = new HashMap<String, String>();
    	
        out.println("Leave a field empty to keep it unchanged.");
        
        // --- Korbin's addition ---
        
        String username;
        do {
        	username = promptUserInput("New username/email (no spaces, '_' '-' '+' allowed in username): ");
        } while (!username.isBlank() && !ProgramPresenter.validUsernameOrEmail(username));
        if (!username.isEmpty()) changes.put(ProgramPresenter.USERNAME, username);
        
        
        if (entry.get(ProgramPresenter.TYPE).equals(ProgramPresenter.TEXT_TYPE)) {
        	String password;
        	do {
        		password = promptUserInput("New password (min: 8 length, 1 upper-case, 1 special, 1 digit): ");
        	} while (!password.isBlank() && !ProgramPresenter.validPassword(password));
        	if (!password.isEmpty()) changes.put("password", password);
        	
        } else {
        	List<String> current_recovery_keys = new ArrayList<String>();
        	int recov_key_index = 0;
        	while (entry.containsKey(ProgramPresenter.RECOVERY_KEY_PREFIX + String.valueOf(recov_key_index)))
        		current_recovery_keys.add(entry.get(ProgramPresenter.RECOVERY_KEY_PREFIX + String.valueOf(recov_key_index++)));
        	
        	out.println("For Existing Recovery Keys, type '-' if you want to remove the recovery key, enter in a new recovery key to replace, or leave blank to skip");
        	recov_key_index = 0;
        	
        	for (String recovery_key : current_recovery_keys) {
        		String recov_choice = promptUserInput(String.format("What do you want to do with '%s'? ", recovery_key));
        		
        		if (recov_choice.isBlank()) {
        			changes.put(ProgramPresenter.RECOVERY_KEY_PREFIX + String.valueOf(recov_key_index++), recovery_key);
        			continue;
        		}
        		if (recov_choice.trim().equals("-")) continue;
        		
        		if (ProgramPresenter.validRecoveryKey(recov_choice)) {
        			changes.put(ProgramPresenter.RECOVERY_KEY_PREFIX + String.valueOf(recov_key_index++), recov_choice);
        		} else {
        			out.println("invalid choice, skipping this recovery key...");
        			changes.put(ProgramPresenter.RECOVERY_KEY_PREFIX + String.valueOf(recov_key_index++), recovery_key);
        		}
        	}
        	
        	String recovery_key;
        	do {
        		recovery_key = promptUserInput("New recovery key (leave blank to finish): ");
        		if (!recovery_key.isBlank()) {
        			if (ProgramPresenter.validRecoveryKey(recovery_key)) changes.put(ProgramPresenter.RECOVERY_KEY_PREFIX + String.valueOf(recov_key_index++), recovery_key);
        			else out.println("The recovery key entered was not valid, please try again");
        		}
        	} while (!recovery_key.isBlank());
        }
        // --- End of Korbin's addition ---

        if (changes.isEmpty()) 
        {
            out.println("Nothing changed.");
            return false;
        }
        boolean edited = presenter.editExistingLogin(entryId, changes);
        out.println(edited ? "Login updated." : "Could not update the login.");
        return edited;
    }

    //To delete the login entry 
    public boolean deleteLoginEntry(int entryId) 
    {
        String confirm = promptUserInput("Type 'yes' to delete this credential permanently: ");
        if (!confirm.equalsIgnoreCase("yes")) 
        {
            out.println("Cancelled.");
            return false;
        }
        
        boolean deleted = presenter.deleteLogin(entryId);
        out.println(deleted ? "Login deleted." : "Could not delete the login.");
        return deleted;
    }

    // Prints a prompt and reads one line of input.
   
    public String promptUserInput(String prompt) 
    {
        out.print(prompt);
        out.flush(); 
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }

    //Splits a comma-separated line into a list of recovery keys
     
    private List<String> parseRecoveryKeys(String line) 
    {
        List<String> keys = new ArrayList<>();
        
        for (String key : line.split(",")) 
        {
            if (!key.trim().isEmpty()) 
            {
                keys.add(key.trim());
            }
        }
        return keys;
    }

    //returns true if a field with this name holds a secret that should be hidden 
    private boolean isSecretField(String name) 
    {
        return name.equalsIgnoreCase(ProgramPresenter.PASSWORD)
                || name.equalsIgnoreCase(ProgramPresenter.RECOVERY_KEY_PREFIX);
    }

    //returns true if at least one field of the login is a secret
    private boolean hasSecretField(Map<String, String> login) 
    {
        for (String name : login.keySet()) 
        {
            if (isSecretField(name)) 
            {
                return true;
            }
        }
        return false;
    }
}
