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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DigitbooksGuestbookJson implements GuestbookHandler {

    public List<HashMap<String, String>> parse(String json) throws JSONException {
        
        if (json == null) {
            return null;
        }
        
        List<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
        
        JSONObject all = new JSONObject(json);
        JSONArray dataArray = new JSONArray(all.getString("data"));
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject data = dataArray.getJSONObject(i);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("rating", data.getString("rating"));
            map.put("content", data.getString("content"));
            map.put("date", data.getString("date"));

            result.add(map);
        }
        
        return result;
    }

}
