var contact = {
	contactform: "#contactform",
	contactform_alert: "#contactform_alert",
	contactform_alert_e: "#contactform_alert_e",
	contactform_input_email: "#contactform_input_email",
	contactform_input_message: "#contactform_input_message",
	submit: function() {
		if($(contact.contactform_input_email).val() == "" || $(contact.contactform_input_message).val() == "") {
			$(contact.contactform_alert_e).css('display', '');
		} else {
			$(contact.contactform).css('display', 'none');
			$(contact.contactform_alert).css('display', '');
			$(contact.contactform_alert_e).css('display', 'none');
		}
	},
	test: function() {
		console.log(document.getElementById(contact.contactform_input_email));
		console.log(document.getElementById(contact.contactform_input_message));
	}
}