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
        String zipCode = scan.next();

        //loop to ensure zipCode is correct
        while (!zipCode.matches("[0-9]+") || zipCode.length() != 5){
            if(!zipCode.matches("[0-9]+")) {
                System.out.print("An alphabetical letter was used. Please enter 5-digit zip code: ");
                zipCode = scan.next();
            }
            else if (zipCode.length()<5){
                System.out.print("Too few digits used. Please enter 5-digit zip code: ");
                zipCode = scan.next();
            }
            else{
                System.out.print("Too many digits used. Please enter 5-digit zip code: ");
                zipCode = scan.next();
            }
        }

        /*
        System.out.print("Please enter miles from disaster wanted: ");
        double distInMiles = scan.nextDouble();
        double distInKilos = distInMiles * 1.60934;
        System.out.println(distInKilos);
        */

        //credentials to use to access the database
        String host = "jdbc:mysql://turing.cs.missouriwestern.edu:3306/misc";
        String user = "csc254";
        String password= "age126";

        //string to be fed into the database query
        String queryString1 = "SELECT city, state, locationtype, zipcode, zipcodetype, lat, `long` FROM zips2 where zipcode = "+zipCode+" AND locationtype = 'PRIMARY' LIMIT 25";
        try {
            //create a connection to the database
            connection = DriverManager.getConnection(host, user, password);

            //create a statement, set it to be the above queryString1
            statement = connection.createStatement();
            rsSet = statement.executeQuery(queryString1);

            while(rsSet.next()){
                //System.out.println("k");

                String city = rsSet.getString("city");
                String state = rsSet.getString("state");
                String locationtype = rsSet.getString("locationtype");
                String zipcode = rsSet.getString("zipcode");
                String zipcodetype = rsSet.getString("zipcodetype");
                double lat = rsSet.getDouble("lat");
                double lon = rsSet.getDouble("long");
                Place newPlace = new Place(city,state,locationtype,zipcode,zipcodetype,lat,lon);
                System.out.println(newPlace);

            }

            connection.close();
        } catch (SQLException e) {
            System.out.println("Trouble accessing information in the database");
            System.exit(1);
        }
    }
}

class Place {

    String city;
    String state;
    String locationType;
    String zipCode;
    String zipCodeType;
    double latitude;
    double longitude;

    public Place(String city, String state, String locationType, String zipCode, String zipCodeType, double latitude, double longitude) {
        this.city = city;
        this.state = state;
        this.locationType = locationType;
        this.zipCode = zipCode;
        this.zipCodeType = zipCodeType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCodeType() {
        return zipCodeType;
    }

    public void setZipCodeType(String zipCodeType) {
        this.zipCodeType = zipCodeType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Place{" +
                "city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", locationType='" + locationType + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", zipCodeType='" + zipCodeType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
