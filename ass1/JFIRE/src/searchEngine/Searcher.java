/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

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
        
        deserializeData();
      //  printIndex(dictionary, invertedIndex);
       
    }
    
    public static void deserializeData(){
        String filename = "dictionary.ser";
        try{
            
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            
            dictionary = (HashMap)ois.readObject();
            
            ois.close();
            fis.close();
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException c){
            System.out.println("Class not found.");
            c.printStackTrace();
        }
        
//        System.out.println("Deserialized " + filename);
//        
//        TreeMap <Integer,String> tm = new TreeMap<>(dictionary);
//        
//        for (Integer i : tm.keySet()){
//            System.out.println(i + "\t" + tm.get(i));
//        }
        
        System.out.println();
        filename = "index.ser";
        try{ 
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            
            invertedIndex = (HashMap)ois.readObject();
            
            ois.close();
            fis.close();
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException c){
            System.out.println("Class not found.");
            c.printStackTrace();
        }
        System.out.println("Deserialized " + filename);
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
    public static void parseQuery(){
        Scanner sc = new Scanner(System.in);
 
    }
    
}
