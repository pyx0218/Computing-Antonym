import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import rita.wordnet.*;


public class MatrixAntonymChecker
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
	
	public MatrixAntonymChecker() {}
	
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
			int result = maker.answerGREQuestion(q,cosineSimilarity);

			if(result != MatrixAntonymChecker.ANSWER_SKIPPED)
			{
				questionsAnswered++;
				if(result == MatrixAntonymChecker.ANSWER_CORRECT)
				{
					questionsCorrect ++;
				}
			}
		}
		double questionsSkipped = questionsTotal - questionsAnswered;
		
		precision = (double)questionsCorrect/questionsAnswered;
		recall = (double)questionsCorrect/questionsTotal;
		f1 = 2*(precision*recall)/(precision+recall);
		
		System.out.println("Total=\t"+questionsTotal);
		System.out.println("Correct=\t"+questionsCorrect);
		System.out.println("Skipped=\t"+questionsSkipped);
		System.out.println("Answered=\t"+questionsAnswered);
		System.out.println("precision=\t"+precision);
		System.out.println("recall=\t"+recall);
		System.out.println("F1=\t"+f1);
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

		runTest2(maker,  questions,cosineSimilarity );
		//
		maker.populteWeights();
		runTest2(maker,  questions ,cosineSimilarity);
		
		//
		System.out.println("Patching....");
		runTest2(maker,  questions ,cosineSimilarity);
		//maker.initWeights();
		maker.patchSeeds(useMohammadSeeds);
		
		//maker.populteWeights();
		runTest2(maker,  questions ,cosineSimilarity);
	}
	
	public static void test1(String testPath, boolean useCosineSimilarity, boolean useMohammadSeeds ) throws Exception
	{
		MatrixAntonymChecker t=new MatrixAntonymChecker();
		//t.runTest2(testPath, true, JobimParser.getJobimQuestions());     //Matrix's method
		System.out.println("Test dataset:		   " + testPath);
		System.out.println("Use cosine similarity: " + useCosineSimilarity);
		System.out.println("Use useMohammadSeeds:  " + useMohammadSeeds);
		t.runTest2(testPath, true, null, useCosineSimilarity, useMohammadSeeds);     //Matrix's method
	}
	
	public static void main(String[] args) throws Exception
	{
		String testPath="resources/testset.txt";
		test1(testPath,true,false);
		test1(testPath,true,true);
		test1(testPath,false,false);
		test1(testPath,false,true);
	}

}

