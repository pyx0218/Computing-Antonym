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
	private int indice_num;
	
	final int max_words=80000;
	final int max_edges=14000000;
	private String[] track_back;
	private int[] head;
	private int[] next;
	private int[] tow;
	private int[] c;
	private int[] h;
	private int[] vh;
	private int[] la;
	private double[] val;
	private int total_edges;

	public MaxFlow()
	{
		wordnet = new RiWordnet();
		indices =new HashMap<String,Integer>();
		total_edges=0;
		head=new int[max_words];
		for(int i=0;i<max_words;i++){
			head[i]=-1;
		}
		indice_num=0;
		next=new int[max_edges];
		tow=new int[max_edges];
		c=new int[max_edges];
		h=new int[max_words];
		vh=new int[max_words];
		la=new int[max_words];
		val=new double[max_edges];
		track_back=new String[max_words];
	}

	private void initMap(){
		for(int i=0;i<max_words;i++){
			la[i]=head[i];
			h[i]=vh[i]=0;
		}
		for(int i=0;i<max_edges;i++){
			c[i]=1;
		}
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
	
	public void AddEdge(int a, int b,double v){
		next[total_edges]=head[a];
		tow[total_edges]=b;
		val[total_edges]=v;
		head[a]=total_edges;
		total_edges++;
		next[total_edges]=head[b];
		tow[total_edges]=a;
		val[total_edges]=v;
		head[b]=total_edges;
		total_edges++;
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
			if(indices.containsKey(w1)==false){track_back[indice_num]=w1;indices.put(w1,indice_num++);}
			if(indices.containsKey(w2)==false){track_back[indice_num]=w2;indices.put(w2,indice_num++);}

			AddEdge(indices.get(w1),indices.get(w2),-0.4);
			
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
		for(String w:words)
		{
			if(indices.containsKey(w)==false){track_back[indice_num]=w;indices.put(w,indice_num++);}
			for(String pos:wordnet.getPos(w))
			{
				String[] a =wordnet.getAllAntonyms(w, pos);
				if(a!=null)
				{
					for(String ant:a)
					{
						if(indices.containsKey(ant)==false){track_back[indice_num]=ant;indices.put(ant,indice_num++);}
						AddEdge(indices.get(w),indices.get(ant),-0.9);
					}
				}
				String[] b =wordnet.getSynonyms(w, pos);
				String[] c =wordnet.getSynset(w, pos);
				if(b!=null){
					Set<String>bb;
					bb=new HashSet(Arrays.asList(b));
					if(c!=null)
						for(String ww:c){
							bb.remove(ww);
						}
					for(String syn:bb)
					{
						if(indices.containsKey(syn)==false){track_back[indice_num]=syn;indices.put(syn,indice_num++);}
						AddEdge(indices.get(w),indices.get(syn),0.5);
					}
				}
				if(c!=null)
					for(String syn:c)
					{
						if(indices.containsKey(syn)==false){track_back[indice_num]=syn;indices.put(syn,indice_num++);}
						AddEdge(indices.get(w),indices.get(syn),0.8);
					}
			}
		}

		System.out.println("Word list size:"+words.size());
		
	}
	
	
	private double findFlow(int x,int st,int ed){
		if(x==ed){
//			System.out.print(track_back[ed]);
			return 1;
		}
		int mi=indice_num,i=la[x];
		double now;
		if(i!=-1)
		{
			do{
				if(c[i]!=0){
					if(h[x]==h[tow[i]]+1){
						if((now=findFlow(tow[i],st,ed))!=0){
//							System.out.print("("+val[i]+")"+"->"+track_back[x]);
							c[i]--;c[i^1]++;
							now*=val[i];
							la[x]=i;return now;
						}
						if(h[st]>ed)return 0;
					}
					if(mi>h[tow[i]])mi=h[tow[i]];
				}
				i=next[i]>-1?next[i]:head[x];
			}while(i!=la[x]);
		}
		if(--vh[h[x]]==0)h[st]=indice_num+1;
		h[x]=mi+1;++vh[h[x]];
		return 0;
	}

	private double FindDiff(int st,int ed){
		double ans=0;
		int cnt=0;
		double quote=0,tmp=0,div=1;
		initMap();
		final int MAXLEN=10;
		double [] dx = new double[MAXLEN];
		int [] dy = new int [MAXLEN];
		while(h[st]<=MAXLEN){
			tmp=findFlow(st,st,ed);
			if(h[st]>=MAXLEN)break;
			if(tmp!=0){
				if(tmp<0)dx[h[st]]+=tmp;
				dy[h[st]]++;
//				System.out.println("");
				cnt++;
			}
		}
		for(int i=1;i<MAXLEN;i++){
			if(dy[i]!=0){
				ans+=dx[i]/dy[i]/(i*i);
			}
			quote+=1.0/(i*i);
		}
		return quote==0?0:ans/quote;
	}
	
	public int answerGREQuestion(GREQuestion q)//stat: 0 answered; 1 answered correctly
	{
		String qStr = q.getQuestion();
		System.out.print("Questions: "+qStr);
		double smallestSim = 100;
		double lastSmallest = 100;
		String ans=null;
		for(String o:q.getOptions())
		{
			double sim = getSimilarity(qStr,o) ;
			System.out.printf("\t%s[%6.4f]", o,  sim );
			
			if(smallestSim>sim)
			{
				ans = o;
				lastSmallest=smallestSim;
				smallestSim=sim;
			}else if(lastSmallest>sim){
				lastSmallest=sim;
			}
		}
		if(smallestSim <0 /*smallestSim/lastSmallest>1.3 */)
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
		return FindDiff(wordIdx1,wordIdx2);
	}
	
	public static void main(String[] args) throws Exception
	{
		String wordList = "resources/google-10000-english.txt";
		String stopWordList = "resources/stopwords";
		MaxFlow m=new MaxFlow();
		m.loadSeedFile("resources/AntonymsLexicon-OppCats.gz");
		m.doWork(wordList,stopWordList);
		String s="good";
		String t[]={"poor","bad","tough","excellent","outstanding","decent","strong","solid","terrific","great"};
		for(int i=0;i<10;i++)
			System.out.printf("===>%s\t[%.3f]\n",t[i],m.getSimilarity(s,t[i]));
	}

}

