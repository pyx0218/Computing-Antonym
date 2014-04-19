import java.util.Arrays;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

import rita.wordnet.RiWordnet;


public class Test1
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		RiWordnet wordnet = new RiWordnet();
		String word="abortifacient";
		for(String pos:wordnet.getPos(word))
		{
			String[] a=wordnet.getAntonyms(word, pos);
			if(a!=null)
			{
				System.out.println(Arrays.toString(a));
			}
			
			a=wordnet.getSynonyms(word, pos);
			if(a!=null)
			{
				System.out.println(Arrays.toString(a));
			}
		}
		
		System.setProperty("wordnet.database.dir", "./resources/WordNet-3.0/dict");
		///////////////
		NounSynset nounSynset; 
		NounSynset[] hyponyms; 

		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = database.getSynsets(word); 
		for (int i = 0; i < synsets.length; i++) { 
		    String[] ws = (synsets[i]).getWordForms();
		    System.out.println("Synset:"+Arrays.toString(ws));
		    
		    for(String w:ws)
		    {
		    	WordSense[] senses = synsets[i].getAntonyms(w);
		    	for(WordSense sense:senses)
		    	{
				    System.out.println("Antonym Sense:"+sense.getWordForm());
		    	}
		    }
//		    hyponyms = nounSynset.getHyponyms(); 
//		    System.err.println(nounSynset.getWordForms()[0] + 
//		            ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms"); 
		}
		
		
	}

}

