package com.cordova.smtp.client;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class send an email from JavaScript.
 */
public class SMTPClient extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		try {
			JSONObject json = new JSONObject(args.getString(0));
			Mail m = new Mail(json.getString("smtpUserName"), json.getString("smtpPassword"));
			String[] toArr = json.getString("emailTo").split(",");
			String emailCC = json.optString("emailCC");
			String[] ccArr = (emailCC.isEmpty()) ? null : emailCC.split(",");
			m.set_to(toArr);
			m.set_cc(ccArr);
			m.set_host(json.getString("smtp"));
			m.set_from(json.getString("emailFrom"));
			m.set_subject(json.getString("subject") + "V1");
			
			JSONArray attachments = json.getJSONArray("attachments");
			String msgAttachs = "";
			String message = "";
			if (attachments != null) {
				msgAttachs = "<br/>Archivos Adjuntos<br/><ui>";
				for (int i = 0; i < attachments.length(); i++) {
					String filename = attachments.getString(i);
					try {
						byte[] uint8array = json.getString("uint8array").getBytes();
						m.addAttachmentArray(filename,uint8array);
						msgAttachs += "<li>" + filename + "</li>";	
						msgAttachs += "<li>[" + json.getString("uint8array") + "]</li>";
					}catch(Exception ex) {
						message = "<li> Error agregando el archivo : " + filename + "</li>";	
						if(ex.getCause() != null && ex.getCause().getMessage().length() > 0) message = ex.getCause().getLocalizedMessage();            
						if(ex.getCause() != null && ex.getCause().getLocalizedMessage().length() > 0) message = ex.getCause().getLocalizedMessage();
						if(ex.getLocalizedMessage().length() > 0) message = ex.getLocalizedMessage();
						if(ex.getMessage().length() > 0) message = ex.getMessage();
						msgAttachs += "<li style='color:red;'> Err  : " + message + "</li>";
					}
					
					try {
						byte[] uint8array = json.getString("binaryArray").getBytes();
						m.addAttachmentArray(filename,uint8array);
						msgAttachs += "<li>" + filename + "</li>";	
						msgAttachs += "<li>[" + json.getString("binaryArray") + "]</li>";
					}catch(Exception ex) {
						message = "<li> Error agregando el archivo : " + filename + "</li>";	
						if(ex.getCause() != null && ex.getCause().getMessage().length() > 0) message = ex.getCause().getLocalizedMessage();            
						if(ex.getCause() != null && ex.getCause().getLocalizedMessage().length() > 0) message = ex.getCause().getLocalizedMessage();
						if(ex.getLocalizedMessage().length() > 0) message = ex.getLocalizedMessage();
						if(ex.getMessage().length() > 0) message = ex.getMessage();
						msgAttachs += "<li style='color:red;'> Err  : " + message + "</li>";
					}
					
					try {
						String dataDirectory = json.getString("dataDirectory");
						m.addAttachment(filename,dataDirectory);
						msgAttachs += "<li style='color:red;'> directory  : " + dataDirectory + "</li>";
					}catch(Exception err) {
						message = "<li> Error agregando el archivo : " + filename + "</li>";	
						if(err.getCause() != null && err.getCause().getMessage().length() > 0) message = err.getCause().getLocalizedMessage();            
						if(err.getCause() != null && err.getCause().getLocalizedMessage().length() > 0) message = err.getCause().getLocalizedMessage();
						if(err.getLocalizedMessage().length() > 0) message = err.getLocalizedMessage();
						if(err.getMessage().length() > 0) message = err.getMessage();
						msgAttachs += "<li style='color:red;'> Err  : " + message + "</li>";
					}
					
					try {
						String dataDirectory = json.getString("dataDirectory").replace("file://","");
						m.addAttachment(filename,dataDirectory);
						msgAttachs += "<li style='color:red;'> directory  : " + dataDirectory + "</li>";
					}catch(Exception err) {
						message = "<li> Error agregando el archivo : " + filename + "</li>";	
						if(err.getCause() != null && err.getCause().getMessage().length() > 0) message = err.getCause().getLocalizedMessage();            
						if(err.getCause() != null && err.getCause().getLocalizedMessage().length() > 0) message = err.getCause().getLocalizedMessage();
						if(err.getLocalizedMessage().length() > 0) message = err.getLocalizedMessage();
						if(err.getMessage().length() > 0) message = err.getMessage();
						msgAttachs += "<li style='color:red;'> Err  : " + message + "</li>";
					}
				}
				msgAttachs += "</ui><br/> Total adjuntos : " + attachments.length() + "<br/>";
			}
			else {
				msgAttachs = "<br/>No existen archivos Adjuntos<br/>";
			}
			m.set_body( json.getString("textBody") + msgAttachs);
			m.send();
			callbackContext.success(msgAttachs);
			return true;
		
        } catch (Exception error) {
			String message = "Error enviando el correo ";
            if(error.getCause() != null && error.getCause().getMessage().length() > 0) message += error.getCause().getLocalizedMessage();            
            if(error.getCause() != null && error.getCause().getLocalizedMessage().length() > 0) message += error.getCause().getLocalizedMessage();
            if(error.getLocalizedMessage().length() > 0) message += error.getLocalizedMessage();
            if(error.getMessage().length() > 0) message += error.getMessage();
			callbackContext.error(message);
			return false;
        }
    }
}
