/*
 * Use Newtone Library (GNU LGPL License) from
 * https://developers.daum.net/services/apis/newtone
 * For voice recognition
 * 
 * Main developers: 류연희
 * Debuggers: 류연희
 */
package com.example.twiddy_ui;

import android.util.Log;
import net.daum.mf.speech.api.TextToSpeechListener;

public class TTSListener implements TextToSpeechListener {
	private DisplayEmotion parent;
	
	public TTSListener(DisplayEmotion _parent) {
		this.parent = _parent;
	}
	/* TextToSpeechListener Methods */
	@Override
	public void onError(int code, String msg) {
		Log.e("Play error " + code, msg);
	}

	@Override
	public void onFinished() {
		this.parent.handleTTSResult();
	}

}
