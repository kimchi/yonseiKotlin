package kr.coursemos.yonsei.a99util;



public class CMThread extends Thread {
	protected CMCommunication CMcommu=null;
	protected StringHashMap shm_param=null;
	protected String page=null;
	protected String what=null;
	protected String method=null;
	protected boolean isalive=true;
	public CMThread(String page, StringHashMap shm_param, String what, String method) {
		this.page=page;
		this.shm_param=shm_param;
		this.what=what;
		this.method=method;
	}
	public CMThread() {
		
	}
}
