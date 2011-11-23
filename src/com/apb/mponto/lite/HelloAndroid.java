package com.apb.mponto.lite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apb.mponto.lite.R;

import com.apb.mponto.datastore.DBHelper;
import com.apb.mponto.model.Evento;


public class HelloAndroid extends Activity implements OnClickListener {
	private final SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm");
	private DBHelper dbHelper;
	private final static String EXPORT_FILE ="memoria_ponto.txt";

	public static final int ONE_ID = Menu.FIRST+7;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dbHelper = new DBHelper(this);
        ArrayList<Evento> lst = dbHelper.listSelectAll();
        
        ViewGroup tracks = (ViewGroup)findViewById(R.id.track_listing);

        for(Evento t : lst) {
        	appendTrackView(tracks, t);
        	}

        Button f =  (Button)findViewById(R.id.f1);
       f.setOnClickListener(this);

       final ViewGroup funcoes = (ViewGroup)findViewById(R.id.funcoes);
       final ViewGroup registros = (ViewGroup)findViewById(R.id.registro);

       final Queue<ViewGroup> views = new LinkedList<ViewGroup>();
       views.add(funcoes);
       views.add(registros);
       
       while(!views.isEmpty()) {
           final ViewGroup g = views.poll();
           final int children = g.getChildCount();

           for(int i = 0; i < children; i++) {
               final View v = g.getChildAt(i);

               if(v instanceof ViewGroup) {
                  // views.add((ViewGroup)v);
               } else if(v instanceof Button) {
                   v.setOnClickListener(this);
               }
           }
       }
       
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    populateMenu(menu);
    return(super.onCreateOptionsMenu(menu));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    return(applyMenuChoice(item) ||
    super.onOptionsItemSelected(item));
    }

    private void populateMenu(Menu menu) {
    	menu.add(Menu.NONE, ONE_ID, Menu.NONE, "Exportar");
    	}
    private boolean applyMenuChoice(MenuItem item) {
    	switch (item.getItemId()) {
    	case ONE_ID:
    		export2file();
    	return(true);
    	}
    	return(false);
    	
   	}




	private void export2file() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (mExternalStorageAvailable & mExternalStorageWriteable) {
		try {
			File path = Environment.getExternalStorageDirectory() ;
			File file = new File(path, EXPORT_FILE);
			OutputStream os = new FileOutputStream(file);


			OutputStreamWriter out=
			new OutputStreamWriter(os);
			ArrayList<Evento> lst = dbHelper.listSelectAll();
	        for(Evento t : lst) {
	        	out.write(t.dataHora.toLocaleString());
	        	out.write(";");
	        	out.write(t.descricao);
	        	out.write("\n");
	        	}
			out.close();
			os.close();
			}
			catch (Throwable t) {
			Toast.makeText(this, "Exception: "+t.toString(), 2000)
			.show();
			}

		}else {
			Toast.makeText(this, "Não encontrou aonde gravar o arquivo", 2000)
			.show();
		}
	}

	private void appendTrackView(ViewGroup tracks, Evento t) {
		final LayoutInflater inflater = getLayoutInflater();
		final ViewGroup line = (ViewGroup)inflater.inflate(
		R.layout.registro_item,
		tracks,
		false);

		final TextView dataTV =	(TextView)line.findViewById(R.id.data);
		String s = df.format(t.dataHora);
		dataTV.setText(s);

		final TextView textTV = (TextView)line.findViewById(R.id.descricao);
		textTV.setText(t.descricao);

		tracks.addView(line);
	}
 
	private void headTrackView(ViewGroup tracks, Evento t) {
		final LayoutInflater inflater = getLayoutInflater();
		final ViewGroup line = (ViewGroup)inflater.inflate(
		R.layout.registro_item,
		tracks,
		false);

		final TextView dataTV =	(TextView)line.findViewById(R.id.data);
		String s = df.format(t.dataHora);
		dataTV.setText(s);

		final TextView textTV = (TextView)line.findViewById(R.id.descricao);
		textTV.setText(t.descricao);

		tracks.addView(line,0);
	}
    public void onClick(final View clicked) {
        boolean operation = false;
    	final TextView tipoTV =	(TextView) findViewById(R.id.tipo);

        CharSequence atual;
		switch(clicked.getId()) {
            case R.id.f1:
            case R.id.f2:
            case R.id.f3:
                //calculator.two();
            	Button qualFuncao = (Button)findViewById(clicked.getId());
            	
        		 atual = tipoTV.getText(); 
        			//qualFuncao.getText().toString();
            	if ( atual.equals(qualFuncao.getText())) {
            		tipoTV.setText("Normal");
            	}else {
            		tipoTV.setText(qualFuncao.getText());
            	}
        		
        		
                break;
            case R.id.entrada:
            case R.id.saida:
            	ViewGroup tracks = (ViewGroup)findViewById(R.id.track_listing);
            	Button qualEvento = (Button)findViewById(clicked.getId());
            	StringBuffer sb = new StringBuffer();
            	sb.append(qualEvento.getText());
            	
            	if( ! "Normal".equals(tipoTV.getText().toString()) ) {
            		sb.append(" ").append(tipoTV.getText());
            	}
            	Evento t = new Evento(new Date(),sb.toString());
            	dbHelper.insert(t.dataHora, t.descricao);
            	headTrackView(tracks, t);
                break;
        }

    }  

}