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
            var bits = item.Value.split(",");
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
