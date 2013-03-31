package org.lc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

public static void main(String[] args){
try{
String usage = "Usage: SearchFiles [-index dir] [-field f] [-query string] \n\n";
String index = "C:/ind";
String field = "contents";
String queryString = "malaria";

int hitsPerPage = 100;
for (int i = 0; i < args.length; i++) {
if ("-index".equals(args[i])) {
index = args[i + 1];
i++;
} else if ("-field".equals(args[i])) {
field = args[i + 1];
i++;
} else if ("-query".equals(args[i])) {
queryString = args[i + 1];
i++;
}
}
new Searcher().searchFiles(index,field,queryString,hitsPerPage);
}catch (Exception e) {
e.printStackTrace();
}
}

public ArrayList<String> searchFiles(String index,String field,
String queryString,int hitsPerPage) throws Exception {

ArrayList<String> returnStringList = new ArrayList<String>();

IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(index)));
Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);
Query query = parser.parse(queryString);
int numberOfPages = 5;
TopDocs results = searcher.search(query, numberOfPages * hitsPerPage);

ScoreDoc[] hits = results.scoreDocs;
int numTotalHits = results.totalHits;
System.out.println(numTotalHits + " total matching documents");
returnStringList.add(numTotalHits + " total matching documents");

int start = 0;
int end = Math.min(numTotalHits, hitsPerPage);
for (int i = start; i < end; i++) {
Document doc = searcher.doc(hits[i].doc);
String path = doc.get("path");
String outFile = "c:/ind/JavaMerged.txt"; 
String lines;
if (path != null) {
//System.out.println((i + 1) + ". " + path);
	BufferedReader inFile=new BufferedReader(new FileReader(new File(path)));
	BufferedWriter outPut=new BufferedWriter(new FileWriter(outFile, true)); 
	while((lines=inFile.readLine()) != null) {
		outPut.write(lines);
		outPut.newLine();
		}
	outPut.flush();
	outPut.close();
	inFile.close(); 
returnStringList.add((i + 1) + ". " + path);
String title = doc.get("title");
if (title != null) {
//System.out.println("   Title: " + doc.get("title"));
returnStringList.add("   Title: " + doc.get("title"));

}
}
}
System.out.println("merged contents in C:/ind/JavaMerged.txt");
searcher.close();
return returnStringList;
}
}