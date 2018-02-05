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

        System.out.print("Please enter miles from disaster wanted: ");
        double distInMiles = scan.nextDouble();



        //credentials to use to access the database
        String host = "jdbc:mysql://turing.cs.missouriwestern.edu:3306/misc";
        String user = "csc254";
        String password= "age126";

        //string to be fed into the database query
        String queryStringAll = "SELECT city, state, locationtype, zipcode, taxreturnsfiled, lat, `long` FROM zips2 where locationtype = 'PRIMARY'";
        String queryStringForZip = "SELECT zipcode, lat, `long` FROM zips2 where locationtype = 'PRIMARY' AND zipcode = "+zipCode;


        try {
            //create a connection to the database
            connection = DriverManager.getConnection(host, user, password);

            double startLat = 0;
            double startLong = 0;

            statement = connection.createStatement();
            rsSet = statement.executeQuery(queryStringForZip);

            while(rsSet.next()) {
                startLat = rsSet.getDouble("lat");
                startLong = rsSet.getDouble("long");
            }
            //System.out.println(startLat);
            //System.out.println(startLong);

            //create a statement, set it to be the above queryStringAll
            statement = connection.createStatement();
            rsSet = statement.executeQuery(queryStringAll);

            while(rsSet.next()){
                //System.out.println("k");

                String city = rsSet.getString("city");
                String state = rsSet.getString("state");
                String locationtype = rsSet.getString("locationtype");
                String zipcode = rsSet.getString("zipcode");
                int taxreturnsfiled = rsSet.getInt("taxreturnsfiled");
                double lat = rsSet.getDouble("lat");
                double lon = rsSet.getDouble("long");
                Place newPlace = new Place(city,state,locationtype,zipcode,taxreturnsfiled,lat,lon);
                if(haversine.totDistance(startLat, startLong, lat, lon)<distInMiles) {
                    System.out.println(newPlace + "\td= " + haversine.totDistance(startLat, startLong, lat, lon));
                }


            }

            //System.out.println(haversine.totDistance(40, -90, 41, -74));
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
    int taxreturnsfiled;
    double latitude;
    double longitude;

    public Place(String city, String state, String locationType, String zipCode, int taxreturnsfiled, double latitude, double longitude) {
        this.city = city;
        this.state = state;
        this.locationType = locationType;
        this.zipCode = zipCode;
        this.taxreturnsfiled = taxreturnsfiled;
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

    public int getZipCodeType() {
        return taxreturnsfiled;
    }

    public void setZipCodeType(int taxreturnsfiled) {
        this.taxreturnsfiled = taxreturnsfiled;
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
                ", taxreturnsfiled='" + taxreturnsfiled + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

class haversine {
    final static double eRadiusKm = 6371.0;
    public static double totDistance(double startLat, double startLong, double endLat, double endLong){
        double deltaLat = Math.toRadians(endLat-startLat);
        double deltaLong = Math.toRadians(endLong-startLong);

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double tempA = Math.pow(Math.sin(deltaLat/2.0), 2.0)+Math.cos(startLat)*Math.cos(endLat)*Math.pow(Math.sin(deltaLong/2.0), 2.0);
        double tempC = 2.0*Math.atan2(Math.sqrt(tempA),Math.sqrt(1.0-tempA));


        return eRadiusKm * tempC *.621371;
    }
}