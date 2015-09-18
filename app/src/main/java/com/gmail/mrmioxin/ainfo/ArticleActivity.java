package com.gmail.mrmioxin.ainfo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.AsyncTask;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;

public class ArticleActivity extends ActionBarActivity {
    private static final String MY_LOG = "My_log.ArticleActivity";
    final String CLASS_IMG = "corner1";
    final String ID_IMG = "left_images_bloc";
    final String CLASS_CONTENT = "news_content";

    ProgressDialog progressd;
    TextView tvTitle;
    TextView tvContent;
    ImageView img;
    String sTitle;
    String sUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvContent = (TextView)findViewById(R.id.tvContent);
        img = (ImageView) findViewById(R.id.img);

        Intent intent = getIntent();
        Log.d(MY_LOG, "title: "+intent.getStringExtra("title")+"; href: "+intent.getStringExtra("href"));
        //long id = intent.getLongExtra("ROW_ID", 0);// long id);
        //Article item = (Article) data_list.get(id);
        sTitle = intent.getStringExtra("title");
        sUrl = intent.getStringExtra("href");
        tvTitle.setText(sTitle);
        //tvContent.setText(sUrl);
        new Download().execute(sUrl);
    }

    protected class Download extends AsyncTask<String, Void, ArrayList<Download.item_e>>{
        protected class item_e {
            String type;
            String cont;

            public item_e(String t, String c) {
                this.type = t;
                this.cont = c;
            }

            String getType() {
                return type;
            }
            String getCont() {
                return  cont;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressd = new ProgressDialog(ArticleActivity.this);
            progressd.setTitle(getString(R.string.ArticleProgressDialogTitle));
            progressd.setMessage(sTitle);
            progressd.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.ArticleProgressDialogButtonNegative), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(MY_LOG, "Отмена загрузки статьи "+sTitle);
                    progressd.dismiss();
                }
            });
            progressd.show();
        }
        @Override
        protected ArrayList<Download.item_e> doInBackground(String... params) {
            ArrayList<item_e> output = new ArrayList<>();
            ArrayList<TagNode> elem = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                HtmlHelper hh = new HtmlHelper(url);
                elem.addAll(hh.getParentsByClass("//div[@id='" + ID_IMG + "']/div[@class='" + CLASS_IMG +"']/img"));
                elem.addAll(hh.getParentsByClass("//div[@class='" + CLASS_CONTENT + "']/*"));

                TagNode[] tn;
                for (TagNode member:elem) {
                    if ("img".equals(member.getName())) {
                        output.add(new item_e(member.getName(), member.getAttributeByName("src")));
                        Log.d(MY_LOG, "Image src: " + member.getAttributeByName("src"));
                    }
                    else {
                        String content = member.getText().toString();
                        if (content.length()>1)  {
                            output.add(new item_e("text", content));
                            Log.d(MY_LOG, "Type of mem: " + member.getName() + "; Content: " + member.getText());
                        }

                    }
                    if (member.hasChildren()) {
                        for (TagNode m:member.getChildTags()) {
                            if ("img".equals(m.getName())) {
                                output.add(new item_e(m.getName(), m.getAttributeByName("src")));
                                Log.d(MY_LOG, "Type of childes: " + m.getName()+"Image src: " + m.getAttributeByName("src") + "Content: " + m.getText());
                            }
                        }
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


        protected void onPostExecute(ArrayList<item_e> output) {
            Log.d(MY_LOG, "Count of element: " + output.size());
            for (item_e ie:output) {
                String name = ie.getType();
                String cont = ie.getCont();
                switch (name) {
                    case "img":
                        break;
                    case "text":

                        break;
                }
            }
            progressd.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
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
