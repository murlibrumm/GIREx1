package searchSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author e1327191@student.tuwien.ac.at
 *         Created on: 03.12.2015
 */
public class TFIDF {

    // dictionary: word => hashmap <file, occurrence-list>
    private HashMap<String, HashMap<String, ArrayList<Integer>>> dictionary;
    // documents: file => hashmap <word, number of occurrences>
    private HashMap<String, HashMap<String, Integer>> documents;
    // tfidf: word => hashmap <file, score>
    private HashMap<String, HashMap<String, Double>> tfidf;
    // tfidfTopic: word => score
    private Map<String, Double> tfidfTopic;

    public TFIDF(HashMap<String, HashMap<String, ArrayList<Integer>>> dictionary,
                 HashMap<String, HashMap<String, Integer>> documents) {
        this.dictionary = dictionary;
        this.documents = documents;
        tfidf = new HashMap<String, HashMap<String, Double>>();
        tfidfTopic = new HashMap<String, Double>();
        createTFIDFForDictionary();
    }

    private void createTFIDFForDictionary() {
        // iterate over dictionary, create tfidf-map with tfidf-scores
        for (Map.Entry<String, HashMap<String, ArrayList<Integer>>> entry : dictionary.entrySet()) {
            String wordKey = entry.getKey();
            HashMap<String, ArrayList<Integer>> wordValue = entry.getValue();

            tfidf.put(wordKey, new HashMap<String, Double>());
            for (Map.Entry<String, ArrayList<Integer>> innerEntry : wordValue.entrySet()) {
                String documentsKey = innerEntry.getKey();
                ArrayList<Integer> documentsValue = innerEntry.getValue();

                // calculate score
                // log(1 + tf_t,d) * log10(N / df_t), with:
                // ... tf_t,d = Term frequency, how often term t occurs in document d
                // ... N = number of documents
                // ... df_t = number of documents, which contain term t
                double score = Math.log((1d + documentsValue.size())) * Math.log10((double)(documents.size() / wordValue.size()));
                //System.out.println("score " + score + " tf_t,d " + documentsValue.size() + " N " + documents.size() + " df_t " + wordValue.size() + " log " +  Math.log((1d + documentsValue.size())) + " log10 " + Math.log10((double) (documents.size() / wordValue.size())));

                // Include document length in score
                // tf_t,d = tf_t,d / max{tf_w,d}
                //score = Math.log(1 + (documentsValue.size() / Collections.max(documents.get(documentsKey).values())))
                //        * Math.log10(documents.size() / wordValue.size());

                tfidf.get(wordKey).put(documentsKey, score);

                /*System.out.println("   document: " + documentsKey);
                System.out.print ("      ");
                for (int i : documentsValue) {
                    System.out.print (i + ", ");
                }
                System.out.println();*/
            }
        }
    }

    public void createIFIDFForTopic(HashMap<String, Double> topicWords) {
        // iterate over all words from our topic (topicWords)
        for (Map.Entry<String, Double> entry : topicWords.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            // if word exists in our tfidf-score-map
            if (tfidf.containsKey(key)) {

                // iterate over all files @ tfidf-score-map
                for (Map.Entry<String, Double> innerEntry : tfidf.get(key).entrySet()) {
                    String tfidfKey = innerEntry.getKey();
                    Double tfidfValue = innerEntry.getValue();

                    // add score to tfidf-map
                    if (!tfidfTopic.containsKey(tfidfKey)){
                        tfidfTopic.put(tfidfKey, 0d);
                    }
                    tfidfTopic.put(tfidfKey, (tfidfTopic.get(tfidfKey) + (tfidfValue * value)));
                }
            }
        }

        // todo: for debugging purposes; delete
        Map<String, Double> sortedCrunchifyMapValue = new HashMap<String, Double>();
        // Sort Map on value by calling crunchifySortMap()
        sortedCrunchifyMapValue = CrunchifyMapUtil.crunchifySortMap(tfidfTopic);
        System.out.println("\nsortedCrunchifyMapValue: \n");
        for (Map.Entry<String, Double> entry : sortedCrunchifyMapValue.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

}
