/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchEngine;

/**
 *
 * @author johnny
 */
public class Posting {
    private Integer docID;
    private Integer termFrequency;
    
    
    public Posting(int docID, int termFrequency){
        this.docID = docID;
        this.termFrequency = termFrequency;
    }
    
    public int getDocID(){
        return docID;
    }
    
    public int getFrequency(){
        return termFrequency;
    }
    
    
    public void incrementFrequency(){
        this.termFrequency++;
    }
    
    @Override
    public String toString(){
        return docID.toString() + " " + termFrequency.toString();
    }
}