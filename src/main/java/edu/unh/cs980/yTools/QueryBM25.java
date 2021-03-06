package edu.unh.cs980.yTools;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.CborFileTypeException;
import edu.unh.cs.treccar_v2.read_data.CborRuntimeException;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class QueryBM25 {
	
	private static void usage() {
        System.out.println("Command line parameters: action OutlineCBOR LuceneINDEX\n" +
                "action is one of output-sections | paragraphs-run-sections | paragraphs-run-pages | pages-run-pages");
        System.exit(-1);
    }
	
    static class MyQueryBuilder {

        private final StandardAnalyzer analyzer;
        private List<String> tokens;

        public MyQueryBuilder(StandardAnalyzer standardAnalyzer){
            analyzer = standardAnalyzer;
            tokens = new ArrayList<>(128);
        }

        public BooleanQuery toQuery(String queryStr) throws IOException {

            TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(queryStr));
            tokenStream.reset();
            tokens.clear();
            while (tokenStream.incrementToken()) {
                final String token = tokenStream.getAttribute(CharTermAttribute.class).toString();
                tokens.add(token);
            }
            tokenStream.end();
            tokenStream.close();
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            for (String token : tokens) {
                booleanQuery.add(new TermQuery(new Term("text", token)), BooleanClause.Occur.SHOULD);
            }
            return booleanQuery.build();
        }
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("file.encoding", "UTF-8");

        if (args.length < 3)
            usage();

        String mode = args[0];
        String indexPath = args[2];


        if (mode.equals("output-sections")) {
            IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene.vectors");

            searcher.setSimilarity(new BM25Similarity());
            final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());

            final String pagesFile = args[1];
            final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));
            for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
                System.out.println("\n\nPage: "+page.getPageId());
                for (List<Data.Section> sectionPath : page.flatSectionPaths()) {
                    System.out.println();
                    System.out.println(Data.sectionPathId(page.getPageId(), sectionPath) + "   \t " + Data.sectionPathHeadings(sectionPath));

                    String queryStr = buildSectionQueryStr(page, sectionPath);

                    // get top 10 documents
                    TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 10);
                    ScoreDoc[] scoreDoc = tops.scoreDocs;
                    System.out.println("Found "+scoreDoc.length+" results.");
                    for (ScoreDoc score : scoreDoc) {
                        final Document doc = searcher.doc(score.doc); // to access stored content
                        // print score and internal docid
                        System.out.println(doc.getField("paragraphid").stringValue()+ " (" + score.doc + "):  SCORE " + score.score);
                        // access and print content
                        System.out.println("  " +doc.getField("text").stringValue());
                    }

                }
                System.out.println();
            }
        } else  if (mode.equals("paragraphs-run-sections")) {
        	
        		final String output = args[3] + "/runfile_para_sections";
            
            File runfile = new File(output);
    			runfile.createNewFile();
    			FileWriter writer = new FileWriter(runfile);
    			
    			//List<String> uniqueLines;
    			
            IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene.vectors");

            searcher.setSimilarity(new BM25Similarity());
            final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());

            final String pagesFile = args[1];
            final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));
            System.out.println("Start paragraphs-run-sections");
            for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
                for (List<Data.Section> sectionPath : page.flatSectionPaths()) {
                	 	
                    final String queryId = Data.sectionPathId(page.getPageId(), sectionPath);
                    //uniqueLines= new ArrayList<String>();

                    String queryStr = buildSectionQueryStr(page, sectionPath);

                    TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 100);
                    ScoreDoc[] scoreDoc = tops.scoreDocs;
                    for (int i = 0; i < scoreDoc.length; i++) {
                        ScoreDoc score = scoreDoc[i];
                        final Document doc = searcher.doc(score.doc); // to access stored content
                        // print score and internal docid
                        final String paragraphid = doc.getField("paragraphid").stringValue();
                        final float searchScore = score.score;
                        final int searchRank = i+1;
                        System.out.println(".");
                        writer.write(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25\n");
                        //System.out.println(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25");
                        /*
                        if(!(uniqueLines.contains(paragraphid)))
                        {
                            writer.write(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25\n");

                        }
                        
                        uniqueLines.add(paragraphid);
                        */
                    }

                }
            }
            
            writer.flush();//why flush?
			writer.close();        
			System.out.println("Removing duplicated terms");
			stripDuplicatesFromFile(output);
			System.out.println("paragraphs-run-sections DONE");
			
        }  else  if (mode.equals("paragraphs-run-pages")) {
        	
        		
        	
        		final String output = args[3] + "/runfile_para_pages";
            
            File runfile = new File(output);
    			runfile.createNewFile();
    			FileWriter writer = new FileWriter(runfile);
    		
            IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene.vectors");

            searcher.setSimilarity(new BM25Similarity());
            final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());

            final String pagesFile = args[1];
            final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));
            for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
                final String queryId = page.getPageId();

                String queryStr = buildSectionQueryStr(page, Collections.<Data.Section>emptyList());

                TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 100);
                ScoreDoc[] scoreDoc = tops.scoreDocs;
                for (int i = 0; i < scoreDoc.length; i++) {
                    ScoreDoc score = scoreDoc[i];
                    final Document doc = searcher.doc(score.doc); // to access stored content
                    // print score and internal docid
                    final String paragraphid = doc.getField("paragraphid").stringValue();
                    final float searchScore = score.score;
                    final int searchRank = i+1;
                    System.out.println(".");
                    //System.out.println(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25");
                    writer.write(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25\n");
                }

            }
            
            writer.flush();//why flush?
    			writer.close();        
            System.out.println("paragraphs-run-pages DONE");
            
        }  else  if (mode.equals("pages-run-pages")) {
            IndexSearcher searcher = setupIndexSearcher(indexPath, "pages.lucene.vectors");

            searcher.setSimilarity(new BM25Similarity());
            final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());

            final String pagesFile = args[1];
            final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));
            for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
                final String queryId = page.getPageId();

                String queryStr = buildSectionQueryStr(page, Collections.<Data.Section>emptyList());

                TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 100);
                ScoreDoc[] scoreDoc = tops.scoreDocs;
                for (int i = 0; i < scoreDoc.length; i++) {
                    ScoreDoc score = scoreDoc[i];
                    final Document doc = searcher.doc(score.doc); // to access stored content
                    // print score and internal docid
                    final String paragraphid = doc.getField("pageid").stringValue();
                    final float searchScore = score.score;
                    final int searchRank = i+1;

                    System.out.println(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25");
                }

            }
        }
    }
    
	// Remove Duplicates from the runfile for sections
	public static void stripDuplicatesFromFile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		Set<String> lines = new HashSet<String>(); // maybe should be bigger
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		reader.close();
		System.out.println("Removing Duplicates");
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		for (String unique : lines) {
			writer.write(unique);
			writer.newLine();
		}
		writer.close();
		System.out.println(" ' Duplicates Removed ' Written to the file");
	}

    @NotNull
    private static IndexSearcher setupIndexSearcher(String indexPath, String typeIndex) throws IOException {
        Path path = FileSystems.getDefault().getPath(indexPath, typeIndex);
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
        return new IndexSearcher(reader);
    }

    @NotNull
    private static String buildSectionQueryStr(Data.Page page, List<Data.Section> sectionPath) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(page.getPageName());
        for (Data.Section section: sectionPath) {
            queryStr.append(" ").append(section.getHeading());
        }
//        System.out.println("queryStr = " + queryStr);
        return queryStr.toString();
    }

    private static Iterable<Document> toIterable(final Iterator<Document> iter) throws CborRuntimeException, CborFileTypeException {
        return new Iterable<Document>() {
            @Override
            @NotNull
            public Iterator<Document> iterator() {
                return iter;
            }
        };
    }

}
