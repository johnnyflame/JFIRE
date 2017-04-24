/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package searchEngine;

import java.io.*;
import java.util.*;

import java.util.zip.*;

import static java.lang.Math.log;
import java.nio.*;

//import gnu.trove.*;


/**
 *
 * @author johnny
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
        
        TreeMap <Integer,DocInfo> tm = new TreeMap<>(dictionary);
        
        for (Integer i : tm.keySet()){
            System.out.println(i + "\t" + tm.get(i).getDocNo());
        }
        
        System.out.println("Doc collection length: " + tm.size());
        
    }
    
    /**
     * Serialize data and write to disk.
     * @param dict the dictionary mapping between docNo and docID
     * @param index the inverted file index.
     */
    public static void serialize(HashMap<String,PostingInfo> o,String name){
        try{
            String filename = name + ".dat";
            File output =  new File("./data/" + filename);
   
            FileOutputStream fos = new FileOutputStream(output);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
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

    public static void writeSingleEntry(HashMap<String,ArrayList<Posting>> index1){
        
        TreeMap<String,ArrayList<Posting>> index = new TreeMap<>(index1);  
        HashMap <String,ArrayList<Posting>> singleEntry = new HashMap<>();
        
        for (String key:index.keySet()){
            singleEntry.put(key, index.get(key));
          for (Posting p:index.get(key)){
              
          }  
          
        try{
            String filename = key + ".ser";
            File output =  new File("./data/" + filename);
            FileOutputStream fos = new FileOutputStream(output);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            
            oos.writeObject(singleEntry);
            oos.flush();
            oos.close();
            fos.close();
          //  System.out.println("Serialized dictionary data has been saved in: " + filename);
            singleEntry.clear();
        }catch(IOException e){
            e.printStackTrace();
        }
        
        
        }
    }
    public static void writeBlockedIndex(HashMap<String,ArrayList<Posting>> index,char p){
        
        
        try{
            char prefix = p;
            String filename = prefix + ".ser";
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
        
        System.out.println("Doc collection length: " + tm.size());
        
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
//
    public static void divideBlocks(HashMap<String,ArrayList<Posting>> index1){
        TreeMap<String,ArrayList<Posting>> index = new TreeMap<>(index1);
        TreeMap<String,ArrayList<Posting>> indexBuffer = new TreeMap<>();
        
        char filename = '0';
        
        int totalCount = 0;
        int currentCount = 0;
        for(int i = 0;i < index.size();i++){
            String key = (String) index.keySet().toArray()[i];
            indexBuffer.put(key, index.get(key));
            currentCount++;
            

            if (i < index.size()-1){
                String nextKey = (String) index.keySet().toArray()[i+1];
                if (key.charAt(0) != nextKey.charAt(0) && (!Character.isDigit(nextKey.charAt(0)))){
                    
                    
                    if((!Character.isDigit(key.charAt(0)))){
                        filename = key.charAt(0);
                    }
                    
                    writeBlockedIndex(new HashMap(indexBuffer), filename);
                    totalCount += currentCount;
                    System.out.println("Tokens starting with " + filename+ ": " + currentCount);
                    System.out.println("----------------------------------------------");
                    
                    currentCount=0;
                    indexBuffer.clear();
                }
            }
        }
        
        filename = 'Z';
        
        writeBlockedIndex(new HashMap(indexBuffer), filename);
        
        
        totalCount += currentCount;
        System.out.println("Tokens starting with z : " + currentCount);
        System.out.println("----------------------------------------------");
        
        currentCount=0;
        indexBuffer.clear();
        
        System.out.println("total count of new keys: " + totalCount);
        
        
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
            
            PostingInfo p = new PostingInfo(startingPos, size);
            
//            ArrayList<Integer> currentPostingDocIDs = new ArrayList<>();
//            ArrayList<Integer> currentPostingFrequencies = new ArrayList<>();
          
            for(int i = 0;i < currentPosting.size();i++){
                file.writeInt(currentPosting.get(i).getDocID());
                file.writeInt(currentPosting.get(i).getFrequency());
            }
            
          
            metaData.put(s, p);
           
        }
        file.close();
        return metaData;
    }
    
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

    public static List<Integer> decode(byte[] byteStream) {
        List<Integer> numbers = new ArrayList<Integer>();
        int n = 0;
        for (byte b : byteStream) {
            if ((b & 0xff) < 128) {
                n = 128 * n + b;
            } else {
                int num = (128 * n + ((b - 128) & 0xff));
                numbers.add(num);
                n = 0;
            }
        }
        return numbers;
    }
     
            
        }
    



