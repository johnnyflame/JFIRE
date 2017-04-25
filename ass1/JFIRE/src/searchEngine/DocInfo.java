/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchEngine;

import java.io.Serializable;

/**
 *
 * @author Johnny Flame Lee 2017
 */
public class DocInfo implements Serializable{
    private int length;
    private final String DocNo;
    
    public DocInfo(String DocNo,int length){
        this.DocNo = DocNo;
        this.length = length;
    }
    public void setDocLength(int length){
        this.length = length;
    }
    
    public String getDocNo(){
        return this.DocNo;
    }
    public int getDocLength(){
        return this.length;
    }
    
}
