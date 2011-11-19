package com.github.memoriaponto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apb.mponto.R;
import com.github.memoriaponto.datastore.DBHelper;
import com.github.memoriaponto.model.Evento;


public class HelloAndroid extends Activity implements OnClickListener {
	private final SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm");
	private DBHelper dbHelper;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dbHelper = new DBHelper(this);
        ArrayList<Evento> lst = dbHelper.listSelectAll();
        
        /*
        TextView tv = new TextView(this);
        tv.setText("Hello, Android clock");
        setContentView(tv);
        */
        ViewGroup tracks = (ViewGroup)findViewById(R.id.track_listing);
//        Evento evts[] = {
//        		new Evento(new Date(150, 5, 5),"Entrada"),
//        		new Evento(new Date(250, 5, 5),"Saida F2"),
//        		new Evento(new Date(350, 5, 5),"Entrada F2"),
//        		new Evento(new Date(450, 5, 5),"Saida")
//        };
//        for(Evento t : evts) {
        for(Evento t : lst) {
        	addTrackView(tracks, t);
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
    


	private void addTrackView(ViewGroup tracks, Evento t) {
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
               	addTrackView(tracks, t);
                break;
        }

//        if(operation) {
//            display = (TextView)switcher.getNextView();
//        }
//
//        display.setText(calculator.getCurrentDisplay());
//
//        if(operation) {
//            switcher.showNext();
//        }
    }  

}