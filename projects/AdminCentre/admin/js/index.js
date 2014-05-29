$("#tab-menu li a").click(function() {
    $(this).blur();
    if (!$(this).closest("li").hasClass("active")) {
        $("#tab-menu li").removeClass("active");
        $(this).closest("li").addClass("active");
        $(".tab-content").hide();
        $("#content-" + $(this).data("tab")).fadeIn(500);
        if ($(this).data("tab") == "reports") {
            $("body").trigger("reports-show");
        } else {
            $("body").trigger("reports-hide");
        }
    }
    return false;
});


$("#form-new").on("keyup", "input", function() { // bind this using .on() so that it also applies to new items added
    if ($(this).val() == "") {
        $(this).parent("li").addClass("has-error");
    } else {
        $(this).parent("li").removeClass("has-error");
    }
});

$("#form-new").on("click", ".sensor-delete", function() { // bind this using .on() so that it also applies to new items added
    if (!$(this).hasClass("disabled")) {
        $(this).parent("li").remove();
    }
    return false;
});

$(".new-sensor").click(function() {
    if (!$(this).hasClass("disabled")) {
        $(this).parent("li").before('<li class="list-group-item sensor-item"><input type="text" value="" class="form-control sensor-name" placeholder="Enter sensor name"><a class="btn btn-danger sensor-delete" href="#"><i class="fa fa-trash-o"></i></a></li>');
        $(this).closest("ul").children(".sensor-item").last().children(".sensor-name").focus();
    }
    return false;
});

$("#form-new").submit(function() {
    var $this = $(this);
    if ($(".sensor-item", $this).hasClass("has-error")) return false;
    $("input, button, select", $this).prop("disabled", true).blur();
    $("a", $this).addClass("disabled");
    $(".buttons-group .btn-primary", $this).html('<i class="fa fa-refresh fa-spin"></i> Adding').blur();
    $(".sensor-item", $this).each(function(index, element) {
        if ($(".sensor-name", element).val() == "") {
            $(this).remove();
        }
    });
    var data = {
        "name": $("#new-name").val(),
        "sensors": [],
        "store": $("#new-store option:selected").val()
    };
    if ($("#new-reporting option:selected").val() != "none") data.reporting = $("#new-reporting option:selected").val();
    $(".sensor-item", $this).each(function(index, element) {
        data.sensors.push($(".sensor-name", element).val());
    });
    console.log(data);
    $.post("ajax-new.jsp", {"post": JSON.stringify(data)}, function success(httpdata, textStatus, jqXHR) {
        var button = $(".buttons-group .btn-primary", $this);
        button.html('<i class="fa fa-check"></i> Added').addClass("btn-success");
        setTimeout(function() { window.location = "?system="+data.name; }, 1500);
    }, "json").fail(function() {
        alert("Error: permission denied - could not write to the config file.");
        var button = $(".buttons-group .btn-primary", $this);
        button.html('<i class="fa fa-exclamation-triangle"></i> Error').addClass("btn-warning");
        setTimeout(function() { button.html('<i class="fa fa-plus-square"></i>&nbsp;&nbsp;Add').removeClass("btn-warning"); }, 1500);
    }).always(function() {
        $("input, button, select", $this).prop("disabled", false);
        $("a", $this).removeClass("disabled");
    });
    return false;
});

$("#form-new").on("reset", function() {
    $(".sensor-item", this).remove();
});


$(".sensor-params").on("keyup", "input", function() { // bind this using .on() so that it also applies to new items added
    if ($(this).val() == "") {
        $(this).parent("li").addClass("has-error");
    } else {
        $(this).parent("li").removeClass("has-error");
    }
});

$(".sensor-params").on("click", ".param-delete", function() { // bind this using .on() so that it also applies to new items added
    if (!$(this).hasClass("disabled")) {
        $(this).parent("li").removeClass("param-item").addClass("param-item-deleted");
    }
    return false;
});

$(".new-param").click(function() {
    if (!$(this).hasClass("disabled")) {
        $(this).parent("li").before('<li class="list-group-item param-item param-item-new"><input type="text" value="" class="form-control param-key"><input type="text" value="" class="form-control param-value"><a class="btn btn-danger param-delete" href="#"><i class="fa fa-trash-o"></i></a></li>');
        $(this).closest("ul").children(".param-item-new").last().children(".param-key").focus();
    }
    return false;
});

$(".sensor-params").submit(function() {
    var $this = $(this);
    if ($(".param-item", $this).hasClass("has-error")) return false;
    $("input, button", $this).prop("disabled", true).blur();
    $("a", $this).addClass("disabled");
    $(".buttons-group .btn-primary", $this).html('<i class="fa fa-refresh fa-spin"></i> Saving').blur();
    $(".param-item-deleted input", $this).prop("disabled", false);
    $(".param-item-deleted", $this).remove();
    $(".param-item-new", $this).each(function(index, element) {
        if ($(".param-key", element).val() == "" || $(".param-value", element).val() == "") {
            $(this).remove();
        }
    });
    $(".param-item-new", $this).removeClass("param-item-new");
    var data = {};
    $(".param-item", $this).each(function(index, element) {
        data[$(".param-key", element).val()] = $(".param-value", element).val();
    });
    $.post("ajax-sensor-params.jsp?system=" + $("#systems-list .dropdown-toggle").text().trim() + "&sensor=" + $this.data("system"), {"post": JSON.stringify(data)}, function success(data, textStatus, jqXHR) {
        var button = $(".buttons-group .btn-primary", $this);
        button.html('<i class="fa fa-check"></i> Saved').addClass("btn-success");
        setTimeout(function() { button.html("Save").removeClass("btn-success"); }, 1500);
    }, "json").fail(function() {
        alert("Error: permission denied - could not write to the config file.");
        var button = $(".buttons-group .btn-primary", $this);
        button.html('<i class="fa fa-exclamation-triangle"></i> Error').addClass("btn-warning");
        setTimeout(function() { button.html("Save").removeClass("btn-warning"); }, 1500);
    }).always(function() {
        $("input, button", $this).prop("disabled", false);
        $("a", $this).removeClass("disabled");
    });
    return false;
});

$(".sensor-params").on("reset", function() {
    $(".param-item-new", this).remove();
    $(".param-item-deleted", this).removeClass("param-item-deleted").addClass("param-item");
});


$("#data-store").submit(function() {
    var $this = $(this);
    $("select, button", $this).prop("disabled", true).blur();
    $(".btn-primary", $this).html('<i class="fa fa-refresh fa-spin"></i> Saving').blur();
    var data = {"store": $("#data-store-name option:selected").val()};
    $.post("ajax-datastore.jsp?system=" + $("#systems-list .dropdown-toggle").text().trim(), {"post": JSON.stringify(data)}, function success(data, textStatus, jqXHR) {
        var button = $(".btn-primary", $this);
        button.html('<i class="fa fa-check"></i> Saved').addClass("btn-success");
        setTimeout(function() { button.html("Save").removeClass("btn-success"); }, 1500);
    }, "json").fail(function() {
        alert("Error: permission denied - could not write to the config file.");
        var button = $(".btn-primary", $this);
        button.html('<i class="fa fa-exclamation-triangle"></i> Error').addClass("btn-warning");
        setTimeout(function() { button.html("Save").removeClass("btn-warning"); }, 1500);
    }).always(function() {
        $("select, button", $this).prop("disabled", false);
    });
    return false;
});


var changing = false;
$(".toggle-switch").bootstrapSwitch({
    size: "normal",
    onSwitchChange: function(event, state) {
        changing = true;
        var data = {"engines": $("#engines input[type=checkbox]:checked").map(function() { return $(this).val(); }).get()};
        $.post("ajax-engines.jsp?system=" + $("#systems-list .dropdown-toggle").text().trim(), {"post": JSON.stringify(data)}, function success(data, textStatus, jqXHR) {
            changing = false;
        }, "json").fail(function() {
            alert("Error: permission denied - could not write to the config file.");
            if (!changing) {
                $("#engines")[0].reset();
                changing = false;
            }
        });
    }
});

$(function() {
    $(window).bind("unload", function() {
        $("input, button, select").prop("disabled", false);
        if ($("#form-new")) $("#form-new")[0].reset();
        if ($("#engines")) $("#engines")[0].reset();
        if ($("#data-store")) $("#data-store")[0].reset();
    });
});
