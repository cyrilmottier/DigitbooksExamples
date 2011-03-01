/*
 * Copyright (C) 2010   Cyril Mottier & Ludovic Perrier
 *              (http://www.digitbooks.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.digitbooks.android.examples.chapitre08;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import fr.digitbooks.android.examples.util.Config;

public class DigitbooksGuestbookXml extends DefaultHandler implements GuestbookHandler {

    private static final String LOG_TAG = DigitbooksGuestbookXml.class.getSimpleName();

    // Utilisé pour garder une trace de l'élément en court de lecture
    @SuppressWarnings("unused")
    private boolean inData = false;

    private List<HashMap<String, String>> mData;

    public void startElement(String uri, String name, String qName, Attributes atts) {
        if (name.trim().equals("data")) {
            inData = true;
            HashMap<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getLocalName(i).equals("rating")) {
                    if (Config.INFO_LOGS_ENABLED) {
                        Log.i(LOG_TAG, "rating = " + atts.getValue(i));
                    }
                    map.put("rating", atts.getValue(i));
                    
                } else if (atts.getLocalName(i).equals("comment")) {
                    if (Config.INFO_LOGS_ENABLED) {
                        Log.i(LOG_TAG, "comment = " + atts.getValue(i));
                    }
                    map.put("content", atts.getValue(i));
                    
                } else if (atts.getLocalName(i).equals("date")) {
                    if (Config.INFO_LOGS_ENABLED) {
                        Log.i(LOG_TAG, "date = " + atts.getValue(i));
                    }
                    map.put("date", atts.getValue(i));
                    
                } else {
                    Log.e(LOG_TAG, "attrinute unknown = " + atts.getLocalName(i));
                }
            }
            mData.add(map);
        }
    }

    public void endElement(String uri, String name, String qName) throws SAXException {
        if (name.trim().equals("data")) {
            inData = false;
        }
    }

    public List<HashMap<String, String>> parse(String xml) throws Exception {

        if (xml == null) {
            return null;
        }

        mData = new ArrayList<HashMap<String, String>>();

        InputSource inputSource = new InputSource(new StringReader(xml));
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        xr.setContentHandler(this);
        xr.parse(inputSource);

        return mData;
    }

}
