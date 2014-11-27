package nbi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Cluster {
	
	List<Document> fDocs = new ArrayList<Document>();
	Document fCentroid = null;
	HashMap<Document, Double> fDistanceCache = new HashMap<Document, Double>();
			
	public Cluster(Document document) {
		fDocs.add(document);
	}

	public void recomputeCentroid() {
		HashMap<String, Double> centroid = new HashMap<String, Double>();
		for(Document doc : fDocs)
			for(Entry<String, Double> entry : doc.fTfIdf.entrySet())
				centroid.put(entry.getKey(), centroid.containsKey(entry.getKey()) ?
						centroid.get(entry.getKey()) + entry.getValue() : entry.getValue());
		
		for(Entry<String, Double> entry : centroid.entrySet())
			centroid.put(entry.getKey(), entry.getValue()/centroid.size());
		
		fCentroid = new Document(centroid);
	}
	
	public void clearDocs(){
		Document doc = fDocs.get(0);
		fDocs.clear();
		fDocs.add(doc);
	}

	public Document getCentroid() {
		return fCentroid;
	}

	public void add(Document doc) {
		fDocs.add(doc);
		fDistanceCache.clear();
	}
	
	public Double getDistance(Document otherDoc){
			Double numerator = 0.0;
			for(Document doc : fDocs)
				numerator += 1.0-doc.getCosSim(otherDoc);
			
			Double res = numerator/fDocs.size();
		return res;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();		
		boolean first = true;
		builder.append("{\n    documents: [");
		for(Document doc : fDocs){
			if(!first)				
				builder.append(",");
			first = false;
			builder.append("\""+doc.getPath().toString()+"\"");			
		}
		builder.append("],\n");
		List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String,Double>>();
		list.addAll(fCentroid.fTfIdf.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {				
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		builder.append("    terms: [");
		for(int i=0; i<10; ++i){
			if(i>0)
				builder.append(",");
			builder.append("\""+list.get(i).getKey()+"\"");
		}
		builder.append("]\n},");
		return builder.toString();
	}
}
