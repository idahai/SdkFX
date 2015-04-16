package sdk.fx;

public class PictureInformation {
	private String piclevel = "";
	private String picUrl = "";
	private String appurl = "";
	private String appname = "";
	private boolean clicked = false;
	private long clickTime = 0L;
	
	public void setClickTime(long time){
		clickTime = time;
	}
	
	public long getClickTime(){
		return clickTime;
	}
	
	public void setPicUrl(String url){
		this.picUrl = url;
	}
	
	public void setPicLevel(String elems) {
		piclevel = elems;
	}

	public void setAppUrl(String url) {
		appurl = url;
	}
	
	public String getPicUrl(){
		return this.picUrl;
	}
	
	public String getPicLevel() {
		return piclevel;
	}

	public String getAppUrl() {
		return appurl;
	}

	
	public void setAppName(String name){
		this.appname = name;
	}
	
	public String getAppName(){
		return appname;
	}
	
	public void setClicked(boolean b){
		clicked = b;
	}
	
	public boolean getClicked(){
		return clicked;
	}
}
