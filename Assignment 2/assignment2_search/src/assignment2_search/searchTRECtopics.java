/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2_search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import java.io.FileWriter ;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author Arun Nekkalapudi
 */
public class searchTRECtopics {

    private final String filePath;
    private TrecTopicsReader trecReader;
    public QualityQuery qq[];
    private static FileWriter  pw;

    searchTRECtopics(String filePath) throws IOException {
        this.filePath = filePath;
        this.trecReader = new TrecTopicsReader();
        this.pw = null;
        try {
            this.qq = trecReader.readQueries(new BufferedReader(new FileReader(filePath)));
        } catch (IOException e) {
            System.out.println("Exception: \n" + e.getMessage());
        }
    }

    private static void close() throws IOException {
        pw.close();
    }

    private static String cleanTitle(String title) {
        String[] titleArray = title.split(":");
        String titleTemp = titleArray[1].trim();
        return titleTemp;
    }

    private static String cleanDescription(String description) {
        String[] descriptionArray = description.split("<smry>");
        return descriptionArray[0];
    }

    private void QueryWriter(int counter, String queryID, String fileName, int rank, double Score, String label, File file) throws IOException {
        try {
            pw = new FileWriter (file);
        } catch (IOException ex) {
            System.out.println("Exception" + ex.getMessage());
        }
        pw.write(queryID + " " + counter + " " + fileName + " " + rank + " " + Score + " " + label);
    }

    public void readAllTRECfilesMyAlgo(String file, String label, String search) throws IOException, ParseException {
        HashMap<String, Double> SQueryResults = null;
        easySearch query = null;
        try {
            pw = new FileWriter (file);
        } catch (IOException ex) {
            System.out.println("Exception" + ex.getMessage());
        }
        for (int i = 0; i < qq.length; i++) {
            int rank = 1;
            QualityQuery eachTopic = qq[i];
            String queryID = eachTopic.getQueryID();
            if (search.equals("title")) {
                String title = cleanTitle(eachTopic.getValue(search));
                query = new easySearch(title);
            } else if (search.equals("description")) {
                String description = cleanDescription(eachTopic.getValue("description"));
                query = new easySearch(description);
            }
            SQueryResults = query.searchForTerm();
            int counter = i;
            Set<String> keys = SQueryResults.keySet();
            for (String key : keys) {
                if(rank > 1000){
                    break;
                }
                //QueryWriter(counter, queryID, key, rank, SQueryResults.get(key), label, file);
                pw.write(queryID + " " + counter + " " + key + " " + rank + " " + SQueryResults.get(key) + " " + label);
                pw.write(System.getProperty("line.separator"));

                rank++;
                
            }
        }
        close();
    }
}
