/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package searchEngine;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.text.*;

/**
 * This class loads the index to memory, then handles queries and returns search results.
 * The results and then ranked and outputted to stdout.
 *
 * @author johnny
 */
public class Searcher {
    
    public static HashMap<Integer,String> dictionary;
    public static HashMap<String,ArrayList<Posting>> invertedIndex;
    public static HashMap<String,PostingInfo> metaData;
    public static ArrayList<String> queryList;
    
    public static void main(String [] args){
        
        long start = System.currentTimeMillis();
        metaData = deserializeMetadata("./data/metaData");
        long end = System.currentTimeMillis();
        
        dictionary = deserializeDict("./data/dictionary");
        
        
        
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.err.print("index loading time: " + formatter.format((end - start) / 1000d) + " seconds\n");
        
        
        start = System.currentTimeMillis();
        queryList = parseQuery();
        end = System.currentTimeMillis();
        
        
        System.err.print("query pre-processing time: " + formatter.format((end - start) / 1000d) + " seconds\n");
        
        
        start = System.currentTimeMillis();
        lookUp(queryList,metaData);
        end = System.currentTimeMillis();
        
        System.err.print("Search time: " + formatter.format((end - start) / 1000d) + " seconds\n");
        
        
        
        
    }
    
    public static void readRandomAccessFile(String filePath, long pos, long size) throws IOException{
        int [] docIDs = new int [(int)size];
        int [] frequencies = new int [(int)size];
        
        
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        
        file.seek(pos);
        
        for(int i = 0; i < size;i++){
            docIDs[i] = file.readInt();
            frequencies[i] = file.readInt();
        }
        
        System.out.println("hits: " + size);
        int totalCount = 0;
        
        
        for(int i = 0; i < size;i++){
            if (i > 0){
                docIDs[i] = docIDs[i] + docIDs[i-1];
            }
            System.out.println("docID: " + docIDs[i] + " Frequencies: " + frequencies[i]);
            totalCount += frequencies[i];
        }
        
        System.out.println("posting list length: " + size);
        System.out.println("total hits: " + totalCount);
        
    }
    
    public static void lookUp(ArrayList<String> queries,HashMap<String,PostingInfo> metaData){
        
        for (String s:queryList){
            System.out.println("You searched for: " + s);
            
            if (!metaData.containsKey(s)){
                System.out.println("Term not found");
                return;
            }else{
                try {
                    long pos = metaData.get(s).getPos();
                    long size = metaData.get(s).getSize();
                    
                    
                    readRandomAccessFile("./data/index.dat",pos,size);
                    
//                    NumberFormat formatter = new DecimalFormat("#0.00000");
//                    System.out.print("Execution time is " + formatter.format((end - start) / 1000d) + " seconds\n");
//

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
    }
    
    public static HashMap<String,PostingInfo> deserializeMetadata(String name){
        String filename = name + ".dat";
        HashMap<String,PostingInfo> dict = new HashMap<>();
        
        try{
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gs = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gs);
            
            dict = (HashMap)ois.readObject();
            
            ois.close();
            fis.close();
        }catch(IOException e){
            System.out.println("term not in collection");
        }catch(ClassNotFoundException c){
            System.out.println("Class not found.");
            c.printStackTrace();
        }
        
        System.out.println("Deserialized " + filename);
        return dict;
    }
    
     public static HashMap<Integer,String> deserializeDict(String name){
        String filename = name + ".dat";
        HashMap<Integer,String> dict = new HashMap<>();
        
        try{
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gs = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gs);
            
            dict = (HashMap)ois.readObject();
            
            ois.close();
            fis.close();
        }catch(IOException e){
            System.out.println("term not in collection");
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
