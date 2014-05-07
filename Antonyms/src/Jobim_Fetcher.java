import java.util.*;
import java.io.*;
import java.net.*;

import org.json.*;

public class Jobim_Fetcher {
	public void process_sentence(String s) throws Exception {
		String url = "http://maggie.lt.informatik.tu-darmstadt.de:10080/jobim/ws/holing?s="+URLEncoder.encode(s, "UTF-8");
		URL jobim_web_demo = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) jobim_web_demo.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		JSONObject jo = new JSONObject(response.toString());
		Iterator<?> keys = jo.keys();
		while(keys.hasNext()){
			String key = (String) keys.next();
			System.out.println(key);
		}
		//JSONObject testjo = jo.getJSONObject("holings");
		JSONArray testja = jo.getJSONArray("holings");
		//testja.get("values");
		System.out.println(testja.toString());
		System.out.println(response.toString());
		
	}
	public ArrayList<WordScorePair> process_word(String w) throws Exception {
		w = identify_word_type(w);
		String url = "http://maggie.lt.informatik.tu-darmstadt.de:10080/jobim/ws/api/stanford/jo/similar/"+URLEncoder.encode(w, "UTF-8");
		URL jobim_web_demo = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) jobim_web_demo.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		JSONObject jo = new JSONObject(response.toString());
		JSONArray ja = jo.getJSONArray("results");
		ArrayList<WordScorePair> word_score_list = new ArrayList<WordScorePair>();
		for (int i=0; i<ja.length(); i++) {
			JSONObject jo1 = ja.getJSONObject(i);
			int score = jo1.getInt("score");
			String word = jo1.getString("key");
			String trim_word = word.substring(0, word.length()-3);
			WordScorePair wsp= new WordScorePair(trim_word, score);			
			word_score_list.add(wsp);
		}
		return word_score_list;        
	}
	// Jobim uses standard formats like "huge#JJ" to specify it as an adjective or "#NN" as noun
	// use this method to convert a word to this standard format
	public String identify_word_type(String w) throws Exception {
		String test_s = "a " + w;
		String url = "http://maggie.lt.informatik.tu-darmstadt.de:10080/jobim/ws/holing?s="+URLEncoder.encode(test_s, "UTF-8");
		URL jobim_web_demo = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) jobim_web_demo.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		String response_s = response.toString();
		//System.out.println(response_s);
		int ind = response_s.indexOf(w+"#");
		int end = ind + w.length()+3;
		String result = response_s.substring(ind, end);
		return result;
		
	}
	class WordScorePair {
		public String word;
		public int score;
		public WordScorePair(String word, int score){
			this.word = word;
			this.score = score;
		}
	}
	public void Parse_GRE_testset(String path) throws Exception{
		GREDataSetParser p=new GREDataSetParser();
		List<GREQuestion> questions = p.parse(path);
		int count = 3;
		PrintWriter p1= new PrintWriter("resources/jobimResult2.txt");
		for(GREQuestion q:questions)
		{
			if(count == 0)
				break;
			else
				count--;
			System.out.println(count);
			String w = q.getQuestion();
			ArrayList<WordScorePair> word_score_list;
			p1.print(w+": ");
			try{
				word_score_list = this.process_word(w);
				Iterator it = word_score_list.iterator();
				while(it.hasNext()){
					WordScorePair tmp = (WordScorePair)it.next();
					p1.print(tmp.word+ " "+ tmp.score + ";");
				}
			}catch (Exception e) {
				System.out.println("not processed");
			}finally {
				p1.println("");
			};
		}
		p1.close();
	}
	public void Parse_wordlist(String path) throws Exception{
		//GREDataSetParser p=new GREDataSetParser();
		//List<GREQuestion> questions = p.parse(path);
		int count = 100;
		PrintWriter p1= new PrintWriter("resources/wordlistResult.txt");
		Scanner s=new Scanner(new FileInputStream(path));
		while(s.hasNext())
		{
			String line=s.nextLine();
		
			if(count == 0)
				break;
			else
				count--;
			System.out.println(count);
			String w = line.trim();
			ArrayList<WordScorePair> word_score_list;
			p1.print(w+": ");
			try{
				word_score_list = this.process_word(w);
				Iterator it = word_score_list.iterator();
				while(it.hasNext()){
					WordScorePair tmp = (WordScorePair)it.next();
					p1.print(tmp.word+ " "+ tmp.score + ";");
				}
			}catch (Exception e) {
				System.out.println("not processed");
			}finally {
				p1.println("");
			};
		}
		p1.close();
	}
	public static void main(String[] args) throws Exception {
		Jobim_Fetcher jf = new Jobim_Fetcher();		
		//String s = "a huge matrix";
		//String w = "huge";
		
		String GRE_path = "resources/testset.txt";
		String wordlist_path = "resources/wordlist.txt";
		try{
			//jf.Parse_GRE_testset(GRE_path);
			jf.Parse_wordlist(wordlist_path);
		} catch (Exception e){
			System.err.println("parse error");
		}
		
		
		/*
		ArrayList<WordScorePair> word_score_list;
		word_score_list = jf.process_word(w);
		Iterator it = word_score_list.iterator();
		while(it.hasNext()){
			WordScorePair tmp = (WordScorePair)it.next();
			System.out.println(tmp.word+ " "+ tmp.score);
		}
        */
	}

}
