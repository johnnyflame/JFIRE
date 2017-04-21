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
    private final int size;
    
    public PostingInfo(long pos, int size){
        this.pos = pos;
        this.size = size;
    }
    
    public long getPos(){
        return this.pos;
    }
    
    public int getSize(){
        return this.size;
    }
    
    
}
