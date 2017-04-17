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
import java.util.zip.*;

/**
 *
 * @author johnny
 */
public class Indexer {
    
    public static HashMap<Integer,String> dictionary = new HashMap<>(); 
    public static HashMap<String,ArrayList<Posting>> invertedIndex;
    
    public static void main (String args[]){
     //   dictionary = makeDictionary();
        invertedIndex = createIndex();
        deltaCompression(invertedIndex);
        //printIndex(dictionary,invertedIndex);
        //serialize(dictionary, invertedIndex);
  
    }
    
    /**
     * Serialize data and write to disk.
     * @param dict the dictionary mapping between docNo and docID
     * @param index the inverted file index.
     */
    public static void serialize(HashMap<Integer,String> dict,
            HashMap<String,ArrayList<Posting>> index){
        
        try{
            String filename = "dictionary.ser";
            FileOutputStream fos = new FileOutputStream(filename);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            
            oos.writeObject(dict);
            oos.flush();
            oos.close();
            fos.close();
            System.out.println("Serialized dictionary data has been saved in: " + filename);
        }catch(IOException e){
            e.printStackTrace();
        }
        
        try{
            String filename = "index.ser";
            FileOutputStream fos = new FileOutputStream(filename);
            
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            
            oos.writeObject(index);
            oos.flush();
            oos.close();
            fos.close();
            System.out.println("Serialized index data has been saved in: " + filename);
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    
    public static void printIndex(HashMap<Integer,String> dict, HashMap<String,
            ArrayList<Posting>> index){
        
        TreeMap<String,ArrayList<Posting>> tm = new TreeMap(index);
        
        
        for (String term:tm.keySet()){
            System.out.println(term + "\t");
            
            for(Posting p : tm.get(term)){
                System.out.println(dict.get(p.getDocID()) + "\t: " +
                        p.getFrequency());
            }
            System.out.println();
            
        }
    }
        
        /**
         * Creates an inverted index of the document collection.
     *
     * @return the index
     */
    public static HashMap<String,ArrayList<Posting>> createIndex(){
        
        HashMap<String,ArrayList<Posting>> index = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        int docID = 0; //docID is zero-indexed
        
        int termCounter = 0;
        String DocNo = "";
        
        while(sc.hasNext()){
            termCounter++;
            String word = sc.nextLine();
            
            
            /* "case 0": If empty line is encountered,increment docID and do nothing. */
            if (word.isEmpty()){
                docID++;
                termCounter = 0;
            }
            
            /* Case 1: word not found in the collection. Do: Create an entry in the index for it */
            else{
                
                if(termCounter == 1){
                    DocNo += word;
                }
                
                if (termCounter == 2){
                    DocNo += "-" + word;                 
                    dictionary.put(docID, DocNo);
                    DocNo = "";
                }
                
                
                if(!index.containsKey(word)){
                    
                    /* Create postinng, tf set to 1 since we've now seen it once. */                    
                    Posting posting = new Posting(docID, 1);                    
                    /* create a new Array List containg postings */
                    ArrayList<Posting> postingItem = new ArrayList<>();
                    postingItem.add(posting);
                    
                    /* finally, we put the entry in the index */
                    index.put(word, postingItem);
                }
                else{
                    /*Case 2: We know the entry exists, check if the last item is this docID*/
                    ArrayList<Posting>currentPostingList = index.get(word);
                    int lastIndex = currentPostingList.size()-1;
                    
                    if ((currentPostingList.get(lastIndex).getDocID()) != docID){
                        currentPostingList.add(new Posting(docID, 1));
                    }
                    /* Case 3: Entry has been seen in current Doc, increment frequency.*/
                    else{
                        currentPostingList.get(lastIndex).incrementFrequency();
                    }
                }   
            }   
        }
        return index;
    }
    /**
     * Extract the DocNo from the collection to use as primary key,put it in
     * a hashMap with the key-value pair as "docID(Integer): DocNo(String)"
     * 
     * NOTE: dictionary is zero-indexed. ie the docID start at 0
     * @return a dictionary for lookup between docID and DocNo
     */
    public static HashMap<Integer,String> makeDictionary(){
        
        HashMap<Integer,String> dict = new HashMap<>();
       
     
        
        int termCounter = 0, docID = 0;
        String DocNo = "";
        
       
        Scanner sc = new Scanner(System.in);
        
        while (sc.hasNext()){
            termCounter++;
            String token = sc.nextLine();
            // System.out.println(termCounter);
            
            // Reset the counter if an empty line is reached.
            if (token.isEmpty()){
                termCounter = 0;
            }
            
            if (termCounter == 1){
                DocNo += token;
            }
            if (termCounter == 2){
                DocNo += "-" + token;
                
                dict.put(docID, DocNo);
                docID++;
                DocNo = "";
            }
        }
        
        
        TreeMap <Integer,String> tm = new TreeMap<>(dict);
        
        for (Integer i : tm.keySet()){
            System.out.println(i + "\t" + tm.get(i));
        }
        
        return dict;
    }
    
    /**
     * Count the number of dictionary(unique) terms and their frequency in the document.
     */
    public static void countUniqueTerms(){
        HashMap<String,Integer> termsTable = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        
        while (sc.hasNext()){
            String token = sc.next();
            
            if(termsTable.containsKey(token)){
                int frequency = termsTable.get(token);
                termsTable.put(token, frequency + 1);
            }else{
                termsTable.put(token,1);
            }
        }
        
        for (String term:termsTable.keySet()){
            int value = termsTable.get(term);
            System.out.printf("%-25s  %d\n",term,value);
        }
        
        
        System.out.println();
        System.out.println("Dictionary Size: " + termsTable.size());
        
        
    }
    
    
    public static void deltaCompression(HashMap<String,ArrayList<Posting>> index1 ){
        
        TreeMap<String,ArrayList<Posting>> index = new TreeMap<>(index1);
        
        for (String key : index.keySet()){
            ArrayList<Posting> currentPostingList = index.get(key);
            System.out.println(key);
            
            int [] diffArray = new int[currentPostingList.size()];
            diffArray[0] = currentPostingList.get(0).getDocID();
            
            for (int i = 1; i < currentPostingList.size();i++){
                int diff = currentPostingList.get(i).getDocID() - currentPostingList.get(i-1).getDocID();
                diffArray[i] = diff;
            }
            
            for (int i = 0; i < currentPostingList.size();i++){
                
                currentPostingList.get(i).setDocID(diffArray[i]);
            }
            
            // System.out.println("docID: " + current.get(i).getDocID() + "\t"+"diff: " + diff);
            //  current.get(i).setDocID(diff); //docID field now holds the diff value.
              for (int i = 0; i < currentPostingList.size();i++){
              System.out.println("diffs: " + currentPostingList.get(i).getDocID());
            }
           
        }

    System.out.println();
    System.out.println("Number of unique entries: " + index.size());
}
//    
//    public static void 
}