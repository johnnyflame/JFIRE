/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package parser;
import java.util.Scanner;



/**
 * Parser.java
 * A simple XML parser, removes tags and convert everything to lower case
 * @author Johnny Flame Lee 2017
 */
public class Parser{
    public static void main(String [] args){
        
        Scanner sc = new Scanner(System.in);
        int count = 0;
        //boolean firstDoc = true;
        while (sc.hasNext()){
            
            String token = sc.next();
            
            if (token.equals("</DOC>")){
                System.out.println("------------------------------------------------------------------------------------");
            }
            //if the token doesn't start with "<", may still contain tag elsewhere
            if (token.indexOf('<') != 0){
                //if '<' exists in the token, printing it.
                if(token.indexOf('<') != -1 ){
                    token = token.substring(0,token.indexOf('<'));
                }
                String strippedToken = token.replaceAll("\\W", "");
                
                if(!strippedToken.isEmpty()){
                System.out.println(count + " " + strippedToken);   
                count++;
                }      
            }
          //  firstDoc = false;
       
        }
        }
    }
