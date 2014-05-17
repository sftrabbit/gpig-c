<%@ page import="java.io.File,java.io.FileReader,java.util.ArrayList,java.util.Arrays,java.util.Iterator,java.util.List,java.util.Set,java.util.Map,java.util.Map.Entry,org.json.simple.parser.JSONParser,org.json.simple.JSONArray,org.json.simple.JSONObject" %>
<%@ page buffer="none" %>
<%!
String filePath = new File("").getAbsolutePath();
private List<String> getEngines(String engineType) {
    List<String> engines = new ArrayList<String>();
    File folder = new File(filePath.concat("/bin/classes/engine/" + engineType + "/com/gpigc/core/" + engineType + "/engine"));
    for (File engineFile : folder.listFiles()) {
        if (engineFile.getName().endsWith(".class")) {
            engines.add(engineFile.getName().substring(0, engineFile.getName().length() - 6));
        }
    }
    return engines;
}
%>
<%
FileReader configFile = new FileReader(filePath.concat("/config/RegisteredSystems.config"));

JSONParser jsonParser = new JSONParser();
JSONObject configJson = (JSONObject) jsonParser.parse(configFile);

JSONArray systems = (JSONArray) configJson.get("Systems");
String systemId = "";
if (request.getParameter("system") != null) {
    systemId = request.getParameter("system").toString();
}

JSONObject currentSystem = new JSONObject();
currentSystem.put("SystemID", "Select a system...");
currentSystem.put("unknown", true);
List<String> otherSystems = new ArrayList<String>();

Iterator<JSONObject> iter = systems.iterator();
while (iter.hasNext()) {
    JSONObject system = (JSONObject) iter.next();
    if (((String) system.get("SystemID")).equals(systemId)) {
        currentSystem = system;
    } else {
        otherSystems.add((String) system.get("SystemID"));
    }
}

List<String> availableDatastores = getEngines("storage");
List<String> availableAnalysis = getEngines("analysis");
List<String> availableNotification = getEngines("notification");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Admin Centre - System: <%= currentSystem.get("SystemID") %></title>

    <!-- Bootstrap core CSS -->
    <link href="../css/bootstrap.css" rel="stylesheet">

    <!-- Add custom CSS here -->
    <link href="../css/modern-business.css" rel="stylesheet">
    <link href="../font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="css/admin.css" rel="stylesheet">
    <link href="../css/custom-gpig.css" rel="stylesheet">
    <link href="css/bootstrap-switch.css" rel="stylesheet">

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
                Administration Centre
                <div id="systems-list">
                    <span>System: </span> 
                    <div class="btn-group">
                        <button type="button" class="btn btn-default btn-lg dropdown-toggle" data-toggle="dropdown">
                            <%= currentSystem.get("SystemID") %> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <%
                            for (String system: otherSystems) {
                            %>
                                <li><a href="?system=<%= system %>"><%= system %></a></li>
                            <%
                            }
                            %>
                        </ul>
                    </div>
                </div>
            </h1>
            <hr>
        </div>
    </div>
    <%
    if (currentSystem.get("unknown") == null) {
    %>
    <ul class="nav nav-tabs nav-justified" id="tab-menu">
        <li class="active"><a href="#" data-tab="sensors"><i class="fa fa-tasks"></i> Sensors</a></li>
        <li><a href="#" data-tab="data"><i class="fa fa-hdd-o"></i> Data store</a></li>
        <li><a href="#" data-tab="engines"><i class="fa fa-cogs"></i> Engines</a></li>
        <li><a href="#" data-tab="reports" id="tab-menu-reports"><i class="fa fa-bar-chart-o"></i> Reporting</a></li>
    </ul>
    <div id="content-sensors" class="tab-content">
    <%
    if (((JSONArray) currentSystem.get("Sensors")).isEmpty()) {
        %>
        <br><ul class="list-group"><li class="list-group-item">No sensors configured</li></ul>
        <%
    } else {
        Iterator<JSONObject> sensorIter = ((JSONArray) currentSystem.get("Sensors")).iterator();
        while (sensorIter.hasNext()) {
            JSONObject sensor = (JSONObject) sensorIter.next();
            %>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><%= sensor.get("SensorID") %></h3>
                </div>
                <div class="panel-body">
                    <form class="sensor-params" data-system="<%= sensor.get("SensorID") %>">
                        <ul class="list-group">
                        <%
                        JSONObject params = (JSONObject) sensor.get("Params");
                        if (params != null) {
                            Set<String> keys = params.keySet();
                            for (String key : keys) {
                            %>
                                <li class="list-group-item param-item">
                                    <input type="text" value="<%= key %>" class="form-control param-key"><input type="text" value="<%= params.get(key) %>" class="form-control param-value"><a class="btn btn-danger param-delete" href="#"><i class="fa fa-trash-o"></i></a>
                                </li>
                            <%
                            }
                        }
                        %>
                            <li class="list-group-item"><a href="#" class="new-param btn"><span>Add a new parameter</span></a></li>
                        </ul>
                        <div class="buttons-group">
                            <button type="submit" class="btn btn-primary">Save</button><button type="reset" class="btn btn-default">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
            <%
        }
    }
    %>
    </div>
    <div id="content-data" class="tab-content" style="display:none;">
        <div class="panel panel-default">
            <div class="panel-body">
                <form id="data-store" class="form-inline">
                    <select id="data-store-name" class="form-control">
                    <%
                    for (String store: availableDatastores) {
                    %>
                        <option value="<%= store %>"<%= store.equals(currentSystem.get("DatastoreGateway")) ? " selected='selected'" : "" %>><%= store %></option>
                    <%
                    }
                    %>
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
                    <%
                    for (String engine: availableAnalysis) {
                    %>
                        <li class="list-group-item">
                            <%= engine %>
                            <input type="checkbox" class="toggle-switch" name="engines[]" value="<%= engine %>"<%= ((JSONArray) currentSystem.get("Engines")).contains(engine) ? " checked='checked'" : "" %>>
                        </li>
                    <%
                    }
                    %>
                    </ul>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Notification engines</h3>
                </div>
                <div class="panel-body">
                    <ul class="list-group">
                    <%
                    for (String engine : availableNotification) {
                    %>
                        <li class="list-group-item">
                            <%= engine %>
                            <input type="checkbox" class="toggle-switch" name="engines[]" value="<%= engine %>"<%= ((JSONArray) currentSystem.get("Engines")).contains(engine) ? " checked='checked'" : "" %>>
                        </li>
                    <%
                    }
                    %>
                    </ul>
                </div>
            </div>
        </form>
    </div>
    <div id="content-reports" class="tab-content" style="display:none;">
        <%
        if (currentSystem.get("Reporting") == null) {
            %>
            <br><ul class="list-group"><li class="list-group-item">No reports configured</li></ul>
            <%
        } else {
            %>
            <div class="col-sm-12 center-block block-top-buffer">
                <div class="panel panel-primary">
                    <div class="panel-heading">
            <%
            if (((String) currentSystem.get("Reporting")).equals("Graph")) {
            %>
                        <h3 class="panel-title">Memory Graph</h3>
                        <script type="text/javascript">
                            var ENDPOINT = "http://gpigc-beta.appspot.com/gpigc-webapp";
                            var REFRESH_SECONDS = 10;
                            var MAX_POINTS = 50;
                            var RECORD_TYPE = "<%= ((JSONObject) ((JSONArray) currentSystem.get("Sensors")).get(0)).get("SensorID") %>";
                            var SYSTEM_ID = "<%= currentSystem.get("SystemID") %>";
                            var FREQUENCY = 10; // 1 to 59
                            var ROUND_SCALE = 1000;
                            var STEPS_SCALE = 20;
                        </script>
                        <script src="js/Chart.js"></script>
                        <script src="js/report-graph.js"></script>
                    </div>
                    <div class="panel-body">
                        <canvas id="graph"></canvas>
            <%
            } else if (((String) currentSystem.get("Reporting")).equals("Map")) {
            %>
                        <h3 class="panel-title">Map</h3>
                    </div>
                    <div class="panel-body">
                        <div id="map"></div>
                        <script type="text/javascript">
                            var ENDPOINT = "http://gpigc-beta.appspot.com/gpigc-webapp";
                            var REFRESH_SECONDS = 120;
                            var MAX_POINTS = 50;
                            var RECORD_TYPE = "<%= ((JSONObject) ((JSONArray) currentSystem.get("Sensors")).get(0)).get("SensorID") %>";
                            var SYSTEM_ID = "<%= currentSystem.get("SystemID") %>";
                        </script>
                        <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA1jQgEtsdQmRQtyOXp6VDwoDla2rN4FbE&sensor=false"></script>
                        <script src="js/report-map.js"></script>
            <%
            } else {
            %>
                        <h3 class="panel-title"><%= currentSystem.get("Reporting") %></h3>
                    </div>
                    <div class="panel-body">
                        <p>Reporting engine not found</p>
            <%
            }
            %>
                    </div>
                </div>
            </div>
            <%
        }
        %>
    </div>
    <%
    }
    %>
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