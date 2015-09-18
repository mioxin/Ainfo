package com.gmail.mrmioxin.ainfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String MY_LOG = "My_log.MainActivity";
    //CSS классы в HTML
    final String CLASS_DATE = "div16";
    final String CLASS_TITLE = "div11";
    final String CLASS_DESCR = "div18";
    final String CLASS_IMG = "corner1";
    final String CLASS_NEXT = "td26";

    String strUrlNext= "";
//    ArrayList<Map<String, Object>> data_list;
    ArrayList<Article> data_list;
    ListView listview;
    AiListAdapter myAdapter;
    View footer;
    //Диалог ожидания
    private ProgressDialog pd;
    ProgressBar progress_bar;
    TextView tvFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Находим ListView
        listview = (ListView) findViewById(R.id.listViewData);
        data_list = new ArrayList<>();//<Map<String, Object>>();
        // создание Footer
        footer = getLayoutInflater().inflate(R.layout.footer, null);
        tvFooter = (TextView) footer.findViewById(R.id.tv_Footer);
        progress_bar = (ProgressBar) footer.findViewById(R.id.progressBar);
        myAdapter = new AiListAdapter(MainActivity.this, data_list);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(MY_LOG, "onItemClick pos = " + position + "; id = " + id);
                Article item = myAdapter.getItem(position);//data_list.get(position);
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("href", item.Href.toString());
                intent.putExtra("title", item.Title);
                startActivity(intent);
            }
        });

        //add footer
        listview.addFooterView(footer);
        listview.setAdapter(myAdapter);
        new ParseSite().execute(getString(R.string.url_site));
    }

   //Слушатель OnClickListener для нашей кнопки
//    private View.OnClickListener myListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            //Показываем диалог ожидания
//            //pd = ProgressDialog.show(MainActivity.this, "Working...", "request to server", true, false);
//             //Запускаем парсинг
//            new ParseSite().execute(getString(R.string.url_site));
//        }
//    };

    public void onFooterClick(View view) {
        new ParseSite().execute(strUrlNext);
    }

//    public void onListViewClick(View view) {
//        ListView lv = (ListView) view;
//        Article item = (Article) lv.getSelectedItem();
//        Log.d(MY_LOG,"href: " + item.Href.toString());
//    }

//    public void onItemClick(View view) {
//        //ListView lv = view;
//    }
//

    private class ParseSite extends AsyncTask<String, Void, ArrayList<Article>> {
        String surlNext;
        protected void onPreExecute() {
            super.onPreExecute();
            tvFooter.setVisibility(View.GONE);
            progress_bar.setVisibility(View.VISIBLE);
        }
        //Фоновая операция
        protected ArrayList<Article> doInBackground(String... arg) {
            //ArrayList<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
            ArrayList<Article> output = new ArrayList<>();
            try {
                URL url = new URL(arg[0]);
                HtmlHelper hh = new HtmlHelper(url);
                List<TagNode> elem = hh.getParentsByClass("//tr[td[div[@class='" + CLASS_IMG + "']]]");
                List<TagNode> urlNext = hh.getParentsByClass("//td[@class='" + CLASS_NEXT + "']");

                for (TagNode element : elem) {
                    String s_date = getContent(element.findElementByAttValue("class", CLASS_DATE, true, false)).trim();
                    //Log.d(MY_LOG, "image:" + new URL(url, element.findElementByAttValue("class", CLASS_IMG, true, false).findElementByName("img", false).getAttributeByName("src")).toString());
                    //Log.d(MY_LOG, "href:" + new URL(url, element.findElementByAttValue("class", CLASS_TITLE, true, false).findElementByAttValue("class", "ln7", true, false).getAttributeByName("href")).toString());
                    output.add(new Article(s_date.substring(0, (s_date.indexOf(" 20") > 0) ? s_date.indexOf(" 20") + 5 : 10)
                            , element.findElementByAttValue("class", CLASS_TITLE, true, false).getText().toString()
                            , getContent(element.findElementByAttValue("class", CLASS_DESCR, true, false)).trim()
                            , new URL(url, element.findElementByAttValue("class", CLASS_IMG, true, false).findElementByName("img", false).getAttributeByName("src"))
                            , new URL(url, element.findElementByAttValue("class", CLASS_TITLE, true, false).findElementByAttValue("class", "ln7", true, false).getAttributeByName("href"))
                            , false));
                }
                for (TagNode element : urlNext) {
                    if (element.findElementByAttValue("class", "ln7", true, false).getText().toString().contains("Туда")) {
                        surlNext = (new URL(url, element.findElementByAttValue("class", "ln7", true, false).getAttributeByName("href"))).toString();
                        Log.d(MY_LOG, "Next Link: " + surlNext);
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
        protected void onPostExecute(ArrayList<Article> output) {
 //добавляем данные в массив для адаптера
            if (!data_list.addAll(output)) {
                Toast.makeText(MainActivity.this, "Новые данные не добавились.",Toast.LENGTH_SHORT).show();
            }
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
