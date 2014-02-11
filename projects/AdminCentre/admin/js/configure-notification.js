function addNewNotification() {
    var event = $('#event-select').find('option:selected').text();
    var priority = $('#priority-select').find('option:selected').val();
    var output = $('#output-select').find('option:selected').text();
    var cooldown = $('#cooldown-time').val();

    var html = '<tr>'+
                '<td class="notification-' + priority + '-priority">' + event + '</td>'+
                '<td>' + output + '</td>'+
                '<td>' + cooldown + '</td>'+
                '<td>'+
                  '<div class="dropdown pull-right">'+
                    '<a href="#" id="drop1" role="button" class="dropdown-toggle" data-toggle="dropdown">Action<b class="caret"></b></a>'+
                    '<ul id="menu1" class="dropdown-menu" role="menu">'+
                      '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Edit</a></li>'+
                      '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Delete</a></li>'+
                    '</ul>'+
                  '</div>'+
                '</td>'+
              '</tr>';

    var tbody = $('#notifications-body');
    tbody.html(tbody.html() + html);
}
