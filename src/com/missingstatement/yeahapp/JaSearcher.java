package com.missingstatement.yeahapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JaSearcher 
{
	static private String URL_STRING = "http://ja.is/m2/?q=";
	
	public static ArrayList<HashMap<String, ArrayList<String>>> search(String queryString)
    {
    	ArrayList<HashMap<String, ArrayList<String>>> results = new ArrayList<HashMap<String, ArrayList<String>>>();
    	try{
	    	String urlString = URL_STRING + queryString;
	    	Document doc = Jsoup.connect(urlString).get();
	    	Elements infos = doc.select("div.inf");	
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
	    			//Log.d("MyApp",name.text());
	    		}
	    		
	    		Elements adrs = info.select("span.adr");
	    		for(Element adr : adrs)
	    		{
	    			address.add(adr.text());
	    			//Log.d("MyApp",adr.text());
	    		}
	    		
	    		Elements pNrs = info.nextElementSibling().select("a");
	    		for(Element phoneNr : pNrs)
	    		{
	    			phoneNrs.add(phoneNr.text());
	    			//Log.d("MyApp",phoneNr.text());
	    		}
	    		res.put("Names", names);
	    		res.put("Address", address);
	    		res.put("PhoneNrs", phoneNrs);
	    		results.add(res);
	    	}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return results;
    }
}
