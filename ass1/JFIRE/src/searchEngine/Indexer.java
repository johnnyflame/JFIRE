/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package searchEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author johnny
 */
public class Indexer {
    
    public static HashMap<Integer,String> dictionary;
    
    public static HashMap<String,ArrayList<Posting>> invertedIndex;
    
    public static void main (String args[]){
       dictionary = makeDictionary();
       
       
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
        
        while(sc.hasNext()){
            String word = sc.nextLine();
            
            
            /* "case 0": If empty line is encountered,increment docID and do nothing. */
            if (word.isEmpty()){
                docID++;
            }
            
            /* Case 1: word not found in the collection. Do: Create an entry in the index for it */
            else{
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
                    
                    /* TODO: implement case 2. Think it through properly.*/
               
                    
                    
                    
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
        Scanner sc = new Scanner(System.in);
        HashMap<Integer,String> dictonary = new HashMap<>();
        
        int termCounter = 0, docID = 0;
        String DocNo = "";
        
        
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
                
                dictonary.put(docID, DocNo);                
                docID++;
                DocNo = "";
            }
        }
        
        return dictonary;
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
}