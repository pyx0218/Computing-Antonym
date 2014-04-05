import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GREDataSetParser
{
	public GREDataSetParser()
	{
	}
	
	private Pattern pattern=Pattern.compile("(\\w+):\\s+(\\w+)\\s+(\\w+)\\s+(\\w+)\\s+(\\w+)\\s+(\\w+)\\s+::\\s+(\\w+)");
	
	public List<GREQuestion> parse(String path) throws Exception
	{
		List<GREQuestion> questions =new LinkedList<GREQuestion>();
		
		Scanner s=new Scanner(new FileInputStream(path));
		while(s.hasNext())
		{
			String line=s.nextLine();
			Matcher m=pattern.matcher(line);
			if(m.find())
			{
				GREQuestion q=new GREQuestion();
				q.initQuestionAnswer(m.group(1), m.group(7));
				for(int i=2;i<7;i++)
				{
					q.addOption(m.group(i));
				}
				questions.add(q);
			}
		}
		s.close();
		
		return questions;
	}
	
	
	
	public static void main(String args[]) throws Exception
	{
		GREDataSetParser p=new GREDataSetParser();
		List<GREQuestion> questions = p.parse("resources/testset.txt");
		for(GREQuestion q:questions)
		{
			System.out.println(q);
		}
	}
}
