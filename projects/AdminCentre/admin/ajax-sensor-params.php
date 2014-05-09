<?php
$configFile = file_get_contents("RegisteredSystems.config");
$configJson = json_decode($configFile, true);
$systemId = (string)$_GET["system"];
$sensorId = (string)$_GET["sensor"];
foreach ($configJson["Systems"] as &$system) {
    if ($system["SystemID"] == $systemId) {
        foreach ($system["Sensors"] as &$sensor) {
            if ($sensor["SensorID"] == $sensorId) {
                if (empty($_POST)) {
                    unset($sensor["Params"]);
                } else {
                    $sensor["Params"] = $_POST;
                }
                break;
            }
        }
        break;
    }
}
$save = file_put_contents("RegisteredSystems.config", json_encode($configJson), LOCK_EX);
if ($save) {
    print_r($_POST);
} else {
    header('HTTP/1.0 403 Forbidden');
}
?>