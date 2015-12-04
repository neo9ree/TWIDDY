package com.example.twiddy_ui;

import android.util.Log;
import twitter4j.Twitter;
import twitter4j.TwitterException;

enum RunningState {
	stop,
	waiting,
	/* upload related */
	startRecording,
	recording,
	askToUpload,
	answeringUpload,
	upload,
	askAgain,
	answeringAgain,
	/* mention related */
	askToRead,
	answeringRead,
	readFeed,
}

public class RunningTwiddy {
	public static String ERROR_STT = "@@ERROR-STT@@";

	public RunningState state = RunningState.waiting;
	private DisplayEmotion parent;

	private String uploadMsg = "";
	private String alarmedMsg = "";

	private int errCount = 0;

	public RunningTwiddy(DisplayEmotion _parent) {
		this.parent = _parent;
	}

	public String getStatus() {
		return runningState(this.state);
	}

	private String runningState(RunningState state) {
		switch (this.state) {
		case stop:
			return "stop";
		case waiting:
			return "waiting";
		case startRecording:
			return "startRecording";
		case recording:
			return "recording";
		case askToUpload:
			return "askToUpload";
		case answeringUpload:
			return "answeringUpload";
		case upload:
			return "upload";
		case askAgain:
			return "askAgain";
		case answeringAgain:
			return "answeringAgain";
		case askToRead:
			return "askToRead";
		case answeringRead:
			return "answeringRead";
		case readFeed:
			return "readFeed";
		}
		return "None";
	}

	private void reset() {
		this.uploadMsg = "";
		this.alarmedMsg = "";
		this.state = RunningState.waiting;
		this.errCount = 0;
	}

	public void stop() {
		this.state = RunningState.stop;
	}

	public boolean isStop() {
		return this.state == RunningState.stop;
	}

	public boolean getNewMention(String sender, String msg) {
		if (this.state == RunningState.waiting) {
			this.state = RunningState.askToRead;
			this.alarmedMsg = TextHandler.feedToSentence(msg);			
			this.parent.performTTS(sender + "님으로 부터 멘션이 도착했지 말입니다. 읽어도 되겠습니까?");
			return true;
		}
		return false;
	}

	public void handleTTSResult() {
		switch (this.state) {
		case stop:
			Log.e("state", "stop");
			break;
		case waiting:
			Log.e("state", "waiting");
			TTStransitionFromWaiting();
			break;
		case startRecording:
			Log.e("state", "startRecording");
			TTStransitionFromStartRecording();
			break;
		case askToUpload:
			Log.e("state", "askToUpload");
			TTStransitionFromAskToUpload();
			break;
		case askAgain:
			Log.e("state",  "askAgain");
			TTStransitionFromAskAgain();
			break;
		case askToRead:
			Log.e("state", "askToRead");
			TTStransitionFromAskToRead();
			break;
		case readFeed:
			Log.e("state", "readFeed");
			TTStransitionFromReadFeed();
			break;
		default:
			Log.e("ERROR", runningState(this.state) + " State does not handle the TTS");
		}
	}

	public void handleSTTResult(String msg) {
		if (!isStop() && msg.equals(ERROR_STT)) {
			Log.e("HANDLESTT", "ERROR_STT");
			if (this.errCount > 3) {
				Log.e("HANDLESTT", "reset");
				reset();
			} else {
				Log.e("HANDLESTT", "errCount++");
				this.errCount++;
			}
			this.parent.performSTT();
			return;
		}
		switch (this.state) {
		case stop:
			Log.e("state", "stop");
			break;
		case waiting:
			Log.e("state", "waiting");
			STTtransitionFromWating(msg);
			break;
		case recording:
			Log.e("state", "recording");
			STTtransitionFromRecording(msg);
			break;
		case answeringUpload:
			Log.e("state",  "answeringUpload");
			STTtransitionFromAnsweringUpload(msg);
			break;
		case answeringAgain:
			Log.e("state",  "answeringAgain");
			STTtransitionFromAnsweringAgain(msg);
			break;
		case answeringRead:
			Log.e("state",  "answeringRead");
			STTtransitionFromAnsweringRead(msg);
			break;
		default:
			Log.e("ERROR", runningState(this.state) + " State does not handle the STT msg: " + msg);
		}

	}

	public void endedUpload() {
		switch (this.state) {
		case upload:
			this.reset();
			this.parent.performTTS("업로드 완료했지 말입니다.");
			this.parent.showEnumEmotion(EnumEmotion.Happy);
			break;
		default:
			Log.e("ERROR", runningState(this.state) + " state does not handles endedUpload()");
		}
	}

	private void STTtransitionFromWating(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommandExtend(msg);
		int rand;
		switch (cmdCode) {
		case startRecording:
			this.state = RunningState.startRecording;
			this.parent.performTTS("내용을 말씀해주시지 말입니다.");
			this.parent.showEnumEmotion(EnumEmotion.Happy);
			break;
		case hi:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("반가워!");
				break;
			case 1:
				this.parent.performTTS("안녕!");
				break;
			case 2:
				this.parent.performTTS("방가 방가!");
				break;
			}			
			this.parent.showEnumEmotion(EnumEmotion.Start);
			break;
		case compliment:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("고마워!");
				break;
			case 1:
				this.parent.performTTS("이 정도 쯤이야!");
				break;
			case 2:
				this.parent.performTTS("유어 웰컴");
				break;
			}
			this.parent.showEnumEmotion(EnumEmotion.Happy);
			break;
		case detention:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("히잉 미안해");
				break;
			case 1:
				this.parent.performTTS("내가 잘못했어");
				break;
			case 2:
				this.parent.performTTS("훌쩍 훌쩍");
				break;
			}
			this.parent.showEnumEmotion(EnumEmotion.Angry);
			break;
		case who:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("나는 너의 친구 테디베어야");
				break;
			case 1:
				this.parent.performTTS("나는 자바 코드로 만들어진 트위디야. 가비지 컬렉터가 일품이지 후후");
				break;
			case 2:
				this.parent.performTTS("나를 트위디라고 불렀을 때 나는 너에게 한마리의 트위디가 되었다.");
				break;
			}
			this.parent.showEnumEmotion(EnumEmotion.Explain);
			break;
		case where:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("나는 대전에서 태어났어");
				break;
			case 1:
				this.parent.performTTS("나는 전산학프로젝트에서 태어났어");
				break;
			case 2:
				this.parent.performTTS("나는 엔원에서 태어났어");
				break;
			}			
			this.parent.showEnumEmotion(EnumEmotion.Explain);
			break;
		case what:
			rand = (int)(Math.random()*3);
			switch(rand) {
			case 0:
				this.parent.performTTS("너의 이야기를 듣고있어.");
				break;
			case 1:
				this.parent.performTTS("보면 모르니?");
				break;
			case 2:
				this.parent.performTTS("여기서 빠져나갈 방법을 찾고있어");
				break;
			}
			this.parent.showEnumEmotion(EnumEmotion.Explain);
			break;
		case yes:
		case no:
		case none:
			this.parent.performTTS("잘 못알아들었습니다.");
			this.parent.showEnumEmotion(EnumEmotion.Normal);
			break;
		
		}
	}

	private void STTtransitionFromRecording(String msg) {
		this.state = RunningState.askToUpload;
		this.uploadMsg = TextHandler.sentenceToFeed(msg);
		this.parent.performTTS(msg + " 라고 말씀하셨지 말입니다. 트위터에 올려도 되겠습니까?");
		this.parent.showEnumEmotion(EnumEmotion.Normal);
	}

	private void TTStransitionFromWaiting() {
		this.reset();
		this.parent.performSTT();
	}

	private void TTStransitionFromStartRecording() {
		this.state = RunningState.recording;
		this.parent.performSTT();
	}

	private void TTStransitionFromAskToUpload() {
		this.state = RunningState.answeringUpload;
		this.parent.performSTT();
	}

	private void TTStransitionFromAskAgain() {
		this.state = RunningState.answeringAgain;
		this.parent.performSTT();
	}

	private void TTStransitionFromAskToRead() {
		this.state = RunningState.answeringRead;
		this.parent.performSTT();
	}

	private void TTStransitionFromReadFeed() {
		this.reset();
		this.parent.performSTT();
	}

	private void STTtransitionFromAnsweringUpload(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case yes:
			this.parent.uploadTweet(this.uploadMsg);
			this.state = RunningState.upload;				
			break;		
		case no:
			this.reset();
			this.state = RunningState.askAgain;
			this.parent.performTTS("다른 하실 말씀 있으십니까?");
			break;
		case startRecording:
		case none:
			this.state = RunningState.askToUpload;
			this.parent.performTTS("다시 말씀해 주세요.");
			break;
		}
	}

	private void STTtransitionFromAnsweringAgain(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case yes:
			this.state = RunningState.startRecording;
			this.parent.performTTS("뭐라고 올리지 말입니까?");
			break;		
		case no:		
			this.reset();
			this.parent.performTTS("올리지 않겠지 말입니다.");
			this.parent.showEnumEmotion(EnumEmotion.Normal);
			break;
		case startRecording:
		case none:
			this.state = RunningState.askAgain;
			this.parent.performTTS("다른 하실 말씀 있으십니까?");
			break;			
		}
	}

	private void STTtransitionFromAnsweringRead(String msg) {
		EnumCommand cmdCode = TextHandler.checkCommand(msg);
		switch (cmdCode) {
		case yes:
			this.state = RunningState.readFeed;
			this.parent.showEmotion(this.alarmedMsg);
			this.parent.performTTS(this.alarmedMsg);
			break;		
		case no:
			this.reset();
			this.parent.performTTS("읽지 않겠지 말입니다.");
			break;
		case startRecording:
		case none:
			this.state = RunningState.askToRead;
			this.parent.performTTS("다시 말씀해주시지 말입니다.");
			break;
		}

	}
}
