import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import rita.wordnet.*;


public class MatrixAntonymCheckerForJobimDemo
{
	public final static int ANSWER_SKIPPED = 1;
	public final static int ANSWER_CORRECT = 2;
	public final static int ANSWER_WRONG = 3;
	
	private int questionsCorrect  = 0;
	private int questionsAnswered = 0;
	private int questionsTotal    = 0;
	private double precision = 0;
	private double recall = 0;
	private double f1 = 0;
	
	public MatrixAntonymCheckerForJobimDemo() {}
	
	private void runTest2(DefMatrixMaker2 maker, List<GREQuestion> questions, boolean cosineSimilarity)
	{
		questionsTotal=0;
		questionsCorrect=0;
		questionsAnswered =0;
		//int[] stats=new int[]{0,0};//stat: 0 answered; 1 answered correctly
		for(GREQuestion q:questions)
		{
			questionsTotal++;
			//answerByWordnet(GREQuestion q) ;
			maker.answerGREQuestion2(q,cosineSimilarity);
		}
		
		System.out.println("Total=\t"+questionsTotal);
	}
	
	public void runTest2(String testPath, boolean expand,List<GREQuestion> questions ,
			boolean cosineSimilarity , boolean useMohammadSeeds ) throws Exception
	{

		String wordList = "resources/google-10000-english.txt";
		String stopWordList = "resources/stopwords";
		DefMatrixMaker2 maker = new DefMatrixMaker2();
		maker.setExpand(expand);
		maker.loadSeedFile("resources/AntonymsLexicon-OppCats.gz");
		//maker.loadSeedFile("resources/AntonymsLexicon-OppCats-Affixes.gz");
		maker.doWork(wordList,stopWordList);
		maker.patchSeeds(useMohammadSeeds);
		
		if(questions==null)
		{
			GREDataSetParser p=new GREDataSetParser();
			questions = p.parse(testPath);
		}
		//
		runTest2(maker,  questions,cosineSimilarity );
		//
		maker.populteWeights();
		//runTest2(maker,  questions ,cosineSimilarity);
		
		//
		//System.out.println("Patching....");
		//maker.initWeights();
		//maker.patchSeeds(useMohammadSeeds);
		runTest2(maker,  questions ,cosineSimilarity);
	}
	
	public static void test1( boolean useCosineSimilarity, boolean useMohammadSeeds ) throws Exception
	{
		MatrixAntonymCheckerForJobimDemo t=new MatrixAntonymCheckerForJobimDemo();
		//t.runTest2(testPath, true, JobimParser.getJobimQuestions());     //Matrix's method
		System.out.println("Use cosine similarity: " + useCosineSimilarity);
		System.out.println("Use useMohammadSeeds:  " + useMohammadSeeds);
		
		List<GREQuestion> questions = JobimParser.getJobimQuestions();
		t.runTest2(null, true, questions, useCosineSimilarity, useMohammadSeeds);     //Matrix's method
	}
	
	public static void main(String[] args) throws Exception
	{
		test1(false,true);
		//test1(true,true);
	}

}

