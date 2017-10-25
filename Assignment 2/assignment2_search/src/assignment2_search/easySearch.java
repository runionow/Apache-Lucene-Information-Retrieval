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
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class easySearch {

    //String queryString = "my name is arun";
    private final IndexReader reader;
    private final IndexSearcher searcher;
    private final Analyzer analyzer;
    private final QueryParser parser;
    private final Query query;
    private final LinkedHashSet<Term> queryTerms;
    private final ClassicSimilarity dSimi;
    private final int noOfTerms;
    private int flag;
    private Map<String, Float> documentIdLength;
    private List<Map<String, Integer>> myMap;
    private List<Map<String, Double>> myScoreMap;
    private ArrayList<String> eachQueryTerm;
    private HashMap<Integer, String> DocIDtoDocName;
    private HashMap<String, Double> overallScore;

    // Constructor
    public easySearch(String queryString) throws IOException, ParseException {
        this.reader = DirectoryReader.open(FSDirectory.open(Paths.get("C:\\Users\\arunt\\Desktop\\Uni\\Sem 1\\Search\\git-backup\\anekkal-information-retrieval\\Assignment 2\\index")));
        this.myMap = new ArrayList<>();
        this.myScoreMap = new ArrayList<>();
        this.DocIDtoDocName = new HashMap<>();
        this.searcher = new IndexSearcher(reader);
        this.analyzer = new StandardAnalyzer();
        this.parser = new QueryParser("TEXT", analyzer);
        this.query = parser.parse(QueryParser.escape(queryString));
        this.queryTerms = new LinkedHashSet<>();
        searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
        this.dSimi = new ClassicSimilarity();
        this.noOfTerms = queryTerms.size();
        this.flag = 0;
        this.overallScore = new LinkedHashMap<>();
    }
    // Sorting
    private static HashMap<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order){

        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            @Override
            public int compare(Entry<String, Double> o1,
                    Entry<String, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        LinkedHashMap <String, Double> sortedMap = new LinkedHashMap<>();
        for (Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }    

    // get total doc count.
    public int totalDocsCount() {
        return this.reader.numDocs();
    }

    // get all the Terms.
    public Set<Term> getTerms() {
        return this.queryTerms;
    }

    // Document Frequency for particular query term - k(t).
    public int getDocFreq(String queryTerm) throws IOException {
        int df = reader.docFreq(new Term("TEXT", queryTerm));
        //System.out.println("Number of documents containing the term \"" + queryTerm + "\" for field\"TEXT\": " + df);
        return df;
    }

    //search for Term.
    public HashMap searchForTerm() throws IOException {

        Double[] valueQuery = new Double[getTerms().size()];
        //Sorting counts for each term and its corresponding document.
        List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
        this.documentIdLength = new HashMap<String, Float>();
        this.eachQueryTerm = new ArrayList<String>();
        int totalDocs = totalDocsCount();
        //Processing each segment

        for (int i = 0; i < leafContexts.size(); i++) {
            // Get document length
            LeafReaderContext leafContext = leafContexts.get(i);

            int startDocNo = leafContext.docBase;
            int numberOfDoc = leafContext.reader().maxDoc();
            for (int docId = 0; docId < numberOfDoc; docId++) {
                // Get normalized length (1/sqrt(numOfTokens)) of the document
                float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
                // Get length of the document
                float docLeng = 1 / (normDocLeng * normDocLeng);
                this.documentIdLength.put(searcher.doc(docId + startDocNo).get("DOCNO"), docLeng);
                this.DocIDtoDocName.put((docId + startDocNo), searcher.doc(docId + startDocNo).get("DOCNO"));
                // To be commented.
                //System.out.println("Length of doc(" + (docId + startDocNo) + ", " + searcher.doc(docId + startDocNo).get("DOCNO") + ") is " + docLeng);
            }

            // creating count to doc id map for each term.
            int size = 0;

            for (Term t : this.getTerms()) {
                PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(), "TEXT", new BytesRef(t.text()));
                Map<String, Integer> termMap = new HashMap<String, Integer>();
                int doc;
                if (de != null) {
                    while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
                        termMap.put(this.DocIDtoDocName.get((de.docID() + startDocNo)), de.freq());
                        // To be commented
                        //System.out.println(t.text() + " occurs " + de.freq() + " time(s) in doc(" + (de.docID() + startDocNo) + ")");
                    }
                }
                if (flag == 0) {
                    this.myMap.add(size, termMap);
                    eachQueryTerm.add(size, t.text());
                } else if (flag == 1) {
                    Map<String, Integer> temp = this.myMap.get(size);
                    temp.putAll(termMap);
                    this.myMap.set(size, temp);
                }
                size = size + 1;
            }
            //System.out.println("Size :"+size);
            flag = 1;
        }

        int termQuery = 0;

        for (Map<String, Integer> map : myMap) {
            Map<String, Double> tempTermScoreMap = new HashMap<String, Double>();
            Set<String> keys = map.keySet();
            double tempValue = 0;
            for (String key : keys) {
                int countForDoc = map.get(key); 
                double docLength = documentIdLength.get(key);
                tempValue = (countForDoc / docLength) * (Math.log10(1 + totalDocs / getDocFreq(eachQueryTerm.get(termQuery))));
                tempTermScoreMap.put(key, tempValue);
                //System.out.println(key+" Score for  query term\"" + eachQueryTerm.get(termQuery) + "\" is " + tempValue);
            }
            myScoreMap.add(termQuery, tempTermScoreMap);
            valueQuery[termQuery] = tempValue;
            termQuery++;
        }

        Set<String> allDocKeys = documentIdLength.keySet();
        for (String key : allDocKeys) {
            double initScore = 0;
            for (Map<String, Double> eachTerm : myScoreMap) {
                initScore = (double) eachTerm.getOrDefault(key, 0.0) + initScore;
                //eachTerm.get(key);
                //System.out.println("Total Score: "+initScore);
            }
            if (initScore != 0) {
                this.overallScore.put(key, initScore);
            }
        }
        
        System.out.println("Completed");
        this.overallScore = sortByComparator(this.overallScore,false);
        reader.close();
        Set<String> keyScore = this.overallScore.keySet();
        for(String s: keyScore) {
            System.out.println( "Document: "+s+" Score :"+this.overallScore.get(s) );
        }
             
        return this.overallScore;
        //this.overallScore = sortByValues(this.overallScore);

    }
}
