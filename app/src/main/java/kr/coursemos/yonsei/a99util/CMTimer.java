package kr.coursemos.yonsei.a99util;

import android.os.CountDownTimer;

public class CMTimer extends CountDownTimer {
	protected String what=null;
	protected long mylong=0;
	public CMTimer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}
	public void setWhat(String what,long mylong){
		this.what=what;
		this.mylong=mylong;
	}
	@Override
	public void onFinish() {

	}

	@Override
	public void onTick(long millisUntilFinished) {

	}

}
