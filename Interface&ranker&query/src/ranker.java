import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class ranker {
    double load = 0.0;
    //double tagsweight = 0;
    double titleweight = 1, h1weight = 0.8, h2weight = 0.6, h3weight = 0.4;
    HashMap<String, Double> links = new HashMap<String, Double>();
    ArrayList<wordResult> romee=new ArrayList<wordResult>();
    ArrayList<String> sortedlinks =new ArrayList<String>();
    HashMap<String, Double> sortedHashMap = new LinkedHashMap<String, Double>();



    public void score(ArrayList<wordResult> r, ArrayList<String> words) {
        //HashMap<String, Double> links = new HashMap<String, Double>();
        this.romee=r;//elhaga eli ana wa2fa 3andha delwa2te
//ana bzwed elscore bta3 ellink l kol kelma

/////to add rank based on the tags
        for (int j = 0; j < r.size(); j++) {
            for (int i = 0; i < r.get(j).Links.size(); i++) {

                    if (r.get(j).Headers.get(i).get(0) == true) {
                        if (links.containsKey(r.get(j).Links.get(i)))
                            links.put(r.get(j).Links.get(i), links.get(r.get(j).Links.get(i)) * 5 * h1weight);
                        else links.put(r.get(j).Links.get(i), h1weight);
                    }
                    if (r.get(j).Headers.get(i).get(1) == true) {
                        if (links.containsKey(r.get(j).Links.get(i)))
                            links.put(r.get(j).Links.get(i), links.get(r.get(j).Links.get(i)) * 5 * h2weight);
                        else links.put(r.get(j).Links.get(i), h2weight);
                    }
                    if (r.get(j).Headers.get(i).get(2) == true) {
                        if (links.containsKey(r.get(j).Links.get(i)))
                            links.put(r.get(j).Links.get(i), links.get(r.get(j).Links.get(i)) * 5 * h3weight);
                        else links.put(r.get(j).Links.get(i), h3weight);
                    }

                }
        }
/////to add rank based on the tf*idf

        for (int j = 0; j < r.size(); j++) {
            for (int i = 0; i < r.get(j).Links.size(); i++) {
                if (links.containsKey(r.get(j).Links.get(i)))
                    links.put(r.get(j).Links.get(i), links.get(r.get(j).Links.get(i)) * 10 * r.get(j).TF.get(i) * r.get(j).idf);
                else links.put(r.get(j).Links.get(i), 20 * r.get(j).TF.get(i) * r.get(j).idf);
            }
        }
/////to add a rank if the words is found in the title
//        for (int j = 0; j < r.size(); j++) {
//            for (int i = 0; i < r.get(j).Titles.size(); i++) {
//                String Str = new String(r.get(j).Titles.get(i));
//                if (Str.contains("(.)" + words.get(i) + "(.)")) {
//                    if (links.containsKey(r.get(j).Links.get(i)))
//                        links.put(r.get(j).Links.get(i), links.get(r.get(j).Links.get(i)) + 7 * 0.5);
//                    else links.put(r.get(j).Links.get(i), 7 * 0.5);
//                }
//            }
//        }
//////to add the rank of pagerank
        for (int j = 0; j < r.size(); j++) {
            for (int i = 0; i < r.get(j).Links.size(); i++) {
                if (links.containsKey(r.get(j).Links.get(i)))
                    links.put(r.get(j).Links.get(i), links.get(r.get(j).Links.get(i)) * 10 * r.get(j).ranks.get(i));
                else links.put(r.get(j).Links.get(i), 10.0 * r.get(j).ranks.get(i));
            }
        }

    }
    //sort elements by values

    void sortByValue()
    {
//convert HashMap into List
        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(links.entrySet());
//sorting the list elements
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2)
            {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
//prints the sorted HashMap

        for (Entry<String, Double> entry : list)
        {
            sortedHashMap.put(entry.getKey(), entry.getValue());
            this.sortedlinks.add(entry.getKey());
        }
       // printHashMap(sortedHashMap);

    }
    //HashMap<String,Double> links = new HashMap<String,Double>();
    //method for printing the elements


    public ArrayList<rankerresults> setresults()
    {
        int indexoflink;//da elindex of the current link
        // TODO: print results
        ArrayList<rankerresults> results=new ArrayList<rankerresults>();
        List<String> all_links = new ArrayList<String>();
        List<String> allTitles = new ArrayList<String>();
        List<String> allDescription = new ArrayList<String>();
        for (int j = 0; j < this.romee.size(); j++) {
           for(int i=0;i<romee.get(j).Links.size();i++) {
               all_links.add(this.romee.get(j).Links.get(i));
               allTitles.add(this.romee.get(j).Titles.get(i));
               allDescription.add(this.romee.get(j).Discreption.get(i));
           }
        }
        for (int i = 0; i < sortedlinks.size(); i++) {
            // sortedHashMap.put(entry.getKey(), entry.getValue());
            rankerresults result = new rankerresults();
            result.link = this.sortedlinks.get(i);//de arraylist mn the link with the highst score
            indexoflink=all_links.indexOf(this.sortedlinks.get(i));
            result.title = allTitles.get( indexoflink);
            result.description = allDescription.get( indexoflink);
            results.add(result);
        }
        return results;

    }
}
 /*for (Entry<String, Double> entry : sortedlinks.entrySet())
        {
            sortedHashMap.put(entry.getKey(), entry.getValue());
            String desc=getdesc(entry.getKey());
            //Document doc=Jsoup.connect(entry.getkey());
            //String title=doc.title();
            rankerresults r1=new rankerresults(entry.getKey(),title,desc);

            results.add(r1);

        }*/