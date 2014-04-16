import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rita.wordnet.RiWordnet;


public class DefMatrixMaker
{
	private RiWordnet wordnet;
	private HashMap<String,Integer> indices ;
	private float[][] matrix;
	private String[] wordList;
	
	public DefMatrixMaker()
	{
		wordnet = new RiWordnet();
	}
	
	private Set<String> readAllWords(String path) throws Exception
	{
		Scanner s=new Scanner(new FileInputStream(path));
		Set<String> words=new TreeSet<String>();
		while(s.hasNext())
		{
			String word = s.next().trim();
			words.add(word);
		}
		return words;
	}
	
	private LinkedList<String> getToekns(String[] defs)
	{
		LinkedList<String> list=new LinkedList<String>();
		for(String dd:defs)
		{
			Matcher m2=wordPattern.matcher(dd);
			while (m2.find()) 
			{ // find next match
			    String match = m2.group();
				list.add(match.toLowerCase());
			}
		}
		return list;
	}
	
	private Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
	
	public void doWork(String wordListPath, String stopWordListPath) throws Exception
	{
		Set<String> words = readAllWords(wordListPath);
		Set<String> stopWords = readAllWords(stopWordListPath);
		for(String w:stopWords)
		{
			words.remove(w);
		}

		wordList=new String[words.size()];
		words.toArray(wordList);
		
		indices =new HashMap<String,Integer>();
		for(int i=0;i<wordList.length;i++)
		{
			String w=wordList[i];
			indices.put(w, i);
		}
		
		matrix = new float[wordList.length][wordList.length];
		
		for(String w:words)
		{
			for(String pos:wordnet.getPos(w))
			{
				String[] a =wordnet.getAllAntonyms(w, pos);
				String[] b =wordnet.getSynonyms(w, pos);
				
				String[] c = wordnet.getSynset(w, pos);
//				String[] defs =wordnet.getAllGlosses(w, pos);
//				if(defs!=null)
//				{
//					LinkedList<String> list = getToekns(defs); 
//					for(String stopword:stopWords)
//					{
//						list.remove(stopword);
//					}
//					setWeights(w, list, 0.01F);
//				}

				if(c!=null)
				{
					LinkedList<String> list = getToekns(c); 
					setWeights(w, list, 0.5F);
				}	

				if(a!=null)
				{
					LinkedList<String> list = getToekns(a); 
					setWeights(w, list, 1F);
				}
					
				if(b!=null)
				{
					LinkedList<String> list = getToekns(b); 
					setWeights(w, list, -1F);
				}
					
			}
		}

		{
			double sim = getSimilarity("hiatus","nexus");	
			System.out.println(sim);
		}

		{
			double sim = getSimilarity("laconic","voluble");	
			System.out.println(sim);
		}
		
		PrintWriter p1= new PrintWriter("wordList.txt");
		PrintWriter p2= new PrintWriter("wordMatrix.txt");
		for(int i=0;i<wordList.length;i++)
		{
			String w=wordList[i];
			p1.println(w+"\t"+i);
		}
		p1.close();
		
		for(int i=0;i<wordList.length;i++)
		{
			//String w=wordList[i];
			for(int j=0;j<wordList.length;j++)
			{
				//String w2=wordList[j];	
				p2.print(matrix[i][j]+" ");
			}
			p2.println();
		}
		p2.close();
		
	}
	
	private void setWeights(String w, LinkedList<String> words, float weight)
	{
		Integer qWordIdx = this.indices.get(w);
		if(qWordIdx==null)
			return;
		for(String word:words)
		{
			Integer aWordIdx = this.indices.get(word);
			if(aWordIdx==null)
				continue;
			matrix[qWordIdx][aWordIdx] = weight;
		}
	}
	
	public double getSimilarity(String w1,String w2)
	{
		Integer wordIdx1 = this.indices.get(w1);
		Integer wordIdx2 = this.indices.get(w2);
		
		if(wordIdx1 ==null || wordIdx2 ==null )
		{
			System.err.println("Words ["+w1 +" or "+ w2 +"] not covered.");
			return 0;
		}
		double dot=0;
		double normX=0;
		double normY=0;
		for(int i=0;i<this.wordList.length;i++)
		{
			dot += this.matrix[wordIdx1][i] * this.matrix[wordIdx2][i];
			normX += Math.pow( this.matrix[wordIdx1][i], 2);
			normY += Math.pow( this.matrix[wordIdx2][i], 2);
		}
		normX = Math.pow(normX, 1/this.wordList.length);
		normY = Math.pow(normY, 1/this.wordList.length);
		
		return dot/(normX*normY);
	}
	
	public static void main(String[] args) throws Exception
	{
		String wordList = "resources/google-10000-english.txt";
		String stopWordList = "resources/stopwords";
		new DefMatrixMaker().doWork(wordList,stopWordList);
	}

}
