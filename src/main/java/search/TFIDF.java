package search;

import model.DocumentData;

import java.util.*;
import java.util.stream.Collectors;

public class TFIDF {

    public static double calculateTermFrequency(List<String> words, String term) {
         double ctos = words.stream()
                .reduce(0,
                        (parcial, word) -> words.equals(term) ? parcial + 1 : parcial,
                        Integer::sum
                );
        return ctos / words.size();
    }

    public static DocumentData createDocument(List<String> words, List<String> terms) {
        DocumentData termFreq = new DocumentData();
        terms.forEach(
                term -> {
                    double freq = calculateTermFrequency(words,term);
                    termFreq.termToFrequency(term, freq);
                }
        );
        return termFreq;
    }

    private static double getInverseDocumentFrequency(String term, Map<String,DocumentData> documentResult){
        double nt = documentResult.keySet()
                .stream()
                .reduce(0,
                        (parcial,doc)-> documentResult.get(doc).getFrequency(term) > 0 ? parcial + 1 : parcial,
                        Integer::sum);
        return nt == 0 ? 0 :  Math.log10(documentResult.size() / nt);
    }

    private static Map<String,Double> getTermToInverseDocumentFrequencyMap(List<String> terms,
                                                                           Map<String,DocumentData> documentResults){
        Map<String, Double> termToIDF = new HashMap<>();
        terms.forEach(
                t -> termToIDF.put(t,getInverseDocumentFrequency(t,documentResults))
        );
        return termToIDF;
    }

    private static double calculateDocumentScore(List<String> terms,
                                                 DocumentData docData,
                                                 Map<String,Double> termToInverseDocumentFrequency){

        return terms.stream()
                .reduce(0.0,
                           (partial,t) -> {
                                       double termFreq = docData.getFrequency(t);
                                       double inverseTermFreq = termToInverseDocumentFrequency.get(t);
                                       return partial + termFreq * inverseTermFreq;
                                       },
                        Double::sum);
    }

    public static Map<Double,List<String>> getDocumentSortedByScore(List<String> terms,
                                                                    Map<String,DocumentData> docs){
        TreeMap<Double,List<String>> scoreToDocuments = new TreeMap<>();
        Map<String,Double> termToIDF = getTermToInverseDocumentFrequencyMap(terms,docs);
        docs.keySet().forEach(
                doc -> {
                    DocumentData docData = docs.get(doc);
                    double score = calculateDocumentScore(terms,docData,termToIDF);
                    addDocumentToTreeScore(scoreToDocuments,score,doc);
                }
        );
        return scoreToDocuments.descendingMap();
    }

    private static void addDocumentToTreeScore(TreeMap<Double, List<String>> scoreToDoc,double score,String doc){
        List<String>  docsWithCurrentScore = Optional.of(scoreToDoc.get(score))
                                                     .orElse(new ArrayList<>());
        docsWithCurrentScore.add(doc);
        scoreToDoc.put(score,docsWithCurrentScore);
    }

    public static List<String> getWordsFromLine(String line){
        return Arrays.asList(line.split(""));
    }

    public static List<String> getWordsFromLine(List<String> lines){
      return  lines.stream()
                .map(
                        TFIDF::getWordsFromLine)
                .flatMap(List::stream)
              .collect(Collectors.toList());
    }
}
