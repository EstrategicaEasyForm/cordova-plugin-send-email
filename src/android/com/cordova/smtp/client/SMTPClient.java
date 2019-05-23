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
			m.set_subject(json.getString("subject"));
			
			JSONArray attachments = json.getJSONArray("attachments");
			String msgAttachs = "";
			if (attachments != null) {
				for (int i = 0; i < attachments.length(); i++) {
					JSONObject pdfJson = attachments.getString(i);
					//if (!pdfJson.isNull("filename")) {
						File f = new File(pdfJson.getString("dataDirectory") + pdfJson.getString("filename"));
						msgAttachs += f.isFile() ? "<br/> existe" : " <br/> no existe";
						msgAttachs += "el archivo " + pdfJson.getString("filename") + " en el directorio " + pdfJson.getString("dataDirectory");
						
						m.addAttachment(pdfJson.getString("filename"),pdfJson.getString("dataDirectory"));
					//}
				}
				m.set_body(json.getString("textBody") + msgAttachs + "<br/> Total adjuntos : " + attachments.length());
			}
			else { 
				m.set_body(json.getString("textBody") + "No existen archivos Adjuntos");
			}
			m.send();
			callbackContext.success(pdfJson.getString("dataDirectory") + pdfJson.getString("filename") + ' ' + msg);
			return true;
		
        } catch (Exception ex) {
			String message = "Error enviando el correo ";
            if(ex.getCause() != null && ex.getCause().getMessage().length() > 0) message = ex.getCause().getLocalizedMessage();            
            if(ex.getCause() != null && ex.getCause().getLocalizedMessage().length() > 0) message = ex.getCause().getLocalizedMessage();
            if(ex.getLocalizedMessage().length() > 0) message = ex.getLocalizedMessage();
            if(ex.getMessage().length() > 0) message = ex.getMessage();
            callbackContext.error(message);
			return false;
        }
    }
}
