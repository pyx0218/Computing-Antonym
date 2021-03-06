import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class JobimParser {
	public List<GREQuestion> parse(String path) throws Exception{
		List<GREQuestion> questions =new LinkedList<GREQuestion>();
		
		Scanner s=new Scanner(new FileInputStream(path));
		while(s.hasNext())
		{
			String line=s.nextLine();
			String[] lines = line.split(":");
			if(lines[1].length() == 1)
				System.out.println("Null");
			else{
				GREQuestion q = new GREQuestion();
				q.initQuestionAnswer(lines[0], "ans");
				//System.out.println(lines[1]);
				String[] lines2 = lines[1].split(";");
				int count = 0;
				for(String l:lines2){
					//System.out.println(l.split(" ")[0]);
					if(count == 0){
						count++;
						continue;
					}
						
					else{
						
						q.addOption(l.split(" ")[0]);

						if(q.getOptions().size()>=10)
						{
							break;
						}
					}
				}
				questions.add(q);
				
				
			}
		}
		return questions;
		
	}


	public static List<GREQuestion> getJobimQuestions()
	{
		String file_path = "resources/jobimResult2.txt";
		JobimParser jp = new JobimParser();
		List<GREQuestion> questions=null;
		try{
			questions = jp.parse(file_path);
			
		}catch (Exception e){
			System.out.println("parse failure");
		}
		return questions;
	}
	
	
	public static void main(String args[]){
		//String file_path = "resources/jobimResult.txt";
		String file_path = "resources/wordlistResult.txt";
		JobimParser jp = new JobimParser();
		List<GREQuestion> questions;
		try{
			questions = jp.parse(file_path);
			for(GREQuestion q:questions)
			{
				System.out.println(q);
			}
			
		}catch (Exception e){
			System.out.println("parse failure");
		}
	}
}
