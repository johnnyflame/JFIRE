/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchEngine;

import java.io.Serializable;

/**
 *
 * @author johnny
 */
public class Posting implements Serializable{
    private int docID;
    private int termFrequency;
    private double rankScore;
    
    
    public Posting(int docID, int termFrequency){
        this.docID = docID;
        this.termFrequency = termFrequency;
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
