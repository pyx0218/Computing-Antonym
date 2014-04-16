import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rita.wordnet.RiWordnet;


public class WordNetIterator
{
	private RiWordnet wordnet;
	private TreeSet<String> wordList;
	private TreeSet<String> relaventList;

	public WordNetIterator()
	{
		wordnet = new RiWordnet();
		wordList =new TreeSet<String>();
		relaventList =new TreeSet<String>();
	}
	
	public void doWork()
	{
		String pos[]=new String[]{RiWordnet.NOUN, RiWordnet.VERB, RiWordnet.ADJ,RiWordnet.ADV};
		for(String p:pos)
		{
			iteratorAll(p);
		}
		
		System.out.println("Total Size:"+wordList.size());
		System.out.println("Total Def Word Size:"+relaventList.size());
		
		//float[][] weights=new float[wordList.size()][wordList.size()];
		for(String w:wordList)
		{
			System.out.println(w);
		}
//		for(String w:relaventList)
//		{
//			System.out.println(w);
//		}
	}
	
	private Pattern ignorePattern = Pattern.compile("[^a-z]");
	private Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
	
	private void iteratorAll(String pos)
	{
		Iterator it = wordnet.iterator(pos);
		int count = 0;
		while(it.hasNext())
		{
			//System.out.println(it.next());
			String w=(String)it.next();
			Matcher m = ignorePattern.matcher(w);
			if(m.find())
			{
				//System.out.println("Ignored:"+w);
				continue;
			}
			//
			
//			String[] a =wordnet.getAllAntonyms(w, pos);
//			String[] b =wordnet.getSynonyms(w, pos);
//			String[] defs =wordnet.getAllGlosses(w, pos);
//			
//			if(a!=null)
//				for(String dd:a)
//				{
//					Matcher m2=wordPattern.matcher(dd);
//					while (m2.find()) 
//					{ // find next match
//					    String match = m2.group();
//					    if(match.contains("0"))
//					    {
//					    	System.out.println("WTF");
//					    }
//						relaventList.add(match.toLowerCase());	
//						//System.out.println(w+" --> "+match);
//					}
//				}
//			if(b!=null)
//				for(String dd:b)
//				{
//					Matcher m2=wordPattern.matcher(dd);
//					while (m2.find()) 
//					{ // find next match
//					    String match = m2.group();
//					    if(match.contains("0"))
//					    {
//					    	System.out.println("WTF");
//					    }
//						relaventList.add(match.toLowerCase());	
//						//System.out.println(w+" --> "+match);
//					}
//				}
//
//			if(defs!=null)
//				for(String dd:defs)
//				{
//					Matcher m2=wordPattern.matcher(dd);
//					while (m2.find()) 
//					{ // find next match
//					    String match = m2.group();
//					    if(match.contains("0"))
//					    {
//					    	System.out.println("WTF");
//					    }
//						relaventList.add(match.toLowerCase());	
//						//System.out.println(w+" --> "+match);
//					}
//				}
//			
			//
			wordList.add(w);
			count++;
		}
		System.out.println("Type["+pos+"]:"+count);
	}
	
	public static void main(String[] args)
	{
		new WordNetIterator().doWork();
	}

}
