/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package searchEngine;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * This class loads the index to memory, then handles queries and returns search results.
 * The results and then ranked and outputted to stdout.
 *
 * @author johnny
 */
public class Searcher {
    public static HashMap<Integer,String> dictionary;
    public static HashMap<String,ArrayList<Posting>> invertedIndex;
    
    public static ArrayList<String> queryList;
    
    public static void main(String [] args){
     //   queryList = parseQuery();
 

    }
    
    public static void lookUp(ArrayList<String> queries){
        for (String s:queryList){
            //retrieve results here
        }
    }
    
    public static HashMap<String,ArrayList<Posting>> deserializeData(String name){
        String filename = name + ".ser";
        HashMap<String,ArrayList<Posting>> dict = new HashMap<>();
        try{
            
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gs = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gs);
            
             dict = (HashMap)ois.readObject();
            
            ois.close();
            fis.close();
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException c){
            System.out.println("Class not found.");
            c.printStackTrace();
        }
        
        System.out.println("Deserialized " + filename);
        return dict;
    }
    
    public static void printIndex(HashMap<Integer,String> dict, HashMap<String,
            ArrayList<Posting>> index){
        
        TreeMap<String,ArrayList<Posting>> tm = new TreeMap(index);
        
        
        for (String term:tm.keySet()){
            System.out.println(term + "\t");
            
            for(Posting p : tm.get(term)){
                System.out.println(p.getDocID() + "\t: " +
                        p.getFrequency());
            }
            System.out.println();
            
        }
    }
    
    
    public static ArrayList<String> parseQuery(){
        Scanner sc = new Scanner(System.in);
        ArrayList<String> queries = new ArrayList<>();
        
        while (sc.hasNext()){
            String term = sc.next();
            term = term.replaceAll("\\W", " ");
            
            Scanner cleanser = new Scanner(term);
            while (cleanser.hasNext()){
                queries.add(cleanser.next().toUpperCase());
            }
        }
        
        
        return queries;
    }
    
}
