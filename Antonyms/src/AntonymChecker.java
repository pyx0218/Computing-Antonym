import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class AntonymChecker
{
	private Map<String,Set<String>> dict=new HashMap<String,Set<String>>();
	
	public AntonymChecker()
	{
		
	}
	
	
	public void init(String path) throws Exception
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
			Set<String> set=dict.get(w1);
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
	
	public void runTest(String testPath) throws Exception
	{
		GREDataSetParser p=new GREDataSetParser();
		List<GREQuestion> questions = p.parse(testPath);
		for(GREQuestion q:questions)
		{
			questionsTotal++;
			if(answerIt(q))
			{
				questionsCorrect++;
			}
		}
		System.out.println("questionsTotal=\t"+questionsTotal);
		System.out.println("questionsCorrect=\t"+questionsCorrect);
		System.out.println("questionsSkipped=\t"+questionsSkipped);
		System.out.println("questionsAnswered=\t"+questionsAnswered);
	}
	
	private int questionsCorrect  = 0;
	private int questionsSkipped  = 0;
	private int questionsAnswered = 0;
	private int questionsTotal    = 0;
	
	public boolean answerIt(GREQuestion q)
	{
		String qStr = q.getQuestion();
		Set<String> set = dict.get(qStr);
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
		String testPath="resources/testset.txt";
		
		AntonymChecker t=new AntonymChecker();
		t.init(dictPath);
		t.runTest(testPath);
	}

}
