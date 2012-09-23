package com.missingstatement.yeahapp.networking;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 9/23/12
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SearchHandler {

    public void handleSearchResponse(ArrayList<HashMap<String, ArrayList<String>>> results);
}
