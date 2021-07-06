// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns bigfoot data as a JSON object, e.g. {"2017": 52, "2018": 34}] */
/* data: https://www.kaggle.com/nadintamer/top-tracks-of-2017/data
 * id,name,artists,danceability,energy,key,loudness,mode,speechiness,acousticness,instrumentalness,liveness,valence,tempo,duration_ms,time_signature
*/
@WebServlet("/music-data-18")
public class MusicNewDataServlet extends HttpServlet {

  private LinkedHashMap<Integer, Float> bigfootSightings = new LinkedHashMap<>();

  public void setUpData(int property) {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(
        "/WEB-INF/2018toptracks.csv"));
    int i = 0;
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      Float sightings = Float.valueOf(cells[property]);

      bigfootSightings.put(i, sightings);
      i++;
    }
    scanner.close();
  }

  private int getProperty(HttpServletRequest request) {
    String propertyString = request.getParameter("property");
    int num;
    try {
        num = Integer.parseInt(propertyString);
    } catch (NumberFormatException e) {
        System.err.println("Could not convert to int: " + propertyString);
        return -1;
    }
    return num;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int property = getProperty(request);
    setUpData(property);
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(bigfootSightings);
    response.getWriter().println(json);
  }
}