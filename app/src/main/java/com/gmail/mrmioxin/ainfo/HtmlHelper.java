package com.gmail.mrmioxin.ainfo;

import android.util.Log;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by palchuk on 29.04.2015.
 */
public class HtmlHelper {
    private static final String MY_LOG = "My_log";
    TagNode rootNode;
    InputStream in;

    //Конструктор
    public HtmlHelper(URL htmlPage) throws IOException
    {
        //URL url = new URL("http://www.android.com/");
        HttpURLConnection urlConnection = (HttpURLConnection) htmlPage.openConnection();
        Log.d(MY_LOG,urlConnection.getErrorStream ().toString());
        Log.d(MY_LOG,urlConnection.getHeaderFields().toString());

        int httpCode = urlConnection.getResponseCode();
        switch (httpCode) {
            case 200:
                Log.d(MY_LOG,"Response 200.");
                in = new BufferedInputStream(urlConnection.getInputStream());
                break;
            case 401:
                Log.d(MY_LOG,"Response 401.");
                break;
            default:
                Log.d(MY_LOG,"Response "+httpCode+".");
                in.close ();
                in = null;

        }
        int length = urlConnection.getContentLength ();
        urlConnection.disconnect();

        //Создаём объект HtmlCleaner
        HtmlCleaner cleaner = new HtmlCleaner();
        //Загружаем html код сайта
        rootNode = cleaner.clean(in);
    }

    List<TagNode> getParentsByClass(String CSSClassname)
    {
        List<TagNode> parentList = new ArrayList<TagNode>();
        try {
            //Object[] nodes = rootNode.evaluateXPath("//td[@class='div18']/parent::*");
            parentList = (List<TagNode>) rootNode.getElementListByAttValue("class",CSSClassname,true,false);

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
