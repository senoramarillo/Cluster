package nbi;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;

public class Document {

	private Path fPath;
	HashMap<String, Integer> fTf = new HashMap<String, Integer>();
	HashMap<String, Double> fIdf = new HashMap<String, Double>();
	HashMap<String, Double> fTfIdf = new HashMap<String, Double>();
	static HashMap<Document, HashMap<Document, Double>> fDistanceCache = new HashMap<Document, HashMap<Document, Double>>();
	Integer fLength = null;

	public Document(Path path) {
		fPath = path;
	}
	
	public Document(HashMap<String, Double> tfIdf){
		fTfIdf = tfIdf;
	}

	public void addTerm(String word) {
		if(fTf.containsKey(word))
			fTf.put(word, fTf.get(word)+1);
		else
			fTf.put(word, 1);
	}

	public void setInverseDocumentFrequencies(HashMap<String, Integer> idf) {
		for(String word : fTf.keySet())
			fIdf.put(word, Math.log(idf.get(word)));
	}
	
	public Path getPath(){
		return fPath;
	}
	
	public Collection<String> getAllTerms(){
		return fTf.keySet();
	}
	
	public void calculateTfIdf(){
		for(String word : fTf.keySet())
			fTfIdf.put(word, (fTf.get(word)/(double)getLength())*fIdf.get(word));
	}
	
	public Integer getLength(){
		if(fLength == null){
			fLength = 0;
			for(Integer c : fTf.values())
				fLength += c;
		}
		
		return fLength;
	}
	
	public Double getCosSim(Document other){
		if(fDistanceCache.containsKey(other))
			if(fDistanceCache.get(other).containsKey(this))
				return fDistanceCache.get(other).get(this);		
		if(fDistanceCache.containsKey(this))
			if(fDistanceCache.get(this).containsKey(other))
				return fDistanceCache.get(this).get(other);

		double numerator = 0.0, denominator1 = 1.0, denominator2 = 1.0;

		for(String word : fTfIdf.keySet()){
			if(other.fTfIdf.containsKey(word)){
				Double thisVal = fTfIdf.get(word);
				Double otherVal = other.fTfIdf.get(word);
				numerator += thisVal * otherVal;
				denominator1 +=  thisVal * thisVal;
				denominator2 +=  otherVal * otherVal;
			}									
		}
		if(!fDistanceCache.containsKey(this))
			fDistanceCache.put(this, new HashMap<Document, Double>());
		
		Double res = numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
		fDistanceCache.get(this).put(other, res);
					

		return res;
	}

}
