package com.cordova.smtp.client;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import org.apache.cordova.CordovaResourceApi;
import android.net.Uri;

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
			
			
			String dataDirectory = json.getString("dataDirectory");
			JSONArray attachments = json.getJSONArray("attachments");
			String msgAttachs = "";
			String message = "";
			String fileUri = "";
			if (attachments != null) {
				msgAttachs = "<br/>Archivos Adjuntos<br/><ui>";
				for (int i = 0; i < attachments.length(); i++) {
					String filename = attachments.getString(i);
					fileUri = dataDirectory + filename;
					
					try {
						CordovaResourceApi resourceApi = webView.getResourceApi();
						Uri uri = resourceApi.remapUri(Uri.parse(fileUri));
						fileUri = this.stripFileProtocol(uri.toString());
						msgAttachs += "<li> uri.toString()= " + uri.toString() + "..... fileUri "+fileUri+"</li>";
					} catch (Exception e) {
						msgAttachs += "<li> error concatenando URI"+fileUri+"</li>";
					}
					
					File file = new File(fileUri);
					if (file.exists()) {
						m.addAttachment(filename,fileUri);
						msgAttachs += "<li>" + filename + "</li>";
					}
					else {
						msgAttachs += "<li style='error'> No se pudo adjuntar el archivo " + fileUri + "</li><br/>";
					}
				}
				msgAttachs += "</ui><br/> Total adjuntos : " + attachments.length() + "<br/>";
			}
			else {
				msgAttachs = "<br/>No existen archivos Adjuntos<br/>";
			}
			m.set_body( json.getString("textBody") + "</br>" + msgAttachs);
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
	
	private String stripFileProtocol(String uriString) {
		if (uriString.startsWith("file://")) {
			uriString = uriString.substring(7);
		} else if (uriString.startsWith("content://")) {
			uriString = uriString.substring(10);
		}
		return uriString;
	}
}
