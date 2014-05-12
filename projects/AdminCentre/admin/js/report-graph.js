var initialised = false;
var chart;
var options = {
    scaleOverride: true,
    scaleSteps: STEPS_SCALE
};
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

$("body").on("reports-show", function() {
    if (!initialised) {
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
        setInterval(refresh, REFRESH_SECONDS * 1000);
        initialised = true;
    } else {
        refresh();
    }
});

function refresh() {
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
