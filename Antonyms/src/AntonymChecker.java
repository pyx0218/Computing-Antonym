import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import rita.wordnet.*;


public class AntonymChecker
{
	private Map<String,HashSet<String>> dict=new HashMap<String,HashSet<String>>();
	private RiWordnet wordnet;
	
	private int questionsCorrect  = 0;
	private int questionsSkipped  = 0;
	private int questionsAnswered = 0;
	private int questionsTotal    = 0;
	private double precision = 0;
	private double recall = 0;
	private double f1 = 0;
	
	public AntonymChecker() {}
	
	public void initAntLexicon(String path) throws Exception
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
			HashSet<String> set=dict.get(w1);
			if(set==null)
			{
				set=new HashSet<String>();
				dict.put(w1, set);
			}
			set.add(w2);
			
			if(lineCount%1000000==0)
			{
				System.out.println("Line:"+lineCount);
			}
		}
		s.close();
		System.out.println("Line:"+lineCount);
		System.out.println("Dictionary Built.");
	}
	
	//Initialize WordNet database
	public void initWordnet(String path) {
		//Use WordNet-3.1
		wordnet = new RiWordnet();
		//Use WordNet-3.0
		//wordnet.setWordnetHome(path+"dict\\");
	}
	
	public void runTest(String testPath) throws Exception
	{
		GREDataSetParser p=new GREDataSetParser();
		List<GREQuestion> questions = p.parse(testPath);
		for(GREQuestion q:questions)
		{
			questionsTotal++;
			//if(answerByAntlexicon(q))
			if(answerByWordnet(q))
			{
				questionsCorrect++;
			}
		}
		precision = (double)questionsCorrect/questionsAnswered;
		recall = (double)questionsCorrect/questionsTotal;
		f1 = 2*(precision*recall)/(precision+recall);
		
		System.out.println("questionsTotal=\t"+questionsTotal);
		System.out.println("questionsCorrect=\t"+questionsCorrect);
		System.out.println("questionsSkipped=\t"+questionsSkipped);
		System.out.println("questionsAnswered=\t"+questionsAnswered);
		System.out.println("precision=\t"+precision);
		System.out.println("recall=\t"+recall);
		System.out.println("F1=\t"+f1);
	}
	
	public Set<String> getSyms(Set<String> inputWords)
	{
		HashSet<String> similarWords = new HashSet<String>();	
		for(String qStr: inputWords)
		{
			similarWords.add(qStr);
			
			String[] partsofspeech = wordnet.getPos(qStr);
	        for (int i = 0; i < partsofspeech.length; i++) {
	            //System.out.println("POS for "+qStr+" : " +partsofspeech[i]);
	        	
	    		//String[] defs = wordnet.getAllGlosses(qStr, partsofspeech[i] );
	    		//System.out.println("Definition :"+  Arrays.toString(defs) );
	        	
	        	//System.out.println("Synnet for "+qStr+" : " + Arrays.toString(wordnet.getSynset(qStr,partsofspeech[i])));
	    		
	        	String[] synset = wordnet.getSynset(qStr, partsofspeech[i]);
	        	if(synset!=null)
	        	{
	        		similarWords.addAll(Arrays.asList(synset));
	        	}

	        	String[] synset2 = wordnet.getSynonyms(qStr, partsofspeech[i]);
	        	if(synset2!=null)
	        	{
	        		similarWords.addAll(Arrays.asList(synset2));
	        	}
	        	
	        }
		}
		
        return similarWords;
	}
	
	public Set<String> getAntonyms(Set<String> similarWords)
	{
		Set<String> set =  new HashSet<String>();
		
        for(String qStr2 : similarWords)
        {
        	String[] pos = wordnet.getPos(qStr2);
        	for (int i = 0; i < pos.length; i++)
        	{
        		String[] ans= wordnet.getAllAntonyms(qStr2,pos[i]);
        		if(ans!=null)
        		{
            		for(String an : ans)
            		{
                		set.add(an);
            		}
        		}
        	}
        }
        
        return set;
	}
	
	private final static int QUESTION_EXPANSION_LEVEL = 1;
	private final static int ANSWEr_EXPANSION_LEVEL = 1;
	
	//Use WordNet to find antonyms
	public boolean answerByWordnet(GREQuestion q) {
		String qStr = q.getQuestion();

		Set<String> allSimilarWords = new HashSet<String>();
		Set<String> similarWords = new HashSet<String>();
		similarWords.add(qStr);
		allSimilarWords.add(qStr);
		
		for(int i=0;i<QUESTION_EXPANSION_LEVEL;i++)
		{
			Set<String> similarWords2=getSyms(similarWords);
			for(String s:allSimilarWords)
			{
				similarWords2.remove(s);
			}
			allSimilarWords.addAll(similarWords2);
			similarWords= similarWords2;
		}

		System.out.println("========================================");
		System.out.println("Question["+qStr+"] Options["+q.getOptions()+"] Answer["+q.getAnswer() +"]");
		System.out.println("Similar:"+qStr +" --> " +allSimilarWords);
		
		Set<String> answerSet =  getAntonyms(allSimilarWords);
        
		System.out.println("Antonyms:"+qStr +" --> " +answerSet);


		Set<String> aaa= new HashSet<String>();
		aaa.add(q.getAnswer());
		System.out.println("Anaswer Sym:"+q.getAnswer() +" --> " +getSyms(aaa));
		System.out.println("Anaswer Ant:"+q.getAnswer() +" --> " +getAntonyms(getSyms(aaa)));
		
		for(String candidate:q.getOptions())
		{
			Set<String> dummyCandidateSet = new HashSet<String>();
			dummyCandidateSet.add(candidate);
			Set<String> allCandidateAnsSet =  new HashSet<String>();
			allCandidateAnsSet.add(candidate);
			
			for(int i=0;i<ANSWEr_EXPANSION_LEVEL;i++)
			{
				Set<String> dummyCandidateSet2=getSyms(dummyCandidateSet);
				for(String s:allCandidateAnsSet)
				{
					dummyCandidateSet2.remove(s);
				}
				allCandidateAnsSet.addAll(dummyCandidateSet2);
				dummyCandidateSet= dummyCandidateSet2;
			}
			
			//if(set.contains(candidate))
			if(hasOverlap(allCandidateAnsSet, answerSet))
			{
				questionsAnswered++;
				boolean correct =  q.checkAnswer(candidate);
				System.out.println("[X] Answering:"+qStr+":"+candidate +" "+(correct?"[Correct]":"[Wrong]"));
				return correct;
			}
		}
		
		questionsSkipped ++;
		return false;
	}
	
	public boolean hasOverlap(Set<String> s1, Set<String> s2)
	{
		for(String e1:s1)
		{
			for(String e2:s2)
			{
				if(e1.equals(e2))
					return true;
			}
		}
		return false;
	}
	
	//Use AntonymsLexicon-OppCats-Affixes to find antonyms
	public boolean answerByAntlexicon(GREQuestion q)
	{
		String qStr = q.getQuestion();
		HashSet<String> set = dict.get(qStr);
		if(set!=null)
		{
			for(String candidate:q.getOptions())
			{
				if(set.contains(candidate))
				{
					questionsAnswered++;
					return q.checkAnswer(candidate);
				}
			}
		}
		questionsSkipped ++;
		return false;
	}
	
	public static void main(String[] args) throws Exception
	{
		String dictPath="resources/AntonymsLexicon-OppCats-Affixes.gz";
		String wordnetPath = "resources\\WordNet-3.0\\";
		String testPath="resources/testset.txt";
		
		AntonymChecker t=new AntonymChecker();
		//t.initAntLexicon(dictPath);
		t.initWordnet(wordnetPath);
		t.runTest(testPath);
	}

}
