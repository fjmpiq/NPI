/*
 *  Copyright 2016 Zoraida Callejas, Michael McTear and David Griol
 *
 *  This is AN UPDATE of the Conversandroid Toolkit, from the book:
 *  The Conversational Interface, Michael McTear, Zoraida Callejas and David Griol
 *  Springer 2016 <https://github.com/zoraidacallejas/>
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

/**
 *
 * @author @mx_psi, @fjmpiq, @jojelupipa, Michael McTear, David Griol
 * @version 4.0, 04/06/18
 */

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

//Check the dependencies necessary to make these imports in
//the build.gradle file
//See tutorial here: https://github.com/dialogflow/dialogflow-android-client
import ai.api.android.AIConfiguration; //<< be careful to use ai.api.android.AI... and not ai.api.AI...
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import conversandroid.talkback.R;

public class MainActivity extends VoiceActivity {

    private static final String LOGTAG = "CLEEPY";
    private static final Integer ID_PROMPT_QUERY = 0;
    private static final Integer ID_PROMPT_INFO = 1;

    private long startListeningTime = 0; // To skip errors (see processAsrError method)

    //Connection to DialogFlow
    private AIDataService aiDataService=null;
    private final String ACCESS_TOKEN = "c9d250a9a574465cacf77f7117c472f4 ";
    // https://dialogflow.com/docs/reference/agent/#obtaining_access_tokens)

    // Access to textView
    private TextView queryResultTextView;

    // Control of the initial prompt
    boolean initialPromptDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set layout
        setContentView(R.layout.activity_main);

        //Initialize the speech recognizer and synthesizer
        initSpeechInputOutput(this);

        //Set up the speech button
        setSpeakButton();

        //Set up the text view
        setTextView();

        //Dialogflow configuration parameters
        final AIConfiguration config = new AIConfiguration(ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(config);
    }

    /**
     * Casts the initial prompt, then asigns startListening method to the button
     */
    private void setSpeakButton() {
        // gain reference to speak button
        Button speak = findViewById(R.id.speech_btn);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!initialPromptDone) {
                    try {
                        speak(getResources().getString(R.string.initial_prompt), "ES", ID_PROMPT_QUERY);

                    } catch (Exception e) {
                        Log.e(LOGTAG, "TTS not accessible");
                    }
                    initialPromptDone = true;
                    try {   // TODO: Buscar una solución mejor para que no se solape la escucha con
                        // la reproducción del mensaje sin usar onTTSDone y que no entre en bucle
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        Log.e(LOGTAG, "Error inesperado en la espera");
                    }
                }


                runOnUiThread(new Runnable() {
                    public void run() {
                        startListening();
                    }
                });
            }
        });
    }

    /**
     * Initializes the text view that will show the answer of the query
     */

    private void setTextView() {
        queryResultTextView = findViewById(R.id.queryResult);
        queryResultTextView.setText("Aquí se mostrará el resultado de la consulta");

    }

    /**
     * Explain to the user why we need their permission to record audio on the device
     * See the checkASRPermission in the VoiceActivity class
     */
    public void showRecordPermissionExplanation() {
        Toast.makeText(getApplicationContext(), R.string.asr_permission, Toast.LENGTH_SHORT).show();
    }

    /**
     * If the user does not grant permission to record audio on the device, a message is shown and the app finishes
     */
    public void onRecordAudioPermissionDenied() {
        Toast.makeText(getApplicationContext(), R.string.asr_permission_notgranted, Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    /**
     * Starts listening for any user input.
     * When it recognizes something, the <code>processAsrResult</code> method is invoked.
     * If there is any error, the <code>onAsrError</code> method is invoked.
     */
    private void startListening() {
        if (deviceConnectedToInternet()) {
            try {
                startListeningTime = System.currentTimeMillis();
                listen(Locale.ENGLISH, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, 1); //Start listening
            } catch (Exception e) {
                this.runOnUiThread(new Runnable() {  //Toasts must be in the main thread
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.asr_notstarted, Toast.LENGTH_SHORT).show();
                        changeButtonAppearanceToDefault();
                    }
                });

                Log.e(LOGTAG, "ASR could not be started");
                try {
                    speak(getResources().getString(R.string.asr_notstarted), "ES", ID_PROMPT_INFO);
                } catch (Exception ex) {
                    Log.e(LOGTAG, "TTS not accessible");
                }

            }
        } else {

            this.runOnUiThread(new Runnable() { //Toasts must be in the main thread
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    changeButtonAppearanceToDefault();
                }
            });
            try {
                speak(getResources().getString(R.string.check_internet_connection), "ES", ID_PROMPT_INFO);
            } catch (Exception ex) {
                Log.e(LOGTAG, "TTS not accessible");
            }
            Log.e(LOGTAG, "Device not connected to Internet");

        }
    }

    /**
     * Invoked when the ASR is ready to start listening. Provides feedback to the user to show that the app is listening:
     * * It changes the color and the message of the speech button
     */
    @Override
    public void processAsrReadyForSpeech() {
        changeButtonAppearanceToListening();
    }

    /**
     * Provides feedback to the user to show that the app is listening:
     * * It changes the color and the message of the speech button
     */
    private void changeButtonAppearanceToListening() {
        Button button = findViewById(R.id.speech_btn); //Obtains a reference to the button
        button.setText(getResources().getString(R.string.speechbtn_listening)); //Changes the button's message to the text obtained from the resources folder
        button.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.speechbtn_listening), PorterDuff.Mode.MULTIPLY);  //Changes the button's background to the color obtained from the resources folder
    }

    /**
     * Provides feedback to the user to show that the app is idle:
     * * It changes the color and the message of the speech button
     */
    private void changeButtonAppearanceToDefault() {
        Button button = findViewById(R.id.speech_btn); //Obtains a reference to the button
        button.setText(getResources().getString(R.string.speechbtn_default)); //Changes the button's message to the text obtained from the resources folder
        button.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.speechbtn_default), PorterDuff.Mode.MULTIPLY);    //Changes the button's background to the color obtained from the resources folder
    }

    /**
     * Provides feedback to the user (by means of a Toast and a synthesized message) when the ASR encounters an error
     */
    @Override
    public void processAsrError(int errorCode) {

        changeButtonAppearanceToDefault();

        long duration = System.currentTimeMillis() - startListeningTime;
        if (duration < 500 && errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            Log.e(LOGTAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. Going to ignore the error");
            stopListening();
        } else {
            int errorMsg;
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMsg = R.string.asr_error_audio;
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMsg = R.string.asr_error_client;
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMsg = R.string.asr_error_permissions;
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMsg = R.string.asr_error_network;
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMsg = R.string.asr_error_networktimeout;
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMsg = R.string.asr_error_nomatch;
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMsg = R.string.asr_error_recognizerbusy;
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMsg = R.string.asr_error_server;
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMsg = R.string.asr_error_speechtimeout;
                    break;
                default:
                    errorMsg = R.string.asr_error; //Another frequent error that is not really due to the ASR, we will ignore it
                    break;
            }
            String msg = getResources().getString(errorMsg);
            this.runOnUiThread(new Runnable() { //Toasts must be in the main thread
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.asr_error, Toast.LENGTH_LONG).show();
                }
            });

            Log.e(LOGTAG, "Error when attempting to listen: " + msg);
            try {
                speak(msg, "ES", ID_PROMPT_INFO);
            } catch (Exception e) {
                Log.e(LOGTAG, "TTS not accessible");
            }
        }
    }

    /**
     * Synthesizes the best recognition result
     */
    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {
        if(nBestList!=null){

            Log.d(LOGTAG, "ASR best result: " + nBestList.get(0));

            if(nBestList.size()>0){
                changeButtonAppearanceToDefault();
                sendMsgToChatBot(nBestList.get(0)); //Send the best recognition hypothesis to the chatbot
            }
        }
    }

    /**
     * Connects to DialogFlow sending the user input in text form
     * @param userInput recognized utterance
     */
    private void sendMsgToChatBot(String userInput) {

        //final AIRequest aiRequest = new AIRequest();
        //aiRequest.setQuery(userInput);

        new AsyncTask<String,Void,AIResponse>() {

            /**
             * Connects to the DialogFlow service
             * @param strings Contains the user request
             * @return language understanding result from DialogFlow
             */
            @Override
            protected AIResponse doInBackground(String... strings) {
                final String request = strings[0];
                Log.d(LOGTAG,"Request: "+strings[0]);
                try {
                    final AIRequest aiRequest = new AIRequest(request);
                    final AIResponse response = aiDataService.request(aiRequest);
                    Log.d(LOGTAG,"Request: "+aiRequest);
                    Log.d(LOGTAG,"Response: "+response);


                    return response;
                } catch (AIServiceException e) {
                    try {
                        speak("Error al conectarse con Daialog Flou", "ES", ID_PROMPT_INFO);
                        Log.e(LOGTAG,"Problems retrieving a response");
                    } catch (Exception ex) {
                        Log.e(LOGTAG, "English not available for TTS, default language used instead");
                    }
                }
                return null;
            }

            /**
             * The semantic parsing is decomposed and the text corresponding to the chatbot
             * response is synthesized
             * @param response parsing corresponding to the output of DialogFlow
             */
            @Override
            protected void onPostExecute(AIResponse response) {
                if (response != null) {
                    // process aiResponse here
                    // Mmore info for a more detailed parsing on the response: https://github.com/dialogflow/dialogflow-android-client/blob/master/apiAISampleApp/src/main/java/ai/api/sample/AIDialogSampleActivity.java

                    final Result result = response.getResult();
                    Log.d(LOGTAG,"Result: "+result.getResolvedQuery());
                    Log.d(LOGTAG,"Action: " + result.getAction());

                    final String chatbotResponse = result.getFulfillment().getSpeech();
                    try {
                        speak(chatbotResponse, "ES", ID_PROMPT_QUERY); //It always starts listening after talking, it is neccessary to include a special "last_exchange" intent in dialogflow and process it here
                        //so that the last system answer is synthesized using ID_PROMPT_INFO.

                        queryResultTextView.setText(chatbotResponse); // The response will be displayed by text
                    } catch (Exception e) { Log.e(LOGTAG, "TTS not accessible"); }

                }
            }
        }.execute(userInput);

    }

    /**
     * Checks whether the device is connected to Internet (returns true) or not (returns false)
     * From: http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
     */
    public boolean deviceConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    /**
     * Shuts down the TTS engine when finished
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        shutdown();
    }

    /**
     * Invoked when the TTS has finished synthesizing.
     *
     * In this case, it starts recognizing if the message that has just been synthesized corresponds to a question (its id is ID_PROMPT_QUERY),
     * and does nothing otherwise.
     *
     * According to the documentation the speech recognizer must be invoked from the main thread. onTTSDone callback from TTS engine and thus
     * is not in the main thread. To solve the problem, we use Androids native function for forcing running code on the UI thread
     * (runOnUiThread).

     *
     * @param uttId identifier of the prompt that has just been synthesized (the id is indicated in the speak method when the text is sent
     * to the TTS engine)
     */

    @Override
    public void onTTSDone(String uttId) {
        /*if(uttId.equals(ID_PROMPT_QUERY.toString())) {
            runOnUiThread(new Runnable() {
                public void run() {
                    startListening();
                }
            });
        }*/
    }

    /**
     * Invoked when the TTS encounters an error.
     *
     * In this case it just writes in the log.
     */
    @Override
    public void onTTSError(String uttId) {
        Log.e(LOGTAG, "TTS error");
    }

    /**
     * Invoked when the TTS starts synthesizing
     *
     * In this case it just writes in the log.
     */
    @Override
    public void onTTSStart(String uttId) {
        Log.d(LOGTAG, "TTS starts speaking");
    }
}
