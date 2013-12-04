var confusers = {
    displayContainer: 'displayContainer',
    modalContainer: 'modal_adduser',
    createduser_name: 'createduser_name',
    adduser_input_username: 'adduser_input_username',
    adduser_input_password: 'adduser_input_password',
    adduser_ok: 'adduser_ok',
    adduser_nok: 'adduser_nok',
    ifr: null,
    adduser: function() {
        console.log("Add user clicked");
        //document.getElementById(confusers.modalContainer).style.display = "none";
        if(document.getElementById(confusers.adduser_input_password).value != "" && document.getElementById(confusers.adduser_input_username).value != "") {
            document.getElementById(confusers.createduser_name).innerHTML = '<strong>Success!</strong> <i>';
            document.getElementById(confusers.createduser_name).innerHTML += document.getElementById(confusers.adduser_input_username).value;
            document.getElementById(confusers.createduser_name).innerHTML += '</i>';
            document.getElementById(confusers.createduser_name).innerHTML += ' was created';
            document.getElementById(confusers.adduser_ok).style.display = '';
        } else {
            document.getElementById(confusers.adduser_nok).style.display = '';
        }
    },
    resetform: function() {
        document.getElementById(confusers.adduser_input_password).value = "";
        document.getElementById(confusers.adduser_input_username).value = "";
    },
    nokhide:function() {
            document.getElementById(confusers.adduser_nok).style.display = 'none';
    },
    okhide:function() {
            document.getElementById(confusers.adduser_ok).style.display = 'none';
    }
}


