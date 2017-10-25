/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2_search;

/**
 *
 * @author Arun Nekkalapudi
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.io.FileWriter ;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

public class compareAlgorithms {

    private final IndexReader reader;
    private final IndexSearcher searcher;
    private final Analyzer analyzer;
    private final QueryParser parser;
    private Query query;
    private static FileWriter  pw;
    private TrecTopicsReader trecReader;
    public QualityQuery qq[];

    public compareAlgorithms(String filePath,String queryString, String similarity) throws IOException, ParseException {
        this.reader = DirectoryReader.open(FSDirectory.open(Paths.get("C:\\Users\\arunt\\Desktop\\Uni\\Sem 1\\Search\\git-backup\\anekkal-information-retrieval\\Assignment 2\\index")));
        this.searcher = new IndexSearcher(reader);
        this.analyzer = new StandardAnalyzer();
        this.parser = new QueryParser("TEXT", analyzer);
        this.trecReader = new TrecTopicsReader();
        try {
            this.qq = trecReader.readQueries(new BufferedReader(new FileReader(filePath)));
        } catch (IOException e) {
            System.out.println("Exception: \n" + e.getMessage());
        }
        this.pw = null;

        switch (similarity) {
            case "BM25":
                this.searcher.setSimilarity(new BM25Similarity());
                break;
            case "LMDS":
                this.searcher.setSimilarity(new LMDirichletSimilarity());
                break;
            case "LMJMS":
                this.searcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));
                break;
            case "VSM":
                this.searcher.setSimilarity(new ClassicSimilarity());
            default:
                break;
        }
    }

    private static void close() throws IOException {
        pw.close();
    }

    private static String cleanDescription(String description) {
        String[] descriptionArray = description.split("<smry>");
        return descriptionArray[0];
    }
    
    private static String cleanTitle(String title) {
        String[] titleArray = title.split(":");
        String titleTemp = titleArray[1].trim();
        return titleTemp;
    }

    public void readAllTRECfiles(String file, String label, String search) throws IOException, ParseException {
        try {
            this.pw = new FileWriter (file);
        } catch (IOException ex) {
            System.out.println("Exception" + ex.getMessage());
        }
        for (int i = 0; i < qq.length; i++) {
            QualityQuery eachTopic = qq[i];
            String queryID = eachTopic.getQueryID();
            if (search.equals("title")) {
                String title = cleanTitle(eachTopic.getValue(search));
                this.query = parser.parse(QueryParser.escape(title));

            } else if (search.equals("description")) {
                String description = cleanDescription(eachTopic.getValue("description"));
                this.query = parser.parse(QueryParser.escape(description));
            }
            TopDocs topDocs = searcher.search(query, 1000);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (int rank = 0; rank < scoreDocs.length; rank++) {
                Document doc = searcher.doc(scoreDocs[rank].doc);
                //QueryWriter(queryID,0, doc.get("DOCNO"),(rank+1), scoreDocs[rank].score, label, file);
                pw.write(queryID+" "+0+" "+doc.get("DOCNO")+ " " +(rank + 1)+ " " +scoreDocs[rank].score+ " " +label);
                pw.write(System.getProperty("line.separator"));
            }
        }
        reader.close();
        pw.close();
        //close();
    }

    private void QueryWriter(String queryID,int counter, String fileName, int rank, double Score, String label, String file) throws IOException {
        try {
            this.pw = new FileWriter (file);
        } catch (IOException ex) {
            System.out.println("Exception" + ex.getMessage());
        }
        this.pw.write(queryID + " " + counter + " " + fileName + " " + rank + " " + Score + " " + label);
    }

}
