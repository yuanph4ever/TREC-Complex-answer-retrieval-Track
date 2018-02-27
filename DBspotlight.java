package com.unh.DBpedia;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;

/**
 * Used to query local spotlight server with a paragraph's content and retrieve a list of entities.
 */
public class DBspotlight {
    private final String url = "http://localhost:2222/rest/annotate";
    private final String data;

    DBspotlight(String content) {
        data = content;
    }

    // Queries DBpedia and returns a list of entities
    ArrayList<String> run() {
        ArrayList<String> entities = new ArrayList();

        try {
            // Connect to database, retrieve entity-linked urls
            Document doc = Jsoup.connect(url)
                    .data("text", data)
                    .post();
            Elements links = doc.select("a[href]");

            // Parse urls, returning only the last word of the url (after the last /)
            for (Element e : links) {
                String title = e.attr("title");
                title = title.substring(title.lastIndexOf("/") + 1);
                entities.add(title);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }

    public static void main(String[] args) throws IOException {
    	DBspotlight entityLinker = new DBspotlight("Chocolate comes under food category.Barack Obama was a nice president. Chicken is consumed in United States");
        ArrayList<String> entities = entityLinker.run();
        for (String entity : entities) {
            System.out.println(entity);
        }
    }
}