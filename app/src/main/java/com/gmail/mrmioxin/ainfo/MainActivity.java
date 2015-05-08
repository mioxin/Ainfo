package com.gmail.mrmioxin.ainfo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String MY_LOG = "My_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Находим кнопку
        Button button = (Button)findViewById(R.id.parse);
        //Регистрируем onClick слушателя
        button.setOnClickListener(myListener);
    }

    //Диалог ожидания
    private ProgressDialog pd;
    //Слушатель OnClickListener для нашей кнопки
    private View.OnClickListener myListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Показываем диалог ожидания
            pd = ProgressDialog.show(MainActivity.this, "Working...", "request to server", true, false);
            //Запускаем парсинг
            new ParseSite().execute(String.valueOf(R.string.site_url));
        }
    };

    private class ParseSite extends AsyncTask<String, Void, List<String>> {
        //Фоновая операция
        protected List<String> doInBackground(String... arg) {
            List<String> output = new ArrayList<String>();
            try
            {
                HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
                //List<TagNode> links = hh.getLinksByClass("question-hyperlink");
                List<TagNode> links = hh.getParentsByClass("div11");
                Log.d(MY_LOG, links.toString());

                for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext();)
                {
                    TagNode divElement = (TagNode) iterator.next();
                    output.add(divElement.findElementByName("a", false).getText().toString());
                    //Log.d(MY_LOG, divElement.getText().toString());
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return output;
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(List<String> output) {
            //Убираем диалог загрузки
            pd.dismiss();
            //Находим ListView
            ListView listview = (ListView) findViewById(R.id.listViewData);
            //Загружаем в него результат работы doInBackground
            listview.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1 , output));
        }
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
}
