<?php
$configFile = file_get_contents("RegisteredSystems.config");
$configJson = json_decode($configFile, true);
$systems = $configJson["Systems"];
$systemId = (string)$_GET["system"];
$currentSystem = array("SystemID" => "Select a system...", "unknown" => true);
$otherSystems = array();
foreach ($systems as $system) {
    if ($system["SystemID"] == $systemId) {
        $currentSystem = $system;
    } else {
        $otherSystems[] = $system["SystemID"];
    }
}

$availableDatastores = array(
    "GWTSystemDataGateway"
);
$availableAnalysis = array(
    "BoundedAnalysisEngine",
    "EarthquakeAnalysisEngine",
    "ExpressionAnalysisEngine",
    "FaceAnalysisEngine"
);
$availableNotification = array(
    "EmailNotificationEngine",
    "PhoneAppNotificationEngine",
    "TwitterNotificationEngine"
);
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Admin Centre - System: <?php echo $currentSystem["SystemID"]; ?></title>

    <!-- Bootstrap core CSS -->
    <link href="../css/bootstrap.css" rel="stylesheet">

    <!-- Add custom CSS here -->
    <link href="../css/modern-business.css" rel="stylesheet">
    <link href="../css/admin.css" rel="stylesheet">
    <link href="../font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="../css/custom-gpig.css" rel="stylesheet">
    <link href="../css/bootstrap-switch.css" rel="stylesheet">

    <!-- JavaScript -->
    <script src="../js/jquery-1.10.2.js"></script>
</head>
<body>

<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index.html">Admin Home</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse navbar-ex1-collapse">
            <ul id="nbarstart" class="nav navbar-nav navbar-right"></ul>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container -->
</nav>

<div class="container">

    <div class="row">
        <div class="col-sm-12 center-block">
            <br>
            <h1>
                Administration Center
                <div id="systems-list">
                    <span>System: </span> 
                    <div class="btn-group">
                        <button type="button" class="btn btn-default btn-lg dropdown-toggle" data-toggle="dropdown">
                            <?php echo $currentSystem["SystemID"]; ?> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <?php
                            foreach ($otherSystems as $system) {
                                echo "<li><a href=\"?system=$system\">$system</a></li>";
                            }
                            ?>
                        </ul>
                    </div>
                </div>
            </h1>
            <hr>
        </div>
    </div>
    <?php
    if (!isset($currentSystem["unknown"])) {
    ?>
    <ul class="nav nav-tabs nav-justified" id="tab-menu">
        <li class="active"><a href="#" data-tab="sensors"><i class="fa fa-tasks"></i> Sensors</a></li>
        <li><a href="#" data-tab="data"><i class="fa fa-hdd-o"></i> Data store</a></li>
        <li><a href="#" data-tab="engines"><i class="fa fa-cogs"></i> Engines</a></li>
        <li><a href="#" data-tab="reports" id="tab-menu-reports"><i class="fa fa-bar-chart-o"></i> Reporting</a></li>
    </ul>
    <div id="content-sensors" class="tab-content">
    <?php
    if (empty($currentSystem["Sensors"])) {
        echo '<br><ul class="list-group"><li class="list-group-item">No sensors configured</li></ul>';
    } else {
        foreach ($currentSystem["Sensors"] as $sensor) {
            ?>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php echo $sensor["SensorID"]; ?></h3>
                </div>
                <div class="panel-body">
                    <form class="sensor-params" data-system="<?php echo $sensor["SensorID"]; ?>">
                        <ul class="list-group">
                        <?php
                        foreach ($sensor["Params"] as $param => $value) {
                        ?>
                            <li class="list-group-item param-item">
                                <input type="text" value="<?php echo $param; ?>" class="form-control param-key"><input type="text" value="<?php echo $value; ?>" class="form-control param-value"><a class="btn btn-danger param-delete" href="#"><i class="fa fa-trash-o"></i></a>
                            </li>
                        <?php
                        }
                        ?>
                            <li class="list-group-item"><a href="#" class="new-param btn"><span>Add a new parameter</span></a></li>
                        </ul>
                        <div class="buttons-group">
                            <button type="submit" class="btn btn-primary">Save</button><button type="reset" class="btn btn-default">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
            <?php
        }
    }
    ?>
    </div>
    <div id="content-data" class="tab-content" style="display:none;">
        <div class="panel panel-default">
            <div class="panel-body">
                <form id="data-store" class="form-inline">
                    <select id="data-store-name" class="form-control">
                    <?php
                    foreach ($availableDatastores as $store) {
                        echo "<option value=\"$store\"".($store == $currentSystem["DatastoreGateway"] ? "selected=\"selected\"" : "").">$store</option>";
                    }
                    ?>
                    </select><button type="submit" class="btn btn-primary">Save</button>
                </form>
            </div>
        </div>
    </div>
    <div id="content-engines" class="tab-content" style="display:none;">
        <form id="engines">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Analysis engines</h3>
                </div>
                <div class="panel-body">
                    <ul class="list-group">
                    <?php
                    foreach ($availableAnalysis as $engine) {
                    ?>
                        <li class="list-group-item">
                            <?php echo $engine; ?>
                            <input type="checkbox" class="toggle-switch" name="engines[]" value="<?php echo $engine; ?>"<?php echo in_array($engine, $currentSystem["Engines"]) ? " checked=\"checked\"" : ""; ?>>
                        </li>
                    <?php
                    }
                    ?>
                    </ul>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Notification engines</h3>
                </div>
                <div class="panel-body">
                    <ul class="list-group">
                    <?php
                    foreach ($availableNotification as $engine) {
                    ?>
                        <li class="list-group-item">
                            <?php echo $engine; ?>
                            <input type="checkbox" class="toggle-switch" name="engines[]" value="<?php echo $engine; ?>"<?php echo in_array($engine, $currentSystem["Engines"]) ? " checked=\"checked\"" : ""; ?>>
                        </li>
                    <?php
                    }
                    ?>
                    </ul>
                </div>
            </div>
        </form>
    </div>
    <div id="content-reports" class="tab-content" style="display:none;">
        <?php
        if (empty($currentSystem["Reporting"])) {
            echo '<br><ul class="list-group"><li class="list-group-item">No reports configured</li></ul>';
        } else {
            if ($currentSystem["Reporting"] == "Graph") {
            ?>
                <div class="col-sm-12 center-block block-top-buffer">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h3 class="panel-title">Memory Graph</h3>
                        </div>
                        <div class="panel-body">
                            <canvas id="graph"></canvas>
                        </div>
                    </div>
                </div>
                <script src="js/Chart.js"></script>
                <script>
                    $(function(){

                        // Configuration
                        var ENDPOINT = "http://gpigc-webapp.appspot.com/gpigc-webapp";
                        var REFRESH_SECONDS = 10;
                        var MAX_POINTS = 50;
                        var RECORD_TYPE = "<?php echo $currentSystem["Sensors"][1]["SensorID"]; ?>";
                        var SYSTEM_ID = "<?php echo $currentSystem["SystemID"]; ?>";
                        var FREQUENCY = 10; // 1 to 59
                        var ROUND_SCALE = 1000;
                        var STEPS_SCALE = 20;

                        var resized = false;
                        $("#tab-menu-reports").click(function() {
                            if (!resized) {
                                setTimeout(function() {
                                    // Initialise
                                    var relative = $("#graph").parent();
                                    $("#graph").attr("width", relative.width()).attr("height", relative.width() * 0.6);
                                    chart = new Chart($("#graph").get(0).getContext("2d"));
                                    $.ajax({
                                        url: ENDPOINT+"?SystemID="+SYSTEM_ID+"&SensorID="+RECORD_TYPE+"&NumRecords="+(MAX_POINTS*FREQUENCY)+"&callback=?",
                                        dataType: "jsonp",
                                        success: function(graphData) {
                                            addItems(graphData.Records);
                                            chart.Line(data, options);
                                            options.animation = false;
                                        }
                                    });
                                    setInterval(refreshChart, REFRESH_SECONDS * 1000);
                                    // End initialisation
                                }, 600);
                                resized = true;
                            }
                        });

                        var chart;
                        var options = {
                            scaleOverride: true,
                            scaleSteps: STEPS_SCALE
                        }
                        var data = {
                            labels : [],
                            datasets : [
                                {
                                    fillColor : "rgba(93,31,173,0.5)",
                                    strokeColor : "rgba(93,31,173,1)",
                                    pointColor : "rgba(93,31,173,1)",
                                    pointStrokeColor : "#fff",
                                    data : []
                                }
                            ]
                        };

                        function refreshChart() {
                            if ($("#content-reports").is(":visible")) {
                                $.ajax({
                                    url: ENDPOINT+"?SystemID="+SYSTEM_ID+"&SensorID="+RECORD_TYPE+"&NumRecords="+(REFRESH_SECONDS*FREQUENCY)+"&callback=?",
                                    dataType: "jsonp",
                                    success: function(graphData) {
                                        addItems(graphData.Records);
                                        chart.Line(data, options);
                                    }
                                });
                            }
                        }

                        function parseDate(input) {
                            var date = new Date(input);
                            return ("0" + date.getUTCDate()).slice(-2) + "/" + ("0" + (date.getUTCMonth() + 1)).slice(-2) + "/" + date.getUTCFullYear() + " " + ("0" + date.getUTCHours()).slice(-2) + ":" + ("0" + date.getUTCMinutes()).slice(-2) + ":" + ("0" + date.getUTCSeconds()).slice(-2);
                        }

                        function addItems(items) {
                            var min = Number.MAX_VALUE;
                            var max = Number.MIN_VALUE;
                            var changed = false;
                            items.reverse();
                            $.each(items, function(key, item) {
                                if (item.SensorID == RECORD_TYPE && (data.labels.indexOf(parseDate(item.CreationTimestamp)) == -1)) {
                                    if (!(new Date(item.CreationTimestamp).getUTCSeconds() % FREQUENCY)) {
                                        if (data.labels.length >= MAX_POINTS) {
                                            data.labels.shift();
                                            data.datasets[0].data.shift();
                                        }
                                        var value = parseFloat(item.Value)
                                        var thisMin = Math.floor(value / ROUND_SCALE) * ROUND_SCALE;
                                        var thisMax = Math.ceil(value / ROUND_SCALE) * ROUND_SCALE;
                                        if (thisMin < min) min = thisMin;
                                        if (thisMax > max) max = thisMax;
                                        data.labels.push(parseDate(item.CreationTimestamp));
                                        data.datasets[0].data.push(value);
                                        changed = true;
                                    }
                                }
                            });
                            if (changed) {
                                var stepWidth = Math.round(((max - min) / (STEPS_SCALE - 2)) / ROUND_SCALE) * ROUND_SCALE;
                                options.scaleStepWidth = stepWidth;
                                options.scaleStartValue = min - stepWidth;
                            }
                        }
                    });
                </script>
            <?php
            } elseif ($currentSystem["Reporting"] == "Map") {
            ?>
                <div class="col-sm-12 center-block block-top-buffer">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h3 class="panel-title">Map</h3>
                        </div>
                        <div class="panel-body">
                            <div id="map"></div>
                        </div>
                    </div>
                </div>
                <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA1jQgEtsdQmRQtyOXp6VDwoDla2rN4FbE&sensor=false"></script>
                <script type="text/javascript">
                    // Configuration
                    var ENDPOINT = "http://gpigc-webapp.appspot.com/gpigc-webapp";
                    var REFRESH_SECONDS = 120;
                    var MAX_POINTS = 50;
                    var RECORD_TYPE = "<?php echo $currentSystem["Sensors"][0]["SensorID"]; ?>";
                    var SYSTEM_ID = "<?php echo $currentSystem["SystemID"]; ?>";

                    var resized = false;
                    $("#tab-menu-reports").click(function() {
                        if (!resized) {
                            setTimeout(initialize, 600);
                            resized = true;
                        }
                    });

                    function initialize() {
                        var relative = $("#map").parent();
                        $("#map").css({"width": relative.width() * 0.9, "height": relative.width() * 0.5, "margin": "0 auto"});

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
                                        {stylers:[
                                                {visibility:"off"}
                                                ]},
                                        {featureType:"water", stylers:[
                                                                    {visibility:"on"},
                                                                    {color:"#d4d4d4"}
                                                                    ]},
                                        {featureType:"landscape", stylers:[
                                                                        {visibility:"on"},
                                                                        {color:"#e5e3df"}
                                                                        ]}
                                    ]
                        };
                        var map = new google.maps.Map(document.getElementById("map"), mapOptions);
                        var eqData = {};
                        var eqCircle;
                        var infoWindow = new google.maps.InfoWindow();

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

                        function refreshMap() {
                            if ($("#content-reports").is(":visible")) {
                                $.ajax({
                                    url: ENDPOINT+"?SystemID="+SYSTEM_ID+"&SensorID="+RECORD_TYPE+"&NumRecords="+MAX_POINTS+"&callback=?",
                                    dataType: "jsonp",
                                    success: addMapData
                                });
                            }
                        }
                        refreshMap();
                        setInterval(refreshMap, REFRESH_SECONDS * 1000);

                    }
                    //google.maps.event.addDomListener(window, 'load', initialize);
                </script>
            <?php
            }
        }
        ?>
    </div>
    <?php
    }
    ?>
</div> <!--/.container -->

<div class="container">
    <hr>
    <footer>
        <div class="row">
            <div class="col-lg-12">
                <p>Copyright &copy; GPIG-C 2014</p>
            </div>
        </div>
    </footer>

</div><!-- /.container -->

<!-- JavaScript -->
<script src="../js/bootstrap.js"></script>
<script src="../js/modern-business.js"></script>
<script src="js/admin-navbar.js"></script>
<script src="js/bootstrap-switch.js"></script>
<script src="js/index.js"></script>
</body>
</html>
