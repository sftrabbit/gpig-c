var count = 0;

function buildHTMLForInput(name, type, id) {
    return 	'<tr>' +
                '<td>'+ id +'</td>' +
                '<td>' +name +'</td>' +
                '<td>' + type + '</td>' +
                '<td>' +
                    '<div class="dropdown">' +
                        '<a href="#" id="drop3" role="button" class="dropdown-toggle pull-right" data-toggle="dropdown">Action<b class="caret"></b></a>' +
                        '<ul id="menu3" class="dropdown-menu" role="menu" aria-labelledby="drop6">' +
                            '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Modify</a></li>' +
                            '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Delete</a></li>' +
                         '</ul>' +
                    '</div>'+
                '</td>' +
             '</tr>';
}

/**
 * Adds a new rule from the modal to the rules table
 */
function addNewInput() {
    var nameField = $('#add-input-name');
    var typeField = $('#add-input-type');
    var id = '164BC'+ count;
    var ruleHTML = buildHTMLForInput(nameField.val(), typeField.val(), id);
    var inputBody = $('#inputs-body');
    inputBody.html(inputBody.html() + ruleHTML);
    nameField.val('');
    count ++;
}

