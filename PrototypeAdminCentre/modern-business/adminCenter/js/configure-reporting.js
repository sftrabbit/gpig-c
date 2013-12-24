
function buildHTMLForEventReport(input, reportType) {
    return 	'<tr>' +
                '<td>'+  input +'</td>' +
                '<td>' +reportType +'</td>' +
                '<td>' +
                    '<div class="dropdown pull-right">' +
                        '<a href="#" id="drop3" role="button" class="dropdown-toggle" data-toggle="dropdown">Action<b class="caret"></b></a>' +
                        '<ul id="menu3" class="dropdown-menu" role="menu">' +
                            '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Edit</a></li>' +
                            '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Delete</a></li>' +
                        '</ul>' +
                    '</div>' +
                '</td>' +
            '</tr>';
}

/**
 * Adds a new rule from the modal to the rules table
 */
function addReportingToolForEvent() {
    var inputField = $('#add-tool-to-event-input');
    var typeField = $('#add-tool-to-event-type');
    var ruleHTML = buildHTMLForEventReport(inputField.val(), typeField.val());
    var reportingBody = $('#reporting-body');
    console.log(reportingBody.html());
    reportingBody.html(reportingBody.html() + ruleHTML);
}

/*
 * When radioToolCustom is toggled, see if we need to display or hide the
 * module upload stuff
 */
$(function() {
    $('input[name="radioTool"]').change(function() {
        if ($('#radioToolCustom').is(':checked')) {
            $(analToolUpload).css("display", "block");
        } else {
            $(analToolUpload).css("display", "none");
        }
    });
});
