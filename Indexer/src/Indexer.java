import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.tartarus.snowball.ext.PorterStemmer;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Indexer implements Runnable {
	
	public static class dbObject {
		String word;
		double idf;
		long count;
		ArrayList<DBObject> data = new ArrayList<DBObject>();
		public dbObject(String word,IndexerdbObject obj)
		{
			setWord(word);
			AddToData(obj);
		}
		public dbObject(String word)
		{
			setWord(word);
		}
		// Getter
        public String getWord() {
            return word;
        }
        // Setter
        public void setWord(String newWord) {
            this.word = newWord;
        }
        // Getter
        public double getIdf() {
            return idf;
        }
        // Setter
        public void setIdf(double newidf) {
            this.idf = newidf;
        }
        // Getter
        public long getCount() {
            return count;
        }
        // Setter
        public void setCount(long newcount) {
            this.count = newcount;
        }
        // Getter
        public ArrayList<DBObject> getData() {
            return data;
        }
        // Setter
        public void setData(ArrayList<DBObject> newData) {
            this.data = newData;
        }
        public void AddToCount(long N)
        {
        	this.count += N;
        }
        public void CalculateIDF(long total)
        {
        	this.idf = (double)Math.log(total)/this.count;
        }
        public void AddToData(IndexerdbObject obj)
        {
        	this.data.add(convertIndexerdbObject(obj));
        	count++;
        }
	}
    public static class IndexerdbObject {
        String url;
        long tf;
        double normalized_tf;
        double pageRank;
        String title;
        String discreption;
        ArrayList < Integer > positions;
        boolean[] headings = {false, false, false};
        public IndexerdbObject() {

        }
        public IndexerdbObject(String Url, int firstPosition) {
            this.tf = 1;
            this.url = Url;
            this.positions = new ArrayList < Integer > ();
            this.positions.add(firstPosition);
        }
        // Getter
        public String getUrl() {
            return url;
        }
        // Setter
        public void setUrl(String newUrl) {
            this.url = newUrl;
        }
        // Getter
        public long getTf() {
            return tf;
        }
        // Setter
        public void setTf(long newTf) {
            this.tf = newTf;
        }
        // Getter
        public double getNormalized_tf() {
            return normalized_tf;
        }
        // Setter
        public void setNormalized_tf(double newNormalized_tf) {
            this.normalized_tf = newNormalized_tf;
        }
        // Getter
        public double getPageRank() {
            return pageRank;
        }
        // Setter
        public void setPageRank(double newPageRank) {
            this.pageRank = newPageRank;
        }
        // Getter
        public ArrayList < Integer > getPositions() {
            return positions;
        }
        // Setter
        public void setPositions(ArrayList < Integer > newPositions) {
            this.positions = newPositions;
        }
        // Getter
        public boolean[] getHeadings() {
            return headings;
        }
        // Setter
        public void setHeadings(boolean[] newHeadings) {
            this.headings = newHeadings;
        }
        // Setter
        public void setTitle(String s) {
            this.title = s;
        }
        // Getter
        public String getTitle() {
            return title;
        }
        // Setter
        public void setDisc(String s) {
            this.discreption = s;
        }
        // Getter
        public String getDisc() {
            return discreption;
        }
        //Add newPostion to positions array and increment tf
        public void addPosition(int newPosition) {
            this.positions.add(newPosition);
            IncrementTf();
        }
        //Increment tf
        public void IncrementTf() {
            ++this.tf;
        }
        //Calculate the normalized tf 
        public void normalize(long totalCount) {
            this.normalized_tf = (double) this.tf / totalCount;
        }
        //set the given heading index to true
        public void setHeadingToTrue(int headingNumber) {
        	if(headingNumber >= 0 && headingNumber < 7)
        		this.headings[headingNumber] = true;
        }
    }
    //Convert from IndexerdbObject to DBObject
    public static BasicDBObject convertIndexerdbObject(IndexerdbObject indexerdbObject) 
    {
        return new BasicDBObject("url", indexerdbObject.getUrl()).append("tf", indexerdbObject.getNormalized_tf()).append("positions", indexerdbObject.getPositions()).append("Title", indexerdbObject.getTitle()).append("Discreption", indexerdbObject.getDisc()).append("headers", indexerdbObject.getHeadings()).append("pageRank", indexerdbObject.getPageRank());
    }
    
    public static DBObject convert(dbObject dbObject) 
    {
        return new BasicDBObject("word",dbObject.getWord()).append("idf", dbObject.getIdf()).append("count", dbObject.getCount()).append("data", dbObject.getData());
    }

    public void run() {
        IndexerMain();
    }
    //return a clean, pre processed and stemmed string
    public static String processString(String txt, String stopWords) {
    	//A string to store the result
        String processd_txt = "";
        //remove special characters and non English ones
        txt = txt.replaceAll("[^a-zA-Z0-9_ ]", "");
        //remove extra white spaces
        txt = txt.replaceAll("\\s+", " ");
        //lower case all characters
        txt = txt.toLowerCase();
        //remove all stop words from the string
        txt = txt.replaceAll("\\b(" + stopWords + ")\\b\\s?", "");
        PorterStemmer stemmer = new PorterStemmer();
        //loop over all words in the string
        for (String iterator: txt.split(" ")) {
        	//set the word
            stemmer.setCurrent(iterator);
            //stem the word
            stemmer.stem();
            //add the stemmed word into the new string
            processd_txt += stemmer.getCurrent() + " ";
        }
        return processd_txt;
    }
    //return a string with the stop words
    public static String loadStopWords() {
        String stopWords = "";
        //try catch to check if the stop words file is opened correctly
        try {
            File stopWordsFile = new File("stopwords.txt");
            Scanner myReader = new Scanner(stopWordsFile);
            //Reading the first stop word
            if (myReader.hasNext())
                stopWords += myReader.nextLine();
            //Reading the remaining stop words
            while (myReader.hasNext())
                stopWords += "|" + myReader.nextLine();
            myReader.close();
        } catch (FileNotFoundException e) {
            //Print an error message if an exception is thrown
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return stopWords;
    }

    static void IndexerMain() {
        //Getting the thread name and using it as an id
        int id = Integer.valueOf(Thread.currentThread().getName());

        //An index to store the index of the current link to be indexed from the links file  
        int current_Index = id;
        //A buffer to use in storing current_index in a file
        BufferedWriter writer = null;
        //Reading the stop words into a string
        String stopWords = loadStopWords();
        //try catch to check if the current index file is opened correctly
        // also create it if not found
        try {
            writer = new BufferedWriter(new FileWriter("currentindex/" + id + ".txt", true));
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //try catch to check if the current index file is opened correctly
        try {
            File currentIndex = new File("currentindex/" + id + ".txt");
            Scanner myReader = new Scanner(currentIndex);
            if (myReader.hasNext())
                current_Index = myReader.nextInt();
            myReader.close();
            currentIndex.delete();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            File myObj = new File("../Data/Links.txt");
            Scanner myReader = new Scanner(myObj);
            File pageRankObj = new File("../Data/pagerank.txt");
            Scanner pageRankReader = new Scanner(pageRankObj);
            String link = "";
            double pageRank = 0;
            int i = -1;
            while (myReader.hasNextLine()) {
                if (i == current_Index)
                    break;
                link = myReader.nextLine();
                pageRank = Double.parseDouble(pageRankReader.nextLine());
                i++;

            }
            System.out.println("after skip " + id + ":" + current_Index);
            boolean firstIndexCheck = false;
            while (myReader.hasNextLine()) {

                boolean flag = false;
                if (firstIndexCheck)
                    for (i = 0; i < numberOfThreads; i++) {
                        if (!myReader.hasNextLine()) {
                            flag = true;
                            break;
                        } else
                        {
                            link = myReader.nextLine();
                            pageRank = Double.parseDouble(pageRankReader.nextLine());
                        }
                    }
                if (flag)
                    break;

                File htmlFile = new File("../Data/html/" + current_Index + ".html");
                String txtString = Jsoup.parse(htmlFile, null).text();
                String stemmedString = processString(txtString, stopWords);
                HashMap < String, IndexerdbObject > wordsMap = new HashMap < String, IndexerdbObject > ();
                HashMap < String, String > originalMap = new HashMap < String, String > ();
                int wordIndex = 0;
                //remove special characters and non English ones
                txtString = txtString.replaceAll("[^a-zA-Z0-9_ ]", "");
                //remove extra white spaces
                txtString = txtString.replaceAll("\\s+", " ");
                //lower case all characters
                txtString = txtString.toLowerCase();
                //remove all stop words from the string
                txtString = txtString.replaceAll("\\b(" + stopWords + ")\\b\\s?", "");
            	String [] words = txtString.split(" ", txtString.length());
                for (String iterator: stemmedString.split(" ")) {
                    if (wordsMap.containsKey(iterator)) {
                        wordsMap.get(iterator).addPosition(wordIndex);
                    } else {
                    	originalMap.put(iterator, words[wordIndex]);
                        wordsMap.put(iterator, new IndexerdbObject(link, wordIndex));
                        wordsMap.get(iterator).setPageRank(pageRank);
                    }
                    wordIndex++;
                }


                String title = "";
                String disc = "";
                Document doc = null;
                String hString[] = null;
            	doc = Jsoup.parse(htmlFile, "UTF-8");
            	try {
            		title = doc.title();
            		hString = new String[3];
            		Elements h = doc.getElementsByTag("h1");
                    hString[0] = Jsoup.parse(h.toString()).text();
            		h = doc.getElementsByTag("h2");
                    hString[1] = Jsoup.parse(h.toString()).text();
            		h = doc.getElementsByTag("h3");
                    hString[2] = Jsoup.parse(h.toString()).text();
            	}
            	catch(Exception ex)
            	{
            		
            	}

                for (Entry < String, Indexer.IndexerdbObject > entry: wordsMap.entrySet()) {
                    if (!(entry.getKey() == (null) || entry.getKey() == "" || entry.getKey().length() > 200)) {
                    	try {
                            Element  description = doc.getElementsContainingOwnText(originalMap.get(entry.getKey())).get(0).clearAttributes();
//                            System.out.println(originalMap.get(entry.getKey()));
//                            System.out.println(entry.getKey());
                            disc =Jsoup.parse(description.toString()).text();
                            if(disc.length() < 20)
                            {
                            	Element meta = doc.select("meta[name=description]").first();
                                disc = Jsoup.parse(meta.attr("content").toString()).text();
                            }
                        }
                        catch (Exception e) {
                        	try {
                        		Element meta = doc.select("meta[name=description]").first();
                                disc = Jsoup.parse(meta.attr("content").toString()).text();
                        	}
                        	catch(Exception ex)
                        	{
                                disc = "no discreption";
                        	}
                        }
                    	for(int j = 0; j < 3; j++)
                    	{
                        hString[j] = processString(hString[j], stopWords);
                        for (String iterator: hString[j].split(" ")) {
                        	if(iterator.equals(entry.getKey()))
                        	{
                        		entry.getValue().setHeadingToTrue(j);
                        		break;
                        	}
                        }
                        
                    	}
	                    	words = disc.split(" ", 51);
	                    	if(words.length > 50)
	                    	{
		                    	disc = words[0];
		                    	for(int k = 1; k < words.length - 2; k++)
		                    	{
		                    		disc +=" ";
		                    		disc += words[k];
		                    	}
	                    	}
                        entry.getValue().setTitle(title);
                        entry.getValue().setDisc(disc);
                        entry.getValue().normalize(wordIndex);
                        synchronized(Indexer.class)
                        {
                        	if (DBMap.containsKey(entry.getKey())) {
                        		DBMap.get(entry.getKey()).AddToData(entry.getValue());
                            } else {
                            	DBMap.put(entry.getKey(), new dbObject(entry.getKey(), entry.getValue()));
                            }
                        }
                    }
                }
                wordsMap.clear();
                System.out.println("Thread : " + id + " finished link num :" + current_Index);
                current_Index += numberOfThreads;
                firstIndexCheck = true;
                synchronized(Indexer.class)
                {
                	docCount++;
                }
            }
            writer = new BufferedWriter(new FileWriter("currentindex/" + id + ".txt", true));
            writer.write(String.valueOf(current_Index));
            writer.close();
            myReader.close();
            System.out.println("Thread " + id + " Finished.");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List < String > links = new ArrayList < String > ();
    public static MongoClient mongoClient;
    public static DB database;
    public static DBCollection collection_main;
    public static HashMap < String, dbObject> DBMap = new HashMap < String, dbObject> ();
    public static int docCount = 0;
    public static int numberOfThreads = 12;
    public static boolean addToDataBasePhase = false;
    public static boolean updateOldDataBasePhase = false;


    @SuppressWarnings({ "deprecation", "unchecked" })
	public static void main(String[] args) throws Exception {

        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDB("Arc");
        collection_main = database.getCollection("Data");
        
        
        System.out.println("Loadin old DataBase.");
        DBCursor cursor;
        cursor = collection_main.find(new BasicDBObject("info", "info"));
        if (cursor.one() == null) {
        	collection_main.insert(new BasicDBObject("info", "info").append("docCount", docCount));
        }
        else
        {
        cursor = collection_main.find(new BasicDBObject("info", "info"));
        docCount += Long.parseLong(cursor.one().get("docCount").toString());
    	collection_main.update(new BasicDBObject("info", "info"), new BasicDBObject("$set", new BasicDBObject("docCount", docCount )));
        }
        cursor.close();
        Iterator<DBObject> iterator = collection_main.find().iterator();
        while(iterator.hasNext())
        {
        	DBObject doc = iterator.next();
        	if("info".equals(doc.get("info")))
        	{
        		continue;
        	}
        	dbObject obj = new dbObject(doc.get("word").toString());
        	obj.setCount( Long.parseLong(doc.get("count").toString()));
        	
        	obj.setData((ArrayList<DBObject>) doc.get("data"));
        	DBMap.put(doc.get("word").toString(), obj);   	
        }
        System.out.println("Finished Loadin old DataBase.");
        
        collection_main.drop();
        
        Thread threads[] = new Thread[numberOfThreads];
        Indexer indexer = new Indexer();
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(indexer);
            threads[i].setName(String.valueOf(i));
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
        }
        
        System.out.println("Preparing Addding to the data base.");

        List < DBObject > list = new ArrayList < DBObject > ();
        for (Entry < String, dbObject > entry: DBMap.entrySet()) {
        	entry.getValue().CalculateIDF(docCount);
        	list.add(convert(entry.getValue()));
        }
        System.out.println("Started Addding to the data base.");
        if(!(list.size() == 0))
        {
	        collection_main = database.getCollection("Data");
	        collection_main.insert(list);
	    	collection_main.insert(new BasicDBObject("info", "info").append("docCount", docCount));
        }
        System.out.println("Finished Addding to the data base.");
        System.out.println("Indexer Finished.");
    }
}