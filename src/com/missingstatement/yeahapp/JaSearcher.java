package com.missingstatement.yeahapp;

import com.missingstatement.yeahapp.utils.Keys;
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
	private String mNextUrl;

	public JaSearcher()
	{
		mNextUrl = null;
	}
	
	public boolean hasNext()
	{
		return mNextUrl != null;
	}
	
	public ArrayList<HashMap<String, ArrayList<String>>> getNext()
	{
		return fetchResult(mNextUrl);
	}

    public String getNextUrl()
    {
        return mNextUrl;
    }

	public ArrayList<HashMap<String, ArrayList<String>>> search(String queryString)
    {
        String url = getQueryUrl(queryString);

		return fetchResult(url);
    }

    private String getQueryUrl(String queryString)
    {
        return URL_STRING + queryString.replaceAll(" ", "+");
    }

	private ArrayList<HashMap<String, ArrayList<String>>> fetchResult(String url)
	{
		ArrayList<HashMap<String, ArrayList<String>>> results = new ArrayList<HashMap<String, ArrayList<String>>>();
    	try
        {
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
	    		res.put(Keys.KEY_NAMES, names);
	    		res.put(Keys.KEY_ADDRESSES, address);
	    		res.put(Keys.KEY_PHONE_NUMBERS, phoneNrs);
	    		results.add(res);
	    	}
	    	if(!pagingLinks.isEmpty())
	    	{
	    		Element lastUrl = pagingLinks.last();
	    		String linkText = lastUrl.text().replaceAll("\\s","").toLowerCase();
	    		if(linkText.equals(Keys.KEY_MORE_RESULTS))
	    		{
	    			mNextUrl = BASE_URL + lastUrl.attr("href").replace(" ", "+");
	    		}
	    		else
	    		{
	    			mNextUrl = null;
	    		}
	    	}
	    	else
	    	{
	    		mNextUrl = null;
	    	}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
        finally
        {
            return results;
        }

	}
}
