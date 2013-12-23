
function buildHTMLForUser(email, type) {
    return 	 '<tr>'  +
        '<td>' + email + '</td>' +
        '<td>' + type  + '</td>' +
        '<td>' +
            '<div class="dropdown pull-right">'+
                '<a href="#" id="drop1" role="button" class="dropdown-toggle" data-toggle="dropdown">Action<b class="caret"></b></a>' +
                '<ul id="menu1" class="dropdown-menu" role="menu">' +
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
function addNewUser() {
    var emailField = $('#add-user-email');
    var typeField = $('#add-user-type');
    var ruleHTML = buildHTMLForUser(emailField.val(), typeField.val());
    var userBody = $('#users-body');
    userBody.html(userBody.html() + ruleHTML);
}

