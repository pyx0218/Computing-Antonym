/* This class uses 3.5 m opposites pairs provided by Mohammad's paper
 * and wordnet (rita api) to test on the test set.
 * Please put AntonymsLexicon-OppCats.gz and testset.txt in the /resources folder
 */

import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import rita.wordnet.*;


public class WordNetandMohammad
{
	String testPath="resources/testset.txt";
	private MohammadOpposites m;
	private WordNet w;
	
	private int questionsCorrect  = 0;
	private int questionsSkipped  = 0;
	private int questionsAnswered = 0;
	private int questionsTotal    = 0;
	private double precision = 0;
	private double recall = 0;
	private double f1 = 0;
	
	public WordNetandMohammad() throws Exception {
		m = new MohammadOpposites();
		w = new WordNet();
	}
	
	
	public void runTest() throws Exception
	{
		GREDataSetParser p=new GREDataSetParser();
		List<GREQuestion> questions = p.parse(testPath);
		for(GREQuestion q:questions)
		{
			questionsTotal++;
			String ans = w.answerByWordnet(q);
			if(ans.equals("")) {
				ans = m.answerByAntlexicon(q);
			}
			
			if(!ans.equals("")){
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

	
	public static void main(String[] args) throws Exception
	{
		WordNetandMohammad wm = new WordNetandMohammad();
		
		wm.runTest();
	}

}