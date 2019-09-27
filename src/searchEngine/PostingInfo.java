/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchEngine;

import java.io.Serializable;

/**
 *
 * @author theFlame
 */
public class PostingInfo implements Serializable {
    private final long pos;
    private final int docIDsize;
    private final int frequencySize;
    
    public PostingInfo(long pos, int docIDSize,int freqSize){
        this.pos = pos;
        this.docIDsize = docIDSize;
        this.frequencySize = freqSize;
    }
    
    public long getPos(){
        return this.pos;
    }
    
    public int getIDSize(){
        return this.docIDsize;
    }
    
    public int getfrequencySize(){
        return this.frequencySize;
    }
    
    
}
