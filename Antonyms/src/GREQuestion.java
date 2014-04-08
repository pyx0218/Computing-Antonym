import java.util.LinkedList;
import java.util.List;


public class GREQuestion
{
	private String question;
	private String answer;
	private List<String> options;
	
	public GREQuestion()
	{
		options = new LinkedList<String>();
	}
	
	public void initQuestionAnswer(String qes,String ans)
	{
		question = qes;
		answer = ans;
	}
	
	public void addOption(String s)
	{
		options.add(s);
	}
	
	public boolean checkAnswer(String s)
	{
		return answer.equalsIgnoreCase(s);
	}

	public String getQuestion()
	{
		return question;
	}

	public void setQuestion(String question)
	{
		this.question = question;
	}

	public List<String> getOptions()
	{
		return options;
	}

	public void setOptions(List<String> options)
	{
		this.options = options;
	}
	
	

	public String getAnswer()
	{
		return answer;
	}

	@Override
	public String toString()
	{
		return "GREQuestion [question=" + question + ", answer=" + answer
				+ ", options=" + options + "]";
	}
	
	
}
