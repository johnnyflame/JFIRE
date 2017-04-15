package parser;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

import java.util.Scanner;



/**
 * Parser.java
 * A simple XML parser, removes tags and convert everything to lower case
 * @author Johnny Flame Lee 2017
 */
public class Parser{
    public static void main(String [] args){
      //  parseTokens();
       dataTester();
    }
    
    public static void findPrimaryKey(){
        
    }
    
    /**
     * This is a testing method checks assumptions about the dataset
     * made in the parsing process. Should not print anything when called if the
     * assumptions made were correct.
     */
    public static void dataTester(){
        Scanner sc = new Scanner(System.in);
        
        while (sc.hasNext()){
            String token =  sc.next();
            
            /* assumption 1: No token in the datset continues after a '>' when
            there is a '<' which preceeds it, which means we
            can safely discard anything that follows a '<*/
            
            
            if (token.indexOf('<') != -1){
                if (token.indexOf('>') != token.length()){
                    System.out.println(token);
                }
            }
            
            
            
            
            //print all tokens which contain left open pointy bracket.
            if (token.indexOf('&') != -1){
                if(token.contains("&amp;")){
                    token = token.replace("&amp;"," ");
                    
                    Scanner cleanToken = new Scanner(token);
                    
                    while (cleanToken.hasNext()){
                        System.out.println(cleanToken.next());
                    }
                }
            }   
        }
    }
    /**
     *
     */
    public static void parseTokens(){
        Scanner sc = new Scanner(System.in);
        int count = 0;
        //boolean firstDoc = true;
        while (sc.hasNext()){
            
            //get a token from stdin
            String token = sc.next();
            
            if (token.equals("</DOC>")){
                System.out.println("------------------------------------------------------------------------------------");
            }
            //if the token starts with '<' we want to ignore it.
            //if it does not start with "<", it may still contain tag elsewhere,so we have another check.
            if (token.indexOf('<') != 0){
                
                /*Check if '<' exists in the string, if so, remove everything after.
                The dataset has been manually checked that no token continues after a close-angle-bracket.*/
                if(token.indexOf('<') != -1 ){
                    token = token.substring(0,token.indexOf('<'));
                }
                
                //First remove $amp; markup, replace with space.
                String strippedToken = token.replaceAll("&amp;", " ");
                /*Then remove all punctuation, replace with space.
                This is a difficult design decision to make. May lead to false positives.*/
                strippedToken = strippedToken.replaceAll("\\W", " ");
                
                Scanner cleanToken = new Scanner(strippedToken);
                while (cleanToken.hasNext()){
                    System.out.println(count + " " +
                            cleanToken.next().toUpperCase());
                    count++;
                }
            }
            //  firstDoc = false;
            
        }
    }
}
