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
import java.util.logging.Level;
import java.util.logging.Logger;

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
        
        
        try {
            metaData = deserializeMetadata("./data/metaData");
            dictionary = deserializeDict("./data/dictionary");
            
            System.out.println("Type in your query: ");
            queryList = parseQuery();
          
            lookUp(queryList,metaData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public static ArrayList<Posting> seekPostings(RandomAccessFile file, long pos, long size) throws IOException{
        
        ArrayList<Posting> postings = new ArrayList<>();

        file.seek(pos);
        
        for(int i = 0; i < size;i++){
            int id = file.readInt();
            int freq = file.readInt();
            
            Posting p = new Posting(id,freq);
            postings.add(p);
        }
        
        
        for(int i = 0; i < size;i++){
            if (i > 0){
                int previous = postings.get(i-1).getDocID();
                postings.get(i).setDocID(previous + postings.get(i).getDocID());
            }
        }
        return postings;
    }
    
    public static void lookUp(ArrayList<String> queries,HashMap<String,PostingInfo> metaData) throws IOException{
        RandomAccessFile file = new RandomAccessFile("./data/index.dat", "r");
        ArrayList<Posting> result;
        
        ArrayList<ArrayList<Posting>> results = new ArrayList();
        
        for (String s:queryList){
            System.out.println("You searched for: " + s);
            
            if (!metaData.containsKey(s)){
                System.out.println("Term not found");
                return;
            }else{
                    long pos = metaData.get(s).getPos();
                    long size = metaData.get(s).getSize();
                    
                    ArrayList<Posting> termResult = seekPostings(file,pos,size);
                    results.add(termResult);
            }
        }
        
        int count = 0;
        result = merge(results);
        for(Posting p : result){
            System.out.println("docID: " + p.getDocID() + "\t frequency: " +
                    p.getFrequency());
            count++;
        }
        System.out.println("count: " + count);
    }
        
        
        
    
    
    public static ArrayList<Posting> merge(ArrayList<ArrayList<Posting>> results){
        ArrayList<Posting> output = results.get(0);
        
        for(int i = 1;i < results.size();i++){
            output = intersect(output, results.get(i));
        }
         return output;
    }
    
    /**
     * Performs boolean AND merge naively. Can be optimized later if needed.
     * @param p1
     * @param p2
     * @return 
     */
    public static ArrayList<Posting> intersect(ArrayList<Posting> p1,ArrayList<Posting> p2){
        ArrayList<Posting> answer = new ArrayList<>();
        int i = 0;
        int j = 0;
        
        while (i < p1.size() && j < p2.size()){
            if (p1.get(i).getDocID() == p2.get(j).getDocID()){
                answer.add(p1.get(i));
                i++;
                j++;
            }else if (p1.get(i).getDocID() < p2.get(j).getDocID()){
                i++;
            }else{
                j++;
            }
                
        }
        return answer;    
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
