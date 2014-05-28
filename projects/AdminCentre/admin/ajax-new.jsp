<%@ page import="com.gpigc.core.Main,com.gpigc.core.Config,java.io.File,java.io.FileReader,java.io.FileWriter,java.util.Iterator,org.json.simple.parser.JSONParser,org.json.simple.JSONArray,org.json.simple.JSONObject,org.json.simple.JSONValue" %>
<%
Config coreConfig = new Config(Main.CONFIG_NAME);

String sysConfigFilePath = System.getProperty("gpigc.configfile");
if (sysConfigFilePath == null) {
    sysConfigFilePath = coreConfig.getConfigFile("RegisteredSystems.config").toString();
}
FileReader configFile = new FileReader(sysConfigFilePath);

JSONParser jsonParser = new JSONParser();
JSONObject configJson = (JSONObject) jsonParser.parse(configFile);

JSONObject newData = new JSONObject();
if (request.getParameter("post") != null) {
    newData = (JSONObject) JSONValue.parse(request.getParameter("post"));
}
JSONObject toWrite = new JSONObject();
toWrite.put("SystemID", newData.get("name"));
JSONArray sensors = (JSONArray) newData.get("sensors");
if (!sensors.isEmpty()) {
    JSONArray sensorsArray = new JSONArray();
    Iterator<JSONObject> iter = sensors.iterator();
    while (iter.hasNext()) {
        JSONObject newSensor = new JSONObject();
        newSensor.put("SensorID", iter.next());
        sensorsArray.add(newSensor);
    }
    toWrite.put("Sensors", sensorsArray);
} else {
    toWrite.put("Sensors", sensors);
}
toWrite.put("Engines", new JSONArray());
toWrite.put("DatastoreGateway", newData.get("store"));
if (newData.get("reporting") != null) toWrite.put("Reporting", newData.get("reporting"));

JSONArray systems = (JSONArray) configJson.get("Systems");
systems.add(toWrite);

try {
    FileWriter file = new FileWriter(sysConfigFilePath);
    file.write(configJson.toJSONString());
    file.flush();
    file.close();
    response.setContentType("text/json");
    out.write(toWrite.toJSONString());
} catch (Exception e) {
    response.setStatus(response.SC_FORBIDDEN); 
}
%>