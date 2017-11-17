/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querycentricpagerank;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.BM25Similarity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;
import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Arun Nekkalapudi
 */
class AuthorIndex {

    private List<Double> Scores;
    private String AuthorID;
    private double priors;
    private double prob;
    private String authorName;
    
    
    public AuthorIndex(String AuthorId, double Scores, String authorName) {
        this.Scores = new ArrayList<>();
        this.priors = Scores;
        this.Scores.add(Scores);
        this.AuthorID = AuthorId;
        this.authorName = authorName;
        
    }

    public String getAuthorID() {
        return this.AuthorID;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public double getPriors() {
        return this.priors;
    }

    public void addScores(double Score) {
        Scores.add(Score);
        //Update Priors On Insert
        this.priors = this.priors + Score;
    }

    public double calculateProb(double totalProb) {
        return this.priors / totalProb;
    }

}

public class AuthorRankwithQuery {

    private final IndexReader reader;
    private final IndexSearcher searcher;
    private final Analyzer analyzer;
    private final QueryParser parser;
    private final Query query;
    private Map<String, Integer> authorIDs;
    private final PageRankWithPriors prp;
    private final PajekNetReader Pajek;
    private HashMap<String, Double> scores;

    public AuthorRankwithQuery(String queryString, String indexPath, String graphPath) throws ParseException, IOException {
        this.reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
        this.searcher = new IndexSearcher(reader);
        this.searcher.setSimilarity(new BM25Similarity());
        this.analyzer = new StandardAnalyzer();
        this.parser = new QueryParser("content", analyzer);
        this.scores = new HashMap<>(); 
        this.query = parser.parse(queryString);
        this.Pajek = new PajekNetReader(FactoryUtils.instantiateFactory(Object.class));
        Graph g = new UndirectedSparseGraph();
        Pajek.load(graphPath, g);
        int counter = 0;
        double totalPrior = 0;
        double beta = 0.85;
        //Q1. 300 Solutions using BM25 Algorihtm.
        TopDocs results = this.searcher.search(this.query, null, 300);
        ScoreDoc[] hits = results.scoreDocs;
        //System.out.println("document \n" + hits[1]);
        this.authorIDs = new HashMap<>();
        Set<String> authorList = new LinkedHashSet<>();

        for (ScoreDoc hit : hits) {
            Document hitDoc = this.searcher.doc(hit.doc);
            //System.out.println(hitDoc.get("authorid"));
            authorList.add(hitDoc.get("authorid"));
        }
        AuthorIndex[] ai = new AuthorIndex[authorList.size()];
        //System.out.println("Size of Authors List : " + authorList.size());
        
        
        for (ScoreDoc hit : hits) {
            Document hitDoc = this.searcher.doc(hit.doc);
            if (!this.authorIDs.containsKey(hitDoc.get("authorid"))) {
                this.authorIDs.put(hitDoc.get("authorid"), counter);
                //System.out.println("Counter :"+counter+"Inserted New Doc with Author ID :" + hitDoc.get("authorid"));
                ai[counter] = new AuthorIndex(hitDoc.get("authorid"), hit.score, hitDoc.get("authorName"));
                totalPrior += hit.score;
                counter = counter + 1;
            } else {
                //System.out.println("Updated Doc with Author ID :" + hitDoc.get("authorid") + "with Score : " + hit.score);
                ai[this.authorIDs.get(hitDoc.get("authorid"))].addScores(hit.score);
                totalPrior += hit.score;
            }
        }

        // Q3. Calculating Priors for the Author.
        Map<String, Double> probs = new HashMap<>();

        for (AuthorIndex ai1 : ai) {
            //System.out.println("Author ID : " + ai1.getAuthorID() + " Prior :" + ai1.calculateProb(totalPrior));
            probs.put(ai1.getAuthorID(), ai1.calculateProb(totalPrior));
        }
        
        // Q4. Solution: Using Jung Algorithm, Ranking Authors with priors generated.
        Transformer<Integer, Double> vertexPrior = new Transformer<Integer, Double>() {
            @Override
            public Double transform(Integer v) {
                if (probs.containsKey(Pajek.getVertexLabeller().transform(v))) {
                    return (double) probs.get(Pajek.getVertexLabeller().transform(v));
                } else {
                    return 0d;
                }
            }
        };
        
        this.prp = new PageRankWithPriors<>(g, vertexPrior , beta);
        this.prp.evaluate();
        
        for (Object v : g.getVertices()) {
            //System.out.println("Label : " + Pajek.getVertexLabeller().transform(v) + "Score : " + pr.getVertexScore(v));
            scores.put((String) Pajek.getVertexLabeller().transform(v), (Double) prp.getVertexScore(v));
        }
        System.out.println("===========================================================================");
        System.out.println("Keyword : "+ queryString + " | alpha : "+beta);
        System.out.println("===========================================================================");       
        scores.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> {
                    System.out.println("Author ID :" + e.getKey() + " Author Name :"+ai[this.authorIDs.get(e.getKey())].getAuthorName() +" Score :" + e.getValue());
                });
        System.out.println("==========================================================================="); 
        this.reader.close();
        
        // System.out.println("Total Size: " + this.authorIDs.size());
        // System.out.println("Prior: " + totalPrior);
    }

}
