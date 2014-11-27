package nbi;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Kmeans {
	
	static final Integer maxDocs = 2500;
	static final Integer maxIterations = 40;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException{
		// Read articles
		Path path = new File("korpusStemmed/").toPath();
		DirectoryStream<Path> stream = Files.newDirectoryStream(path);
		
		List<String> lines;
		List<Document> docs = new ArrayList<Document>();
		
		HashMap<String, Integer> idf = new HashMap<String, Integer>();
		int i=0;

		for(Path entry : stream){
			if(++i > maxDocs)
			break;
			
			lines = Files.readAllLines(entry, StandardCharsets.UTF_8);
			Document doc = new Document(entry);
			docs.add(doc);

			for(String line : lines){				
				for(String word : line.split("\\W")){
					if(word.matches("\\w+"))
						doc.addTerm(word);
				}
			}
			
			for(String word : doc.getAllTerms())
				if(idf.containsKey(word))
					idf.put(word, idf.get(word)+1);
				else
					idf.put(word, 1);
		}
		
		for(Document doc : docs){
			doc.setInverseDocumentFrequencies(idf);
			doc.calculateTfIdf();
		}
		idf = null;
		
		if(args.length > 0 && args[0].equalsIgnoreCase("findk"))
		for(int j=1; j<=50; ++j){
			System.out.println("**** K: " + j + " ****");
			kMeans(new ArrayList<Document>(docs), j, false);
		}
		else{
			kMeans(docs, 44, true);			
		}
	}

	private static void kMeans(List<Document> docs, int k, boolean printClusters) {
		List<Cluster> res = new ArrayList<Cluster>();		
		
		// Declare k seed points
		for(int i=0; i<k; ++i){
			int rand = (int) Math.floor(Math.random() * docs.size());
			res.add(new Cluster(docs.get(rand)));
			docs.remove(rand);
		}
		
		Double newQuality = 0.002;
		Double oldQuality = 0.0;
		int iters = maxIterations;
		
		while(Math.abs(newQuality - oldQuality) > 0.001 && iters-- > 0){
			
			oldQuality = newQuality;

			for(Cluster c : res){
				c.recomputeCentroid();
				c.clearDocs();
			}
						


			// Add each document to the cluster with the nearest centroid
			for(Document doc : docs){
				Cluster nearest = null;
				Double bestSim = -1.0;

				for(Cluster c : res){
					Double curSim = doc.getCosSim(c.getCentroid());
					if(bestSim < curSim){
						nearest = c;
						bestSim = curSim; 
					}				
				}
				nearest.add(doc);						
			}

			// Compute the quality of the clustering
			Double Ssum = 0.0;
			for(Cluster c : res){
				Ssum += getSilhouette(c, res);		
				//System.out.println("Progress: " + (++progress/(double)k)*100);
			}

			newQuality = (Ssum/res.size());		
		}
		
		System.out.println("Quality: " + newQuality);
		
		if(printClusters)
			for(Cluster c : res){
				System.out.println(c.toString());
				System.out.println("\n");
			}
	}

	private static Double getSilhouette(Cluster c, List<Cluster> res) {
		Double numerator = 0.0;
		for(Document doc : c.fDocs)
			numerator += getSilhouette(c, doc, res);
				
		return c.fDocs.size() == 0 ? 0.0 : numerator/c.fDocs.size();
	}

	private static Double getSilhouette(Cluster c, Document doc,
			List<Cluster> clusters) {
		Double dist = c.getDistance(doc);
		
		if(dist == 0.0) return 0.0;
		
		Double minDist = Double.MAX_VALUE;
		Cluster minC = c;
		
		for(Cluster other : clusters){
			if(other == c) continue;
			
			Double curDist = 1.0-other.getCentroid().getCosSim(c.getCentroid());
			if(minDist > curDist){
				minDist = curDist; 
				minC = other;
			}				
		}		
		
		Double otherDist = minC.getDistance(doc);
		Double res = (otherDist - dist)/Math.max(otherDist,  dist);
		
		return res;
	}
}
