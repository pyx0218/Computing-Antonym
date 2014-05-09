/* This class uses wordnet (rita api) to test on the test set.
 * Please put testset.txt in the /resources folder
 */

import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import rita.wordnet.*;


public class WordNet
{
	String testPath="resources/testset.txt";
	
	private RiWordnet wordnet;
	
	private int questionsCorrect  = 0;
	private int questionsSkipped  = 0;
	private int questionsAnswered = 0;
	private int questionsTotal    = 0;
	private double precision = 0;
	private double recall = 0;
	private double f1 = 0;
	
	public WordNet() {
		initWordnet();
	}
	
	//Initialize WordNet database
	public void initWordnet() {
		//Use WordNet-3.1
		wordnet = new RiWordnet();
	}
	
	public void runTest() throws Exception
	{
		GREDataSetParser p=new GREDataSetParser();
		List<GREQuestion> questions = p.parse(testPath);
		for(GREQuestion q:questions)
		{
			questionsTotal++;
			String ans = answerByWordnet(q);
			if(!ans.equals("")) {
				questionsAnswered++;
				if(q.checkAnswer(ans))
				{
					questionsCorrect++;
				}
			}
			else {
				questionsSkipped++;
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
	
	//Use WordNet to find antonyms
	public String answerByWordnet(GREQuestion q) {
		String qStr = q.getQuestion();
		
		Set<String> set =  new HashSet<String>();

    	String[] pos = wordnet.getPos(qStr);
    	for (int i = 0; i < pos.length; i++)
    	{
    		String[] ans= wordnet.getAllAntonyms(qStr,pos[i]);
    		if(ans!=null)
    		{
        		for(String an : ans)
        		{
            		set.add(an);
        		}
    		}
    	}
        
    	if(set != null) {
			for(String candidate:q.getOptions())
			{	
				if(set.contains(candidate))
				{
					return candidate;
				}
			}
    	}
		return "";
	}
	

	public static void main(String[] args) throws Exception
	{	
		WordNet w = new WordNet();
		w.runTest();
	}

}