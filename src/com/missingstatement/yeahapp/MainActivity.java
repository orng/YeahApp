package com.missingstatement.yeahapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {
	private EditText queryText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queryText = (EditText) findViewById(R.id.edtQuery);
        
        final Button button =(Button) findViewById(R.id.btnSearch);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSubmit(button);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void onSubmit(View view)
    {
    	Toast.makeText(this, "Lol", Toast.LENGTH_LONG).show();
    	switch(view.getId())
    	{
    		case R.id.btnSearch:
    			if(queryText.getText().length() == 0)
    			{
    				Toast.makeText(this, "Vinsamlegast sladu inn leitarstreng.",
    						Toast.LENGTH_LONG).show();	
    				return;
    			}
    			
    			String queryString = queryText.getText().toString();
    			JaSearcher searcher = new JaSearcher();
    			ArrayList<HashMap<String, ArrayList<String>>> results = searcher.search(queryString);
    			for(int i=0; i < results.size(); i++ )
    			{
    				HashMap<String, ArrayList<String>> item = results.get(i);
    				Log.d("Result", item.toString());
    			}
    	}
    }
}
