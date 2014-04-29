import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import rita.wordnet.RiWordnet;


public class MaxFlow
{
	private RiWordnet wordnet;
	private HashMap<String,Integer> indices ;
	private int indices_num;
	
	const int max_words=80000;
	const int max_edges=7000000;
	private int[] head;
	private int[] next;
	private int[] tow;
	private int[] lnk;
	private int[] tak;
	private float[] val;
	private int total_edges;

	public MaxFlow()
	{
		wordnet = new RiWordnet();
		indices =new HashMap<String,Integer>();
		total_edge=0;
		head=new int[max_words];
		for(int i=0;i<max_words;i++){
			head[i]=-1;
		}
		indices_num=0;
		next=new int[max_edges];
		tow=new int[max_edges];
		val=new int[max_edges];
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
	
	public void AddEdge(int a, int b,float v){
		next[total_edge]=head[a];
		tow[total_edge]=b;
		val[total_edge]=v;
		lnk[total_edge]=1;
		head[a]=total_edge;
		total_edge++;
		next[total_edge]=head[b];
		tow[total_edge]=a;
		val[total_edge]=v;
		lnk[total_edge]=1;
		head[b]=total_edge;
		total_edge++;
	}

	public void loadSeedFile(String path) throws Exception
	{	
		Scanner s=new Scanner(new GZIPInputStream(new FileInputStream(path)));
		int lineCount = 0;
		while(s.hasNext())
		{
			String line=s.nextLine();
			String[] tokens=line.split(" ");
			lineCount++;

			String w1=tokens[0];
			String w2=tokens[1];
			if(indices.containsKey(w1)==false)indices.put(w1,indice_num++);
			if(indices.containsKey(w2)==false)indices.put(w2,indice_num++);

			AddEdge(indices.get(w1),indices.get(w2),-0.9);
			
			if(lineCount%1000000==0)
			{
				System.out.println("Line:"+lineCount);
			}
		}
		s.close();
		System.out.println("Line:"+lineCount);
		System.out.println("Dictionary Built.");
	}

	private Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
	private Set<String> words ;
	
	public void doWork(String wordListPath, String stopWordListPath) throws Exception
	{
		words = readAllWords(wordListPath);
		//words.addAll(dictWords);
		Set<String> stopWords = readAllWords(stopWordListPath);
		for(String w:stopWords)
		{
			words.remove(w);
		}
		///
		/////
		if(expand)
		{
			for(String w:words)
			{
				if(indices.containsKey(w)==false)indices.put(w,indice_num++);
				for(String pos:wordnet.getPos(w))
				{
					String[] a =wordnet.getAllAntonyms(w, pos);
					for(String ant:a)
					{
						if(indices.containsKey(ant)==false)indices.put(ant,indice_num++);
						AddEdge(indices.get(w),indices.get(ant),-0.9);
					}
					String[] b =wordnet.getSynonyms(w, pos);
					for(String syn:b)
					{
						if(indices.containsKey(syn)==false)indices.put(syn,indice_num++);
						AddEdge(indices.get(w),indices.get(syn),0.9);
					}
				}
			}
		}

		System.out.println("Word list size:"+words.size());
		
	}
	
	
	
	public int answerGREQuestion(GREQuestion q)//stat: 0 answered; 1 answered correctly
	{
		String qStr = q.getQuestion();
		System.out.print("Questions: "+qStr);
		double smallestSim = 100;
		String ans=null;
		for(String o:q.getOptions())
		{
			double sim = getSimilarity(qStr,o) ;
			System.out.printf("\t%s[%6.4f]", o,  sim );
			
			if(smallestSim>sim)
			{
				ans = o;
				smallestSim=sim;
			}
		}
		if(smallestSim <0 )
		{
			System.out.print("\t-->Guess["+ans+"]\tCorrctAns["+q.getAnswer()+"]\n");
			boolean isCorrect =  q.checkAnswer(ans);
			return isCorrect?AntonymChecker.ANSWER_CORRECT:AntonymChecker.ANSWER_WRONG;
		}
		else
		{
			System.out.print("\tSkipping\tCorrctAns["+q.getAnswer()+"]\n");
			return AntonymChecker.ANSWER_SKIPPED;
		}
		
	}

	private static double logistic(double x)
	{
		return ( 2.0/(1 + Math.exp(-1f*x)) ) - 1 ;
	}
	
	
	public double getSimilarity(String w1,String w2)
	{
		Integer wordIdx1 = this.indices.get(w1);
		Integer wordIdx2 = this.indices.get(w2);
		
		if(wordIdx1 ==null )
		{
			System.err.println("Words ["+w1 +"] not covered.");
			return 0;
		}
		if( wordIdx2 ==null )
		{
			System.err.println("Words ["+ w2 +"] not covered.");
			return 0;
		}
		double dot=0;
		double normX=0;
		double normY=0;
		for(int i=0;i<this.wordList.length;i++)
		{
			double dotProd = this.matrix[wordIdx1][i] * this.matrix[wordIdx2][i];
			if(dotProd<0)
			{
				dot += dotProd;
			}
			normX += Math.pow( this.matrix[wordIdx1][i], 2);
			normY += Math.pow( this.matrix[wordIdx2][i], 2);
		}
		normX = Math.pow(normX, 0.5);
		normY = Math.pow(normY, 0.5);
		
		return dot/(normX*normY);
	}
	
	public static void main(String[] args) throws Exception
	{
		String wordList = "resources/google-10000-english.txt";
		String stopWordList = "resources/stopwords";
		MaxFlow m=new MaxFlow();
		m.loadSeedFile("resources/AntonymsLexicon-OppCats-Affixes.gz");
		m.setExpand(false);
		m.doWork(wordList,stopWordList);
		m.populteWeights();
		m.output();
	}

}

