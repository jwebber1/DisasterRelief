/*
CSC-346 Data Exchange Technologies Assignment 02 Disaster Relief Simulation
    The agency needs a program that takes a 5-digit zip code and a distance in miles.  The program should produce a list
of places within the given distance of the specified zip code along with the total population and the total number of
housing units.
    For example, if given a zip code of 64507 and a distance of 300, the program would give a list of all places within
300 miles of St. Joseph. Each place should be listed only once, even if it has several zip codes.  For each place be
sure to list the place name, state, the total population, and the total number of housing units.  It should also list
the distance from the source in both kilometers and miles.
    If there are several zip codes for a place, use any of the distances you calculated. Obviously this program is going
to miss places outside the US if you give a latitude and longitude near a US border.  We can't help that at this point.
    Also, you may find that at the borders a place may be partially in or partially out.  Don't sweat that too much.
It is just fine if you only calculate the parts that are within the specified distance.
    In practice during most disasters the borders of the affected areas are always a bit fuzzy. Here are a couple of
resources you can try using.  I think all the information you need is within them.
    When you are doing places, make sure you don't combine two cities in different states.  If you search for cities
with 200 miles of St. Louis you should have both Springfield, MO and Springfield, IL listed as different places.
*/

import java.sql.*;
import java.util.*;

public class DET_02_DisasterRelief {
    static Connection connection;
    static ResultSet rsSet;
    static Statement statement;

    public static void main(String[] args) {

        //create scanner for user input of zip code and miles
        Scanner scan = new Scanner(System.in);

        //input for zipcode
        System.out.print("Please enter 5-digit zip code: ");
        String zipCode = scan.next();

        //loop to ensure zipCode is correct
        while (!zipCode.matches("[0-9]+") || zipCode.length() != 5) {
            if (!zipCode.matches("[0-9]+")) {
                System.out.print("An alphabetical letter was used. Please enter 5-digit zip code: ");
                zipCode = scan.next();
            } else if (zipCode.length() < 5) {
                System.out.print("Too few digits used. Please enter 5-digit zip code: ");
                zipCode = scan.next();
            } else {
                System.out.print("Too many digits used. Please enter 5-digit zip code: ");
                zipCode = scan.next();
            }
        }

        //input for distance
        System.out.print("Please enter miles from disaster wanted: ");
        double distInMiles = scan.nextDouble();

        //loop to ensure distance isn't negative
        while (distInMiles <= 0) {
            System.out.println("It appears a negative number was entered. Please use positive numbers for distance.");
            System.out.print("Please enter miles from disaster wanted: ");
            distInMiles = scan.nextDouble();
        }

        //credentials to use to access the database
        String host = "jdbc:mysql://turing.cs.missouriwestern.edu:3306/misc";
        String user = "csc254";
        String password = "age126";

        //strings to be fed into the database queries
        String queryStringAll = "SELECT zips.city, zips2.state, zips2.locationtype, zips.population, zips.housingunits, "+
                "zips2.zipcode, zips.lat, zips.lon FROM zips inner join zips2 on zip_code=zipcode WHERE NOT state = 'PR' "+
                "AND locationtype = 'PRIMARY' ORDER BY zipcode ASC";
        String queryStringForZip = "SELECT zipcode, lat, `long` FROM zips2 where locationtype = 'PRIMARY' AND zipcode = " + zipCode;


        try {
            //create a connection to the database
            connection = DriverManager.getConnection(host, user, password);

            //temporary variables to start latitude and longitude
            double startLat = 0.0;
            double startLong = 0.0;

            //query to find latitude and longitude for initial zipCode
            statement = connection.createStatement();
            rsSet = statement.executeQuery(queryStringForZip);
            while (rsSet.next()) {
                startLat = rsSet.getDouble("lat");
                startLong = rsSet.getDouble("long");
            }

            //create arraylist to hold cities, states and other info
            ArrayList<Place> disasterCities = new ArrayList<>();

            //create a statement, set it to be the above queryStringAll
            statement = connection.createStatement();
            rsSet = statement.executeQuery(queryStringAll);

            //looking through the database for the specified info
            while (rsSet.next()) {
                //set city, state, and other variables to use in Place
                String city = rsSet.getString("city");
                String state = rsSet.getString("state");
                String locationType = rsSet.getString("locationtype");
                int population = rsSet.getInt("population");
                int houses = rsSet.getInt("housingunits");
                String zipcode = rsSet.getString("zipcode");
                double lat = rsSet.getDouble("lat");
                double lon = rsSet.getDouble("lon");

                //create new place
                Place place = new Place(city, state, locationType, population, houses, zipcode, lat, lon);

                //find distance of place from origin and store it
                place.setDistFromOrigin(haversine.totDistance(startLat, startLong, lat, lon));

                //if it is within the user specified distance, put it into the array
                if(place.getDistFromOrigin()<distInMiles){
                    disasterCities.add(place);
                }
            }

            //initial loop to catch IF two cities next to each other in the array have the same name and state, add the
            // population and housing and remove the extra
            for(int i = 0; i<disasterCities.size(); i++){
                for(int j = i+1; j<disasterCities.size(); j++){
                    if(disasterCities.get(i).getZipCode().equals(disasterCities.get(j).getZipCode())) {
                        disasterCities.get(i).setPopulation(disasterCities.get(i).getPopulation() + disasterCities.get(j).getPopulation());
                        disasterCities.get(i).setHouses(disasterCities.get(i).getHouses() + disasterCities.get(j).getHouses());
                        disasterCities.remove(j);
                        j--;
                    }
                }
            }

            //redundant loop to ensure catching IF two cities of the same name are separated by another city in the above loop
            for(int i = 0; i<disasterCities.size(); i++){
                for(int j = i+1; j<disasterCities.size(); j++){
                    if(disasterCities.get(i).getZipCode().equals(disasterCities.get(j).getZipCode())) {
                        disasterCities.get(i).setPopulation(disasterCities.get(i).getPopulation() + disasterCities.get(j).getPopulation());
                        disasterCities.get(i).setHouses(disasterCities.get(i).getHouses() + disasterCities.get(j).getHouses());
                        disasterCities.remove(j);
                        j--;
                    }
                }
            }

            //Currently prints out the array to the console
            for(int i = 0; i<disasterCities.size(); i++){
                if(disasterCities.get(i).getPopulation()>0){
                    System.out.printf("%-25s,\t%-2s\t%-5s,\tpopulation = %8d,\thousing units = %8d\n",
                            disasterCities.get(i).getCity(), disasterCities.get(i).getState(), disasterCities.get(i).getZipCode(),
                            disasterCities.get(i).getPopulation(), disasterCities.get(i).getHouses());
                }
            }

            //close connection to the database
            connection.close();
            }
            catch(SQLException e){
                System.out.println("Trouble accessing information in the database");
                System.exit(1);
            }
        }
    }

//creating the class Place to later create place objects and store information like zipcode and population
class Place {
    private String city;
    private String state;
    String locationType;
    private String zipCode;
    double latitude;
    double longitude;
    private double distFromOrigin;
    private int houses;
    private int population;

    //constructor for Place
    public Place(String city, String state, String locationType, int population, int houses, String zipCode, double latitude, double longitude) {
        this.city = city;
        this.state = state;
        this.locationType = locationType;
        this.zipCode = zipCode;
        this.houses = houses;
        this.population = population;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distFromOrigin = 0.0;

    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getHouses() {
        return houses;
    }

    public void setHouses(int houses) {
        this.houses = houses;
    }

    public String getZipCode() {
        return zipCode;
    }

    public double getDistFromOrigin() {
        return distFromOrigin;
    }

    public void setDistFromOrigin(double distFromOrigin) {
        this.distFromOrigin = distFromOrigin;
    }
}

//creation of class
class haversine {
    private final static double eRadiusKm = 6371.0;
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