/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generateindex;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.MultiFields;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Administrator1
 */
public class indexComparison {

    public void generateanalytics() throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get((GenerateIndex.INDEXDIR))));

        //Print the total number of documents in the corpus
        System.out.println("Total number of documents in the corpus: " + reader.maxDoc());

        //Print the number of documents containing the term "new" in <field>TEXT</field>.
        System.out.println("Number of documents containing the term \"new\" for field \"TEXT\": " + reader.docFreq(new Term("TEXT", "new")));

        //Print the total number of occurrences of the term "new" across all documents for <field>TEXT</field>.
        System.out.println("Number of occurrences of \"new\" in the field \"TEXT\": " + reader.totalTermFreq(new Term("TEXT", "new")));

        Terms vocabulary = MultiFields.getTerms(reader, "TEXT");

        //Print the size of the vocabulary for <field>TEXT</field>, applicable when the index has only one segment.
        System.out.println("Size of the vocabulary for this field: " + vocabulary.size());

        //Print the total number of documents that have at least one term for <field>TEXT</field>
        System.out.println("Number of documents that have at least one term for this field: " + vocabulary.getDocCount());

        //Print the total number of tokens for <field>TEXT</field>
        System.out.println("Number of tokens for this field: " + vocabulary.getSumTotalTermFreq());

        //Print the total number of postings for <field>TEXT</field>
        System.out.println("Number of postings for this field: " + vocabulary.getSumDocFreq());

        //Print the vocabulary for <field>TEXT</field>
        TermsEnum iterator = vocabulary.iterator();

        BytesRef byteRef = null;
//        System.out.println("\n*******Vocabulary-Start**********");
//        while ((byteRef = iterator.next()) != null) {
//            String term = byteRef.utf8ToString();
//            System.out.print(term + "\t");
//        }
//        System.out.println("\n*******Vocabulary-End**********");

        reader.close();
    }

    public GenerateIndex selectAnalyzer() {
        GenerateIndex index = null;
        System.out.println("*Select Analyzer");
        System.out.println("================");
        System.out.println("1: Standard Analyzer - TEXT Field");
        System.out.println("2: Keyword Analyzer - TEXT Field");
        System.out.println("3: Simple Analyzer - TEXT Field");
        System.out.println("4: Stop Analyzer - TEXT Field");
        System.out.println("5: Standard Analyzer - [All] fields");
        System.out.println("6: Stop");

        System.out.println();
        Scanner kbd = new Scanner(System.in);
        System.out.printf("Input : ");
        int number = kbd.nextInt();

        switch (number) {
            case 1:
                System.out.println("Standard Analyzer - [TEXT]");
                index = new GenerateIndex(new StandardAnalyzer(), false);
                break;
            case 2:
                System.out.println("Keyword Analyzer - [TEXT]");
                index = new GenerateIndex(new KeywordAnalyzer(), false);
                break;
            case 3:
                System.out.println("Simple Analyzer - [TEXT]");
                index = new GenerateIndex(new SimpleAnalyzer(), false);
                break;
            case 4:
                System.out.println("Stop Analyzer - [TEXT]");
                index = new GenerateIndex(new StopAnalyzer(), false);
                break;
            case 5:
                System.out.println("Standard Analyzer - [ALL FIELDS]");
                index = new GenerateIndex(new StandardAnalyzer(), true);
                break;
            case 6:
                System.out.println("STOPPING");
                index = null;
                break;
        }
        return index;
    }

    public static void main(String[] args) throws IOException, Exception {
        PrintStream out = new PrintStream(new FileOutputStream("output1.txt"));
        indexComparison diffIndex = new indexComparison();
        GenerateIndex index = diffIndex.selectAnalyzer();
        if (index != null) {
            List<String> listOfFiles = index.getFiles();
            for (int i = 0; i < listOfFiles.size(); i++) {
                //System.out.println("file Name : %s" + listOfFiles.get(i));
                File file = new File(listOfFiles.get(i));
                try {
                    index.readFile(file);
                } catch (Exception ex) {
                    Logger.getLogger(GenerateIndex.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("*****************************Indexing has been Started*****************************");
            index.startIndexing(GenerateIndex.documents);
            diffIndex.generateanalytics();
            System.out.println("*************Indexing and Generating Analytics has been Completed******************");
        }
        System.setOut(out);

    }
}
