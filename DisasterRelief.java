import java.sql.*;
import java.util.*;
import java.io.*;

public class DET_02_DisasterRelief {
    
	static Connection connection;
    static ResultSet rsSet;
    static Statement statement;
    
	public static void main(String[] args) {
        
		//create scanner for user input of zip code and miles
        Scanner scan = new Scanner(System.in);
        System.out.print("Please enter 5-digit zip code: ");
        int zipCode = scan.nextInt();
        System.out.print("Please enter 5-digit zip code: ");
        int distInMiles = scan.nextInt();
}