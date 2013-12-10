var confusers = {
    displayContainer: 'displayContainer',
    modalContainer: 'modal_adduser',
    createduser_name: 'createduser_name',
    adduser_input_username: 'adduser_input_username',
    adduser_input_password: 'adduser_input_password',
    adduser_input_email: 'adduser_input_email',
    adduser_ok: 'adduser_ok',
    adduser_nok: 'adduser_nok',
    adduser_input_typeselected: 'adduser_input_typeselected',
    adduser_type_selected: 'adduser_type_selected',
    ifr: null,
    adduser: function() {
        console.log("Add user clicked");
        //document.getElementById(confusers.modalContainer).style.display = "none";
        if (document.getElementById(confusers.adduser_input_email).value != "") {
            document.getElementById(confusers.created_email).innerHTML = '<strong>Success!</strong> <i>';
            document.getElementById(confusers.created_email).innerHTML += document.getElementById(confusers.adduser_input_email).value;
            document.getElementById(confusers.created_email).innerHTML += '</i>';
            document.getElementById(confusers.created_email).innerHTML += ' was created';
            document.getElementById(confusers.adduser_ok).style.display = '';
        } else {
            document.getElementById(confusers.adduser_nok).style.display = '';
        }
    },
    resetform: function() {
        document.getElementById(confusers.adduser_input_username).value = "";
        document.getElementById(confusers.adduser_input_typeselected).value = "";
        document.getElementById(confusers.adduser_type_selected).value = "User";
        document.getElementById(confusers.adduser_input_email).value = "";
    },
    nokhide: function() {
            document.getElementById(confusers.adduser_nok).style.display = 'none';
    },
    okhide: function() {
            document.getElementById(confusers.adduser_ok).style.display = 'none';
    },
    hover: function(object) {
        object.style.background = "#f3f3f3";
    },
    dehover: function(object) {
        object.style.background = "";
    },
    onclickAdmin: function(object) {
        console.log("Admin selected");
        document.getElementById(confusers.adduser_type_selected).innerText = object.innerText;
        document.getElementById(confusers.adduser_input_typeselected).value = object.innerText;
    },
    onclickUser: function(object) {
        console.log("User selected");
        document.getElementById(confusers.adduser_type_selected).innerText = object.innerText;
        document.getElementById(confusers.adduser_input_typeselected).value = object.innerText;
    }
}
