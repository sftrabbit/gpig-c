
function buildHTMLForEventReport(event, reportType) {
    return 	'<tr>' +
                '<td>'+  event +'</td>' +
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
    var eventField = $('#add-tool-to-event-event');
    var typeField = $('#add-tool-to-event-type');
    var ruleHTML = buildHTMLForEventReport(eventField.val(), typeField.val());
    var reportingBody = $('#reporting-body');
    console.log(reportingBody.html());
    reportingBody.html(reportingBody.html() + ruleHTML);
}
