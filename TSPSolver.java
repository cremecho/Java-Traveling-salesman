//Student Nmae: TIANXI WEN 
//Student ID: 1824097

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class TSPSolver {
    public static ArrayList<City> readFile(String filename) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = null;
            while((line = in.readLine()) != null) {
                String[] blocks = line.trim().split("\\s+");
                if (blocks.length == 3) {
                    City c = new City();
                    c.city = Integer.parseInt(blocks[0]);
                    c.x = Double.parseDouble(blocks[1]);
                    c.y = Double.parseDouble(blocks[2]);
                    //System.out.printf("City %s %f %f\n", c.city, c.x, c.y);
                    cities.add(c);
                } else {
                    continue;
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        City.distances = new double[cities.size()][cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            City ci = cities.get(i);
            for (int j = i; j < cities.size(); j++) {
                City cj = cities.get(j);
                City.distances[i][j] = City.distances[j][i] = Math.sqrt(Math.pow((ci.x - cj.x),2) + Math.pow((ci.y - cj.y),2));
            }
        }
        return cities;
    }

    public static ArrayList<City> solveProblem(ArrayList<City> citiesToVisit) {
        ArrayList<City> routine = new ArrayList<City>();
        City start = null;
        City current = null;
        // get city 0;
        for (int i = 0; i < citiesToVisit.size(); i++) {
            if (citiesToVisit.get(i).city == 0) {
                start = current = citiesToVisit.remove(i);
                routine.add(current);
                break;
            }
        }
        if (current == null) {
            System.out.println("Your problem instance is incorrect! Exiting...");
            System.exit(0);
        }
        // visit cities
        while (!citiesToVisit.isEmpty()) {
            double minDist = Double.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < citiesToVisit.size(); i++) {
                double distI = current.distance(citiesToVisit.get(i));
                // index == -1 is needed in case the distance is really Double.MAX_VALUE.
                if (index == -1 || distI < minDist) {
                    index = i;
                    minDist = distI;
                }
            }
            //int index = 0;

            current = citiesToVisit.remove(index);
            routine.add(current);
        }
        routine.add(start); // go back to 0
        return routine;
    }

    public static double printSolution(ArrayList<City> routine) {
        double totalDistance = 0.0;
        for (int i = 0; i < routine.size(); i++) {
            if (i != routine.size() - 1) {
                System.out.print(routine.get(i).city + "->");
                totalDistance += routine.get(i).distance(routine.get(i+1));
            } else {
                System.out.println(routine.get(i).city);
            }
        }
        return totalDistance;
    }

    /*
        Just evaluate the total distance. A simplified version of getDistance()
     */
        public static double evaluateRoutine(ArrayList<City> routine, int index1, int index2) {
        double totalDistance = 0.0;
        for (int i = index1 - 1; i <= index2; i++) {
            totalDistance += routine.get(i).distance(routine.get(i+1));
        }
        return totalDistance;
    }

    /*
        Moves the city at index "from" to index "to" inside the routine
     */
    private static void moveCity(ArrayList<City> routine, int from, int to) {
        City temp = routine.get(from);
        routine.remove(from);
        routine.add(to, temp);
    }

    /*
        Evaluate the relocation of city and returns the change in total distance.
        The return value is (old total distance - new total distance).
        As a result, a positive value means that the relocation of city results in routine improvement;
        a negative value means that the relocation leads to worse routine. A zero value means same quality.
     */
    public static double evalMove(ArrayList<City> routine, int from, int to) {
        double oldDistance = evaluateRoutine(routine,from,to);
        moveCity(routine, from, to);
        double newDistance = evaluateRoutine(routine,from, to);
        return oldDistance - newDistance;
    }

    public static boolean moveFirstImprove(ArrayList<City> routine) {
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 1; j < routine.size() - 1; j++) {
                double diff = evalMove(routine, i, j);
                if (diff + 0.00001 < 0) { 
                    moveCity(routine, j, i);
                    //return true;
                }
            }
        }
        return false;
    }

    public static void swapCity(ArrayList<City> routine, int index1, int index2) {
        City temp = routine.get(index1);
        routine.set(index1, routine.get(index2));
        routine.set(index2, temp);
    }

    /*
        Can you improve the performance of this method?
        You are allowed to change the implementation of this method and add other methods.
        but you are NOT allowed to change its method signature (parameters, name, return type).
     */
    public static double evalSwap(ArrayList<City> routine, int index1, int index2) {
        double oldDistance = evaluateRoutine(routine,index1,index2);
        swapCity(routine, index1, index2);
        double newDistance = evaluateRoutine(routine,index1,index2);
        return oldDistance - newDistance;
    }

    /*
        This function iterate through all possible swapping positions of cities.
            if a city swap is found to lead to shorter travelling distance, that swap action
            will be applied and the function will return true.
            If there is no good city swap found, it will return false.
     */
    public static boolean swapFirstImprove(ArrayList<City> routine) {
        for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 1; j < routine.size() - 1; j++) {
                double diff = evalSwap(routine, i, j);
                if (diff + 0.00001 < 0) { 
                    swapCity(routine, i, j);
                    //return true;
                }
            }
        }
        return false;
    }
    
    
    
    
    
    
    //other indiviual methods 
    
    //get total distance of routine
    public static double getDistance(ArrayList<City> routine){      
        double totalDistance = 0.0;
        for (int i = 0; i < routine.size() - 1; i++) {
            totalDistance += routine.get(i).distance(routine.get(i+1));
        }
        return totalDistance;
    }
    
    //in my own method, cities of number num swap/move at the same time, so it need another premeter num
    public static double evaluateRoutine2(ArrayList<City> routine, int index1, int index2, int num) {       
        double totalDistance = 0.0;
        for (int i = index1 - 1; i <= index2 + num - 1; i++) {
            totalDistance += routine.get(i).distance(routine.get(i+1));
        }
        return totalDistance;
    }

    //move cities of number num at the same time, for example if num = 2 then move city(from) and city(from + 1) to city(to) and city(to + 1)
    private static void moveCity2(ArrayList<City> routine, int from, int to, int num) {
        int i = 0;
        ArrayList<City> tempCity = new ArrayList<>();
        while(i < num){
            City temp = routine.get(from);
            routine.remove(from);
            tempCity.add(temp);
            i++;
        }
        i = 0;
        while(i < num){
            routine.add(to + i,tempCity.get(i));
            i++;
        }
    }
    
    public static double evalMove2(ArrayList<City> routine, int from, int to, int num) {
        double oldDistance = evaluateRoutine2(routine,from,to, num);
        moveCity2(routine, from, to, num);
        double newDistance = evaluateRoutine2(routine,from, to, num);
        return oldDistance - newDistance;
    }

    public static boolean moveFirstImprove2(ArrayList<City> routine, int num) {
        for (int i = 1; i < routine.size() - num; i++) {
            for (int j = i + num; j < routine.size() - num; j++) {
                double diff = evalMove2(routine, i, j, num);
                if (diff + 0.00001 < 0) { 
                    moveCity2(routine, j, i, num);
                    //return true;
                }
            }
        }
        return false;
    }
    
    //swap cities of number num at the same time, for example if num = 2, then swap city(index1) and city(index1 + 1) with city(index2) and city(index2 + 1)
    public static void swapCity2(ArrayList<City> routine, int index1, int index2, int num) {
        int i = 0;
        while(i < num){
            City temp = routine.get(index1 + i);
            routine.set(index1 + i, routine.get(index2 + i));
            routine.set(index2 + i, temp);
            i++;
        }
        
    }

    public static double evalSwap2(ArrayList<City> routine, int index1, int index2, int num) {
        double oldDistance = evaluateRoutine2(routine,index1,index2, num);
        swapCity2(routine, index1, index2, num);
        double newDistance = evaluateRoutine2(routine,index1,index2, num);
        return oldDistance - newDistance;
    }

    public static boolean swapFirstImprove2(ArrayList<City> routine, int num) {
        for (int i = 1; i < routine.size() - num; i++) {
            for (int j = i + num; j < routine.size() - num; j++) {
                double diff = evalSwap2(routine, i, j, num);
                if (diff + 0.00001 < 0) {
                    swapCity2(routine, i, j, num);
                    //return true;
                }
            }
        }
        return false;
    }
    
    //improve by reverse the order of cities between index1 and index2
    public static void reverse(ArrayList<City> routine, int index1 , int index2){
        ArrayList <City> temp = new ArrayList <>(routine.subList(index1, index2));
        Collections.reverse(temp);
        int  j = 0;
        for(int i = index1; i < index2; i++){
            routine.set(i, temp.get(j));
            j++;
        }
    }
    
    public static double reverseEval(ArrayList<City> routine, int index1 , int index2){
        double oldDistance = evaluateRoutine(routine,index1,index2);
        reverse(routine, index1, index2);
        double newDistance = evaluateRoutine(routine,index1,index2);
        return oldDistance - newDistance;
    }
    
    public static void reverseImprove(ArrayList<City> routine){
       for (int i = 1; i < routine.size() - 1; i++) {
            for (int j = i + 1; j < routine.size() - 1; j++) {
                double diff = reverseEval(routine, i, j);
                if (diff + 0.00001 < 0) { 
                    reverse(routine, i, j);
                    //return true;
                }
        }
    }
}
    
    public static ArrayList<City> improveRoutine(ArrayList<City> routine) {
        for(int i = 0; i < 2; i++)
           swapFirstImprove(routine);
        for(int i=1; i < 15; i++)
            swapFirstImprove2(routine, i);
        for(int i = 0; i < 10; i++)
            moveFirstImprove(routine);
        for(int i=0; i < 15; i++)
            moveFirstImprove2(routine, i);
        for (int  i = 0; i < 10 ; i++)
            reverseImprove(routine);
        
        //annealImprove(routine);
        return routine;
    }
    
    
    
    /*
        public  static void annealImprove(ArrayList<City> routine){
        double currentTemperature = 200;
        double minTemperature = 1e-5;
        double coolRating = 0.998;
        ArrayList<City> currentSolution = new ArrayList<>();
        cloneCity(currentSolution, routine);
        while( currentTemperature > minTemperature){
            for(int i = 0; i < 1000; i++){
                currentSolution = newPath(currentSolution, currentTemperature);
            
            if(getDistance(currentSolution) < getDistance(routine)){
                routine = new ArrayList<City>(currentSolution);
            }
            }
            currentTemperature *= coolRating;

        }
    }
    public static double acceptanceProbability(double energy, double newEnergy, double temperature) {
        if (newEnergy < energy) {
            return 1.0;
        }
        return Math.exp((energy - newEnergy) / temperature);
        
    }
    public static ArrayList<City> newPath(ArrayList<City> currentPath, double temperature){
        ArrayList<City> newPath = new ArrayList<>();
        cloneCity(newPath, currentPath);
        int r1 = ((int) (1000 * Math.random())) + 1;
        int r2 = ((int) (1000 * Math.random())) + 1;
        while(r1 == r2){
            r1 = ((int) (1000 * Math.random())) + 1;
        }
        if(r1 > r2){
            int temp =r1;
            r1 = r2;
            r2 = temp;
        }
        reverse(newPath,r1,r2);
        double energy = getDistance(currentPath);
        double newEnergy = getDistance(newPath);
        if(acceptanceProbability(energy,newEnergy, temperature) > Math.random()){
            currentPath = new ArrayList<City>(newPath);
        }
        return currentPath;
    }
    public static void cloneCity(ArrayList<City> newPath, ArrayList<City> path){
        for(City ct : path){
            newPath.add(ct);
        }
    }
    */
}
