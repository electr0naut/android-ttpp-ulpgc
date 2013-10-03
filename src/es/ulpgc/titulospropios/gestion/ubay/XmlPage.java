package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class XmlPage {
	private Context mContext;

	public XmlPage(Context mContext) {
		this.mContext = mContext;
	}

	public String getPage(String target_addr) throws IOException {
		GlobalState gs = (GlobalState) mContext.getApplicationContext();
		HttpContext localContext = gs.getLocalContext();
		DefaultHttpClient client = gs.getHttpClient();

		HttpGet httpGet = new HttpGet(target_addr);
		HttpResponse responseGET = null;
		HttpEntity getEntity = null;
		String response = null;
		try {
			responseGET = client.execute(httpGet, localContext);
			getEntity = responseGET.getEntity();
			response = EntityUtils.toString(getEntity);
			getEntity.consumeContent();
			Log.d("getClass:", mContext.getClass().toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	}
}
