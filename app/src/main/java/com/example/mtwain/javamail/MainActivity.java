package com.example.mtwain.javamail;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    final String login = "login";
    final String pass = "pass";
    Button btnSend,btnRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new SendMail().execute();
            }
        });
        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("","-------------Click-------------------");
                new ReadMail().execute();
            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ReadMail extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... strings) {

            Mail m = new Mail(login, pass);
            // get inbox
            m.getInbox();
            return null;
        }
    }

    private class SendMail extends AsyncTask<String, Integer, Void> {

        protected void onProgressUpdate() {
            //called when the background task makes any progress
        }

        @Override
        protected Void doInBackground(String... strings) {
            Mail m = new Mail(login,pass);

            String[] toArr = {"mtwain94@gmail.com","paladin@armysosdev.com.ua"};

            m.setTo(toArr);
            try {

                if(m.send())
                    Log.d("MailApp", "Email was sent successfully.");
                else {
                    Log.d("MailApp", "Email was not sent.");
                }

            } catch(Exception e) {
                //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                Log.e("MailApp", "Could not send email", e);
            }
            return null;
        }

        protected void onPreExecute() {
            //called before doInBackground() is started
        }
        protected void onPostExecute() {
            //called after doInBackground() has finished
        }
    }
}
