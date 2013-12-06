/**
 * Created with IntelliJ IDEA.
 * User: rosytucker
 * Date: 05/12/2013
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */

$(function(){
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
});