package Crawler;

import org.jsoup.Jsoup;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.net.MalformedURLException;
import java.util.HashSet;



public class Crawler implements Runnable {
	public static int numofpages = 5000;
	public static List<String> links =new ArrayList<String>();
	public static HashMap<String, Integer> maplinks = new HashMap<String,Integer>();
	public static HashSet<String> comlist = new HashSet<String>();
	public static HashSet<String> domains = new HashSet<String>();
	public static HashSet<String> blocked = new HashSet<String>();
	public static HashSet<String> allowed = new HashSet<String>();
	public static HashSet<String>[] hyperlinks3 = new HashSet[numofpages];
	public static int counter = 0;
	public static int numofthreads = 16;
	public static Integer[][] matrix = new Integer [numofpages+10][numofpages+10];
	public static double[] pagerank = new double [numofpages+10];
		
	public void calc(double totalNodes) {

		  double InitialPageRank;
		  double OutgoingLinks = 0;
		  double DampingFactor = 0.85;
		  double TempPageRank[] = new double[5100];
		  int ExternalNodeNumber;
		  int InternalNodeNumber;
		  int k = 1; // For Traversing
		  int ITERATION_STEP = 1;
		  InitialPageRank = 1 / totalNodes;
		  System.out.printf(" Total Number of Nodes :" + totalNodes + "\t Initial PageRank  of All Nodes :" + InitialPageRank + "\n");

		  // 0th ITERATION  _ OR _ INITIALIZATION PHASE //
		  BufferedWriter writer = null;
		  try {
			writer = new BufferedWriter(new FileWriter("test.txt", true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  for (k = 1; k <= totalNodes; k++) {
		   this.pagerank[k] = InitialPageRank;
		  }

		  System.out.printf("\n Initial PageRank Values , 0th Step \n");
		  for (k = 1; k <= totalNodes; k++) {
		   System.out.printf(" Page Rank of " + k + " is :\t" + this.pagerank[k] + "\n");
		   try {
				writer.write(" Page Rank of " + k + " is :\t" + this.pagerank[k] + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }

		  while (ITERATION_STEP <= 1) // Iterations
		  {
		   // Store the PageRank for All Nodes in Temporary Array 
		   for (k = 1; k <= totalNodes; k++) {
		    TempPageRank[k] = this.pagerank[k];
		    this.pagerank[k] = 0;
		   }

		   for (InternalNodeNumber = 1; InternalNodeNumber <= totalNodes; InternalNodeNumber++) {
		    for (ExternalNodeNumber = 1; ExternalNodeNumber <= totalNodes; ExternalNodeNumber++) {
		    	//System.out.println("i = "+InternalNodeNumber+" j = "+ExternalNodeNumber+" value = "+this.matrix[ExternalNodeNumber][InternalNodeNumber]);
		     if (this.matrix[ExternalNodeNumber][InternalNodeNumber] == 1) {
		      k = 1;
		      OutgoingLinks = 0; // Count the Number of Outgoing Links for each ExternalNodeNumber
		      while (k <= totalNodes) {
		       if (this.matrix[ExternalNodeNumber][k] == 1) {
		        OutgoingLinks = OutgoingLinks + 1; // Counter for Outgoing Links
		       }
		       k = k + 1;
		      }
		      // Calculate PageRank     
		      this.pagerank[InternalNodeNumber] += TempPageRank[ExternalNodeNumber] * (1 / OutgoingLinks);
		     }
		    }
		   }

		   System.out.printf("\n After " + ITERATION_STEP + "th Step \n");
		   try {
					writer.write("\n After " + ITERATION_STEP + "th Step \n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		   for (k = 1; k <= totalNodes; k++)
		   {
		    System.out.printf(" Page Rank of " + k + " is :\t" + this.pagerank[k] + "\n");
		   try {
				writer.write(" Page Rank of " + k + " is :\t" + this.pagerank[k] + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
		   ITERATION_STEP = ITERATION_STEP + 1;
		  }
		  // Add the Damping Factor to PageRank
		  for (k = 1; k <= totalNodes; k++) {
		   this.pagerank[k] = (1 - DampingFactor) + DampingFactor * this.pagerank[k];
		  }

		  // Display PageRank
		  System.out.printf("\n Final Page Rank : \n");
		   try {
					writer.write("\n Final Page Rank : \n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  for (k = 1; k <= totalNodes; k++) {
		   System.out.printf(" Page Rank of " + k + " is :\t" + this.pagerank[k] + "\n");
		   try {
				writer.write(" Page Rank of " + k + " is :\t" + this.pagerank[k] + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	static void rec_crawler(int id) throws Exception {
		int size = links.size(); 
		if(id>=size)
			return;
		String link = links.get(id);
		System.out.println(link);
		URL url = null;
		try {
			url = new URL(link);
		} catch (MalformedURLException e1) {
			return;
		}
		String domain = url.getHost();
		if(link.substring(0, 5).equals("https"))
			domain = "https://"+domain;
		else
			domain = "http://"+domain;
		
		String content = "";
		File f = new File("../Data/html/"+id+".html");
		if(f.exists()) { 
			Scanner myReader1 = new Scanner(f);
			while(myReader1.hasNext()) {
				content += myReader1.nextLine();
			}
			myReader1.close();
		}
		else {
			System.out.println("Thread "+Integer.valueOf(Thread.currentThread().getName())+" cant find"+id);
			return;
		}
		
		String newlink="";
		while(content.indexOf("<a")!= -1) {
			char c = '"';
			content = content.substring(content.indexOf("<a")+3, content.length());
			content = content.substring(content.indexOf("href=")+5, content.length());
			c = content.charAt(0);
			content = content.substring(1, content.length());
			newlink = content.substring(0, content.indexOf(c));
			content.substring( content.indexOf(c),content.length());
			if(newlink.length()>1) {
	    		if(newlink.charAt(0)=='/' && newlink.charAt(1) != '/') {
	    			newlink = domain + newlink;
	    		}
	    		else if(newlink.charAt(0)=='/' && newlink.charAt(1) == '/') {
	    			newlink = "https:" + newlink;
	    		}
	    		if(newlink.charAt(0)=='h') {
	    			String [] com = {null,null};
	    			if(robot(newlink,domain)) {
				    		if(hyperlinks3[id].add(newlink)){
				    			BufferedWriter writer1 = new BufferedWriter(new FileWriter("sublinks/"+id+"_links"+".txt", true));
					    		writer1.write(newlink+"\n");
					    		writer1.close();
				    		}
	    				//}
	    			}
	    			if( robot(newlink,domain) && (com=comstr(newlink))[0]!=null) {
	    				synchronized(Crawler.class) {
	    					if(counter>=numofpages)
	    						return;
	    					if ( comlist.add(com[0].toString()) && !maplinks.containsKey(newlink)) {
					    		links.add(newlink);
					    		maplinks.put(newlink, counter);
					    		System.out.println("Thread "+Integer.valueOf(Thread.currentThread().getName())+ " : "+newlink);
					    		BufferedWriter writer1 = new BufferedWriter(new FileWriter("../Data/Links.txt", true));
					    		writer1.write(newlink+"\n");
					    		writer1.close();
					    		writer1 = new BufferedWriter(new FileWriter("comlist.txt", true));
					    		writer1.write(com[0]+"\n");
					    		writer1.close();
					    		writer1 = new BufferedWriter(new FileWriter("../Data/html/"+counter+".html", false));
					    		writer1.write(com[1]);
					    		writer1.close();
					    		writer1 = new BufferedWriter(new FileWriter("values/counter.txt", false));
					    		writer1.write(String.valueOf(counter));
					    		writer1.close();
					    		counter++;
	    					}
	    				}
		    		}
	    		}
    		}
		}
		int k = id+numofthreads;
		System.out.println("Thread "+Integer.valueOf(Thread.currentThread().getName())+" ID = "+k+" ");
		BufferedWriter writer1 = new BufferedWriter(new FileWriter("values/"+Integer.valueOf(Thread.currentThread().getName())+".txt", false));
		writer1.write(String.valueOf(k));
		writer1.close();
	}
	
	@SuppressWarnings({ "resource", "null" })
	static Boolean robot(String link, String domain) {
		if(!domains.add(domain)) {
			while(link.contains("/")) {
				if(blocked.contains(link)) {
					return false;
				}
				if(link.lastIndexOf("/")==0) {
					return true;
				}
				if(link.charAt(link.length()-1)=='/')
					link = link.substring(0, link.length()-1);
				else
					link = link.substring(0, link.lastIndexOf("/")+1);
			}
			return true;
		}
		else {
			URLConnection connection = null;
			try {
				connection = new URL(domain+"/robots.txt").openConnection();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return true;
			}
			connection.setConnectTimeout(4000);
			connection.setReadTimeout(4000);
			Scanner scanner = null;
			try {
				scanner = new Scanner(connection.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return true;
			}
			catch (Exception e) {
				return true;
			}
			scanner.useDelimiter("\\Z");
			String content = scanner.next();
			if(content.contains("User-agent: *")) {
				content = content.substring(content.indexOf("User-agent: *")+14, content.length());
			}
			else if(content.contains("User-Agent: *")) {
				content = content.substring(content.indexOf("User-Agent: *")+14, content.length());
			}
			else {
				return true;
			}
			if(content.indexOf("User-agent:")!=-1)
				content = content.substring(0,content.indexOf("User-agent:"));
			int indxal = -1;
			int indxbl = -1;
			boolean b=true;
			while(b) {
				indxbl = content.indexOf("Disallow:");
				indxal = content.indexOf("Allow:");
				if(indxbl == -1 && indxal == -1)
					b = false;
				else {
					String bl = "";
					if((indxbl<indxal || indxal == -1) && indxbl !=-1)
					{
						while(content.charAt(indxbl+9)==' ')
							indxbl++;
						content = content.substring(indxbl+9, content.length());
						try{
							bl=content.substring(0, content.indexOf("\n"));
						}
						catch(Exception e) {
							bl=content.substring(0, content.length());
						}
						blocked.add(domain+bl);
					}
					else if((indxal < indxbl || indxbl == -1) && indxal!=-1) {
						while(content.charAt(indxal+6)==' ')
							indxal++;
						content = content.substring(indxal+6, content.length());
						try {
							bl=content.substring(0, content.indexOf("\n"));
						}
						catch(Exception e) {
							bl=content.substring(0, content.length());
						}		
						allowed.add(domain+bl);
					}
				}
				if(indxal == indxbl)
					break;
			}
			while(link.contains("/")) {
				if(allowed.contains(link)){
					return true;
				}
				else if(blocked.contains(link)) {
					System.out.println("Link Blocked");
					return false;
				}
				if(link.lastIndexOf("/")==0) {
					return true;
				}
				if(link.charAt(link.length()-1)=='/')
					link = link.substring(0, link.length()-1);
				else
					link = link.substring(0, link.lastIndexOf("/")+1);
			}
			return true;
		}
	}
	
	@SuppressWarnings("resource")
	static String [] comstr(String link) throws Exception {
		String [] arr = {null,null};
		if(maplinks.containsKey(link)) {
			return arr;
		}
		String content = "";
		
		try {
	    TrustManager[] trustAllCerts = new TrustManager[] {
	    	       new X509TrustManager() {
	    	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	    	            return null;
	    	          }

	    	          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

	    	          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

	    	       }
	    	    };

	    	    SSLContext sc = SSLContext.getInstance("SSL");
	    	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	    	    // Create all-trusting host name verifier
	    	    HostnameVerifier allHostsValid = new HostnameVerifier() {
	    	        public boolean verify(String hostname, SSLSession session) {
	    	          return true;
	    	        }
	    	    };
	    	    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		}
		catch(Exception e) {
			
		}
	    	    // Install the all-trusting host verifier
	    	    
		URLConnection connection = null;
		//link = "https://www.time.com";
		try {
			connection =  new URL(link).openConnection();
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			return arr;
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			return arr;
		}
		
		connection.setConnectTimeout(4000);
		connection.setReadTimeout(4000);
		  Scanner scanner = null;
		try {
			scanner = new Scanner(connection.getInputStream());
		}
		catch (IOException e2) {
			// TODO Auto-generated catch block
			return arr;
		}
		catch(Exception e) {
			return arr;
			
		}
		  scanner.useDelimiter("\\Z");
		  String file = "";
		  try{
			  content = scanner.next();
			  file = content;
		  } catch(Exception e) {
			  return arr;
		  }
		  if(content.indexOf("<body")!=-1)
			  content = content.substring(content.indexOf("<body"), content.length());
		  else
			  return arr;
			if(content.contains("</header>"))
				content = content.substring(content.indexOf("</header>")+10, content.length());
	    try {
		  content = content.substring(content.indexOf("<body")+5, content.length());
		  content = content.substring(content.indexOf(">")+1, content.length());
	    }
	    catch(Exception e) {
	    	return arr;
	    }
			if(content.contains("</header>"))
				content = content.substring(content.indexOf("</header>")+10, content.length());
			  String body = Jsoup.parse(content).text();
			  String [] words = body.split(" ", 300);
			  String compstr = "";
			  int c = 0;
			  for(int i = words.length-2 ; i>=0  && c<50; i--) {
				  if(words[i].length()>1) {
					  compstr += words[i].charAt(0);
					  c++;
				  }
			  }
				if(!comlist.contains(compstr)) {
					arr[0] = compstr;
					arr[1] = file;
					return arr;
				}
			else {
				return arr;
			}

	}
	
	@SuppressWarnings("resource")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int id = Integer.valueOf(Thread.currentThread().getName());
		File file =new File("values/"+id+".txt");
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Scanner myReader1 = null;
		try {
			myReader1 = new Scanner(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(myReader1.hasNextLine()) {
			id = myReader1.nextInt();
			System.out.println("Thread "+Integer.valueOf(Thread.currentThread().getName())+" ID = "+id+" ");
		}
		//System.out.println(counter);
		int size; 
		while(counter < numofpages - 1) {
		//while(true) {
			//System.out.println("in while");		
			size = links.size();
			if(id <= size) {
				try {
					rec_crawler(id);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					System.out.println("Thread "+Integer.valueOf(Thread.currentThread().getName())+"out side func");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Thread "+Integer.valueOf(Thread.currentThread().getName())+"out side func");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				id+=numofthreads;
			}
			else
			{
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				size = links.size();
			}
			
		}
		
	}
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < numofpages; i++) {
			hyperlinks3[i] = new HashSet<String>();
        }
		BufferedWriter writer = new BufferedWriter(new FileWriter("../Data/Links.txt", true));
		File file =new File("values/counter.txt");
		file.createNewFile();
		Scanner myReader1 = new Scanner(file);
		if(myReader1.hasNextLine()) {
			counter = myReader1.nextInt();
			myReader1 = new Scanner(new File("../Data/Links.txt"));
			int indx = 0;
			while(myReader1.hasNext()) {
				String link = myReader1.nextLine();
	    	    links.add(link);
	    	    maplinks.put(link, indx);
	    	    indx++;
			}
			myReader1 = new Scanner(new File("comlist.txt"));
			while(myReader1.hasNext()) {
				String comstr = myReader1.nextLine();
				comlist.add(comstr);
			}
			for(int i = 0; i<counter; i++) {
				File f = new File("sublinks/"+i+"_links.txt");
				if(f.exists() && !f.isDirectory()) { 
					myReader1 = new Scanner(f);
					while(myReader1.hasNext()) {
						hyperlinks3[i].add(myReader1.nextLine());
					}
				}
				else
					break;			
			}
			
		}
		else {
			Scanner myReader = new Scanner(new File("Seed Set.txt"));
				int seedcount = 0;
				while (myReader.hasNextLine()){
					seedcount++;
					String [] com = {null,null};
					String link = myReader.nextLine();
					//System.out.println(link);
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			    TrustManager[] trustAllCerts = new TrustManager[] {
			    	       new X509TrustManager() {
			    	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			    	            return null;
			    	          }
	
			    	          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
	
			    	          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
	
			    	       }
			    	    };
	
			    	    SSLContext sc = SSLContext.getInstance("SSL");
			    	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
			    	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	
			    	    // Create all-trusting host name verifier
			    	    HostnameVerifier allHostsValid = new HostnameVerifier() {
			    	        public boolean verify(String hostname, SSLSession session) {
			    	          return true;
			    	        }
			    	    };
			    	    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    	    com=comstr(link);
	    	    BufferedWriter writer1 = new BufferedWriter(new FileWriter("comlist.txt", true));
	    		writer1.write(com[0]+"\n");
	    		writer1.close();
	    		writer1 = new BufferedWriter(new FileWriter("../Data/html/"+counter+".html", false));
	    		writer1.write(com[1]);
	    		writer1.close();
	    	    writer.write(link+"\n");
	    	    comlist.add(com[0].toString());
	    	    links.add(link);
	    	    maplinks.put(link, counter);
				counter++;
	    		writer1 = new BufferedWriter(new FileWriter("values/counter.txt", false));
	    		writer1.write(String.valueOf(counter));
	    		writer1.close();
			}
				writer.close();
				myReader.close();
				if(seedcount == 0) {
					System.out.println("No links in Seed set");
					return;
				}
		}
		
		Thread threads [] = new Thread[numofthreads];
		Crawler crawler = new Crawler();
		for(int i=0; i<numofthreads; i++) {
			int size = links.size();
			while(i>=size) {
				Thread.sleep(500);
				size = links.size();			
				//System.out.println("Size = "+ links.size()+" Counter = "+counter+" i = "+i);
			}
			threads[i] = new Thread(crawler);
			threads[i].setName(String.valueOf(i));
			threads[i].start();
		}
		System.out.println("Start All Threads");
		for(int i=0; i<numofthreads; i++) {
			threads[i].join();
		}
		for(int i = 0;i<links.size() && i<numofpages; i++) {
			System.out.println(links.get(i)+"  has links :");
			for(String link : hyperlinks3[i]) {
				System.out.println("	"+link);
			}
		}
		for(java.util.Map.Entry<String, Integer> entry: maplinks.entrySet()) {
		      System.out.println("link : "+entry.getKey()+"   And index = "+entry.getValue());
		}
		for(int i=0;i<counter+2 ; i++) {
			for(int j=0 ; j<counter+2; j++) {
				matrix[i][j] = 0;
			}
		}
		for(int i = 1; i<counter+1; i++) {
			for(String link : hyperlinks3[i-1]) {
				System.out.println(link);
				if(maplinks.containsKey(link)) {
					int j =maplinks.get(link);
					System.out.println("i = "+i+"  j = "+j);
					matrix[i][j+1]= 1;
				}
			}
		}
		for(int i=0; i<counter+2; i++) {
			matrix[i][i] = 0;
		}
		crawler.calc(counter+1);
		
		BufferedWriter writer1 = new BufferedWriter(new FileWriter("../Data/pagerank.txt", false));
		 for (int k = 1; k < counter+1; k++) {
			 writer1.write(pagerank[k]+"\n"); 
		 }
		 writer1.close();
		
	}
		
	}

