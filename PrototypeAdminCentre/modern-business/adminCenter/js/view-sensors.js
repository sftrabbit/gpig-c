/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var sensors = {
    init: function() {
        sensors.enginetemp.init();
        sensors.poll();
    },
    poll: function() {
        console.log("Sensor poll");
        
        sensors.enginetemp.update();
        setTimeout('sensors.poll()', '500');
    },

    enginetemp: {
        enginetemp_rand_min: 400,
        enginetemp_rand_max: 700,
        enginetemp_now: 'enginetemp_now',
        enginetemp_max: 'enginetemp_max',
        enginetemp_progressbar: 'enginetemp_progressbar',
        
        enginerpm_rand_min: 650,
        enginerpm_rand_max: 680,
        enginerpm_now: 'enginerpm_now',
        enginerpm_max: 'enginerpm_max',
        enginerpm_progressbar: 'enginerpm_progressbar',
        
        enginepower_now: 'enginepower_now',
        enginepower_max: 'enginepower_max',
        enginepower_progressbar: 'enginepower_progressbar',
        
        init: function() {
            // Engine Temperature Init
            document.getElementById(sensors.enginetemp.enginetemp_progressbar).setAttribute('aria-valuemax', sensors.enginetemp.enginetemp_rand_max);
            document.getElementById(sensors.enginetemp.enginetemp_progressbar).setAttribute('aria-valuemin', sensors.enginetemp.enginetemp_rand_min);
            
            // Engine RPM Init
        },
        update: function() {
            
            // Engine Temperature Update
            var r = Math.random();
            var rInt = Math.ceil(100*r);
            var d = sensors.enginetemp.enginetemp_rand_max-sensors.enginetemp.enginetemp_rand_min;
            var temp = Math.ceil(r*d+sensors.enginetemp.enginetemp_rand_min);
            document.getElementById(sensors.enginetemp.enginetemp_max).innerHTML = sensors.enginetemp.enginetemp_rand_max;
            document.getElementById(sensors.enginetemp.enginetemp_now).innerHTML = temp;
            document.getElementById(sensors.enginetemp.enginetemp_progressbar).setAttribute('aria-valuenow', temp);
            document.getElementById(sensors.enginetemp.enginetemp_progressbar).style.width = "" + rInt + "%";
            if (rInt > 67) {
                document.getElementById(sensors.enginetemp.enginetemp_progressbar).setAttribute('class', 'progress-bar progress-bar-danger');
            } else if (rInt > 34) {
                document.getElementById(sensors.enginetemp.enginetemp_progressbar).setAttribute('class', 'progress-bar progress-bar-warning');
            } else {
                document.getElementById(sensors.enginetemp.enginetemp_progressbar).setAttribute('class', 'progress-bar progress-bar-success');
            }
            
            // Engine RPM Update
            var r = Math.random();
            var rInt = Math.ceil(100*r);
            var d = sensors.enginetemp.enginerpm_rand_max-sensors.enginetemp.enginerpm_rand_min;
            var rpm = Math.ceil(r*d+sensors.enginetemp.enginerpm_rand_min);
            document.getElementById(sensors.enginetemp.enginerpm_max).innerHTML = sensors.enginetemp.enginerpm_rand_max;
            document.getElementById(sensors.enginetemp.enginerpm_now).innerHTML = rpm;
            document.getElementById(sensors.enginetemp.enginerpm_progressbar).setAttribute('aria-valuenow', rpm);
            document.getElementById(sensors.enginetemp.enginerpm_progressbar).style.width = "" + rInt + "%";
            if (rInt > 67) {
                document.getElementById(sensors.enginetemp.enginerpm_progressbar).setAttribute('class', 'progress-bar progress-bar-danger');
            } else if (rInt > 34) {
                document.getElementById(sensors.enginetemp.enginerpm_progressbar).setAttribute('class', 'progress-bar progress-bar-warning');
            } else {
                document.getElementById(sensors.enginetemp.enginerpm_progressbar).setAttribute('class', 'progress-bar progress-bar-success');
            }
            
            // Engine Power Update
            var dInt = Math.ceil(36+rInt/10+5*Math.random());
            document.getElementById(sensors.enginetemp.enginepower_progressbar).setAttribute('aria-valuenow', rpm);
            document.getElementById(sensors.enginetemp.enginepower_progressbar).style.width = "" + dInt + "%";
            document.getElementById(sensors.enginetemp.enginepower_now).innerHTML = Math.ceil(dInt);
            if (dInt > 45) {
                document.getElementById(sensors.enginetemp.enginepower_progressbar).setAttribute('class', 'progress-bar progress-bar-success');
            } else if (dInt > 40) {
                document.getElementById(sensors.enginetemp.enginepower_progressbar).setAttribute('class', 'progress-bar progress-bar-warning');
            } else {
                document.getElementById(sensors.enginetemp.enginepower_progressbar).setAttribute('class', 'progress-bar progress-bar-danger');
            }
            
        }
    }
}

try {
    sensors.init();
} catch(e) {
    setTimeout('sensors.init()', '500');
}