package com.gmail.mrmioxin.ainfo;

import android.media.Image;

import java.net.URL;

/**
 * Created by palchuk on 05.08.2015.
 * Agroinform progect
 */
public class Article {
    String Date;
    String Title;
    String Descr;
    URL Img;
    URL Href;
    Boolean Checkb;

    Article(String _Date, String _Title, String _Descr, URL _LitleImg, URL _href, Boolean _Checkb) {
        Date = _Date;
        Title= _Title;
        Descr= _Descr;
        Img= _LitleImg;
        Href = _href;
        Checkb= _Checkb;

    }
}
