/*
 *  Copyright 2016 Zoraida Callejas, Michael McTear and David Griol
 *
 *  This file is part of the Conversandroid Toolkit, from the book:
 *  The Conversational Interface, Michael McTear, Zoraida Callejas and David Griol
 *  Springer 2016 <https://github.com/zoraidacallejas/ConversationalInterface/>
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>. 
 */

package conversandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import conversandroid.richasr.R;

/**
 * RichASR: App with a rich control of the ASR using the SpeechRecognizer class
 * and implementing the RecognitionListener interface
 * (it is an improvement over the SimpleASR app)
 * 
 * Simple demo in which the user speaks and the recognition results
 * are showed in a list along with their confidence values
 *
 * @author Zoraida Callejas, Michael McTear, David Griol
 * @version 3.0, 09/25/18
 *
 */

public class RichASR extends Activity implements RecognitionListener, android.speech.tts.TextToSpeech.OnInitListener{


	private SpeechRecognizer myASR;

	// Default values for the language model and maximum number of recognition results
	// They are shown in the GUI when the app starts, and they are used when the user selection is not valid
	private final static int DEFAULT_NUMBER_RESULTS = 10;
	private final static String DEFAULT_LANG_MODEL = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;

	private int numberRecoResults = DEFAULT_NUMBER_RESULTS; 
	private String languageModel = DEFAULT_LANG_MODEL;

	private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 22;
	private final static String LOGTAG = "RICHASR";

    private long startListeningTime = 0; // To skip errors (see onError method)

	private final static int TTS_DATA_CHECK = 12;    // Request code to identify the intent that looks for a TTS Engine in the device

	private String language;

	private TextToSpeech mytts;

	/**
	 * Sets up the activity initializing the GUI and the ASR
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.richasr);

        //Initialize ASR
        initASR();
		
		//Shows in the GUI the default values for the language model and the maximum number of recognition results
		showDefaultValues(); 

		setSpeakButton();

		language = "ES";
		mytts.setLanguage(new Locale(language));

		// Invoke the method to initialize text to speech
		initTTS();
	}

	/* ***************************************************************************************
	 MANAGE ASR
	 */

	/**
	 * Creates the speech recognizer instance if it is available
	 * */
	public void initASR() {

		// find out whether speech recognition is supported
		List<ResolveInfo> intActivities = this.getPackageManager().queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        ////To avoid running on a simulated device
        //if("generic".equals(Build.BRAND.toLowerCase())){
        //	Toast toast = Toast.makeText(getApplicationContext(),"Virtual device: "+R.string.asr_notsupported, Toast.LENGTH_SHORT);
        //	toast.show();
        //	Log.d(LOGTAG, "ASR attempt on virtual device");
        // }

        // Another way of finding out whether ASR is supported (compared with the SimpleASR project)
        if (intActivities.size() != 0) {
            myASR = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            myASR.setRecognitionListener(this);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.asr_notsupported, Toast.LENGTH_SHORT);
            toast.show();
            Log.d(LOGTAG, "ASR not supported");
        }

        Log.i(LOGTAG, "ASR initialized");
	}

	private void initTTS() {
		//Check if the engine is installed, when the check is finished, the
		//onActivityResult method is automatically invoked
		Intent checkIntent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, TTS_DATA_CHECK);
	}

	/**
	 * Starts speech recognition after checking the ASR parameters
	 *
	 * @param language Language used for speech recognition (e.g. Locale.ENGLISH)
	 * @param languageModel Type of language model used (free form or web search)
	 * @param maxResults Maximum number of recognition results
	 */
	public void listen(final Locale language, final String languageModel, final int maxResults)
	{
        Button b = findViewById(R.id.speech_btn);
        b.setEnabled(false);

		if((languageModel.equals(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) || languageModel.equals(RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)) && (maxResults>=0))
		{
			// Check we have permission to record audio
			checkASRPermission();

			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

			// Specify the calling package to identify the application
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
			//Caution: be careful not to use: getClass().getPackage().getName());

			// Specify language model
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel);

			// Specify how many results to receive. Results listed in order of confidence
			intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults);

			// Specify recognition language
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);

            Log.i(LOGTAG, "Going to start listening...");
            this.startListeningTime = System.currentTimeMillis();
			myASR.startListening(intent);

		}
		else {
			Log.e(LOGTAG, "Invalid params to listen method");
			((TextView) findViewById(R.id.feedbackTxt)).setText(R.string.asr_invalid_params);
		}

	}

	/**
	 * Checks whether the user has granted permission to the microphone. If the permission has not been provided,
	 * it is requested. The result of the request (whether the user finally grants the permission or not)
	 * is processed in the onRequestPermissionsResult method.
	 *
	 * This is necessary from Android 6 (API level 23), in which users grant permissions to apps
	 * while the app is running. In previous versions, the permissions were granted when installing the app
	 * See: http://developer.android.com/intl/es/training/permissions/requesting.html
	 */
	public void checkASRPermission() {
		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED) {

			// If  an explanation is required, show it
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
				Toast.makeText(getApplicationContext(), R.string.asr_permission, Toast.LENGTH_SHORT).show();

			// Request the permission.
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
					MY_PERMISSIONS_REQUEST_RECORD_AUDIO); //Callback in "onRequestPermissionResult"
		}
	}

	/**
	 * Processes the result of the record audio permission request. If it is not granted, the
	 * abstract method "onRecordAudioPermissionDenied" method is invoked. Such method must be implemented
	 * by the subclasses of VoiceActivity.
	 * More info: http://developer.android.com/intl/es/training/permissions/requesting.html
	 * */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		if(requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				Log.i(LOGTAG, "Record audio permission granted");
			else {
				Log.i(LOGTAG, "Record audio permission denied");
				Toast.makeText(getApplicationContext(), R.string.asr_permission_notgranted, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Stops listening to the user
	 */
	public void stopListening(){
        myASR.stopListening();
        Log.i(LOGTAG, "Stopped listening");
        Button b = findViewById(R.id.speech_btn);
        b.setEnabled(true);
	}

	/* *******************************************************************************************************
	 * Process ASR events
	 */

	/*
	 * (non-Javadoc)
	 *
	 * Invoked when the ASR provides recognition results
	 *
	 * @see android.speech.RecognitionListener#onResults(android.os.Bundle)
	 */
	@Override
	public void onResults(Bundle results) {
		if(results!=null){
            Log.i(LOGTAG, "ASR results received ok");
            ((TextView) findViewById(R.id.feedbackTxt)).setText(R.string.asr_results);

			//Retrieves the N-best list and the confidences from the ASR result
			ArrayList<String> nBestList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

			//Attention: confidence scores are supported only from API level 14
			float[] nBestConfidences = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

			//Creates a collection of strings, each one with a recognition result and its confidence
			//following the structure "Phrase matched (conf: 0.5)"
			ArrayList<String> nBestView = new ArrayList<>();

			assert nBestList != null;
			for(int i = 0; i<nBestList.size(); i++){
				if(nBestConfidences!=null){
					if(nBestConfidences[i]>=0)
						nBestView.add(nBestList.get(i) + " (conf: " + String.format(this.getResources().getConfiguration().getLocales().get(0), "%.2f", nBestConfidences[i]) + ")");
					else
						nBestView.add(nBestList.get(i) + " (no confidence value available)");
				}
				else
					nBestView.add(nBestList.get(i) + " (no confidence value available)");
			}

			//Read volume from seekbar. The seekbar allows choosing integer values from 0 to 10,
			//we must translate them to a float from 0 to 1
			float volumeLevel = 1;

			//The speak method accepts four parameters:
			//text: the string to be spoken (obtained from the interface)
			//QUEUE_ADD: queuing strategy = this message is added to the end of the playback queue
			//tts_params: a bundle with tts parameters, in our case the volume level
			//"msg": unique identifier for this request
			Bundle tts_params = new Bundle();
			tts_params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volumeLevel);
			mytts.speak("hola", TextToSpeech.QUEUE_ADD, tts_params, "msg");

			//Includes the collection in the ListView of the GUI
			setListView(nBestView);

		}
		else{
            Log.e(LOGTAG, "ASR results null");
			//There was a recognition error
			((TextView) findViewById(R.id.feedbackTxt)).setText(R.string.asr_error);
        }

        stopListening();

	}

	/*
     * (non-Javadoc)
     *
     * Invoked when the ASR is ready to start listening
     *
     * @see android.speech.RecognitionListener#onReadyForSpeech(android.os.Bundle)
     */
	@Override
	public void onReadyForSpeech(Bundle arg0) {
		((TextView) findViewById(R.id.feedbackTxt)).setText(R.string.asr_readyforspeech);
	}

	/*
     * (non-Javadoc)
     *
     * Invoked when the ASR encounters an error
     *
     * @see android.speech.RecognitionListener#onError(int)
     */
	@Override
	public void onError(final int errorCode) {

        //Possible bug in Android SpeechRecognizer: NO_MATCH errors even before the the ASR
        // has even tried to recognize. We have adopted the solution proposed in:
        // http://stackoverflow.com/questions/31071650/speechrecognizer-throws-onerror-on-the-first-listening
        long duration = System.currentTimeMillis() - startListeningTime;
        if (duration < 500 && errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            Log.e(LOGTAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. Going to ignore the error");
            stopListening();
        }
        else {
            String errorMsg;
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMsg = getResources().getString(R.string.asr_error_audio);
					break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMsg = getResources().getString(R.string.asr_error_client);
					break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMsg = getResources().getString(R.string.asr_error_permissions);
					break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMsg = getResources().getString(R.string.asr_error_network);
					break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMsg = getResources().getString(R.string.asr_error_networktimeout);
					break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMsg = getResources().getString(R.string.asr_error_nomatch);
					break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMsg = getResources().getString(R.string.asr_error_recognizerbusy);
					break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMsg = getResources().getString(R.string.asr_error_server);
					break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMsg = getResources().getString(R.string.asr_error_speechtimeout);
					break;
                default:
                    errorMsg = "";
					break;
            }
            if (errorCode == 5 && errorMsg.equals("")) {
                Log.e(LOGTAG, "Going to ignore the error");
                //Another frequent error that is not really due to the ASR
            } else {
            	String error = getString(R.string.asr_error, errorMsg);
                ((TextView) findViewById(R.id.feedbackTxt)).setText(error);
                Log.e(LOGTAG, "Error -> " + error);
                stopListening();
            }
        }
	}

	/*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onBeginningOfSpeech()
     */
	@Override
	public void onBeginningOfSpeech() {
		((TextView) findViewById(R.id.feedbackTxt)).setText(R.string.asr_beginningofspeech);
	}

	/*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onBufferReceived(byte[])
     */
	@Override
	public void onBufferReceived(byte[] buffer) {
		((TextView) findViewById(R.id.feedbackTxt)).setText(R.string.asr_buffer);
	}

	/*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onBeginningOfSpeech()
     */
	@Override
	public void onEndOfSpeech() {
		((TextView) findViewById(R.id.feedbackTxt)).setText(R.string.asr_endofspeech);
	}

	/*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onEvent(int, android.os.Bundle)
     */
	@Override
	public void onEvent(int arg0, Bundle arg1) {}

	/*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onPartialResults(android.os.Bundle)
     */
	@Override
	public void onPartialResults(Bundle arg0) {
		((TextView) findViewById(R.id.feedbackTxt)).setText(R.string.asr_parcialresult);
	}

	/*
 	* (non-Javadoc)
 	* @see android.speech.RecognitionListener#onRmsChanged(float)
 	*/
	@Override
	public void onRmsChanged(float arg0) {}



	/* **************************************************************************************
	 GUI
	 */

	/**
	 * Sets up the listener for the button that the user
	 * must click to start talking
	 */
	private void setSpeakButton() {
		//Gain reference to speak button
		Button speak = findViewById(R.id.speech_btn);

		//Set up click listener
		speak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                setRecognitionParams(); //Read speech recognition parameters from GUI
                listen(Locale.ENGLISH, languageModel, numberRecoResults); 				//Set up the recognizer with the parameters and start listening
			}
		});
	}

	/**
	 * Shows in the GUI the default values for the language model (checks radio button)
	 * and the maximum number of recognition results (shows the number in the text field)
	 */
	private void showDefaultValues() {
		//Show the default number of results in the corresponding EditText
		((EditText) findViewById(R.id.numResults_editText)).setText(String.valueOf(DEFAULT_NUMBER_RESULTS));
		
		//Show the language model
		if(languageModel.equals(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM))
			((RadioButton) findViewById(R.id.langModelFree_radio)).setChecked(true);
		else
			((RadioButton) findViewById(R.id.langModelFree_radio)).setChecked(true);
	}
	
	/**
	 * Reads the values for the language model and the maximum number of recognition results
	 * from the GUI
	 */
	private void setRecognitionParams()  {
		String numResults = ((EditText) findViewById(R.id.numResults_editText)).getText().toString();
		
		//Converts String into int, if it is not possible, it uses the default value
		try{
			numberRecoResults = Integer.parseInt(numResults);
		} catch(Exception e) {	
			numberRecoResults = DEFAULT_NUMBER_RESULTS;	
		}
		//If the number is <= 0, it uses the default value
		if(numberRecoResults<=0)
			numberRecoResults = DEFAULT_NUMBER_RESULTS;
		
		
		RadioGroup radioG = findViewById(R.id.langModel_radioGroup);
		switch(radioG.getCheckedRadioButtonId()){
			case R.id.langModelFree_radio:
				languageModel = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
				break;
			case R.id.langModelWeb_radio:
				languageModel = RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH;
				break;
			default:
				languageModel = DEFAULT_LANG_MODEL;
				break;
		}
	}
	
	/**
	 * Includes the recognition results in the list view
	 * @param nBestView list of matches
	 */
	private void setListView(ArrayList<String> nBestView){
		
		// Instantiates the array adapter to populate the listView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nBestView);
    	ListView listView = findViewById(R.id.nbest_listview);
    	listView.setAdapter(adapter);

	}

    @Override
    public void onDestroy() {
        myASR.destroy();
        Log.i(LOGTAG, "ASR destroyed");

		if (mytts.isSpeaking())
			mytts.stop();
		mytts.shutdown();
		mytts = null;			/*
		 						This is necessary in order to force the creation of a new TTS instance after shutdown.
		 						It is useful for handling runtime changes such as a change in the orientation of the device,
		 						as it is necessary to create a new instance with the new context.
		 						See here: http://developer.android.com/guide/topics/resources/runtime-changes.html
							*/
		super.onDestroy();
    }

	@Override
	public void onInit(int status) {
		if (status != TextToSpeech.ERROR) {
			Toast.makeText(RichASR.this, "inicializado", Toast.LENGTH_LONG).show();
		} else {
			Log.e(LOGTAG, "Error initializing the TTS");
		}

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();

	}

}