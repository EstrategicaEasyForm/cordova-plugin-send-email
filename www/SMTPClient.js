var exec = require('cordova/exec');

exports.sendMail = function (MailSettings, success, error) {
    //exec(success, error, 'SMTPClient', 'sendMail', [arg0]);
	cordova.exec(success, error, 'SMTPClient', 'sendMail', [MailSettings]);
};
