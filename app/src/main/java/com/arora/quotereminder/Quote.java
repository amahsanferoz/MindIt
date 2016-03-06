package com.arora.quotereminder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Quote extends AppCompatActivity {

    private Button closeQuote;
    private TextView showQuote;
    String quote = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        showQuote = (TextView) findViewById(R.id.showQuote);
        closeQuote = (Button) findViewById(R.id.closeQuote);

        quote = this.getIntent().getStringExtra("quote");

        showQuote.setText(quote);
        Log.d("Quote", "Quotesssss: " + quote);

        closeQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
