package com.missingstatement.yeahapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class JaSearcher 
{
	private final String BASE_URL = "http://ja.is";
	private final String URL_STRING = BASE_URL + "/m2/hvitar/?q=";
	private String nextUrl;
	
	public JaSearcher()
	{
		nextUrl = null;
	}
	
	public boolean hasNext()
	{
		return nextUrl != null;
	}
	
	public ArrayList<HashMap<String, ArrayList<String>>> getNext()
	{
		return fetchResult(nextUrl);
	}
	
	public ArrayList<HashMap<String, ArrayList<String>>> search(String queryString)
    {
		String url = URL_STRING + queryString.replaceAll(" ", "+");
		return fetchResult(url);
    }

	private ArrayList<HashMap<String, ArrayList<String>>> fetchResult(String url)
	{
		ArrayList<HashMap<String, ArrayList<String>>> results = new ArrayList<HashMap<String, ArrayList<String>>>();
    	try{
	    	Document doc = Jsoup.connect(url).get();
	    	Elements infos = doc.select("div.inf");	
	    	Elements pagingLinks = doc.select("div.paging").select("a");
	    	for(Element info : infos)
	    	{
	    		HashMap<String, ArrayList<String>> res = new HashMap<String, ArrayList<String>>();
	    		ArrayList<String> names = new ArrayList<String>();
	    		ArrayList<String> address = new ArrayList<String>();
	    		ArrayList<String> phoneNrs = new ArrayList<String>();
	    		
	    		Elements nm = info.select("span.nm");
	    		for(Element name : nm)
	    		{
	    			names.add(name.text());
	    		}
	    		
	    		Elements adrs = info.select("span.adr");
	    		for(Element adr : adrs)
	    		{
	    			address.add(adr.text());
	    		}
	    		
	    		Elements pNrs = info.nextElementSibling().select("a");
	    		for(Element phoneNr : pNrs)
	    		{
	    			phoneNrs.add(phoneNr.text());
	    		}
	    		res.put("Names", names);
	    		res.put("Address", address);
	    		res.put("PhoneNrs", phoneNrs);
	    		results.add(res);
	    	}
	    	if(!pagingLinks.isEmpty())
	    	{
	    		Element lastUrl = pagingLinks.last();
	    		String linkText = lastUrl.text().replaceAll("\\s","").toLowerCase();
	    		if(linkText.equals("n�sta")) //TODO fix encoding
	    		{
	    			nextUrl = BASE_URL + lastUrl.attr("href").replace(" ", "+");
	    		}
	    		else
	    		{
	    			nextUrl = null;
	    		}
	    	}
	    	else
	    	{
	    		nextUrl = null;
	    	}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return results;
	}
}
