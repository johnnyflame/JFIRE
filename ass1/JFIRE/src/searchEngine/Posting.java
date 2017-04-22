/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author johnny
 */
public class Posting implements Serializable{
    private int docID;
    private int termFrequency;
    private double rankScore;
    private HashMap<String,Integer> resultTermFrequency;
    private HashMap<String,Double> tfIDf;
    
    public void put(String key, Integer value){
        this.resultTermFrequency.put(key, value);
    }
    
    public double getRankScore(){
        return this.rankScore;
    }
    
    public void updateTFIDF(String s, Double value){
        tfIDf.put(s, value);
    }
    
    public HashMap<String,Double> getTFIDF(){
        return this.tfIDf;
    }
    
    public void setRankScore(double rank){
        this.rankScore = rank;
    }
    
   
    
    public HashMap<String,Integer> getResultTermFrequency(){
        return this.resultTermFrequency;
    }
    
    
    public Posting(int docID, int termFrequency){
        this.docID = docID;
        this.termFrequency = termFrequency;
        this.rankScore = 0;
        this.resultTermFrequency = new HashMap<>();
        this.tfIDf = new HashMap<>();
    }
    
    public int getDocID(){
        return docID;
    }
    
    public void setDocID(int i){
        this.docID = i;
    }
    
    public int getFrequency(){
        return termFrequency;
    }
    
    
    public void incrementFrequency(){
        this.termFrequency++;
    }
    
    public void setFrequency(int i){
        this.termFrequency = i;
    }
    
//    @Override
//    public String toString(){
//        return docID.toString() + " " + termFrequency.toString();
//    }
}
