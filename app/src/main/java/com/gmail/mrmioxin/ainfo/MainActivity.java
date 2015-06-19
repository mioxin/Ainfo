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
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private static final String MY_LOG = "My_log.MainActivity";
    // имена атрибутов для Map
    final String ATTR_DATE = "date";
    final String ATTR_TITLE = "title";
    final String ATTR_DESCR = "descr";
    final String ATTR_IMAGE = "image";
    final String CLASS_DATE = "div16";
    final String CLASS_TITLE = "div11";
    final String CLASS_DESCR = "div18";
    final String CLASS = "corner1";
    ListView listview;
    View footer;
    //Диалог ожидания
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Находим кнопку
        Button button = (Button)findViewById(R.id.parse);
        //Находим ListView
        listview = (ListView) findViewById(R.id.listViewData);
        //Регистрируем onClick слушателя
        button.setOnClickListener(myListener);
        // создание Footer
        footer = getLayoutInflater().inflate(R.layout.footer, null);

    }

    //Слушатель OnClickListener для нашей кнопки
    private View.OnClickListener myListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Показываем диалог ожидания
            pd = ProgressDialog.show(MainActivity.this, "Working...", "request to server", true, false);
             //Запускаем парсинг
            new ParseSite().execute(getString(R.string.url_site));
        }
    };

    public void onFooterClick(View view) {
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        try {
            pb.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
            new ParseSite().execute(getString(R.string.url_site));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pb.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
        }
    }

    private class ParseSite extends AsyncTask<String, Void, ArrayList<Map<String, Object>>> {
        //Фоновая операция
        protected ArrayList<Map<String, Object>> doInBackground(String... arg) {
            //List<String> output = new ArrayList<String>();
            ArrayList<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
            Map<String, Object> m;
            try
            {
                HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
                //List<TagNode> links = hh.getLinksByClass("question-hyperlink");
//                List<TagNode> title = hh.getParentsByClass(CLASS_TITLE);
//                List<TagNode> date = hh.getParentsByClass(CLASS_DATE);
//                List<TagNode> descr = hh.getParentsByClass(CLASS_DESCR);
                List<TagNode> elem = hh.getParentsByClass(CLASS);
                Log.d(MY_LOG, "links: " + elem.toString());
                String site = hh.rootNode.getName();

                for (Iterator<TagNode> iterator = elem.iterator(); iterator.hasNext();)
                {
                    m = new HashMap<String, Object>();
                    TagNode element = (TagNode) iterator.next();
                    m.put(ATTR_TITLE, element.findElementByAttValue("class", CLASS_TITLE, true, false).getText());
                    m.put(ATTR_DESCR, getContent(element.findElementByAttValue("class", CLASS_DESCR, true, false)).trim());//.getText());
                    String s_date = getContent(element.findElementByAttValue("class", CLASS_DATE, true, false)).trim();

                    m.put(ATTR_DATE, s_date.substring(0, (s_date.indexOf(" 20")>0)?s_date.indexOf(" 20")+5:10));
                    m.put(ATTR_IMAGE, 0); //
                    //Log.d(MY_LOG, "element:" + element.findElementByAttValue("class", CLASS, true, false).findElementByAttValue("src",".jpg",true,false).getText());
                    Log.d(MY_LOG, "element:" + element.findElementByAttValue("class", CLASS, true, false).findElementByName("img", false).getAttributeByName("src"));
                    output.add(m);
                    //Log.d(MY_LOG, divElement.getText().toString());
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return output;
        }

        protected String getContent(TagNode node) {
            StringBuilder result = new StringBuilder();
            for (Object item : node.getAllChildren()) {
                if (item instanceof ContentNode) {
                    result.append(((ContentNode) item).getContent());
                }
            }
            return result.toString();
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(ArrayList<Map<String, Object>> output) {
            // массив имен атрибутов, из которых будут читаться данные
            String[] from = { ATTR_DATE, ATTR_TITLE, ATTR_DESCR, ATTR_IMAGE};
            // массив ID View-компонентов, в которые будут вставлять данные
            int[] to = { R.id.tvDate, R.id.tvTitle, R.id.tvDescr, R.id.imageView };

            //Убираем диалог загрузки
            pd.dismiss();
            //Загружаем в него результат работы doInBackground
            //listview.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1 , output));
            //add footer
            listview.addFooterView(footer);
            // создаем адаптер
            listview.setAdapter(new SimpleAdapter(MainActivity.this, output, R.layout.item, from, to));
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
