package org.lc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
public class Indexer {
	 public static void main(String[] args) {
	  String usage = "java IndexFiles  [-index C:/index] [-docs C:/input] \n\n"
	   + "This indexes the documents in DOCS_PATH, creating a Lucene index in"
	   + "INDEX_PATH that can be searched with SearchFiles";
	  String indexPath = "C:/ind";
	  String docsPath = "C:/input";
	  for (int i = 0; i < args.length; i++) {
	   if ("-index".equals(args[i])) {
	    indexPath = args[i + 1];
	    i++;
	   } else if ("-docs".equals(args[i])) {
	    docsPath = args[i + 1];
	    i++;
	   }
	  }
	  if (docsPath == null) {
	   System.err.println("Usage: " + usage);
	   System.exit(1);
	  }
	  final File docDir = new File(docsPath);
	  if (!docDir.exists() || !docDir.canRead()) {
	   System.out.println("Document directory "
	   + docDir.getAbsolutePath()
	   + "does not exist or is not readable, please check the path");
	   System.exit(1);
	  }
	  Date start = new Date();
	  try {
	   System.out.println("Indexing to directory '" + indexPath + "'...");
	   Directory dir = FSDirectory.open(new File(indexPath));

	   Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
	   IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31,analyzer);
	   iwc.setOpenMode(OpenMode.CREATE);
	   IndexWriter writer = new IndexWriter(dir, iwc);
	   findFilesAndIndex(writer, docDir);

	   writer.close();
	   Date end = new Date();
	   System.out.println(end.getTime() - start.getTime()+ " total milliseconds");
	  } catch (IOException e) {
	   System.out.println(" caught a " + e.getClass()+ "\n with message: " + e.getMessage());
	  }
	 }

	 static void findFilesAndIndex(IndexWriter writer, File file) throws IOException {
	  FileInputStream fis = null;
	  try{
	  if (file.canRead()) {
	   if (file.isDirectory()) {
	   String[] files = file.list();
	   if (files != null) {
		   for (int i = 0; i < files.length; i++) {
			    findFilesAndIndex(writer, new File(file, files[i]));
			    }
			   }
			   } else {
			    fis = new FileInputStream(file);
			    indexDoc(writer, file,fis);
			   }
			  }
			  }catch (IOException e) {
			   System.out.println(" caught a " + e.getClass()+ "\n with message: " + e.getMessage());
			  }finally {
			   if(fis != null){
			    fis.close();
			   }
			  }
			 }

			 static void indexDoc(IndexWriter writer, File file,FileInputStream fis) throws IOException {
			  Document doc = new Document();
			  Field pathField = new Field("path", file.getPath(),Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
			  pathField.setOmitTermFreqAndPositions(true);
			  doc.add(pathField);

			  Field titleField = new Field("title", file.getName(),Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
			  pathField.setOmitTermFreqAndPositions(true);
			  doc.add(titleField);
			  NumericField modifiedField = new NumericField("modified");
			  modifiedField.setLongValue(file.lastModified());
			  doc.add(modifiedField);

			  doc.add(new Field("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

			  System.out.println("adding " + file);
			  writer.addDocument(doc);
			 }
			}
	   
