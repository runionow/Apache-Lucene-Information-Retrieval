/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querycentricpagerank;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import edu.uci.ics.jung.io.PajekNetReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections15.FactoryUtils;

/**
 *
 * @author Arun Nekkalapudi
 */
// Challenges 
// ==========
// 1. Unable to Sort HashMaps based on value to get top n values - Done.
// 2. Get Author Label - Done. 

public class AuthorRank {

    private final PajekNetReader Pajek;
    private final PageRank pr;
    private HashMap<String, Double> scores;

    public AuthorRank(String graphPath) throws ParseException, IOException {

        // Loading Graph and finding the ranks
        this.Pajek = new PajekNetReader(FactoryUtils.instantiateFactory(Object.class));
        this.scores = new HashMap<>();
        Graph g = new UndirectedSparseMultigraph();
        Pajek.load(graphPath, g);
        this.pr = new PageRank<>(g, 0.85);
        pr.evaluate();
        
        for (Object v : g.getVertices()) {
            //System.out.println("Label : " + Pajek.getVertexLabeller().transform(v) + "Score : " + pr.getVertexScore(v));
            scores.put((String) Pajek.getVertexLabeller().transform(v), (Double) pr.getVertexScore(v));
        }

        scores.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> {
                    System.out.println("Author ID :" + e.getKey() + " Score :" + e.getValue());
                });
    }
}
