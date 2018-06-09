package com.pshkrh.notes.Helper;

public class StringHelper {

    public static String fixQuery(String query){
        String fixedQuery="";

        /*for(int i=0;i<query.length();i++){
            if(query.charAt(i) == '\''){
                fixedQuery = query.substring(0,i) + "\'" + query.substring(i+1,query.length());
            }
        }*/

        fixedQuery = query.replace("'","''");
        return fixedQuery;
    }

}
