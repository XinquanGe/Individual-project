package cosc310_T28_librarySystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 
 * @author Team 28
 *
 *         Each User object stores the username and password and other
 *         information for one library user account, and has no methods.
 */
public class User extends Account {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // private String name;
    private final int max_borrow = 100;
    private int borrow_num;
    private  ArrayList<Book> borrow_books=new <Book>ArrayList();
    
    public User() {}

    public ArrayList<Book> getBorrow_books() {
        return borrow_books;
    }

    public void setBorrow_books(ArrayList<Book> borrow_books) {
        this.borrow_books = borrow_books;
    }



    public User(String userName, String password, int type) {
	super(userName, password, type);

    }
    //Apply for borrowing books
    Book borrowBook(Scanner scanner, LocalLibraryData localLibraryData) {
    ArrayList<Book> bookFoundlist = searchForABook(scanner,localLibraryData,true);
    if(bookFoundlist.isEmpty()) {
	return null;
    }
    System.out.println("Choose the id(number before the book) of book you want to select: ");
    if (!scanner.hasNextLine()) {
	    return null;
	} 
    int enter;
    while(true){
        if(scanner.hasNextInt()){
           enter = scanner.nextInt();
            if(enter>=1&&enter<=bookFoundlist.size())
                break;
            else
            System.out.println("Please enter a right id: ");
        }else
            System.out.println("Please enter a number: ");
    }
    
    Book wantToBorrow = bookFoundlist.get(Integer.valueOf(enter)-1);
    System.out.println("Sure to borrow book: Title = " + wantToBorrow.title + " ISBN = " + wantToBorrow.iSBN+"?");
    boolean df=false;
    String define = scanner.nextLine();
    while(!define.equals("Y")&& !define.equals("N")){
            System.out.println("Please enter 'Y' or 'N'");
            define = scanner.nextLine();
        }
    if(define.equals("Y"))
        df=true;
    else if(define.equals("N"))
        df=false;

   
    if(df==true){
        if(borrow_books.size()>=max_borrow){
            System.out.println("You have already borrow "+borrow_books.size()+" book(s)! Cannot borrow more.");
            return null;
        }else{
        if (localLibraryData.freeToLend.contains(wantToBorrow)) {
            localLibraryData.freeToLend.remove(wantToBorrow);
            wantToBorrow.holder=User.this;
            localLibraryData.readyToLend.add(wantToBorrow);
            System.out.println("You order to borrow book: Title = " + wantToBorrow.title + " ISBN = " + wantToBorrow.iSBN+" is received.\nPlease Please go to the library to get it later");
            borrow_books.add(wantToBorrow);
            return wantToBorrow;
    }else{
            System.out.println("Sorry, this book has been lended.");
        return null;
    }
    }
    }else{
        System.out.println("Borrow stop.");
    }
    return null;
    }
    // get list for borrow book
    void orderList(){
        for(int i=0;i<borrow_books.size();i++){
            System.out.println(borrow_books.get(i).title);
        }
    }

    /**
     * generated by Eclipse
     */
    
    
}