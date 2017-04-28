/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package searchEngine;

import java.io.*;


import java.util.zip.*;
import java.util.zip.Deflater;

import static java.lang.Math.log;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;





/**
 * Indexer.java
 * 
 * A program that creates inverted file index from tokens.
 * @author Johnny Flame Lee 2017
 */
public class Indexer {
    
    public static HashMap<Integer,DocInfo> dictionary = new HashMap<>();
    public static HashMap<String,ArrayList<Posting>> invertedIndex;
    public static HashMap<String,PostingInfo> metaData;
    
    public static void main (String args[]){
      
        
       
        invertedIndex = createIndex();
        deltaCompression(invertedIndex);
        
        try{
        metaData = writeRandomAccessFile(invertedIndex);
        }catch(IOException e){
            System.err.println("IO error!");
            e.printStackTrace();
        }
        
        serialize(metaData, "metaData");
        serializeDict(dictionary,"dictionary");
    }
    
 
    
    
    public static void serialize(HashMap<String,PostingInfo> o,String name){
        try{
            String filename = name + ".dat";
            File output =  new File("./data/" + filename);
   
            FileOutputStream fos = new FileOutputStream(output);
            
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            
            
            
//            Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
//            DeflaterOutputStream dos = new DeflaterOutputStream(fos, def, 4 * 1024);   
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            
            oos.writeObject(o);
            oos.flush();
            oos.close();
            fos.close();
      //      System.out.println("Serialized dictionary data has been saved in: " + filename);
        }catch(IOException e){
            e.printStackTrace();
        }  
    }
    
    public static void serializeDict(HashMap <Integer,DocInfo> h,String name){
        
        System.out.println("doc collection size" + h.size());
           try{
            String filename = name + ".dat";
            File output =  new File("./data/" + filename);
   
            FileOutputStream fos = new FileOutputStream(output);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            
            oos.writeObject(h);
            oos.flush();
            oos.close();
            fos.close();
      //      System.out.println("Serialized dictionary data has been saved in: " + filename);
        }catch(IOException e){
            e.printStackTrace();
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
        DocInfo d;
        
        while(sc.hasNext()){
            termCounter++;
            String word = sc.nextLine();
            
            
            /* "case 0": If empty line is encountered,increment docID and do nothing. */
            if (word.isEmpty()){
                d = new DocInfo(DocNo,termCounter);
                dictionary.put(docID, d);
                
                
                docID++;
                termCounter = 0;
                DocNo = "";
            }
            
            /* Case 1: word not found in the collection. Do: Create an entry in the index for it */
            else{
                
                if(termCounter == 1){
                    DocNo += word;
                }
                
                if (termCounter == 2){
                    DocNo += "-" + word;    
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
        d = new DocInfo(DocNo,termCounter);
        dictionary.put(docID, d);
        return index;
    }

    
    /**
     * Calculate the diff value between two numbers.
     * @param index 
     */
    public static void deltaCompression(HashMap<String,ArrayList<Posting>> index ){
        
//        TreeMap<String,ArrayList<Posting>> index = new TreeMap<>(index1);

for (String key : index.keySet()){
    ArrayList<Posting> currentPostingList = index.get(key);
    //System.out.println(key);
    
    int[] actualDocID = new int [currentPostingList.size()];
    int [] diffArray = new int[currentPostingList.size()];
    diffArray[0] = currentPostingList.get(0).getDocID();
    actualDocID[0] = diffArray[0];
    
    
    for (int i = 1; i < currentPostingList.size();i++){
        int diff = currentPostingList.get(i).getDocID() - currentPostingList.get(i-1).getDocID();
        diffArray[i] = diff;
        actualDocID[i] = currentPostingList.get(i).getDocID();
    }
    
    for (int i = 0; i < currentPostingList.size();i++){
        currentPostingList.get(i).setDocID(diffArray[i]);
    }
}

System.out.println();
System.out.println("Number of unique entries: " + index.size());
    }

    
    public static HashMap<String,PostingInfo> writeRandomAccessFile(HashMap<String,ArrayList<Posting>> index) 
    throws IOException{
        RandomAccessFile file = new RandomAccessFile("./data/index.dat", "rw");

        HashMap<String,PostingInfo> metaData = new HashMap();
         // Storing the start and end file pointer of docID and frequency
        
        for (String s: index.keySet()){
            
            ArrayList<Posting> currentPosting = index.get(s);
        
            long startingPos = file.getFilePointer();
            int size = currentPosting.size();
            
      //      PostingInfo p = new PostingInfo(startingPos, size);
            
            ArrayList<Integer> currentPostingDocIDs = new ArrayList<>();
            ArrayList<Integer> currentPostingFrequencies = new ArrayList<>();
          
            for(int i = 0;i < currentPosting.size();i++){
                currentPostingDocIDs.add(currentPosting.get(i).getDocID());
                currentPostingFrequencies.add(currentPosting.get(i).getFrequency());
            }
            
            byte[] docIDByteArray = encode(currentPostingDocIDs);
            byte[] frequencyByteArray = encode(currentPostingFrequencies);
            
            int docIDSize = docIDByteArray.length;
       //     System.out.println("docID byte array size before writing to disk: " + docIDSize);
            int frequencySize = frequencyByteArray.length;
            
            PostingInfo p = new PostingInfo(startingPos, docIDSize, frequencySize);
            
            file.write(docIDByteArray);
            file.write(frequencyByteArray);
          
            metaData.put(s, p);
           
        }
        file.close();
        return metaData;
    }
    
    
    
    /* Variable byte encoding from https://gist.github.com/zhaoyao/1239611 */
    
    
    
    private static byte[] encodeNumber(int n) {
        if (n == 0) {
            return new byte[]{0};
        }
        int i = (int) (log(n) / log(128)) + 1;
        byte[] rv = new byte[i];
        int j = i - 1;
        do {
            rv[j--] = (byte) (n % 128);
            n /= 128;
        } while (j >= 0);
        rv[i - 1] += 128;
        return rv;
    }

    public static byte[] encode(List<Integer> numbers) {
        ByteBuffer buf = ByteBuffer.allocate(numbers.size() * (Integer.SIZE / Byte.SIZE));
        for (Integer number : numbers) {
            buf.put(encodeNumber(number));
        }
        buf.flip();
        byte[] rv = new byte[buf.limit()];
        buf.get(rv);
        return rv;
    }

 
     
            
}
    



