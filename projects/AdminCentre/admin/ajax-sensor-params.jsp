<%@ page import="com.gpigc.core.FileUtils,java.io.File,java.io.FileReader,java.io.FileWriter,java.util.Iterator,org.json.simple.parser.JSONParser,org.json.simple.JSONArray,org.json.simple.JSONObject,org.json.simple.JSONValue" %>
<%
FileReader configFile = new FileReader(FileUtils.getExpandedFilePath("res/config/RegisteredSystems.config"));

JSONParser jsonParser = new JSONParser();
JSONObject configJson = (JSONObject) jsonParser.parse(configFile);

String sensorId = "";
if (request.getParameter("sensor") != null) {
    sensorId = request.getParameter("sensor").toString();
}

String systemId = "";
if (request.getParameter("system") != null) {
    systemId = request.getParameter("system").toString();
}

JSONObject toWrite = new JSONObject();
if (request.getParameter("post") != null) {
    toWrite = (JSONObject) JSONValue.parse(request.getParameter("post"));
}

JSONArray systems = (JSONArray) configJson.get("Systems");
Iterator<JSONObject> iter = systems.iterator();
while (iter.hasNext()) {
    JSONObject system = (JSONObject) iter.next();
    if (((String) system.get("SystemID")).equals(systemId)) {
        JSONArray sensors = (JSONArray) system.get("Sensors");
        Iterator<JSONObject> iter2 = sensors.iterator();
        while (iter2.hasNext()) {
            JSONObject sensor = (JSONObject) iter2.next();
            if (((String) sensor.get("SensorID")).equals(sensorId)) {
                if (toWrite.isEmpty()) {
                    sensor.remove("Params");
                } else {
                    sensor.put("Params", toWrite);
                }
                break;
            }
        }
        break;
    }
}

try {
    FileWriter file = new FileWriter(filePath);
    file.write(configJson.toJSONString());
    file.flush();
    file.close();
    response.setContentType("text/json");
    out.write(toWrite.toJSONString());
} catch (Exception e) {
    response.setStatus(response.SC_FORBIDDEN); 
}
%>