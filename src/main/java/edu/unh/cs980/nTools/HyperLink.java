package edu.unh.cs980.nTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.Data.Page.SectionPathParagraphs;
import edu.unh.cs.treccar_v2.Data.ParaBody;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class HyperLink {

	public HyperLink(String cborFile) throws FileNotFoundException {
		// TODO Auto-generated constructor stub
		int count = 0;
		final FileInputStream fileInputStream = new FileInputStream(new File(cborFile));
		Map<String, List<String>> hyperlinkDictionary = new HashMap<String, List<String>>();
		for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream)) {
			for (SectionPathParagraphs sectionPathParagraphs : page.flatSectionPathsParagraphs()) {
				for (ParaBody paraBody : sectionPathParagraphs.getParagraph().getBodies()) {
					if (paraBody instanceof Data.ParaLink) {
						String entityName = ((Data.ParaLink) paraBody).getPage();
						String anchorText = ((Data.ParaLink) paraBody).getAnchorText();
						// System.out.println(entityName + " - - " +anchorText);
						count++;
						if (hyperlinkDictionary.containsKey(entityName)) {
							List<String> entityAnchorList = hyperlinkDictionary.get(entityName);
							if (entityAnchorList.contains(anchorText) == false) {
								entityAnchorList.add(anchorText);
								hyperlinkDictionary.replace(entityName, entityAnchorList);
								//
							}
						} else {
							List<String> newEntityAnchorList = new ArrayList<String>();
							newEntityAnchorList.add(anchorText);
							hyperlinkDictionary.put(entityName, newEntityAnchorList);
						}
					}
				}
			}
		}

		for (Entry<String, List<String>> entry : hyperlinkDictionary.entrySet()) {
			System.out.println(count);
		}

	}

}
