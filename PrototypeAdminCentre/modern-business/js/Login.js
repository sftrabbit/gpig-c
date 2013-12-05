/**
 * Created with IntelliJ IDEA.
 * User: rosytucker
 * Date: 05/12/2013
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */


/**
 * Account can only login when username and password fields are non empty
 */
$(function() {
    $('#login_button').prop('disabled',true);
    var password_input = $('#password');
    var email_input = $('#email');
    var button =  $('#login_button');
    password_input.keyup(function() {
        check();
    });
    email_input.keyup(function() {
        check();
    });

    function check() {
        if (password_input.val() == '' || email_input.val() == '') {
            button.prop('disabled', true);
        } else {
            button.prop('disabled', false);
        }
    }
});

