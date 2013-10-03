package es.ulpgc.titulospropios.gestion.ubay;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.app.Application;

public class GlobalState extends Application{

		private CookieStore cookieStore;
		private HttpContext localContext;
		private DefaultHttpClient client;
		private String base_url;
		private boolean role;
		
		public String getTargetAddress(){
			return base_url;
		}
		public CookieStore getCookieStore(){
			return cookieStore;
		}
		public HttpContext getLocalContext(){
			return localContext;
		}
		public DefaultHttpClient getHttpClient(){
			return client;
		}
		public boolean getRole(){
			return this.role;
		}
		public void setCookieStore(CookieStore cs){
			this.cookieStore = cs;
		}
		public void setLocalContext(HttpContext lc){
			this.localContext = lc;
		}
		public void setHttpClient(DefaultHttpClient hc){
			this.client = hc;
		}
		public void setRole(boolean role){
			this.role = role;
		}
		public void setBaseAddress(String base_url){
			this.base_url = base_url;
		}
}


