/**
 * Created with IntelliJ IDEA.
 * User: rosytucker
 * Date: 05/12/2013
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */


function buildHTMLforRule(ruleText){
    return '<tr>'+
       ' <td class="rule">' + ruleText + '</td>' +
        '<td>'                                +
            '<div class="dropdown pull-right">' +
                '<a href="#" id="drop3" role="button" class="dropdown-toggle" data-toggle="dropdown">Action<b class="caret"></b></a>' +
                '<ul id="menu3" class="dropdown-menu" role="menu">' +
                   ' <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Edit</a></li> ' +
                    '<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Delete</a></li>' +
               ' </ul> '+
            '</div>' +
        '</td>' +
    '</tr>';
}

/**
 * Adds a new rule from the modal to the rules table
 */
function addNewRule(){
    var input = $('#input-select');
    var operator = $('#comparison-select');
    var event = $('#event-select');
    var value = $('#comp-value');
    var text1 =input.find('option:selected').text();
    var text2 =operator.find('option:selected').text();
    var text3 =value.val();
    var text4 =event.find('option:selected').text();
    var ruleText = 'When Input '+text1 + ' Is '+ text2 + ' '+ text3 + " Trigger Event "+ text4;
    var ruleHTML = buildHTMLforRule(ruleText);
    var rulesBody = $('#rules-body');
    rulesBody.html(rulesBody.html() + ruleHTML);
    highlightRules();
}

/**
 * Syntax Highlighting for rules
 */
function highlightRules(){
    var rules = $('.rule');
    var newContent = "";
    var currentRule;
    var words;

    for(var j = 0; j < rules.length; j++){

        newContent = "";
        currentRule = rules.eq(j);
        words = currentRule.text().split(" ");

        for(var i= 0 ; i < words.length; i++){
            if(words[i] == 'When' || words[i] == 'Input' || words[i] == 'Is' ||
                words[i] == 'Trigger' || words[i] == 'Event')
                words[i] =   '<span class="rule-keyword">' + words[i] + '</span>';
            else
                words[i] =   '<span class="rule-non-keyword">' + words[i] + '</span>';
            newContent += words[i] + ' ';
        }
        currentRule.html(newContent);
    }
}

$(function(){
    highlightRules();
});

/*
 * When radioToolCustom is toggled, see if we need to display or hide the
 * module upload stuff
 */
$(function(){
    $('input[name="radioTool"]').change(function() {
        if($('#radioToolCustom').is(':checked')) {
            $(analToolUpload).css("display", "block");
        } else {
            $(analToolUpload).css("display", "none");
        }
    });
});