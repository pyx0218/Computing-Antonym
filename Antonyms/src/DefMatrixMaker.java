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
	boolean expand = true;
	
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
	
	
	public boolean isExpand()
	{
		return expand;
	}

	public void setExpand(boolean expand)
	{
		this.expand = expand;
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
		
		/////
		if(expand)
		{
			Set<String> words2 = new HashSet<String>();
			for(String w:words)
			{
				for(String pos:wordnet.getPos(w))
				{
					String[] a =wordnet.getAllAntonyms(w, pos);
					String[] b =wordnet.getSynonyms(w, pos);
//					String[] c = wordnet.getSynset(w, pos);
//
//					if(c!=null)
//					{
//						for(String s:getToekns(c))
//						{
//							words2.add(s);
//						}
//					}	
					if(b!=null)
					{
						for(String s:b)
						{
							words2.add(s);
						}
					}	
					if(a!=null)
					{
						for(String s:a)
						{
							words2.add(s);
						}
					}	
				}
			}
			words.addAll(words2);
			System.out.println("Word list size:"+words.size());
		}
		
		/////

		wordList=new String[words.size()];
		words.toArray(wordList);
		
		indices =new HashMap<String,Integer>();
		for(int i=0;i<wordList.length;i++)
		{
			String w=wordList[i];
			indices.put(w, i);
		}
		

		
		matrix = new float[wordList.length][wordList.length];
		for(int i=0;i<wordList.length;i++)
		{
			matrix[i][i]=1;
		}
		
		for(String w:words)
		{
			for(String pos:wordnet.getPos(w))
			{
				String[] a =wordnet.getAllAntonyms(w, pos);
				String[] b =wordnet.getSynonyms(w, pos);
				
//				String[] c = wordnet.getSynset(w, pos);
				
//				String[] defs =wordnet.getAllGlosses(w, pos);
//				if(defs!=null)
//				{
//					LinkedList<String> list = getToekns(defs,5); 
//					for(String stopword:stopWords)
//					{
//						list.remove(stopword);
//					}
//					setWeights(w, list, 0.3F);
//				}

//				if(c!=null)
//				{
//					LinkedList<String> list = getToekns(c); 
//					setWeights(w, list, 0.5F);
//				}	

				if(a!=null)
				{
					List<String> list = Arrays.asList(a);
					setWeights(w, list, -1F);
				}
					
				if(b!=null)
				{
					List<String> list = getToekns(b); 
					setWeights(w, list, 1F);
				}
					
			}
		}
		
		//self validation  : too slow. won't finish
//		int correct=0;
//		int total=0;
//		for(String w:words)
//		{
//			for(String pos:wordnet.getPos(w))
//			{
//				String[] a =wordnet.getAllAntonyms(w, pos);
//				String[] b =wordnet.getSynonyms(w, pos);
//
//				if(a!=null)
//				{
//					LinkedList<String> list = getToekns(a); 
//					for(String k:list)
//					{
//						total++;
//						double sim=getSimilarity(w,k);
//						if(sim<0)
//						{
//							correct++;
//						}
//						//System.out.printf("Antonyms:%s\t%s\t%f\n",w,k, sim);
//					}
//				}
//					
//				if(b!=null)
//				{
//					LinkedList<String> list = getToekns(b); 
//					for(String k:list)
//					{
//						total++;
//						double sim=getSimilarity(w,k);
//						if(sim>0)
//						{
//							correct++;
//						}
//						//System.out.printf("Synonyms:%s\t%s\t%f\n",w,k, sim);
//					}
//				}
//					
//			}
//		}
//		System.out.printf("Self validation: Total %d\n", total);
//		System.out.printf("Self validation: Correct %d\n", correct);


		//validation
		
		//test
//		{
//			double sim = getSimilarity("hiatus","nexus");	
//			System.out.println(sim);
//		}
//
//		{
//			double sim = getSimilarity("laconic","voluble");	
//			System.out.println(sim);
//		}
//		
//		populteWeights();
//		
//
//		{
//			double sim = getSimilarity("hiatus","nexus");	
//			System.out.println(sim);
//		}
//
//		{
//			double sim = getSimilarity("laconic","voluble");	
//			System.out.println(sim);
//		}
//		
//		populteWeights();
//		
//
//		{
//			double sim = getSimilarity("hiatus","nexus");	
//			System.out.println(sim);
//		}
//
//		{
//			double sim = getSimilarity("laconic","voluble");	
//			System.out.println(sim);
//		}
//		
		
		
	}
	
	public void output() throws Exception
	{
		System.out.println("Outputting to : /tmp/wordMatrix.txt");
		System.out.println("Outputting to : /tmp/wordList.txt");
		
		//output
		PrintWriter p2= new PrintWriter("/tmp/wordList.txt");
		
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
		

		PrintWriter p1= new PrintWriter("/tmp/wordList.txt");
		for(int i=0;i<wordList.length;i++)
		{
			String w=wordList[i];
			p1.println(w+"\t"+i);
		}
		p1.close();
	}
	public void answerGREQuestion(GREQuestion q, int[] stats)//stat: 0 answered; 1 answered correctly
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
			stats[0]++;

			System.out.print("\t-->Guess["+ans+"]\tCorrctAns["+q.getAnswer()+"]");
			boolean isCorrect =  q.checkAnswer(ans);
			if(isCorrect)
			{
				stats[1]++;
			}
		}
		else
		{
			System.out.print("\tSkipping\tCorrctAns["+q.getAnswer()+"]");
		}
		System.out.println();
		
	}

	private static double logistic(double x)
	{
		return ( 2.0/(1 + Math.exp(-1f*x)) ) - 1 ;
	}
	
	public void populteWeights()
	{
		try
		{
			String path = "/tmp/defMatrix.dat";
			DataOutputStream os=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
			
			for(int i=0;i<wordList.length;i++)
			{
				float[] newWeights =  new float[wordList.length];
				//String w=wordList[i];
				
				for(int j=0;j<wordList.length;j++)
				{
					float val = matrix[i][j];
					if(val!=0)
					{
						for(int k=0;k<wordList.length;k++)
						{
							newWeights[k] += (float)( matrix[j][k]*0.3f* val);
						}
						newWeights[j] += val;
					}
				}
				
				for(int k=0;k<wordList.length;k++)
				{
					float log = (float) logistic(newWeights[k]);
					os.writeFloat(log);
				}
				if(i%1000==0)
					System.out.println("Updating row "+ i);
			}
			
			os.flush();
			os.close();

			DataInputStream is=new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
			for(int i=0;i<wordList.length;i++)
			{
				for(int j=0;j<wordList.length;j++)
				{
					matrix[i][j] = is.readFloat();
				}
			}
			System.out.println("is.available() = "+ (is.available()));
			is.close();
			
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void setWeights(String w, List<String> words, float weight)
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
			dot += this.matrix[wordIdx1][i] * this.matrix[wordIdx2][i];
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
		DefMatrixMaker m=new DefMatrixMaker();
		m.setExpand(false);
		m.doWork(wordList,stopWordList);
		m.output();
	}

}

