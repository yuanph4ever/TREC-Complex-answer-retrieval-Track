package edu.unh.cs980.nTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class HeadingContentExtractor {

	public HeadingContentExtractor() throws FileNotFoundException {

	}

	public Map<String, String> mapParaHeading(String path) throws FileNotFoundException {
		final String paragraphsFile = path;
		final FileInputStream fileInputStream2 = new FileInputStream(new File(paragraphsFile));
		final Iterator<Data.Paragraph> paragraphIterator = DeserializeData.iterParagraphs(fileInputStream2);
		Map<String, String> paragraphHeading = new HashMap<String, String>();
		System.out.println("creating map of paragraph and Headings......");
		for (int i = 1; paragraphIterator.hasNext(); i++) {

			String para = paragraphIterator.next().getTextOnly();
			String paraId = paragraphIterator.next().getParaId();
			if (i % 100000 == 0) {
				System.out.print('.');
				paragraphHeading.put(paraId, para);
			}
			

		}
		System.out.println("Added to the map");
		return paragraphHeading;

	}

	public List<String> headingsY1(String path) throws FileNotFoundException {
		List<String> headingList = new ArrayList<String>();

		final FileInputStream fileInputStream3 = new FileInputStream(new File(path));
		for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
			final String pageId = page.getPageId();
			headingList.add(pageId);
		}

		return headingList;
	}

}
