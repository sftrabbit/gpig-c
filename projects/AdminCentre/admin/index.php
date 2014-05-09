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
                <div class="btn-group" id="systems-list">
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
            </h1>
            <hr>
        </div>
    </div>
    <?php
    if (!isset($currentSystem["unknown"])) {
    ?>
    <ul class="nav nav-tabs nav-justified" id="tab-menu">
        <li class="active"><a href="#" data-tab="sensors">Sensors</a></li>
        <li><a href="#" data-tab="engines">Engines</a></li>
    </ul>
    <div id="content-sensors" class="tab-content">
    <?php
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
    ?>
    </div>
    <div id="content-engines" class="tab-content" style="display:none;">
        <br>
        <ul class="list-group">
        <?php
        foreach ($currentSystem["Engines"] as $engine) {
            echo "<li class=\"list-group-item\">$engine</li>";
        }
        ?>
        </ul>
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
<script src="../js/jquery-1.10.2.js"></script>
<script src="../js/bootstrap.js"></script>
<script src="../js/modern-business.js"></script>
<script src="js/admin-navbar.js"></script>
<script src="js/index.js"></script>
</body>
</html>
