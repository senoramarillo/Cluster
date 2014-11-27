package nbi;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;

public class Main {

	static TreeSet<String> fStopWords = new TreeSet<String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// Read stopwords
		Path path = new File("stop.txt").toPath();
		
		List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
		for(String line : lines){
			String[] fields = line.split("\\s");
			PorterWord stopWord = new PorterWord(fields[0].toLowerCase());
			fStopWords.add(stopWord.toString());
		}
		
		// Read articles
		path = new File("korpus/").toPath();
		DirectoryStream<Path> stream = Files.newDirectoryStream(path);
		
		path = new File("korpusStemmed/").toPath();
		Files.createDirectory(path);
				
		
		for(Path entry : stream){
			lines = Files.readAllLines(entry, StandardCharsets.UTF_8);
			
			path = Files.createFile(new File("korpusStemmed/" + entry.getFileName()).toPath());
			PrintWriter writer = new PrintWriter(Files.newOutputStream(path)); 
			
			for(String line : lines){
				for(String word : line.split("\\b")){
					PorterWord stem = new PorterWord(word);					
					if(fStopWords.contains(stem.toString().toLowerCase()))
						continue;
					
					writer.write(stem.toString());
				}
				writer.write("\n");				
			}
			
			writer.close();
		}		
	}

}
