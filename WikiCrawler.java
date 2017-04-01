import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.io.PrintWriter;


public class WikiCrawler {
	public static final String BASE_URL = "https://en.wikipedia.org";
	private String seedUrl;
	private int max;
	private String fileName;
	
	/**
	 * 
	 * @param seedUrl 
	 A string seedUrl{relative address of the seed url (within Wiki domain)
	 * @param max
	 An integer max representing Maximum number pages to be crawled
	 * @param fileName
	 A string fileName representing name of a file{The graph will be written to this file
	 */
	public WikiCrawler(String seedUrl, int max, String fileName){
		this.seedUrl = seedUrl;
		this.max = max;
		this.fileName = fileName;
	}
	
	/**
	 * 
	 * @param doc: gets a string (that represents contents of a .html)
	 * @return  
	 * return an array list (of Strings) consisting of links from doc.
	 */
	public ArrayList<String> extractLinks(String doc){
		ArrayList<String> result = new ArrayList<String>();
		try{
			File f = new File(doc);
			Scanner s = new Scanner(f);
			
			boolean hasP = false;
			
			while(s.hasNext()){
				String tmp = s.next();
				if(tmp.contains("<P>")||tmp.contains("<p>")){
					hasP = true;
				}
				if(hasP){
					if(!(tmp.contains(":"))&&!(tmp.contains("#"))){
						if(tmp.contains("href=\"/wiki/")){
							String link = tmp.substring(tmp.indexOf("href=\"/")+6, tmp.length()-1);
							System.out.println(link);
							result.add(link);
						}
					}
				}
			}
			s.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Wait for at least 3 seconds after every 100 requests.
	 */
	public void crawl(){
		int request = 0;
		int n = 0;
		Queue<String> q = new LinkedList<String>();
		ArrayList<String> visited = new ArrayList<String>();
		ArrayList<String> extractedLinks;
		StringBuilder output = new StringBuilder();
				
		q.add(seedUrl);
		visited.add(seedUrl);
		
		while(!(q.isEmpty())){
			String currentP = (String)(q.remove());
			if(request%100==0){
				try {
					wait(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try{
				URL url = new URL(BASE_URL + currentP);
				InputStream is = url.openStream();
				request++;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String address = null;
                while(br.readLine()!=null){
                	address += br.readLine();
                }
                br.close();
                
                extractedLinks = extractLinks(address);
                for(String nextLink : extractedLinks){
                	if(!visited.contains(nextLink)&& !nextLink.equals(currentP)){
                		visited.add(nextLink);
                		output.append(currentP +" "+nextLink+'\n');
                	}
                	if(max>n){
                		n++;
                		q.add(nextLink);
                	}
                }
                
			} catch (Exception e){
				
			}
		}
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter(fileName);
	        printWriter.write(output.toString());
	        printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
