import java.util.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class Jobim_Fetcher {
	public JSONObject process_sentence(String s) throws Exception {
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
		return jo;
		//Iterator<?> keys = jo.keys();
		//while(keys.hasNext()){
		//	String key = (String) keys.next();
		//	System.out.println(key);
		//}
		
		
	}
	public ArrayList<String> process_word(String w) throws Exception {
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
		ArrayList<String> word_string = new ArrayList<String>();
		for (int i=0; i<ja.length(); i++) {
			JSONObject jo1 = ja.getJSONObject(i);
			String word = jo1.getString("key");
			String trim_word = word.substring(0, word.length()-3);
			word_string.add(trim_word);
		}
		return word_string;        
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
	public static void main(String[] args) throws Exception {
		Jobim_Fetcher jf = new Jobim_Fetcher();		
		String s = "a huge matrix";
		JSONObject jo = jf.process_sentence(s);
		System.out.print(jo);
		String w = "huge";
		ArrayList<String> word_string;
		word_string = jf.process_word(w);
		Iterator it = word_string.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
        
    }

}
