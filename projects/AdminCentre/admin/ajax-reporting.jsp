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

String systemId = "";
if (request.getParameter("system") != null) {
    systemId = request.getParameter("system").toString();
}

String reportType = "";
if (request.getParameter("post") != null) {
    JSONObject post = (JSONObject) JSONValue.parse(request.getParameter("post"));
    if (!post.isEmpty() && post.get("reporting") != null) {
        reportType = (String) post.get("reporting");
    }
}

JSONArray systems = (JSONArray) configJson.get("Systems");
Iterator<JSONObject> iter = systems.iterator();
while (iter.hasNext()) {
    JSONObject system = (JSONObject) iter.next();
    if (((String) system.get("SystemID")).equals(systemId)) {
        if (reportType.equals("")) {
            system.remove("Reporting");
        } else {
            system.put("Reporting", reportType);
        }
        break;
    }
}

try {
    FileWriter file = new FileWriter(sysConfigFilePath);
    file.write(configJson.toJSONString());
    file.flush();
    file.close();
    response.setContentType("text/json");
    JSONArray output = new JSONArray();
    output.add(reportType);
    out.write(output.toJSONString());
} catch (Exception e) {
    response.setStatus(response.SC_FORBIDDEN); 
}
%>