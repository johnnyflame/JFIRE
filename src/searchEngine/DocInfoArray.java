/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchEngine;

import java.io.Serializable;

/**
 * A container class holding 3 items in lined-up arrays: docID, docNO and docLength
 * @author theFlame
 */
public class DocInfoArray implements Serializable {
    private final int [] docID;
    private final String [] docNo;
    private final int [] docLength;
    
    public DocInfoArray(int [] docID, String [] docNo,int[] docLength){
        this.docID = docID;
        this.docNo = docNo;
        this.docLength = docLength;
    }
    
    public int[] getdocIDArray(){
        return this.docID;
    }
    
    public String [] getDocNoArray(){
        return this.docNo;
    }
    
    public int [] getDocLength(){
        return this.docLength;
    }
    
    public int getDocIDItem(int i){
        return docID[i];
    }
    
    public String getDocNoItem(int i){
        return docNo[i];
    }
    
    public int getLengthItem(int i){
        return docLength[i];
    }
    
    
    public int size(){
        return this.docID.length;
    }
    
    
    
}
