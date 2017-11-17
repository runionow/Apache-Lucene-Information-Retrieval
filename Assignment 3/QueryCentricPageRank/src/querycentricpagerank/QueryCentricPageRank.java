/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querycentricpagerank;

import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 * Personalized page ranking algorithm. - query centric page rank algorithm.
 *
 * 1) Search and rank the publications based on the user information meed when
 * rank the documents or objects .
 *
 * For publication Search author ranking can be really important
 *
 * @author Arun Nekkalapudi
 */
public class QueryCentricPageRank {

    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, IOException {
        // TODO code application logic here
        String indexPath = "D:\\Uni\\Sem 1\\Search\\git-backup\\anekkal-information-retrieval\\Assignment 3\\author_index\\";
        String authorGraph = "D:\\Uni\\Sem 1\\Search\\git-backup\\anekkal-information-retrieval\\Assignment 3\\author.net";
        
        AuthorRank ar = new AuthorRank(authorGraph);
        AuthorRankwithQuery arq = new AuthorRankwithQuery("Data Mining", indexPath, authorGraph);
        AuthorRankwithQuery arq1 = new AuthorRankwithQuery("Information Retrieval", indexPath, authorGraph);
    }

}
