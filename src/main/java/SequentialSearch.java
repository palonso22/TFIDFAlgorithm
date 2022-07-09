import model.DocumentData;
import search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SequentialSearch {
    public static final String BOOKS_DIRECTORY = "./resources/books";
    public static final String SEARCH_QUERY1 = "The best directives that catches many criminals using his deductive methods";
    public static final String SEARCH_QUERY2 = "The girl that falls  through a rabbit into a fantasy wonderland";
    public static final String SEARCH_QUERY3 = "A war between Francia and Russia in the cold winter";

    public static void main(String[] args){
        File documentsDirectoy = new File(BOOKS_DIRECTORY);
        List<String> documents = Arrays.asList(documentsDirectoy.list()).stream()
                .map(docName -> BOOKS_DIRECTORY + "/" + docName)
                .collect(Collectors.toList());
        List<String> terms = TFIDF.getWordsFromLine()
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> terms){
        Map<String,DocumentData> docResults =  documents.stream()
                .reduce(
                        new HashMap<String, DocumentData>(),

                        (docDataMap,document) -> {
                            FileReader reader;
                            try {
                                reader = new FileReader(document);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            BufferedReader buffer = new BufferedReader(reader);
                            List<String> lines = buffer.lines().collect(Collectors.toList());
                            List<String> words = TFIDF.getWordsFromLine(lines);
                            DocumentData documentData = TFIDF.createDocument(words,terms);
                            docDataMap.put(document,documentData);
                            return docDataMap;
                        }
                );

        printResults(TFIDF.getDocumentSortedByScore(terms, docResults));
    }

    private static void printResults(Map<Double,List<String>> docByScore){
        docByScore.entrySet().forEach(
                docScorePair -> {
                    double score = docScorePair.getKey();
                    docScorePair.getValue().forEach(
                            b -> System.out.println(String.format("Book : %s - score : %f", b.split("/")[3],score))
                    );
                }
        );
    }
}
