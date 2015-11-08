package com.example.twiddy_ui;

import android.util.Log;
import net.daum.mf.speech.api.TextToSpeechListener;

public class TTSListener implements TextToSpeechListener {
	private VoiceActivity parent;
	
	public TTSListener(VoiceActivity _parent) {
		this.parent = _parent;
	}
	/* TextToSpeechListener Methods */
	@Override
	public void onError(int code, String msg) {
		Log.e("Play error " + code, msg);
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub	
	}

}