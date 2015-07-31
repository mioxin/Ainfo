package com.gmail.mrmioxin.ainfo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends ActionBarActivity {

    private static final String MY_LOG = "My_log.MainActivity";
    // имена атрибутов для Map
    final String ATTR_DATE = "date";
    final String ATTR_TITLE = "title";
    final String ATTR_DESCR = "descr";
    final String ATTR_IMAGE = "image";
    //CSS классы в HTML
    final String CLASS_DATE = "div16";
    final String CLASS_TITLE = "div11";
    final String CLASS_DESCR = "div18";
    final String CLASS_IMG = "corner1";
    final String CLASS_NEXT = "td26";

    String strUrlNext= "";
    ArrayList<Map<String, Object>> data_list;
    ListView listview;
    SimpleAdapter myAdapter;
    View footer;
    //Диалог ожидания
    private ProgressDialog pd;
    ProgressBar progress_bar;
    TextView tvFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Находим кнопку
        Button button = (Button)findViewById(R.id.parse);
        //Находим ListView
        listview = (ListView) findViewById(R.id.listViewData);
        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { ATTR_DATE, ATTR_TITLE, ATTR_DESCR, ATTR_IMAGE};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.tvDate, R.id.tvTitle, R.id.tvDescr, R.id.imageView };
        data_list = new ArrayList<Map<String, Object>>();

        //Регистрируем onClick слушателя
        button.setOnClickListener(myListener);
        // создание Footer
        footer = getLayoutInflater().inflate(R.layout.footer, null);
        progress_bar = (ProgressBar) footer.findViewById(R.id.progressBar);
        tvFooter = (TextView) footer.findViewById(R.id.tv_Footer);
        //add footer
        listview.addFooterView(footer);
        myAdapter = new SimpleAdapter(MainActivity.this, data_list, R.layout.item, from, to);
        listview.setAdapter(myAdapter);

        new ParseSite().execute(getString(R.string.url_site));
    }

   //Слушатель OnClickListener для нашей кнопки
    private View.OnClickListener myListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Показываем диалог ожидания
            //pd = ProgressDialog.show(MainActivity.this, "Working...", "request to server", true, false);
             //Запускаем парсинг
            new ParseSite().execute(getString(R.string.url_site));
        }
    };

    public void onFooterClick(View view) {
        new ParseSite().execute(strUrlNext);
    }

    private class ParseSite extends AsyncTask<String, Void, ArrayList<Map<String, Object>>> {
        String surlNext;
        protected void onPreExecute() {
            super.onPreExecute();
            tvFooter.setVisibility(View.GONE);
            progress_bar.setVisibility(View.VISIBLE);
        }
        //Фоновая операция
        protected ArrayList<Map<String, Object>> doInBackground(String... arg) {
            //List<String> output = new ArrayList<String>();
            ArrayList<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
            Map<String, Object> m;
            try {
                URL url = new URL(arg[0]);
                HtmlHelper hh = new HtmlHelper(url);
                List<TagNode> elem = hh.getParentsByClass("//tr[td[div[@class='" + CLASS_IMG + "']]]");
                List<TagNode> urlNext = hh.getParentsByClass("//td[@class='" + CLASS_NEXT + "']");
                Log.d(MY_LOG, "urlNext.length: " + urlNext.size());
                Log.d(MY_LOG, "Url next name: " + urlNext.get(0).findElementByAttValue("class", "ln7", true, false).getAttributeByName("href"));

                for (Iterator<TagNode> iterator = elem.iterator(); iterator.hasNext(); ) {
                    m = new HashMap<String, Object>();
                    TagNode element = (TagNode) iterator.next();
                    m.put(ATTR_TITLE, element.findElementByAttValue("class", CLASS_TITLE, true, false).getText());
                    m.put(ATTR_DESCR, getContent(element.findElementByAttValue("class", CLASS_DESCR, true, false)).trim());//.getText());
                    String s_date = getContent(element.findElementByAttValue("class", CLASS_DATE, true, false)).trim();

                    m.put(ATTR_DATE, s_date.substring(0, (s_date.indexOf(" 20") > 0) ? s_date.indexOf(" 20") + 5 : 10));
                    m.put(ATTR_IMAGE, 0); //
                    //Log.d(MY_LOG, "element:" + element.findElementByAttValue("class", CLASS_IMG, true, false).findElementByAttValue("src",".jpg",true,false).getText());
                    Log.d(MY_LOG, "image:" + new URL(url, element.findElementByAttValue("class", CLASS_IMG, true, false).findElementByName("img", false).getAttributeByName("src")).toString());
                    output.add(m);
                    //Log.d(MY_LOG, divElement.getText().toString());
                }

                for (Iterator<TagNode> iterator = urlNext.iterator(); iterator.hasNext(); ) {
                    TagNode element = (TagNode) iterator.next();
                    Log.d(MY_LOG, "1.Next: " + element.findElementByAttValue("class", "ln7", true, false).getText().toString().trim() +
                            " *" + element.findElementByAttValue("class", "ln7", true, false).getText().toString().contains("Туда")+"* ");

                    if (element.findElementByAttValue("class", "ln7", true, false).getText().toString().contains("Туда")) {
                        surlNext = (new URL(url, element.findElementByAttValue("class", "ln7", true, false).getAttributeByName("href"))).toString();
                        Log.d(MY_LOG,"2.Next. Link: " + surlNext);
                    }
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
 //добавляем данные в массив для адаптера
            if (!data_list.addAll(output)) {
                Toast.makeText(MainActivity.this, "Новые данные не добавились.",Toast.LENGTH_SHORT).show();
            };
            //Убираем ProgressBar  в футере
            progress_bar.setVisibility(View.GONE);
            tvFooter.setVisibility(View.VISIBLE);

            //обновляем данные в адаптере
            myAdapter.notifyDataSetChanged();
            //ссылка для следующей страницы
            strUrlNext = surlNext;
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
