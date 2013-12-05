var navbar = {
	init: function() {
		try {
			navbar.setupNavbar();
		} catch(e) {
			setTimeout('navbar.init()', 500);
		}
	},
	
	setupNavbar: function() {
		document.getElementById('nbarstart').innerHTML = document.getElementById('nbarstart').innerHTML + navbar.createli('services.html', 'Services');
		document.getElementById('nbarstart').innerHTML = document.getElementById('nbarstart').innerHTML + navbar.createli('contact.php', 'Contact');
		document.getElementById('nbarstart').innerHTML = document.getElementById('nbarstart').innerHTML + navbar.createli('guide.html', 'Documentation');
        document.getElementById('nbarstart').innerHTML = document.getElementById('nbarstart').innerHTML + navbar.createbutton('../login.html', 'Log Out');
    },
	
	createli: function(hrefLink, hrefName) {
		console.log("Adding link: " + hrefName + " => " + hrefLink);
		return '<li><a href="' + hrefLink + '">' + hrefName + '</a>';
	},

    createbutton: function(hrefLink, hrefName){
        console.log("Adding link: " + hrefName + " => " + hrefLink);
        return '<li><a href="' + hrefLink + '"><button  type="submit" class= "btn btn-primary btn-less-padding">' + hrefName +'</button></a></li>'
    }
}
navbar.init();