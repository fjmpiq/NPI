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

import android.content.ActivityNotFoundException; // Launch intent
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager; // Launch intent
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
// Sensors
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import org.andresoviedo.util.android.AssetUtils;
import org.andresoviedo.util.android.ContentUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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

public class MainActivity extends VoiceActivity implements SensorEventListener {

    ///////////////////////////////////////////////////////////////////////////
    // ATTRIBUTES                                                            //
    ///////////////////////////////////////////////////////////////////////////

    private static final String LOGTAG = "CLEEPY";
    private static final Integer ID_PROMPT_QUERY = 0;
    private static final Integer ID_PROMPT_INFO = 1;
    // Access to textView
    private TextView queryResultTextView;


    // 3D MODELS


    // Parameters for 3D models
    private Map<String, Object> loadModelParameters = new HashMap<>();


    // VOICE AND TEXT


    private long startListeningTime = 0; // To skip errors (see processAsrError method)

    // Control of the initial prompt
    boolean initialPromptDone = false;


    // DIALOGFLOW INTEGRATION


    //Connection to DialogFlow
    private AIDataService aiDataService=null;
    // https://dialogflow.com/docs/reference/agent/#obtaining_access_tokens)



    // SENSORS


    SensorManager sManager;
    Sensor proximitySensor;
    Sensor accelSensor;

    // Accelerations for shake detection
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity


    // RANDOM ARTWORKS


    // JSON object with random artworks
    private JSONArray artworks;

    ///////////////////////////////////////////////////////////////////////////
    // METHODS                                                               //
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set layout
        setContentView(R.layout.activity_main);

        //Initialize sensors
        setUpSensors();

        //Set up the text view
        setTextView();

        //Initialize the speech recognizer and synthesizer
        initSpeechInputOutput(this);

        //Set up the speech button
        setSpeakButton();

        // Set up the 3D button
        set3DButton();

        // Set up the QR button

        //Dialogflow configuration parameters
        String ACCESS_TOKEN = "c9d250a9a574465cacf77f7117c472f4 ";
        final AIConfiguration config;
        config = new AIConfiguration(ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(config);

        //Initialize shake detector
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        //Parse artworks object
        InputStream is = getResources().openRawResource(R.raw.artworks);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                try {
                    writer.write(buffer, 0, n);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String artworksString = writer.toString();
        try {
            artworks = new JSONArray(artworksString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     *   Asigns startListening method to the button
     */
    private void set3DButton() {
        // gain reference to speak button
        Button b3D = findViewById(R.id.launch3d_btn);
        b3D.setOnClickListener(v -> loadModelFromAssets());
    }

    private void setQRbutton() {
        Button qr_button = findViewById(R.id.qr_scanner);

    }


    /**
     * Initializes the text view that will show the answer of the query
     */

    private void setTextView() {
        queryResultTextView = findViewById(R.id.queryResult);
        queryResultTextView.setText(R.string.initial_textView_message);

    }

    /**
     *   Asigns startListening method to the button, if it's the first time it's pressed
     *   it will display the prompt message
     */
    private void setSpeakButton() {
        // gain reference to speak button
        Button speak = findViewById(R.id.speech_btn);

        speak.setOnClickListener(this::onClick);
    }

    protected void onResume() {
        super.onResume();
        sManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
    }


    // 3D MODELS


    private void loadModelFromAssets() {
        AssetUtils.createChooserDialog(this, "Elige el modelo", null, "models", "(?i).*\\.(obj|stl|dae)",
                (String file) -> {
                    if (file != null) {
                        launchModelRendererActivity(Uri.parse("assets://" + getPackageName() + "/" + file),file);
                    }
                });
    }


    private void launchModelRendererActivity(Uri uri, String name) {
        ContentUtils.provideAssets(this); /////
        Log.i("Menu", "Launching renderer for '" + uri + "'");
        Intent intent = new Intent(getApplicationContext(), ModelActivity.class);
        intent.putExtra("uri", uri.toString());
        intent.putExtra("name", name);
        // content provider case
        if (!loadModelParameters.isEmpty()) {
            intent.putExtra("type", loadModelParameters.get("type").toString());
            loadModelParameters.clear();
        }

        startActivity(intent);
    }


    // VOICE AND TEXT


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
        try {
            speak(getResources().getString(R.string.asr_permission_notgranted), "ES", ID_PROMPT_INFO);
        } catch (Exception e) {
            Log.e(LOGTAG, "TTS not accessible");
        }
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
                //Toasts must be in the main thread
                this.runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.asr_notstarted, Toast.LENGTH_SHORT).show();
                    changeButtonAppearanceToDefault();
                });

                Log.e(LOGTAG, "ASR could not be started", e);
                try {
                    speak(getResources().getString(R.string.asr_notstarted), "ES", ID_PROMPT_INFO);
                } catch (Exception ex) {
                    Log.e(LOGTAG, "TTS not accessible", e);
                }

            }
        } else {

            //Toasts must be in the main thread
            this.runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                changeButtonAppearanceToDefault();
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
            //Toasts must be in the main thread
            this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), R.string.asr_error, Toast.LENGTH_LONG).show());

            Log.e(LOGTAG, "Error when attempting to listen: " + msg);
            try {
                speak(msg, "ES", ID_PROMPT_INFO);
            } catch (Exception e) {
                Log.e(LOGTAG, "TTS not accessible", e);
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
     * Checks whether the device is connected to Internet (returns true) or not (returns false)
     * From: http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
     */
    public boolean deviceConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork;
        activeNetwork = cm.getActiveNetworkInfo();
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
        sManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
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


    private void onClick(View v) {

        if (!initialPromptDone) {
            try {
                speak(getResources().getString(R.string.initial_prompt), "ES", ID_PROMPT_QUERY);

            } catch (Exception e) {
                Log.e(LOGTAG, "TTS not accessible");
            }
            initialPromptDone = true;
        }

        if(!isTTSSpeaking()) {
            runOnUiThread(this::startListening);
        }
    }


    // DIALOGFLOW INTEGRATION


    private class MyAsyncTaskClass extends AsyncTask<String,Void,AIResponse>{
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
                    speak("Error al conectarse con Dialog Flow", "ES", ID_PROMPT_INFO);
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
                if (chatbotResponse.matches(".+\\.obj")) {
                    launchModelRendererActivity(Uri.parse("assets://" + getPackageName() + "/" + chatbotResponse), chatbotResponse);
                }
                else {
                    try {
                        speak(chatbotResponse, "ES", ID_PROMPT_QUERY); //It always starts listening after talking, it is neccessary to include a special "last_exchange" intent in dialogflow and process it here
                        //so that the last system answer is synthesized using ID_PROMPT_INFO.
                        queryResultTextView.setText(chatbotResponse); // The response will be displayed by text
                    } catch (Exception e) { Log.e(LOGTAG, "TTS not accessible"); };
                }

            }
        }
    };

    /**
     * Connects to DialogFlow sending the user input in text form
     * @param userInput recognized utterance
     */

    private void sendMsgToChatBot(String userInput) {

        //final AIRequest aiRequest = new AIRequest();
        //aiRequest.setQuery(userInput);

        AsyncTask<String,Void,AIResponse> myAsyncTask = new MyAsyncTaskClass();
        myAsyncTask.execute(userInput);

    }


    // SENSORS


    /**
     * @brief Sets up sensors and its manager
     */
    private void setUpSensors() {
        sManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        proximitySensor = sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelSensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 12) {
                onShake();
            }
        }

        if(event.sensor.getType()==Sensor.TYPE_PROXIMITY) {
            if(event.values[0]==0) {
                stop();
            }
        }
    }


    /**
     * @brief Called when device is shaken
     */
    private void onShake(){
        Toast toast = Toast.makeText(getApplicationContext(), R.string.onShake_toast, Toast.LENGTH_LONG);
        toast.show();
        String msg = randArtwork();
        sManager.unregisterListener(this, accelSensor);
        
        try {
            speak(msg, "ES", ID_PROMPT_QUERY); //It always starts listening after talking, it is necessary to include a special "last_exchange" intent in dialogflow and process it here
            //so that the last system answer is synthesized using ID_PROMPT_INFO.
        } catch (Exception e) {
            Log.e(LOGTAG, "TTS not accessible");
        }
        queryResultTextView.setText(msg); // The response will be displayed by text
    }


    // RANDOM ARTWORKS


    /**
     * @brief Randomly selects and artwork and shows information about it
     */
    private String randArtwork(){
        String artworkName = null;
        String artworkCreator = null;
        String artworkCountry = null;
        String artworkLoc = null;
        String artworkInception = null;
        String msg;

        int randIndex;

        String illegalNameRegex = "Q[0-9]+";
        do{
            randIndex = new Random().nextInt(artworks.length());

            try {
                artworkName = artworks.getJSONObject(randIndex).getString("itemLabel");
                artworkLoc = artworks.getJSONObject(randIndex).getString("locLabel");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } while (artworkName.matches(illegalNameRegex) || artworkLoc.matches(illegalNameRegex));

        try {
            artworkCreator = artworks.getJSONObject(randIndex).getString("creatorLabel");
            artworkCountry = artworks.getJSONObject(randIndex).getString("countryLabel");
            artworkInception = artworks.getJSONObject(randIndex).getString("inception").split("-")[0];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        msg = String.format("%s es una obra de %s que data del %s. Se encuentra en %s, %s.", artworkName, artworkCreator, artworkInception, artworkLoc, artworkCountry);
        return msg;
    }
}
