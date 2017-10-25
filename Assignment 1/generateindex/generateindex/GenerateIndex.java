/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generateindex;

/**
 *
 * @author Arun Nekkalapudi
 */
import org.apache.commons.lang3.*;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.codecs.simpletext.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.ArrayList;

public class GenerateIndex {

    public static final String INDEXDIR = "C:/Users/arunt/Desktop/search/Index";
    private static final String CORPUSDIR = "C:/Users/arunt/Desktop/search/corpus";
    public static ArrayList<HashMap<String, String>> documents = new ArrayList();
    public static int docCount = 0;
    private static Analyzer anaylzer;
    private static boolean analyzerFlag;

    //constructor 
    GenerateIndex(Analyzer anaylzer, boolean analyzerFlag) {
        this.anaylzer = anaylzer;
        this.analyzerFlag = analyzerFlag;

    }

    public IndexWriter createIndexWriter() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(INDEXDIR));
        IndexWriterConfig config = new IndexWriterConfig(GenerateIndex.anaylzer);
        config.setOpenMode(OpenMode.CREATE);
        IndexWriter Indexer = new IndexWriter(directory, config);
        return Indexer;
    }

    // Start Indexing.
    public void startIndexing(ArrayList<HashMap<String, String>> documents) throws IOException {
        IndexWriter Indexer = createIndexWriter();
        for (HashMap<String, String> document : documents) {
            addToIndex(Indexer, document, analyzerFlag);
        }
        Indexer.forceMerge(1);
        Indexer.commit();
        Indexer.close();
    }

    // Call this function to add files to the Indexer.
    public void addToIndex(IndexWriter indexer, HashMap<String, String> document, boolean flag) throws IOException {
        Document doc = new Document();
        if (flag) {
            doc.add(new StringField("DOCNO", document.get("DOCNO"), Field.Store.YES));
            doc.add(new TextField("HEAD", document.get("HEAD"), Field.Store.YES));
            doc.add(new TextField("BYLINE", document.get("BYLINE"), Field.Store.YES));
            doc.add(new TextField("DATELINE", document.get("DATELINE"), Field.Store.YES));
            doc.add(new TextField("TEXT", document.get("TEXT"), Field.Store.YES));
        } else {
            doc.add(new TextField("TEXT", document.get("TEXT"), Field.Store.YES));
        }
        indexer.addDocument(doc);
    }

    // Getting the Filepaths of all the files in the corpus.
    public List<String> getFiles() {
        List<String> corpusFiles = new ArrayList<>();
        File folder = new File(CORPUSDIR);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.getName().toLowerCase().endsWith(".trectext")) {
                String filePath = file.getPath().replace('\\', '/');
                corpusFiles.add(filePath);
            }
        }
        System.out.println("Number of Documents :" + corpusFiles.size());
        return corpusFiles;
    }

    //Add to Master Hash map.
    private void addToMasterHashMap(HashMap Document) {
        documents.add(Document);
    }

    // Get data from various fields.
    public String getData(String Search, String start, String end) {
        String[] data = StringUtils.substringsBetween(Search, start, end);
        StringBuilder concatData = new StringBuilder();
        if (data != null) {
            for (String data1 : data) {
                concatData.append(" " + data1);
            }
        }
        return concatData.toString().trim();
    }

    // Reading the file and generating a local Hash Map and updating gloabl Array List Hashmap.
    public String readFile(File filePath) throws Exception, IOException {
        String loadContent = FileUtils.readFileToString(filePath, "utf-8");
        String Docs[] = loadContent.split("<DOC>");
        

        HashMap<String, String> localDocument; 
        
        for (int i = 1; i < Docs.length; i++) {
            // local HashMap
            localDocument = new HashMap<String, String>();
            // Retrieving the data from each Document.
            String DOCNO = getData(Docs[i], "<DOCNO>", "</DOCNO>");
            String HEAD = getData(Docs[i], "<HEAD>", "</HEAD>");
            String BYLINE = getData(Docs[i], "<BYLINE>", "</BYLINE>");
            String DATELINE = getData(Docs[i], "<DATELINE>", "</DATELINE>");
            String TEXT = getData(Docs[i], "<TEXT>", "</TEXT>");

            // Loading the documents with necessary content.
            localDocument.put("DOCNO", DOCNO);
            localDocument.put("HEAD", HEAD);
            localDocument.put("BYLINE", BYLINE);
            localDocument.put("DATELINE", DATELINE);
            localDocument.put("TEXT", TEXT);

            // Adding to master hash map.
            addToMasterHashMap(localDocument);

        }
        
        return "";
    }

    // Creating Fields.
    public void createFields() {
        // Creating Field Types. 

        // 1. DOCNO -- documentNumber.
        FieldType documentNumber = new FieldType();
        documentNumber.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        documentNumber.setStored(true);
        documentNumber.setTokenized(true);
        documentNumber.setStoreTermVectors(true);
        documentNumber.setStoreTermVectorPositions(true);
        documentNumber.setStoreTermVectorOffsets(true);

        // 2. HEAD -- head.
        FieldType head = new FieldType();
        head.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        head.setStored(true);
        head.setTokenized(true);
        head.setStoreTermVectors(true);
        head.setStoreTermVectorPositions(true);
        head.setStoreTermVectorOffsets(true);

        // 3. BYLINE -- byline.
        FieldType byline = new FieldType();
        byline.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        byline.setStored(true);
        byline.setTokenized(true);
        byline.setStoreTermVectors(true);
        byline.setStoreTermVectorPositions(true);
        byline.setStoreTermVectorOffsets(true);

        // 4. DATELINE -- dateline.
        FieldType dateline = new FieldType();
        dateline.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        dateline.setStored(true);
        dateline.setTokenized(true);
        dateline.setStoreTermVectors(true);
        dateline.setStoreTermVectorPositions(true);
        dateline.setStoreTermVectorOffsets(true);

        // 5. TEXT -- text.
        FieldType text = new FieldType();
        text.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        text.setStored(true);
        text.setTokenized(true);
        text.setStoreTermVectors(true);
        text.setStoreTermVectorPositions(true);
        text.setStoreTermVectorOffsets(true);
    }

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) throws IOException {
//        // TODO code application logic here
//        Analyzer a = new StandardAnalyzer();
//        GenerateIndex indexer = new GenerateIndex(a, true);
//        List<String> listOfFiles = indexer.getFiles();
//
//        for (int i = 0; i < listOfFiles.size(); i++) {
//            //System.out.println("file Name : %s" + listOfFiles.get(i));
//            File file = new File(listOfFiles.get(i));
//            try {
//                indexer.readFile(file);
//            } catch (Exception ex) {
//                Logger.getLogger(GenerateIndex.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        System.out.println("*************Indexing has been Started********************");
//        indexer.startIndexing(documents);
//        System.out.println("Total Number of documents Indexed:" + docCount);
//        System.out.println("*************Indexing has been Completed******************");
//    }
}
