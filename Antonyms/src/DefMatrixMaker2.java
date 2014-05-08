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


public class DefMatrixMaker2
{
	private RiWordnet wordnet;
	private HashMap<String,Integer> indices ;
	private float[][] matrix;
	private String[] wordList;
	boolean expand = true;
	private Map<String,List<String>> dict=new HashMap<String,List<String>>();
	private Set<String> dictWords=new HashSet<String>();
	
	public DefMatrixMaker2()
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
	
	public boolean isExpand()
	{
		return expand;
	}

	public void setExpand(boolean expand)
	{
		this.expand = expand;
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
			List<String> set=dict.get(w1);
			if(set==null)
			{
				set=new LinkedList<String>();
				dictWords.add(w1);
				dict.put(w1, set);
			}
			set.add(w2);
			dictWords.add(w2);
			
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
			Set<String> words2 = new HashSet<String>();
			for(String w:words)
			{
				for(String pos:wordnet.getPos(w))
				{
					String[] a =wordnet.getAllAntonyms(w, pos);
					String[] b =wordnet.getSynset(w, pos);
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
		}

		System.out.println("Word list size:"+words.size());
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
		
		initWeights();
		
		
	}
	
	public void initWeights()
	{
		for(String w:words)
		{
			for(String pos:wordnet.getPos(w))
			{
				String[] a =wordnet.getAllAntonyms(w, pos);
				String[] b =wordnet.getSynset(w, pos);

				if(a!=null)
				{
					List<String> list = Arrays.asList(a);
					setWeights(w, list, -1F);
				}
					
				if(b!=null)
				{
					List<String> list =  Arrays.asList(b); 
					setWeights(w, list, 1F);
				}
					
			}
		}
		
	}
	
	
	public void patchSeeds(boolean useMohammadSeeds)
	{
		if(!useMohammadSeeds)
			return;
		for(Map.Entry<String,List<String>> entry :dict.entrySet())
		{
			String key = entry.getKey(); 
			List<String> list = entry.getValue();
			setWeights(key, list, -0.1F);
			
			for(String v:list)
			{
				LinkedList<String> ll=new LinkedList<String>();
				ll.add(key);
				setWeights(v, ll, -0.1F);
			}
		}
	}
	
	public void output() throws Exception
	{
		String contrastLinks ="/tmp/contrast-links.txt";
		String wordMatrix ="/tmp/wordMatrix.dat";
		System.out.println("Outputting to : /tmp/wordMatrix.dat");
		System.out.println("Outputting to : /tmp/wordList.txt");
		System.out.println("Outputting to : "+ contrastLinks);
		
		//output
		//PrintWriter p2= new PrintWriter("/tmp/wordMatrix.txt");
		DataOutputStream os=new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(wordMatrix)));
		DataOutputStream p3= new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(contrastLinks)));
		
		for(int i=0;i<wordList.length;i++)
		{
			//String w=wordList[i];
			for(int j=0;j<wordList.length;j++)
			{
				//String w2=wordList[j];	
				//p2.print(matrix[i][j]+" ");
				os.writeFloat(matrix[i][j]);
				if(matrix[i][j]!=0)
				{
					p3.writeFloat(i);
					p3.writeFloat(j);
					p3.writeFloat(matrix[i][j]);
				}
			}
			//p2.println();
		}
		//p2.close();
		p3.close();
		os.close();
		
		

		PrintWriter p1= new PrintWriter("/tmp/wordList.txt");
		for(int i=0;i<wordList.length;i++)
		{
			String w=wordList[i];
			p1.println(w+"\t"+i);
		}
		p1.close();
	}
	
	public int answerGREQuestion(GREQuestion q, boolean cosineSimilarity)//stat: 0 answered; 1 answered correctly
	{
		String qStr = q.getQuestion();
		System.out.print("Questions: "+qStr+"\n");
		double smallestSim = 100;
		String ans=null;
		for(String o:q.getOptions())
		{
			double sim = 0 ;
			if(cosineSimilarity)
			{
				sim = getCosineSimilarity(qStr,o) ;
			}
			else
			{
				sim = getDisSimilarity(qStr,o) ;	
			}
			
			System.out.printf("\t%s[%6.4f]\n", o,  sim );
			
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
			return isCorrect?MatrixAntonymChecker.ANSWER_CORRECT:MatrixAntonymChecker.ANSWER_WRONG;
		}
		else
		{
			System.out.print("\tSkipping\tCorrctAns["+q.getAnswer()+"]\n");
			return MatrixAntonymChecker.ANSWER_SKIPPED;
		}
		
	}
	
	public void answerGREQuestion2(GREQuestion q, boolean cosineSimilarity)//stat: 0 answered; 1 answered correctly
	{
		String qStr = q.getQuestion();
		AnswerAndScore options[]=new AnswerAndScore[q.getOptions().size()];
		int i=0;
		for(String o:q.getOptions())
		{
			double sim = 0 ;
			if(cosineSimilarity)
			{
				sim = getCosineSimilarity(qStr,o) ;
			}
			else
			{
				sim = getDisSimilarity(qStr,o) ;	
			}
			
			options[i++]= new AnswerAndScore(o, sim);
		}
		Arrays.sort(options);
		System.out.print("===> Questions: "+qStr+"\n");
		for(AnswerAndScore s:options)
		{
			System.out.printf("===>%s\t[%6.4f]\n",s.answer, s.score);
		}
	}
	
	static class AnswerAndScore implements Comparable<AnswerAndScore>
	{
		private String answer;
		private double score;
		public AnswerAndScore(String answer, double score)
		{
			super();
			this.answer = answer;
			this.score = score;
		}
		@Override
		public int compareTo(AnswerAndScore arg0)
		{
			// TODO Auto-generated method stub
			double s=arg0.score - this.score;
			if(s<0)
				return 1;
			else if(s==0)
				return 0;
			else 
				return -1;
		}
		
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
				
				float sum = 0;
				for(int j=0;j<wordList.length;j++)
				{
					float val = matrix[i][j];
					sum += Math.abs(val);
					if(val!=0)
					{
						for(int k=0;k<wordList.length;k++)
						{
							newWeights[k] += (float)( matrix[j][k]* val);
						}
						newWeights[j] += val;
					}
				}
				
				for(int k=0;k<wordList.length;k++)
				{
					//float log = (float) logistic(newWeights[k]/sum);
					float log = (float) newWeights[k]/sum;
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
	
	public double getCosineSimilarity(String w1,String w2)
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
			dot += dotProd;
			normX += Math.pow( this.matrix[wordIdx1][i], 2);
			normY += Math.pow( this.matrix[wordIdx2][i], 2);
		}
		normX = Math.pow(normX, 0.5);
		normY = Math.pow(normY, 0.5);
		double score=dot/(normX*normY);
		return score;
	}
	
	public double getDisSimilarity(String w1,String w2)
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
		StringBuffer sb=new StringBuffer("["+w1+" and "+w2+"]:\n");
		
		int count=0;
		for(int i=0;i<this.wordList.length;i++)
		{
			double dotProd = this.matrix[wordIdx1][i] * this.matrix[wordIdx2][i];
			if(dotProd< 0 && Math.abs(this.matrix[wordIdx1][i])>=0.005 && Math.abs(this.matrix[wordIdx2][i])>=0.005 )
			{
				dot += dotProd;
				sb.append("\t<"+wordList[i]+"\t"+
						String.format("%.4f",  matrix[wordIdx1][i])+"\t" +
						String.format("%.4f",  matrix[wordIdx2][i])+">\n");
				count++;
			}
			normX += Math.pow( this.matrix[wordIdx1][i], 2);
			normY += Math.pow( this.matrix[wordIdx2][i], 2);
		}
		normX = Math.pow(normX, 0.5);
		normY = Math.pow(normY, 0.5);
		double score=dot/(normX*normY);
		sb.append("====\n");
		System.out.println(sb.toString());
		return score;
	}
	
	public static void main(String[] args) throws Exception
	{
		String wordList = "resources/google-10000-english.txt";
		String stopWordList = "resources/stopwords";
		DefMatrixMaker2 m=new DefMatrixMaker2();
		m.loadSeedFile("resources/AntonymsLexicon-OppCats-Affixes.gz");
		m.setExpand(false);
		m.doWork(wordList,stopWordList);
		m.populteWeights();
		m.output();
	}

}

