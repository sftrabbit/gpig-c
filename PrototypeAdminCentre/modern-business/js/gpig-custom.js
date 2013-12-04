/**
 * Created with IntelliJ IDEA.
 * User: rosytucker
 * Date: 04/12/2013
 * Time: 20:55
 * To change this template use File | Settings | File Templates.
 */

$(function() {
    $('#create_account_button').prop('disabled',true);
    $('.password-input input').keyup(function() {
        var password_inputs = $('.password-input');
        var admin_pass_input = $('input[name="admin_pass"]');
        var admin_pass = admin_pass_input.val();
        var confirm_admin_pass_input = $('input[name="confirm_admin_pass"]');
        var confirm_admin_pass = confirm_admin_pass_input.val();
        var button =  $('#create_account_button');
        if (admin_pass == '' && confirm_admin_pass == '') {
            password_inputs.removeClass('has-success has-error');
            button.prop('disabled', true);
        } else {
            if (admin_pass == confirm_admin_pass)   {
                password_inputs.addClass('has-success').removeClass('has-error');
                button.prop('disabled', false);
            }else{
                password_inputs.addClass('has-error').removeClass('has-success');
                button.prop('disabled', true);
            }
        }
    });
});