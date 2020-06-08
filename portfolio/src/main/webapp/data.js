// Add chart
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawCharts);

function drawCharts() {
    drawBigfoot();
    //drawMusic();
}

/** Fetches bigfoot sightings data and uses it to create a chart. */
function drawBigfoot() {
  fetch('/bigfoot-data').then(response => response.json())
  .then((bigfootSightings) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Year');
    data.addColumn('number', 'Sightings');
    Object.keys(bigfootSightings).forEach((year) => {
      data.addRow([year, bigfootSightings[year]]);
    });

    const options = {
      'title': 'Bigfoot Sightings',
      'width':600,
      'height':500
    };

    const chart = new google.visualization.LineChart(
        document.getElementById('bigfoot-chart-container'));
    chart.draw(data, options);
  });
}

/** Fetches bigfoot sightings data and uses it to create a chart. */
/*
function drawMusic() {
  fetch('/music-data').then(response => response.json())
  .then((bigfootSightings) => {
    const data1 = new google.visualization.DataTable();
    data1.addColumn('string', 'Most Played Track');
    data1.addColumn('number', 'Danceability');
    Object.keys(bigfootSightings).forEach((year) => {
      data1.addRow([year, bigfootSightings[year]]);
    });

    const options = {
      'title': 'Top 100 tracks by Danceability',
      'width':600,
      'height':500
    };

    const chart = new google.visualization.LineChart(
        document.getElementById('music-chart-container'));
    chart.draw(data1, options);
  });
}*/

/** Fetches bigfoot sightings data and uses it to create a chart. */
async function drawMusic(property, propertyString) {
    var queryString = '/music-data?property=' + property;
    const response = await fetch(queryString);

    const musicData = await response.json();
    const data1 = new google.visualization.DataTable();
    data1.addColumn('string', 'Most Played Track');
    data1.addColumn('number', propertyString);
    Object.keys(musicData).forEach((year) => {
      data1.addRow([year, musicData[year]]);
    });

    const options = {
      'title': 'Top 100 tracks by ' + propertyString,
      'width':600,
      'height':500
    };

    const chart = new google.visualization.LineChart(
        document.getElementById('music-chart-container'));
    chart.draw(data1, options);
}



function getMusicChange() {
    var el = document.getElementById("musicProperty");
    var property = el.options[el.selectedIndex].value;
    var propertyString = el.options[el.selectedIndex].text;
    drawMusic(property, propertyString);
}