/* This class uses 3.5 m opposites pairs provided by Mohammad's paper
 * to test on the test set.
 * Please put AntonymsLexicon-OppCats.gz and testset.txt in the /resources folder
 */

import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class MohammadOpposites {
	//6.3 m
	//private String dictPath = "resources/AntonymsLexicon-OppCats-Affixes.gz";
	//3.5 m
	private String dictPath = "resources/AntonymsLexicon-OppCats.gz";
	//Test set
	private String testPath="resources/testset.txt";
	//Development set
	//private String testPath="resources/devset.txt";
	private Map<String,HashSet<String>> dict=new HashMap<String,HashSet<String>>();
	
	private int questionsCorrect  = 0;
	private int questionsSkipped  = 0;
	private int questionsAnswered = 0;
	private int questionsTotal    = 0;
	private double precision = 0;
	private double recall = 0;
	private double f1 = 0;
	
	public MohammadOpposites() throws Exception {
		initAntLexicon(dictPath);
	}
	
	private void initAntLexicon(String path) throws Exception
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
			HashSet<String> set1=dict.get(w1);
			if(set1==null)
			{
				set1=new HashSet<String>();
				dict.put(w1, set1);
			}
			set1.add(w2);
			HashSet<String> set2=dict.get(w2);
			if(set2==null)
			{
				set2=new HashSet<String>();
				dict.put(w2, set2);
			}
			set2.add(w1);
			
			if(lineCount%1000000==0)
			{
				System.out.println("Line:"+lineCount);
			}
		}
		s.close();
		System.out.println("Line:"+lineCount);
		System.out.println("Dictionary Built.");
	}
	
	public void runTest() throws Exception
	{
		GREDataSetParser p=new GREDataSetParser();
		List<GREQuestion> questions = p.parse(testPath);
		for(GREQuestion q:questions)
		{
			questionsTotal++;
			String ans = answerByAntlexicon(q);
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
	
	//Use AntonymsLexicon-OppCats-Affixes to find antonyms
	public String answerByAntlexicon(GREQuestion q)
	{
		String qStr = q.getQuestion();
		HashSet<String> set = dict.get(qStr);
		if(set!=null)
		{
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
	
	public static void main(String[] args) throws Exception {
		MohammadOpposites m = new MohammadOpposites();
		m.runTest();
	}
}