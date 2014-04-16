import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import rita.wordnet.RiWordnet;


public class WordNetConvageTest
{
	private RiWordnet wordnet=new RiWordnet();
	
	public void runTest(String testPath) throws Exception
	{
		GREDataSetParser p=new GREDataSetParser();
		List<GREQuestion> questions = p.parse(testPath);
		TreeSet<String> words=new TreeSet<String>();
		for(GREQuestion q:questions)
		{
			words.add(q.getQuestion());
			for(String s:q.getOptions())
			{
				words.add(s);	
			}
		}
		
		PrintWriter writer=new PrintWriter("resources/gretestset_wordlist");
		int totalQuestions = questions.size();
		int total = words.size();
		int coverred = 0;
		for(String w:words)
		{
			boolean found=false;
			for(String pos:wordnet.getPos(w))
			{
				found=true;
			}
			if(found)
			{
				coverred++;
			}
			else
			{
				System.out.println("404:"+w);
			}
			
			writer.println(w);
		}

		writer.close();
		System.out.println("total questions=\t"+totalQuestions);
		System.out.println("total words=\t"+total);
		System.out.println("covered=\t"+coverred);
		System.out.println("coverage=\t"+ (coverred*1.0/total));
	}
	
	public static void main(String[] args) throws Exception
	{
		String testPath="resources/testset.txt";
		new WordNetConvageTest().runTest(testPath);
	}
	

}
