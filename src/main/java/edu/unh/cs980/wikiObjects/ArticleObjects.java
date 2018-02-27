package edu.unh.cs980.wikiObjects;

import java.util.HashMap;
import java.util.List;

public class ArticleObjects {
	String articleName;
	String articleURL;
	String articleId;
	String categoryName;
	String categoryId;
	HashMap <String,String> sectionContentList;
	
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getArticleName() {
		return articleName;
	}
	public String getCategoryName()
	{
		return categoryName;
	}
	public String getCategoryId()
	{
		return categoryId;
	}
	
	
	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}
	public String getArticleURL() {
		return articleURL;
	}
	public void setArticleURL(String articleURL) {
		this.articleURL = articleURL;
	}
	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}
	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}
	public HashMap<String, String> getSectionContentList() {
		return sectionContentList;
	}
	public void setSectionContentList(
			HashMap<String, String> sectionContentList) {
		this.sectionContentList = sectionContentList;
	}
	

}