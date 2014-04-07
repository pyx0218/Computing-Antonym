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
	
	//Use WordNet to find antonyms
	public boolean answerByWordnet(GREQuestion q) {
		String qStr = q.getQuestion();
		String[] ants = wordnet.getAllAntonyms(qStr, RiWordnet.ADJ);

		if(ants != null) {
			HashSet<String> set = new HashSet<String>(Arrays.asList(ants));	
			for(String candidate:q.getOptions())
			{
				if(set.contains(candidate))
				{
					System.out.println(qStr+":"+candidate);
					questionsAnswered++;
					return q.checkAnswer(candidate);
				}
			}
		}
		questionsSkipped ++;
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
