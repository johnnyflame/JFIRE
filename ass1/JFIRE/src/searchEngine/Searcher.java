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
 * @author Johnny Flame Lee 2017
 */
public class Searcher {
    
    public static HashMap<Integer,DocInfo> dictionary;
    public static HashMap<String,PostingInfo> metaData;
    public static DocInfoArray docInfo;
    
    public static int docCollectionLength;
    
    public static void main(String [] args){
        ArrayList<String> queryList;
        
        System.out.println("Welcome to JFIRE");
        
        

        
        //      metaData = deserializeMetadata("./data/metaData");
        //      dictionary = deserializeDict("./data/dictionary");
       
        
        docInfo = deserializeDictArray("./data/dictionary.dat");
        docCollectionLength = docInfo.size();
        
          
        while (true){
            System.out.println("Type in your query: ");
            queryList = parseQuery();
            
            if(queryList.isEmpty()){
                System.out.println("Quitting now. JFIRE wish you a nice day.");
                System.exit(0);
            }
            else{
                try {
                    metaData = getMetaData(queryList);
                    lookUp(queryList,metaData);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                
            }
        }
    }
        
    
    public static DocInfoArray deserializeDictArray(String filename){
        DocInfoArray output = null;
        
        try{
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gs = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gs);
            
            output = (DocInfoArray)ois.readObject();
            
            ois.close();
            fis.close();
        }catch(IOException e){
            System.out.println("file not found");
            System.exit(1);
        }catch(ClassNotFoundException c){
            System.out.println("Class not found.");
            c.printStackTrace();
        }
        
        //  System.out.println("Deserialized " + filename);
        return output;
    }
    
    
    
    
    /* Return a HashMap of terms in this query by repeatedly seeking for the terms on the disk*/
    public static HashMap<String,PostingInfo> getMetaData(ArrayList<String> queries){
        HashMap<String,PostingInfo> metadata = new HashMap<>();
        
        for(String s : queries){
            HashMap<String,PostingInfo> tmp = deserializeSingleEntry(s);
            metadata.put(s,tmp.get(s));
        }
        return metadata;
}
    
    
    public static ArrayList<ResultPosting> seekPostings(RandomAccessFile file,PostingInfo pi) throws IOException{
        long pos = pi.getPos();
        int docIDSize = pi.getIDSize();
        int freqSize = pi.getfrequencySize();
        
        
        System.out.println("LOOKHERE--item ID byteSIZE: " + docIDSize);
        System.out.println("LOOKHERE--item freq byteSIZE: " + freqSize);
        
        ArrayList<ResultPosting> postings = new ArrayList<>();
        
        //now we're in the correct position for retrieval
        file.seek(pos);
        
        byte[] IDTmp = new byte[docIDSize];  
        file.readFully(IDTmp);
        
         //off by 1 here.
        ArrayList<Integer> docIDs = new ArrayList<>(decode(IDTmp));
     
        
    
        
        byte[] freqTmp = new byte[freqSize];
        file.readFully(freqTmp);
        ArrayList<Integer> freqencies = new ArrayList<>(decode(freqTmp));
        
        
       
        int count = 0;
        
       
        
     
//        
    
//         for(Integer i : freqencies){
//            count++;
//            System.out.println(i);
//            System.out.println("count " + count);
//        }
    
        for(int i = 0; i < docIDs.size();i++){
            int id = docIDs.get(i) - 1; //NOTE: variable byte encoding swallows up zeros, so the docIDs are padded out.
            int freq = freqencies.get(i);
            ResultPosting p = new ResultPosting(id,freq);
            postings.add(p);
        }
        
        
        //undiff
        for(int i = 0; i < docIDs.size();i++){
            if (i > 0){
                int previous = postings.get(i-1).getDocID();
                postings.get(i).setDocID(previous + postings.get(i).getDocID());
            }
        }
        return postings;
    }
    
    public static void lookUp(ArrayList<String> queries,HashMap<String,PostingInfo> metaData) throws IOException{
        RandomAccessFile file = new RandomAccessFile("./data/index.dat", "r");
        ArrayList<ResultPosting> result = null;
        HashMap<String,ArrayList<ResultPosting>> results = new HashMap<>();
        
        
        HashMap<String,Double> invertedDocFreq = new HashMap<>();
        
        
        for (String s:queries){            
            if (!metaData.containsKey(s)){
                System.out.println("Term not found");
                return;
            }else{
                        
                ArrayList<ResultPosting> termResult = seekPostings(file,metaData.get(s));
                
               
                
                results.put(s,termResult);
               
              
                double idf = Math.log((double)docCollectionLength/termResult.size());
                invertedDocFreq.put(s, idf);
            }
        }
        
        
        
        int count = 0;
        
        
        result = merge(queries,results);
        
        
        
        Comparator<ResultPosting> c = new Comparator<ResultPosting>() {
            @Override
            public int compare(ResultPosting p1, ResultPosting p2) {
                return p1.getID().compareTo(p2.getDocID());
            }
        };
        //Do binary search here.
        
        for(int i = 0;i < result.size();i++){
            for(String s:results.keySet()){
                ArrayList<ResultPosting> rawResultListPerTerm = results.get(s);
                int index = Collections.binarySearch(rawResultListPerTerm, result.get(i), c);
                // System.out.println("found in: "+ index);
                result.get(i).put(s, rawResultListPerTerm.get(index).getFrequency());
            }
        }
        
        
        
        
        
        for(ResultPosting p : result){
            for (String s : p.getResultTermFrequency().keySet()){
                int tf = p.getResultTermFrequency().get(s);
                int docLength = docInfo.getLengthItem(p.getDocID());
          //      System.out.println("new doc Length : "+ docLength);
                 // old way to get docLength, incase the new one doesnt work.
                // docLength = dictionary.get(p.getDocID()).getDocLength();
             //   System.out.println("old docLength: " + docLength);
                
             
          //   System.out.println("tf : " + tf);
             double tfNormalized = (double)tf/docLength;
         //    System.out.println("tfNormalized: " + tfNormalized);
             double tfidf = tfNormalized * invertedDocFreq.get(s);
        //     System.out.println("tfidf: " + tfidf);
             
                
                p.updateTFIDF(s, tfidf);
            }
        }
        
        for (ResultPosting p: result){
            double accumulator = 0;
            for(String s: p.getTFIDF().keySet()){
                accumulator += p.getTFIDF().get(s);
            }
            p.setRankScore(accumulator);
            //       System.out.println("Rankscore after set : " + accumulator);
        }
        
        //TODO:  SORT posting objects by rank score.
        
        Comparator<ResultPosting> comparator = new Comparator<ResultPosting>() {
            @Override
            public int compare(ResultPosting p1, ResultPosting p2) {
                if(p1.getRankScore() < p2.getRankScore()) return 1;
                if(p1.getRankScore() == p2.getRankScore())return 0;
                return -1;
            }//To change body of generated methods, choose Tools | Templates.
        };
        
        
        Collections.sort(result, comparator);
        
        for(ResultPosting p : result){
//            dictionary.get(p.getDocID()).getDocNo()
            System.out.println(docInfo.getDocNoItem(p.getDocID()) + "\t" + p.getRankScore());
            count++;
        }
        System.out.println("count: " + count);
    }
    
//NOTE: Ranking has to be done before the merge happens. Because we'll need the IDF
    
    
    
    public static ArrayList<ResultPosting> merge(ArrayList<String> querieTerms,HashMap<String,ArrayList<ResultPosting>> results){
        ArrayList<ResultPosting> output = results.get(querieTerms.get(0));
        
        for(int i = 1;i < querieTerms.size();i++){
            output = intersect(output, results.get(querieTerms.get(i)));
        }
        return output;
    }
    
    public static ArrayList<ResultPosting> intersect(ArrayList<ResultPosting> p1,ArrayList<ResultPosting> p2){
        ArrayList<ResultPosting> answer = new ArrayList<>();
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
    
    
    public static HashMap<String,PostingInfo> deserializeSingleEntry(String name){
        String filename = "./data/metadata/"+ name + ".metadat";
        HashMap<String, PostingInfo> termMetaData = new HashMap<>();
        
        try{
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gs = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gs);
            
            termMetaData = (HashMap)ois.readObject();
            ois.close();
            fis.close();
            
        }catch(IOException e){
            System.out.println("The term you searched for is not in the doc collection");
            System.exit(1);
        }catch(ClassNotFoundException c){
            System.out.println("Class not found.");
            System.exit(1);
        }
        return termMetaData;   
    }
        
    
    
    public static HashMap<String,PostingInfo> deserializeMetadata(String name){
        String filename = name + ".dat";
        HashMap<String,PostingInfo> dict = new HashMap<>();
        
        try{
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gs = new GZIPInputStream(fis);        
            //     InflaterInputStream inf = new InflaterInputStream(fis);
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
        
        //  System.out.println("Deserialized " + filename);
        return dict;
    }
    
    public static HashMap<Integer,DocInfo> deserializeDict(String name){
        String filename = name + ".dat";
        HashMap<Integer,DocInfo> dict = new HashMap<>();
        
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
        
        //  System.out.println("Deserialized " + filename);
        return dict;
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
    
    
    
    /**
     * decodes variable byte encoded items. 
     * 
     * Reference : https://gist.github.com/zhaoyao/1239611
     * 
     * @param byteStream a byte stream of VB encoded ints.
     * @return a list of Integers post decoding.
     */
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
