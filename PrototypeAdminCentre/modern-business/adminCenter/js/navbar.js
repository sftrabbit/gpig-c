function createli (hrefLink, hrefName) {
    return '<li><a href="' + hrefLink + '">' + hrefName + '</a>';
}

function createbutton(hrefLink, hrefName){
    return '<li><a href="' + hrefLink + '">' +
        '<button  type="submit" class= "btn btn-less-padding">' + hrefName +'</button></a></li>'
}

$(function() {
    $('#nbarstart').html($('#nbarstart').html() + createli('../index.html', 'GPIG-C'));
    $('#nbarstart').html($('#nbarstart').html() + createli('../services.html', 'Services'));
    $('#nbarstart').html($('#nbarstart').html() + createli('../contact.html', 'Contact'));
    $('#nbarstart').html($('#nbarstart').html() + createli('../guide.html', 'Documentation'));
    $('#nbarstart').html($('#nbarstart').html() + createbutton('../login.html', 'Log Out'));
} );
	


