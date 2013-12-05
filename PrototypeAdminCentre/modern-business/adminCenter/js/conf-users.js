var confusers = {
    displayContainer: 'displayContainer',
    modalContainer: 'modal_adduser',
    created_email: 'created_email',
    adduser_input_email: 'adduser_input_email',
    adduser_ok: 'adduser_ok',
    adduser_nok: 'adduser_nok',
    ifr: null,
    adduser: function() {
        console.log("Add user clicked");
        //document.getElementById(confusers.modalContainer).style.display = "none";
        if(document.getElementById(confusers.adduser_input_email).value != "") {
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
        document.getElementById(confusers.adduser_input_email).value = "";
    },
    nokhide:function() {
            document.getElementById(confusers.adduser_nok).style.display = 'none';
    },
    okhide:function() {
            document.getElementById(confusers.adduser_ok).style.display = 'none';
    }
}


