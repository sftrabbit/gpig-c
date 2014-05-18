<%@ page import="java.io.File,java.io.FileReader,java.io.FileWriter,java.util.Iterator,org.json.simple.parser.JSONParser,org.json.simple.JSONArray,org.json.simple.JSONObject,org.json.simple.JSONValue" %>
<%
String filePath = new File("").getAbsolutePath().concat("/res/config/RegisteredSystems.config");
FileReader configFile = new FileReader(filePath);

JSONParser jsonParser = new JSONParser();
JSONObject configJson = (JSONObject) jsonParser.parse(configFile);

String systemId = "";
if (request.getParameter("system") != null) {
    systemId = request.getParameter("system").toString();
}

JSONArray toWrite = new JSONArray();
if (request.getParameter("post") != null) {
    JSONObject post = (JSONObject) JSONValue.parse(request.getParameter("post"));
    if (!post.isEmpty() && post.get("engines") != null) {
        toWrite = (JSONArray) post.get("engines");
    }
}

JSONArray systems = (JSONArray) configJson.get("Systems");
Iterator<JSONObject> iter = systems.iterator();
while (iter.hasNext()) {
    JSONObject system = (JSONObject) iter.next();
    if (((String) system.get("SystemID")).equals(systemId)) {
        system.put("Engines", toWrite);
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