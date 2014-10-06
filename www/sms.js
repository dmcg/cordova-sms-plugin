var smsExport = {};

smsExport.sendMessage = function(messageInfo, successCallback, errorCallback) {
    if (messageInfo == null || typeof messageInfo !== 'object') {
    
        if (errorCallback) {
            errorCallback({
                code: "INVALID_INPUT",
                message: "Invalid Input"
            });
        }
       
        return;
    }
           
    var phoneNumber = messageInfo.phoneNumber;
    var textMessage = messageInfo.textMessage || "Default Text from SMS plugin";
    cordova.exec(successCallback, errorCallback, "Sms", "sendMessage", [phoneNumber, textMessage]);
};

module.exports = smsExport;
