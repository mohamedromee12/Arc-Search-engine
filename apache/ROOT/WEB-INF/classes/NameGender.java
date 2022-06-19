import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.tartarus.snowball.ext.PorterStemmer;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import Indexer.IndexerdbObject;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Math;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import java.util.HashSet;
//import java.util.Set;
//@WebServlet("/NameGenderRequest")
@SuppressWarnings("serial")

public class NameGender extends HttpServlet{
	
	
	public static String processString(String txt, String stopWords)
    {
        String processd_txt ="";
            txt = txt.replaceAll("[^a-zA-Z0-9 ]", "");
            txt = txt.replaceAll("\\s+", " ");
            txt  =txt.toLowerCase();
            txt = txt.replaceAll("\\b(" + stopWords +")\\b\\s?", "");
            PorterStemmer stemmer = new PorterStemmer();
            for(String iterator:txt.split(" "))
            {
                stemmer.setCurrent(iterator);
                stemmer.stem();
                processd_txt += stemmer.getCurrent() +" ";
            }
        return processd_txt;
    }
	static String fileFound = "found";
    public static String loadStopWords()
    {
        String stopWords ="";
        //try catch to check if the stop words file is opened correctly
        try {
            File stopWordsFile = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\ROOT\\WEB-INF\\classes\\stopwords.txt");
            Scanner myReader = new Scanner(stopWordsFile);
            //Reading the first stop word
            if(myReader.hasNext())
                stopWords +=myReader.nextLine();
            //Reading the remaining stop words
            while(myReader.hasNext())
                stopWords += "|"+ myReader.nextLine();
            myReader.close();
            } catch (FileNotFoundException e) {
                //Print an error message if an exception is thrown
            	fileFound = "not found";
                  System.out.println("An error occurred.");
                  e.printStackTrace();
                }
        return stopWords;
    }
	
	
	
    @SuppressWarnings("removal")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
        String name = request.getParameter("Name");
       
        boolean phrase = false;
        if (name.length() > 0)
        {
        	if (name.charAt(0) == '"' && name.charAt(name.length()-1) == '"')
            {
            	phrase = true;
            }
        }
        String totalSearch = " ";

        String stopWords = loadStopWords();
        
        totalSearch = processString (name , stopWords);
        
        /**** Get database ****/
        // if database doesn't exists, MongoDB will create it for you
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase mydatabase = mongoClient.getDatabase("Arc");

       // String[] words = totalSearch.split(" ");
        
        List<String> words = new ArrayList<String>();
        
        if (phrase)
        {
        	totalSearch.replaceAll("\"", "");
        	
        	if (totalSearch.length() > 0)
            {
            	for(String iterator:totalSearch.split(" "))
                {
                	words.add(iterator);
                
                }
            }
        }
        else
        {
        	 if (totalSearch.length() > 0)
             {
             	for(String iterator:totalSearch.split(" "))
                 {
                 	words.add(iterator);
                 
                 }
             }
        }
        
        wordResult Results [ ] = new wordResult[words.size()];
        int resultIndex = 0;
        name = name.replaceAll("\"", "");
        
     // for phase searching
        HashMap < String, Integer > wordsMap = new HashMap < String, Integer > ();
        
	        for (int i=0; i<words.size(); i++)
	        {
	        	wordResult w = new wordResult();
	        	final Class<? extends List> docClazz = new ArrayList<Document>().getClass();
		        FindIterable<org.bson.Document> mydatabaserecords = mydatabase.getCollection(words.get(i)).find();
		        MongoCursor<org.bson.Document> iterator = mydatabaserecords.iterator();
		        String link="";
		        String title="";
		        String disc="";
		        double tf;
		        Document doc2 = null;
		        Element description;
		        String t="";
		        while (iterator.hasNext()) 
		           {
		        		
		        		 try {
		        			 org.bson.Document doc = iterator.next();
		        			 link = doc.getString("url");
//		        			 doc2 = Jsoup.connect(link).get();
//		        			  try {
//		        				  Element  description = doc2.getElementsContainingOwnText(word).get(0).clearAttributes();
//		        				  String descriptionString =Jsoup.parse(description.toString()).text();
//			        	          
//		        			  }
//		        			  catch (Exception e) {
//		        				  disc = "no discreption";
//		        			  }
			        	     
			        	  // for phase searching
			        	     if (wordsMap.containsKey(link)) {
			                        wordsMap.put(link,wordsMap.get(link)+1 );
			                    } else {
			                        wordsMap.put(link, 1);
			                    }
			        	     tf = doc.getDouble("tf");
			        	     title = doc.getString("Title");
			        	     disc = doc.getString("Discreption");
			        	     List<Integer> pos = doc.get("positions",docClazz);
			        	     
			        	     int index = w.Links.indexOf(link);
			        	     if (index == -1)
			        	     {
			        	    	 w.addValue(link);
				        	     w.addTF(tf);
				        	     w.addTitle(title);
				        	     w.addDisc(disc);
				        	     w.addPosition(pos);
			        	     }
		        		 }
		        		 catch (NullPointerException e ) {
		        			 org.bson.Document doc = iterator.next();
		        			 w.addIDF(doc.getDouble("idf"));
		        			System.out.println("not found");
		        		 }
		        	}
		        Results[resultIndex] = w;
		        resultIndex++;
	        }
	        																					
	   
	        
        
        
        List<String> arrLinks = new ArrayList<String>();
        List<String> arrP = new ArrayList<String>();
        List<String> strings = new ArrayList<String>();
        String v="phrasing";
         if (phrase )
         {
        	
        		 int size = Results[0].Links.size();
        		 for (int i=0; i<size; i++)
        		 {
        			 if (wordsMap.get(Results[0].Links.get(i)) == Results.length)
        			 {
        				 int posSize = Results[0].Positions.get(i).size();
        				 for (int j=0; j<posSize; j++)
        				 {
        					 int value = Results[0].Positions.get(i).get(j);
        					 boolean exist = true;
        					 for (int k=1; k<Results.length; k++)
        					 {
        						 int index = Results[k].Links.indexOf(Results[0].Links.get(i));
        						 if (index != -1)
        						 {
        							 boolean flag = true;
            						 for (int z=0; z<Results[k].Positions.get(index).size(); z++)
            						 {
            							 if (Results[k].Positions.get(index).get(z) == value+1)
            							 {
            								 value++;
            								 break;
            							 }
            							 else if (Results[k].Positions.get(index).get(z) > value+1 || (z == Results[k].Positions.get(index).size()-1))
            							 {
            								 flag = false;
            								 break;
            							 }
            							 
            						 }
            						 if (!flag)
            						 {
            							 exist = false;
            							 break;
            						 }
        						 }
        							 
        						 
        						 
        						 
        					 }
        					 
        					 if (exist)
        					 {
        						 arrLinks.add(Results[0].Links.get(i));
        						 arrP.add(Results[0].Titles.get(i));
        						 strings.add(Results[0].Discreption.get(i));
        					 }
        					 
        				 }
        			 }
        		 }
        	 
         }
         
         else
         {
        	 for (int i=0; i<Results.length; i++)
 	        {
 	        	for (Integer j=0; j<Results[i].Links.size(); j++)
 	        	{

 	        		
 	        		arrLinks.add(Results[i].Links.get(j));
 	        		arrP.add(Results[i].Titles.get(j));
 	        		strings.add(Results[i].Discreption.get(j));
 	        	}
 	        }
         
         }
	        
        
        	///////////////////////////////////////
        	
        	
        	
        	
        	double pageCount2 =  Math.ceil((double)(arrLinks.size()) / 10.0);
        	
        	int pageCount = (int) pageCount2;
        //	pageCount=1;
        	int index = 0;
            String message = "Your name after stemming is " + totalSearch;
            System.out.println(message);
            response.setContentType("text/html");
            StringBuilder page = new StringBuilder();
            	page .append( "<!DOCTYPE html>\r\n"
        		+ "<html lang=\"en\">\r\n"
        		+ "    <head>\r\n"
        		+ "        <meta charset=\"UTF-8\">\r\n"
        		+ "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\r\n"
        		+ "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\r\n"
        		+ "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\r\n"
        		+ "        <!-- <link\r\n"
        		+ "          href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css\"\r\n"
        		+ "          rel=\"stylesheet\"\r\n"
        		+ "          integrity=\"sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1\"\r\n"
        		+ "          crossorigin=\"anonymous\"\r\n"
        		+ "        /> -->\r\n"
        		+ "        <title>ARC</title>\r\n"
        		+ "        <!----Custom CSS Filw Link--->\r\n"
        		+ "        <link rel=\"stylesheet\" href=\"CSS/master.css\"> \r\n"
        		+ "        <!-- <link rel=\"stylesheet\" href=\"CSS/normalies.css\"> -->\r\n"
        		+ "        <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\r\n"
        		+ "        <link rel=\"stylesheet\" href=\"CSS/allmin.css\">\r\n"
        		+			"<link rel=\"stylesheet\" href=\"./CSS/master.css\">"
        		+ "        <!-- <link rel=\"stylesheet\" href=\"CSS/master.css\"> -->\r\n"
        		+ "        <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\r\n"
        		+ "        <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\r\n"
        		+ "        <link href=\"https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300;0,400;0,600;0,700;0,800;1,300;1,400;1,600;1,700;1,800&display=swap\" rel=\"stylesheet\">\r\n"
        		+ "        <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\r\n"
        		+ "      <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\r\n"
        		+ "      <link href=\"https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap\" rel=\"stylesheet\">\r\n"
        		+ "    </head>\r\n"
        		+ "<body style=\"  background-repeat: no-repeat;\r\n"
        		+ "background-size:100% 100%; height: 100vh;\">\r\n"
        		+ " <div class=\"mt-4\" id=\"div_language\" style=\"display: none;\" >\r\n"
        		+ "    <h2 class=\"mb-3 text-light\">Select Language</h2>\r\n"
        		+ "    <select class=\"form-select bg-secondary text-light\" id=\"select_language\" onchange=\"updateCountry()\"></select>\r\n"
        		+ "    <select class=\"form-select bg-secondary text-light mt-2\" id=\"select_dialect\"></select>\r\n"
        		+ "  </div>\r\n"
        		+ "    <form action=\"NameGenderRequest\" method=\"GET\" id=\"NameGenderRequest\" style=\"display: flex; \r\n"
        		+ "    flex-direction:row;\r\n"
        		+ "    align-items: center;\r\n"
        		+ "    margin: 20px;\">\r\n"
        		+ "       <img src=\"images/ARC.png\" class=\"\" style=\"width: 100px; margin-right: 50px;\" alt=\"\">\r\n"
        		+ "          \r\n"
        		+ "        <br>\r\n"
        		+ "        <div id=\"searchBox\" style=\"width: 500px;\r\n"
        		+ "        border-radius: 55px;\r\n"
        		+ "        padding: 25px;\r\n"
        		+ "        background: #e1e9f2;\r\n"
        		+ "        display: flex;\r\n"
        		+ "        align-items: center;\r\n"
        		+ "        box-shadow: 6px 6px 10px -1px rgb(0 0 0 / 15%), 0px 0px 10px 2px rgb(97 153 199);\r\n"
        		+ "        max-width: 80%;\r\n"
        		+ "        height: 10px;\r\n"
        		+ "        position: relative;\">\r\n"
        		+ "       \r\n"
        		+ "          <!-- <img src=\"image/google.png\" id=\"googleIcon\"> -->\r\n"
        		+ "          <!-- <i class=\"fa-solid fa-magnifying-glass\" id=\"googleIcon\"></i> -->\r\n"
        		+ "          <img src=\"images/searchengin-brands.svg\" alt=\"\" id=\"googleIcon\" style=\"width: 30px;\r\n"
        		+ "          cursor: pointer;\">\r\n"
        		+ "          <input type=\"text\" required placeholder=\"Search ARC or type a URL\" id=\"final\" name=\"Name\" class=\"\" style=\"border: 0;\r\n"
        		+ "          background: transparent;\r\n"
        		+ "          outline: none;\r\n"
        		+ "          transition: 2s;\r\n"
        		+ "          max-width: 100%;\r\n"
        		+ "          width: 80%;\r\n"
        		+ "          height: 50px;\r\n"
        		+ "          margin-left: 10px;\">\r\n"
        		+ "          <!-- <img src=\"image/microphn.png\" class=\"mic-icon\"> -->\r\n"
        		+ "          <!-- <img src=\"images/microphone-lines-solid.svg\" alt=\"\" class=\"mic-icon\"> -->\r\n"
        		+ "          <button class=\"btn btn-success \"  type=\"button\" id=\"start\" style=\"width: 36px   ;\r\n"
        		+ "          transition: 0.5;\r\n"
        		+ "          transition-delay: 0.5s;\r\n"
        		+ "          background-color: transparent !important;\r\n"
        		+ "          border-color: transparent !important;\r\n"
        		+ "          cursor: pointer;\r\n"
        		+ "          position: absolute;\r\n"
        		+ "          right: 20px;\"><img src=\"images/microphone-lines-solid.svg\" alt=\"\" class=\"\"></button>\r\n"
        		+ "          <!-- <i class=\"fa-solid fa-microphone\"></i> -->\r\n"
        		+ "        </div>\r\n"
        		+ "        <br>\r\n"
        		+ "        <!-- <button class=\"stopB\" id=\"stop\">Stop</button> -->\r\n"
        		+ "\r\n"
        		+ "        <input type=\"submit\" value=\"Search\" class=\"\" style=\"padding: 8px 10px;\r\n"
        		+ "        background-color: #e9964d;\r\n"
        		+ "        color: white;\r\n"
        		+ "        border-radius: 13px;\r\n"
        		+ "        border-color: transparent;\r\n"
        		+ "        cursor: pointer;\r\n"
        		+ "        transition: 0.5s !important;\r\n"
        		+ "        width: fit-content !important;\r\n"
        		+ "        position: relative;\r\n"
        		+ "        margin-left: 10px;\" id=\"stop\">\r\n"
        		+ "  </form>\r\n");
            	if (pageCount != 0)
            	{
            		page.append ( "  <div class=\"masterDiv\">"
        	
        		+" <div class=\" mainDiv\">\r\n");
            	for (int j=0; j<pageCount; j++)
            	{
            		//page.append("<div>" + Results[0].Links.size() +" "+Results.length+" "+ words[0]+" "+ totalSearch+" "+ p+" "+ z+"</div>");
            		page.append (" <div class=\"midDiv\" id=\"");
            		page.append(String.valueOf(j+pageCount));
            		page.append ("\">\r\n");
            		for (int i=0; i<10; i++)
                	{
                	
                		if (index < arrLinks.size())
                		{
                			page.append( "<div class=\"oneResult\">\r\n");
                        	
                    		page.append(   "<a href=\"");
                    				page.append	 (arrLinks.get(index));
                    				page.append(	"\" class=\"arrLink\">");
                    				page.append (arrP.get(index));
                    				page.append ("</a>\r\n");
                    				page.append ("<p class=\"arrGreen\">");
                    				page.append (arrLinks.get(index));
                    				page.append ("</p>\r\n");
                    				page.append ("<p class=\"arrStr\">");
                    				page.append (strings.get(index));
                    				page.append ("</p>\r\n");
                		   page.append  (  "</div>\r\n");
                		   index++;
                		}
                		else {
                			break;
                		}
                		
                	}
                	//page.append ("\r\n");
                	page.append ("    </div>");
            		//page.append("</div>\\r\\n");
                	if (j==0)
                	{
                		page.append("<div class=\"buttons\" id=\"buttons\">\r\n");
            			for (int i=0; i<pageCount; i++)
            			{
            				
            					page.append("<button id=\"");
                				page.append(String.valueOf(i));
                				page.append ("\" class=\"DefultB  \"> \r\n");
                				page.append(String.valueOf(i+1));
                				page.append("</button>");
            				
            			}
            	page.append("</div>");
                	}
            	}
            	page.append("</div>\r\n");
            	
            	page.append("</div>");
            	
            	
            	if (phrase)
            	{
            		page.append("<div> Phrase Searching for ");
            		page.append(totalSearch + " " + fileFound + " 1 ");
            		page.append("</div>");
            	}
            	}
            	else
            	{
            		page.append("<div> Opps !! ");
            		page.append(name + " " + "have no results in our Search engine");
            		page.append("</div>");
            	}
        		page.append(  "<div class=\"mt-4\" style=\"display: none;\">\r\n"
        		+ "    <button class=\"btn btn-success\" id=\"start\"><img src=\"images/microphone-lines-solid.svg\" alt=\"\" class=\"mic-icon\"></button>\r\n"
        		+ "    <button class=\"btn btn-danger\" id=\"stop\">Stop</button>\r\n"
        		+ "    <p id=\"status\" class=\"lead mt-3 text-light\" style=\"display: none\" style=\"background-image: url(images/pexels-faik-akmd-1025469.jpg);\">Listenting ...</p>\r\n"
        		+ "  </div>\r\n"
        		
        		+ "   <script src=\"./js.js\"></script>\r\n"
        		+ "   <script src=\"./language.js\"></script>\r\n"
        		+ "   <script src=\"./speech.js\"></script>\r\n"
        		+ "	  <script src=\"./jsj.js\"></script>\r\n"
        		+"<style>\r\n"
        		+ "  \r\n"
        		+ "form {\r\n"
        		+ "    display: flex !important;\r\n"
        		+ "    flex-direction: row !important;\r\n"
        		+ "    align-items: center !important;\r\n"
        		+ "    margin: 20px !important;\r\n"
        		+ "    box-shadow: 6px 6px 10px -1px rgb(0 0 0 / 15%), 0px 0px 12px 2px #e9964d !important;\r\n"
        		+ "    padding: 20px !important;\r\n"
        		+ "    border-radius: 20px !important;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".totalResult {\r\n"
        		+ "    margin-left: 110px !important;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".oneResult {\r\n"
        		+ "    margin-bottom: 30px !important;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "a.arrLink {\r\n"
        		+ "    \r\n"
        		+ "    text-decoration: none !important;\r\n"
        		+ "    font-size: 20px !important;\r\n"
        		+ "    font-family: sans-serif !important;\r\n"
        		+ "    color: #4f4fa9;"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "p.arrStr {\r\n"
        		+ "    color: #898989 !important;\r\n"
        		+ "    font-family: sans-serif !important;\r\n"
        		+ "    font-size: 14px !important;\r\n"
        		+ "    margin-top: -1px;\r\n"
        		+ "}"
        		+ " p.arrGreen {\r\n"
        		+ "    color: #018e01;\r\n"
        		+ "    margin: -1px 0;\r\n"
        		+ "}"
        		+ "\r\n"
        		+ "div#searchBox {\r\n"
        		+ "    width: 600px !important;\r\n"
        		+ "    border-radius: 55px !important;\r\n"
        		+ "    padding: 25px !important;\r\n"
        		+ "    background: #e1e9f2 !important;\r\n"
        		+ "    display: flex !important;\r\n"
        		+ "    align-items: center !important;\r\n"
        		+ "    box-shadow: 6px 6px 10px -1px rgb(0 0 0 / 15%), 0px 0px 10px 2px rgb(97 153 199) !important;\r\n"
        		+ "    max-width: 80% !important;\r\n"
        		+ "    height: 10px !important;\r\n"
        		+ "    position: relative !important;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "input#stop{\r\n"
        		+ "    padding: 8px 10px !important;\r\n"
        		+ "    background-color: #e9964d !important;\r\n"
        		+ "    color: white !important;\r\n"
        		+ "    border-radius: 13px !important;\r\n"
        		+ "    border-color: transparent !important;\r\n"
        		+ "    cursor: pointer !important;\r\n"
        		+ "    transition: 0.5s !important;\r\n"
        		+ "    width: fit-content !important;\r\n"
        		+ "    position: relative !important;\r\n"
        		+ "    margin-left: 15px !important;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".mainDiv {\r\n"
        		+ "    margin: 30px;\r\n"
        		+ "    position: relative;\r\n"
        		+ "    margin-left: 100px;"
        		+ "    height: 1100px;"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".midDiv {\r\n"
        		+ "    position: absolute;\r\n"
        		+ "    top: 0;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".masterDiv {\r\n"
        		+ "    position: relative;\r\n"
        		+ "    min-height: 100vh;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".buttons {\r\n"
        		+ "    position: absolute;\r\n"
        		+ "    bottom: 0;\r\n"
        		+ "    margin: 20px 0;"
        		+ "    background-image: linear-gradient(to right, #e9964d , #aabfd0);\r\n"
        		+ "    border-radius: 5px; "
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".displayNone {\r\n"
        		+ "    display: none ;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".Rcolor{\r\n"
        		+ "    color: red;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".displayNow {\r\n"
        		+ "    display: block;\r\n"
        		+ "}"
        		+ ".DefultB {\r\n"
        		+ "    color: white;\r\n"
        		+ "    border-color: transparent;\r\n"
        		+ "    border: none;\r\n"
        		+ "    padding: 8px;\r\n"
        		+ "    cursor: pointer;\r\n"
        		+ "    background-color: transparent !important;"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".GradiantB {\r\n"
        		+ "    background-image: linear-gradient(to right, #e9964d , #aabfd0);\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".OrangeB{\r\n"
        		+ "    background-color: #e9964d;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".BlueB{\r\n"
        		+ "    background-color: #aabfd0;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".curveLeft{\r\n"
        		+ "    border-top-left-radius: 5px;\r\n"
        		+ "    border-bottom-left-radius: 5px;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ ".curveRight{\r\n"
        		+ "    border-top-right-radius: 5px;\r\n"
        		+ "    border-bottom-right-radius: 5px;\r\n"
        		+ "}"
        		+ "</style>"
        		+ "</body>\r\n"
        		+ "</html>");
        response.getWriter().println(page);
    }

}

