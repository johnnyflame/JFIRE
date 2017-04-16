/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
        
        
       
    }
    
    public static void deserialize(){
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
        
        System.out.println("Deserialized " + filename);
        
        TreeMap <Integer,String> tm = new TreeMap<>(dictionary);
        
        for (Integer i : tm.keySet()){
            System.out.println(i + "\t" + tm.get(i));
        }
    }
    
    public static void parseQuery(){
        Scanner sc = new Scanner(System.in);
        
        
        
        
        
    }
    
}
