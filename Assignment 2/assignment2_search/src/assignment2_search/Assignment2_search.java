/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2_search;



import java.io.IOException;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;

import org.apache.lucene.queryparser.classic.ParseException;


/**
 *
 * @author Arun Nekkalapudi
 */
public class Assignment2_search {
    /**
     * @param args the command line arguments
     * @throws org.apache.lucene.queryparser.classic.ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        // TODO code application logic here
//        String queryString = "arun";
//        
//        easySearch ES = new easySearch(queryString);
//        ES.searchForTerm(); 

        
        
        String filePath = "C:\\Users\\arunt\\Desktop\\Uni\\Sem 1\\Search\\git-backup\\anekkal-information-retrieval\\Assignment 2\\topics.51-100";
        
        searchTRECtopics sTREC = new searchTRECtopics(filePath);
        sTREC.readAllTRECfilesMyAlgo("MyAlgoShortQuery.txt", "MyAlgo_short", "title");
//        
//        searchTRECtopics sTREC1 = new searchTRECtopics(filePath);
//        sTREC1.readAllTRECfilesMyAlgo("MyAlgoLongQuery.txt", "MyAlgo_long", "description");
        
//        compareAlgorithms ca = new compareAlgorithms(filePath,queryString, "BM25");
//        ca.readAllTRECfiles("BM25ShortQuery.txt", "BM25_short", "title");
//        
//        compareAlgorithms ca1 = new compareAlgorithms(filePath,queryString, "LMDS");
//        ca1.readAllTRECfiles("LMDSShortQuery.txt", "LMDS_short", "title");
//        
//        compareAlgorithms ca2 = new compareAlgorithms(filePath,queryString, "LMJMS");
//        ca2.readAllTRECfiles("LMJMSShortQuery.txt", "LMJMS_short", "title");
//        
//        compareAlgorithms ca3 = new compareAlgorithms(filePath,queryString, "VSM");
//        ca3.readAllTRECfiles("VSMShortQuery.txt", "VSM_short", "title");
//        
//        compareAlgorithms ca4 = new compareAlgorithms(filePath,queryString, "BM25");
//        ca4.readAllTRECfiles("BM25LongQuery.txt", "BM25_long", "description");
//        
//        compareAlgorithms ca5 = new compareAlgorithms(filePath,queryString, "LMDS");
//        ca5.readAllTRECfiles("LMDSLongQuery.txt", "LMDS_short", "description");
//        
//        compareAlgorithms ca6 = new compareAlgorithms(filePath,queryString, "LMJMS");
//        ca6.readAllTRECfiles("LMJMSLongQuery.txt", "LMJMS_long", "description");
//        
//        compareAlgorithms ca7 = new compareAlgorithms(filePath,queryString, "VSM");
//        ca7.readAllTRECfiles("VSMLongQuery.txt", "VSM_long", "description");
    }
}
