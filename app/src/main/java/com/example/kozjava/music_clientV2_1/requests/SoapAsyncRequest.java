package com.example.kozjava.music_clientV2_1.requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by kozjava on 22.5.17.
 */

public class SoapAsyncRequest extends AsyncTask<Object, Object, SoapObject> {


    private static final String NAMESPACE = "http://musicserver.mycloud.by/";
   // private static final String NAMESPACE = "http://192.168.100.2:3000/";
    private static final String SOAP_ACTION = "http://musicserver.mycloud.by/wsdl";
    //private static final String SOAP_ACTION = "http://192.168.100.2:3000/wsdl";
    private static final String URL = SOAP_ACTION;
    private ProgressDialog dialog;
    private Context context;
    private OnPostExecuteListener listener;
    public interface OnPostExecuteListener {
        void onSoapPostExecute(SoapObject response);

    }



    public SoapAsyncRequest(OnPostExecuteListener listener, Context context)
    {
        this.listener = listener;
        this.context = context;
        dialog = new ProgressDialog(this.context);
    }


    @Override
    protected void onPreExecute() {
        this.dialog.setTitle("SOAP запрос");
        this.dialog.setMessage("Подождите...");
        this.dialog.setCancelable(false);
        this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        this.dialog.show();
    }

    @Override
    protected void onPostExecute(SoapObject s) {

        if (listener != null)
            listener.onSoapPostExecute(s);
        if (dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected SoapObject doInBackground(Object... params) {

        SoapObject request = new SoapObject(NAMESPACE, "Request");
        if (params.length == 2)
        {
            request.addProperty("username", params[0]);
            request.addProperty("password", params[1]);
        }
        else
        {
            request.addProperty("firstName", params[0]);
            request.addProperty("lastName", params[1]);
            request.addProperty("username", params[2]);
            request.addProperty("password", params[3]);
            request.addProperty("email", params[4]);
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        try
        {
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.call(SOAP_ACTION, envelope);
            SoapObject response = (SoapObject)envelope.bodyIn;
            if (response != null)
                return response;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }


        return null;
    }
}
