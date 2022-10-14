package cosc310_T28_librarySystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class UserAndManagerTerminal extends Thread {
    LocalLibraryData localLibraryData;

    public UserAndManagerTerminal(LocalLibraryData localLibraryData) {
	super();
	this.localLibraryData = localLibraryData;
    }

    /**
     * This thread will will keep asking the user for input and replying to all the
     * input.
     * 
     * This function is automatically run by the thread after start() is called.
     */
    public void run() {
	boolean finishedWithoutInterruption = false; // this boolean tells the try-finally clause whether
						     // scanner.hasNextLine()
						     // failed
	try (Scanner scanner = new Scanner(System.in)) {
	    Account currentAccount;
	    System.out.println("Welcoming to Team 28's Library System (version 0.1).");
	    
	    //log in or create first account
	    if (localLibraryData.managerAccounts.isEmpty()) {
                System.out.println("Currently there are no manager (librarian) accounts. Please create a manager account.");
		currentAccount = tryCreatingAccount(scanner, true);
		if (currentAccount == null) {
		    System.out.println("Account creation failed. Exiting.");
                    finishedWithoutInterruption = true;
                    return;
		}
                localLibraryData.managerAccounts.put(currentAccount.getUsername(), (Manager) currentAccount);
	    } else {
		currentAccount = tryLoggingIn(scanner, localLibraryData);
		if (currentAccount == null) {
		    System.out.println("Login failed. Exiting.");
                    finishedWithoutInterruption = true;
                    return;
		}
	    }
	    
	    //this while loop is the main loop for the user or manager to do any command
            UserOrManagerCommandResult userOrManagerCommandResult;
	    do {
		userOrManagerCommandResult = askAndDoNextUserOrManagerCommand(scanner, localLibraryData, currentAccount);	
	    } while (userOrManagerCommandResult != UserOrManagerCommandResult.EXIT);

	    finishedWithoutInterruption = true;
	} finally {
	    if (!finishedWithoutInterruption) {
		System.out.println("Program interrupted. Exiting.");
	    }
	}
    }

    private Account tryCreatingAccount(Scanner scanner, boolean isManager) {
        String username = null;
        while (username == null) {
            System.out.print("Username: ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String usernameEntered = scanner.nextLine();
            if (!usernameEntered.matches("[a-zA-Z0-9 _-]*")) {
        	System.out.println("The username must contain only letters, numbers, _- symbols, or spaces.");
            } else if (!(1 <= usernameEntered.length() && usernameEntered.length() <= 99)) {
        	System.out.println("The username must be 1 to 99 characters long.");
            } else {
        	username = usernameEntered;
            }
        }
        String password = null;
        while (password == null) {
            System.out.print("Password: ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String passwordEntered = scanner.nextLine();
            if (!passwordEntered.matches("[a-zA-Z0-9 _-`~!@#$%^&*()=+\\[{\\]}\\\\|;:'\",<.>/?]*")) {
        	System.out.println("The password must contain only letters, numbers, _-`~!@#$%^&*()=+[{]}\\|;:'\",<.>/? symbols, or spaces.");
            } else if (!(1 <= passwordEntered.length() && passwordEntered.length() <= 99)) {
        	System.out.println("The password must be 1 to 99 characters long.");
            } else {
        	password = passwordEntered;
            }
        }
        Account newAccount;
        if (isManager) {
            newAccount = new Manager(username, password, 0); 
            System.out.println("The manager account " + username + " was created successfully.");
        } else {
            newAccount = new User(username, password, 0); 
            System.out.println("The user account " + username + " was created successfully.");
        }
        return newAccount;
    }
    private Account tryLoggingIn(Scanner scanner, LocalLibraryData localLibraryData) {
        System.out.println("Please log in to an account.");
        Account accountToLogIn = null;
        while (accountToLogIn == null) {
            System.out.print("Username: ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String usernameEntered = scanner.nextLine();
            if (!localLibraryData.userAccounts.containsKey(usernameEntered)) {
        	System.out.println("Account not found.");
            } else {
        	accountToLogIn = localLibraryData.userAccounts.get(usernameEntered);
            }
        }
        while (true) {
            System.out.print("Password: ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String passwordEntered = scanner.nextLine();
            if (!accountToLogIn.passwordEquals(passwordEntered)) {
        	System.out.println("Password incorrect.");
            } else {
        	return accountToLogIn;
            }
        }
    }
    private UserOrManagerCommandResult askAndDoNextUserOrManagerCommand(Scanner scanner, LocalLibraryData localLibraryData, Account currentAccount) {
        System.out.println("Welcome " + currentAccount.getUsername() + ". What would you like to do? Enter a number to make a selection.");
        if (currentAccount instanceof Manager) {
            System.out.println("1: search for a book");
            System.out.println("2: checkout a book");
            System.out.println("3: save session");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String selection = scanner.nextLine();
            switch (selection) {
                case "1":
                    currentAccount.searchForABook(scanner, localLibraryData);
                    return UserOrManagerCommandResult.NEXT_COMMAND;
                case "3":
                    saveSession(scanner, localLibraryData);
                    return UserOrManagerCommandResult.NEXT_COMMAND;
        	default:
                    System.out.println("Selection unavailable");
                    return UserOrManagerCommandResult.NEXT_COMMAND;
            }
        } else if (currentAccount instanceof User) {
            
        } else {
            throw new IllegalStateException();
        }
        return UserOrManagerCommandResult.NEXT_COMMAND;
    }
    static enum UserOrManagerCommandResult {
	EXIT,
	NEXT_COMMAND,
	LOG_OUT;
    }

    private void saveSession(Scanner scanner, LocalLibraryData localLibraryData) {
	try {
	    if (!Files.exists(Paths.get("cosc310_T28_library_system_saved_files/"))) {
		Files.createDirectories(Paths.get("cosc310_T28_library_system_saved_files/")); // does not overwrite anyways
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try (
		FileOutputStream fileOut = new FileOutputStream("cosc310_T28_library_system_saved_files/main.ser");
//		GZIPOutputStream zipOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream out = new ObjectOutputStream(fileOut)
		) {
	    out.writeObject(localLibraryData);
	} catch (IOException i) {
	    i.printStackTrace();
	}
    }
    private LocalLibraryData loadSession(Scanner scanner) {
	if (Files.exists(Paths.get("cosc310_T28_library_system_saved_files/main.ser"))) {
	    try (
		    FileInputStream fileIn = new FileInputStream("cosc310_T28_library_system_saved_files/main.ser");
//		    GZIPInputStream zipIn = new GZIPInputStream(fileIn);
		    ObjectInputStream in = new ObjectInputStream(fileIn)
		    ) {
		LocalLibraryData localLibraryData = (LocalLibraryData) in.readObject();
		return localLibraryData;
	    } catch (IOException | ClassNotFoundException c) {
		c.printStackTrace();
		return null;
	    }	
	}
	return null;
    }
}