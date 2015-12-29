package com.gmail.mrmioxin.ainfo;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity  implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MY_LOG = "My_log.MainActivity";
    //CSS классы в HTML
//    final String CLASS_DATE     = "div16";
//    final String CLASS_TITLE    = "div11";
//    final String CLASS_DESCR    = "div18";
//    final String CLASS_IMG      = "corner1";
//    final String CLASS_NEXT     = "td26";

    String strUrlNext= "";
//    ArrayList<Map<String, Object>> data_list;
    ArrayList<Article> data_list;
    ListView listview;
    AiListAdapter myAdapter;
    AiCursorAdapter myCurAdapter;
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
        myCurAdapter = new AiCursorAdapter(MainActivity.this, null, 0);
        getSupportLoaderManager().initLoader(0, null, this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(MY_LOG, "onItemClick pos = " + position + "; id = " + id);
                Article item = (Article) myAdapter.getItem(position);//data_list.get(position);
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("href", item.Href.toString());
                intent.putExtra("title", item.Title);
                startActivity(intent);
            }
        });

        //add footer
        listview.addFooterView(footer);
        listview.setAdapter(myAdapter);
        //listview.setAdapter(myCurAdapter);
        new ParseSite().execute(getString(R.string.url_site2));
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

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.CursorLoader(
                MainActivity.this,
                ContractClass.Articles.CONTENT_URI,
                ContractClass.Articles.DEFAULT_PROJECTION,
                null,
                null,
                null
        );
    }

     @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        myCurAdapter.swapCursor(data);

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        myCurAdapter.swapCursor(null);
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
                List<TagNode> elem = hh.getParentsByClass(getString(R.string.xss2_elem));
                List<TagNode> urlNext = hh.getParentsByClass(getString(R.string.xss2_next));

                for (TagNode element : elem) {
                    String s_date = getContent(element.findElementByAttValue("class", getString(R.string.url2_class_date), true, false)).trim();
                    output.add(new Article(s_date.substring(0, (s_date.indexOf(" 20") > 0) ? s_date.indexOf(" 20") + 5 : 16)
                            , element.findElementByAttValue("class", getString(R.string.url2_class_title), true, false).getText().toString()
                            , getContent(element.findElementByAttValue("class", getString(R.string.url2_class_descr), true, false)).trim()
                            , new URL(url, element.findElementByAttValue("class", getString(R.string.url2_class_img), true, false).findElementByName("a", false)                                                                                                      .findElementByName("img", false).getAttributeByName("src"))
                            , new URL(url, element.findElementByAttValue("class", getString(R.string.url2_class_title), true, false).findElementByName("a", false).getAttributeByName("href"))
                            , false));
                }
                if (urlNext.size() >1) {
                    for (TagNode element : urlNext) {
                        if (element.getText().toString().contains("Туда")) {
                            surlNext = (new URL(url, element.getAttributeByName("href"))).toString();
                        }
                    }
                } else {
                    surlNext = (new URL(url, urlNext.get(0).getAttributeByName("href"))).toString();
                }
                Log.d(MY_LOG, "Next Link: " + surlNext);
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
            myCurAdapter.notifyDataSetChanged();
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
