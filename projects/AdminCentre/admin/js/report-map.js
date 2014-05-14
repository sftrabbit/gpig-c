var initialised = false;
var mapOptions = {
    center: new google.maps.LatLng(20, 0),
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    zoom: 2,
    panControl: false,
    zoomControl: true,
    mapTypeControl: true,
    scaleControl: false,
    streetViewControl: false,
    overviewMapControl: false,
    zoomControlOptions: {
        style: google.maps.ZoomControlStyle.LARGE,
        position: google.maps.ControlPosition.RIGHT_BOTTOM
    },
    styles: [
        {stylers: [
            {visibility: "off"}
        ]},
        {featureType: "water", stylers: [
            {visibility: "on"},
            {color: "#d4d4d4"}
        ]},
        {featureType: "landscape", stylers: [
            {visibility: "on"},
            {color: "#e5e3df"}
        ]}
    ]
};
var map;
var eqData = {};
var eqCircle;
var infoWindow = new google.maps.InfoWindow();

$("body").on("reports-show", function() {
    if (!initialised) {
        var relative = $("#map").parent();
        $("#map").css({"width": relative.width() * 0.9, "height": relative.width() * 0.5, "margin": "0 auto"});
        map = new google.maps.Map(document.getElementById("map"), mapOptions);
        $.ajax({
            url: ENDPOINT+"?SystemID="+SYSTEM_ID+"&SensorID="+RECORD_TYPE+"&NumRecords="+MAX_POINTS+"&callback=?",
            dataType: "jsonp",
            success: addMapData
        });
        setInterval(refresh, REFRESH_SECONDS * 1000);
        initialised = true;
    } else {
        refresh();
    }
});

function refresh() {
    if ($("#content-reports").is(":visible")) {
        $.ajax({
            url: ENDPOINT+"?SystemID="+SYSTEM_ID+"&SensorID="+RECORD_TYPE+"&NumRecords="+MAX_POINTS+"&callback=?",
            dataType: "jsonp",
            success: addMapData
        });
    }
}

function addMapData(mapData) {
    mapData.Records.reverse();
    $.each(mapData.Records, function(key, item) {
        if (item.SensorID == RECORD_TYPE && !(item.CreationTimestamp in eqData)) {
            if (eqData.length >= MAX_POINTS) {
                eqData.shift();
            }
            var bits = CSVToArray(item.Value)[0];
            addMapPoint(item.CreationTimestamp, bits[0], bits[1], bits[2]);
        }
    });
}

function addMapPoint(ts, magnitude, latitude, longitude) {
    eqData[ts] = {"magnitude": parseFloat(magnitude), "location": new google.maps.LatLng(latitude, longitude)};
    var earthquake = {
        strokeColor: '#FF0000',
        strokeOpacity: 0.7,
        strokeWeight: 1,
        fillColor: '#FF0000',
        fillOpacity: 0.3,
        map: map,
        clickable: true,
        center: eqData[ts].location,
        radius: eqData[ts].magnitude * 100000,
        infoWindowIndex: ts
    };
    eqCircle = new google.maps.Circle(earthquake);
    google.maps.event.addListener(eqCircle, 'click', (function(eqCircle, ts) {
        return function() {
            infoWindow.setContent("Magnitude: "+magnitude+"<br>"+latitude+", "+longitude+"<br>"+new Date(ts).toUTCString());
            infoWindow.setPosition(eqCircle.getCenter());
            infoWindow.open(map);
        }
    })(eqCircle, ts));
}

//
// https://stackoverflow.com/a/1293163/2329111
//
// This will parse a delimited string into an array of
// arrays. The default delimiter is the comma, but this
// can be overriden in the second argument.
function CSVToArray( strData, strDelimiter ){
    // Check to see if the delimiter is defined. If not,
    // then default to comma.
    strDelimiter = (strDelimiter || ",");

    // Create a regular expression to parse the CSV values.
    var objPattern = new RegExp(
        (
            // Delimiters.
            "(\\" + strDelimiter + "|\\r?\\n|\\r|^)" +

            // Quoted fields.
            "(?:\"([^\"]*(?:\"\"[^\"]*)*)\"|" +

            // Standard fields.
            "([^\"\\" + strDelimiter + "\\r\\n]*))"
        ),
        "gi"
        );


    // Create an array to hold our data. Give the array
    // a default empty first row.
    var arrData = [[]];

    // Create an array to hold our individual pattern
    // matching groups.
    var arrMatches = null;


    // Keep looping over the regular expression matches
    // until we can no longer find a match.
    while (arrMatches = objPattern.exec( strData )){

        // Get the delimiter that was found.
        var strMatchedDelimiter = arrMatches[ 1 ];

        // Check to see if the given delimiter has a length
        // (is not the start of string) and if it matches
        // field delimiter. If id does not, then we know
        // that this delimiter is a row delimiter.
        if (
            strMatchedDelimiter.length &&
            (strMatchedDelimiter != strDelimiter)
            ){

            // Since we have reached a new row of data,
            // add an empty row to our data array.
            arrData.push( [] );

        }


        // Now that we have our delimiter out of the way,
        // let's check to see which kind of value we
        // captured (quoted or unquoted).
        if (arrMatches[ 2 ]){

            // We found a quoted value. When we capture
            // this value, unescape any double quotes.
            var strMatchedValue = arrMatches[ 2 ].replace(
                new RegExp( "\"\"", "g" ),
                "\""
                );

        } else {

            // We found a non-quoted value.
            var strMatchedValue = arrMatches[ 3 ];

        }


        // Now that we have our value string, let's add
        // it to the data array.
        arrData[ arrData.length - 1 ].push( strMatchedValue );
    }

    // Return the parsed data.
    return( arrData );
}
