package com.gmail.mrmioxin.ainfo;

import android.util.Log;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by palchuk on 29.04.2015.
 */
public class HtmlHelper {
    private static final String MY_LOG = "My_log.HtmlHelper";
    TagNode rootNode;
    InputStreamReader in;

    //Конструктор
    public HtmlHelper(URL htmlPage) throws IOException
    {
        //URL url = new URL("http://www.android.com/");
        Boolean done = false;
        int counter = 5;
        HttpURLConnection urlConnection = null;
        // First set the default cookie manager.
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        while (!done || (counter == 0)) {
            urlConnection = (HttpURLConnection) htmlPage.openConnection();

            Log.d(MY_LOG, urlConnection.getHeaderFields().toString());

            int httpCode = urlConnection.getResponseCode();
            switch (httpCode) {
                case 200:
                    Log.d(MY_LOG, "Response 200.");
                    in = new InputStreamReader(urlConnection.getInputStream());
                    done = true;
                    break;
                case 401:
                    Log.d(MY_LOG, "Response 401.");
                    --counter;
                    break;
                default:
                    Log.d(MY_LOG, "Response " + httpCode + ".");
                    --counter;
            }

            int length = urlConnection.getContentLength();
        }
        //urlConnection.disconnect();
        //Создаём объект HtmlCleaner
        HtmlCleaner cleaner = new HtmlCleaner();
        //Загружаем html код сайта
        rootNode = cleaner.clean(in);
    }

    List<TagNode> getParentsByClass(String XPath) throws XPatherException
    {
        List<TagNode> parentList = new ArrayList<TagNode>();
        try {
            Object[] tags = rootNode.evaluateXPath(XPath);
            Log.d(MY_LOG, "Beginning HTMLparsing..." + tags.length);
            //parentList = (List<TagNode>) rootNode.getElementListByAttValue("class",CSSClassname,true,false);
            for (Object t:tags) {
                parentList.add((TagNode)t);
                //Log.d(MY_LOG, "tag: "+ t.toString());
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return parentList;
    }

    List<TagNode> getLinksByClass(String CSSClassname)
    {
        List<TagNode> linkList = new ArrayList<TagNode>();

        //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName("a", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            //получаем атрибут по имени
            String classType = linkElements[i].getAttributeByName("class");
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (classType != null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }

        return linkList;
    }
}
