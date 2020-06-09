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
async function drawMusic(property, propertyString, numTracks) {
    var queryString = '/music-data-18?property=' + property;
    const response18 = await fetch(queryString);
    const musicData18 = await response18.json();

    var queryString = '/music-data-17?property=' + property;
    const response17 = await fetch(queryString);
    const musicData17 = await response17.json();

    const data1 = new google.visualization.DataTable();
    data1.addColumn('number', 'Most Played Track');
    data1.addColumn('number', '2017');
    data1.addColumn('number', '2018');

    for (i = 0; i < numTracks; i++) {
        data1.addRow([i, musicData17[i], musicData18[i]]);
    }

    const options = {
      'title': propertyString + ' of the Top 100 Tracks',
      'width':600,
      'height':500
    };

    const chart = new google.visualization.LineChart(
        document.getElementById('music-chart-container'));
    chart.draw(data1, options);
}



function getMusicChange(numTracks) {
    var el = document.getElementById("musicProperty");
    var property = el.options[el.selectedIndex].value;
    var propertyString = el.options[el.selectedIndex].text;
    if (numTracks === -1) {
        numTracks = 100;
    }
    drawMusic(property, propertyString, numTracks);
}