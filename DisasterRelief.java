import java.sql.*;
import java.util.*;
import java.io.*;

public class DET_02_DisasterRelief {
    static Connection connection;
    static ResultSet rsSet;
    static Statement statement;
    public static void main(String[] args) {
        //create scanner for user input of zip code and miles
        /*
        Scanner scan = new Scanner(System.in);
        System.out.print("Please enter 5-digit zip code: ");
        String zipCode = scan.next();
        System.out.print("Please enter miles from disaster wanted: ");
        double distInMiles = scan.nextDouble();
        double distInKilos = distInMiles * 1.60934;
        System.out.println(distInKilos);
        */

        //credentials to use to access the database
        String host = "jdbc:mysql://turing.cs.missouriwestern.edu:3306/misc";
        String user = "csc254";
        String password="age126";

        //string to be fed into the database query
        String queryString = "SELECT city, region, country, latitude, longitude FROM cities where region LIKE 'MO' LIMIT 25";

        try {
            //create a connection to the database
            connection = DriverManager.getConnection(host, user, password);

            //create a statement, set it to be the above queryString
            statement = connection.createStatement();
            rsSet = statement.executeQuery(queryString);




            while(rsSet.next()){
                String country = rsSet.getString("country");
                String name = rsSet.getString("city");
                String region = rsSet.getString("region");
                double lat = rsSet.getDouble("latitude");
                double lon = rsSet.getDouble("longitude");
                Place place = new Place(name,region,country,lat,lon);
                System.out.println(place);
            }

            connection.close();
        } catch (SQLException e) {
            System.out.println("Trouble connecting to the database");
            System.exit(1);
        }
    }
}

class Place {
    String name;
    String region;
    String country;
    double latitude;
    double longitude;
    double distFromOrigin;

    public Place(String name, String region, String country, double latitude, double longitude) {
        this.name = name;
        this.region = region;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public double getDistFromOrigin() {
        return distFromOrigin;
    }

    public void setDistFromOrigin(double distFromOrigin) {
        this.distFromOrigin = distFromOrigin;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distFromOrigin=" + distFromOrigin +
                '}';
    }
}
