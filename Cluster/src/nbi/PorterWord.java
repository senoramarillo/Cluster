package nbi;

public class PorterWord implements Comparable<PorterWord>{
	protected String fWord;
	final String fVowels = "aAeEiIoOuUyYäÄöÖüÜ";
	
	public PorterWord(String word){
		fWord = new String(stem(word));		
	}
	

	private String stem(String word) {
		final String sEndings = "bdfghklmnrt";
		final String stEndings = "bdfghklmnt";					
		
		// First replace ß by ss
		word = word.replaceAll("ß", "ss");	
		
		// Put u and y to upper case
		word = word.replaceAll("(?=.*[" + fVowels + "])[u](?=[" + fVowels + "].*)", "U");
		word = word.replaceAll("(?=.*[" + fVowels + "])[y](?=[" + fVowels + "].*)", "Y");
		
		int r1 = getRegion(word);
		int r2 = getRegion(word.substring(r1));
		
		// Adjust r1
		if(r1 < 3)
			r1 = 3;
		
		String suffix = word.length() < r1 ? "" : word.substring(r1);
		
		// Step 1
		// a (3 letters)
		if(suffix.endsWith("ern"))
			word = word.substring(0, word.length()-3);
		else{
			// a and b (2 letters)
			suffix = suffix.length() >= 2 ? suffix.substring(suffix.length()-2, suffix.length()) : ""; 
			switch(suffix)
			{
			case "em":
				word = word.substring(0, word.length()-2);
				break;
			case "er":
			case "en":
			case "es":
				word = word.substring(0, word.length()-2);
				if(word.endsWith("niss"))
					word = word.substring(0, word.length()-1);
				break;
				
			default: // b (1 letter) and c
				if(suffix.endsWith("e")){
					word = word.substring(0, word.length()-1);
					if(word.endsWith("niss"))
						word = word.substring(0, word.length()-1);
				}
				else if(suffix.endsWith("s") && sEndings.contains(new Character(word.charAt(word.length()-2)).toString()))
					word = word.substring(0, word.length()-1);
			}			
		}
		
		suffix = word.length()-1 > r1 ? word.substring(r1) : ""; 
			
		// Step 2
		// a (3 letters)
		if(suffix.endsWith("est"))
			word = word.substring(0, word.length()-3);
		else{
			suffix = suffix.length() >= 2 ? suffix.substring(suffix.length()-2, suffix.length()) : ""; 
			// a and b (2 letters)
			switch(suffix)
			{
			case "er":
			case "en":
				word = word.substring(0, word.length()-2);
				break;
				
			case "st":
				if(word.length() > 2 && stEndings.contains(new Character(word.charAt(word.length()-3)).toString()))
					word = word.substring(0, word.length()-2);
			}
		}
			
		suffix = word.length()-2 > r2 ? word.substring(r2) : "";
		suffix = suffix.length() >= 4 ? suffix.substring(suffix.length()-4, suffix.length()) : ""; 
		
		// Step 3
		// d-suffixes (4 letters)
		switch(suffix){
		case "isch":
			if(word.charAt(word.length()-5) != 'e')
				word = word.substring(0, word.length()-4);
			suffix = "";
			break;
			
		case "lich":
		case "heit":			
			word = word.substring(0, word.length()-4);			
			if(word.length() >= 6 && r1 < word.length()-4 && (word.substring(r1,word.length()-4).endsWith("er") ||
					word.substring(r1,word.length()-4).endsWith("en")))
				word = word.substring(0, word.length()-2);
			suffix = "";
			break;
			
		case "keit":
			word = word.substring(0, word.length()-4);
			if(r2 < word.length() && word.substring(r2).endsWith("lich"))
				word = word.substring(0, word.length()-4);
			else if(r2 < word.length() && word.substring(r2).endsWith("ig"))
				word = word.substring(0, word.length()-2);
			suffix = "";
			break;
		}
		
		// d-suffixes (3 letters)
		if(suffix.length() > 3)
			suffix = suffix.substring(suffix.length() - 3, suffix.length());
		
		switch(suffix){
		case "end":
		case "ung":
			word = word.substring(0, word.length()-3);
			if(word.substring(r2).endsWith("lich"))
				word = word.substring(0,  word.length()-4);
			else if(word.substring(r2).endsWith("ig"))
				word = word.substring(0,  word.length()-2);
			
			suffix = "";
			break;
		}
		
		// d-suffixes (2 letters)
		if(suffix.length() > 1)
			suffix = suffix.substring(suffix.length() - 2, suffix.length());
		
		switch(suffix){
		case "ig":
		case "ik":
			if(word.charAt(word.length()-3) != 'e')
				word = word.substring(0, word.length()-2);
		}
		
		
		// turn U and Y back into lower case, and remove the umlaut accent from a, o and u.
		word = word.replaceAll("(?=.*[" + fVowels + "])[U](?=.*)", "u");
		word = word.replaceAll("(?=.*[" + fVowels + "])[Y](?=.*)", "y");
		word = word.replaceAll("ö", "o");
		word = word.replaceAll("ü", "u");
		word = word.replaceAll("ä", "a");
		
		return word;
	}



	private int getRegion(String substring) {
		int res = 0;
		boolean lkfVowels = true;
		for(Character c : substring.toCharArray()){
			++res;
			
			if(lkfVowels && fVowels.contains(c.toString()))
				lkfVowels = false;
				
			else if(!lkfVowels && !fVowels.contains(c.toString()))
				break;
		}
		return res;
	}
	
	
	public String toString(){
		return fWord;
	}
	
	
	public boolean equals(Object otherWord){
		return fWord.equals(otherWord);
	}
	
	
	public int compareTo(PorterWord otherWord) {		
		return fWord.compareTo(otherWord.toString());
	}
	
}
