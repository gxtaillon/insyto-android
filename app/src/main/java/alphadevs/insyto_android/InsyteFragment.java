package alphadevs.insyto_android;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.IOException;

import alphadevs.insyto_android.models.InsyteItemData;
import alphadevs.insyto_android.models.InsyteMediaAudio;
import alphadevs.insyto_android.models.InsyteMediaText;
import alphadevs.insyto_android.models.InsyteMediaVideo;


public class InsyteFragment extends Fragment {
    static final String ARG_INSYTE_ID= "id";
    private static final Gson gson = InsytoGson.getInstance();
    private Integer mInsyteId;

    ProgressDialog pDialog;

    private InsytoVolley iVolley = InsytoVolley.getInstance();

    private View rootView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static InsyteFragment newInstance(Integer id) {
        InsyteFragment fragment = new InsyteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INSYTE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public InsyteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInsyteId = getArguments().getInt(ARG_INSYTE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_insyte, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadInsyte();
    }

    private void loadInsyte()
    {
        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                InsytoUrlBuilder.getInsyteUrl(mInsyteId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        InsyteItemData insyteData = gson.fromJson(response, InsyteItemData.class);
                        TextView cardTitle = (TextView) rootView.findViewById(R.id.card_title);
                        cardTitle.setText(insyteData.getTitle());

                        TextView cardDescription = (TextView) rootView.findViewById(R.id.card_description);
                        cardDescription.setText(insyteData.getDescription());

                        TextView cardContent = (TextView) rootView.findViewById(R.id.card_content);

                        // TODO find a better method
                        if(insyteData.getMedia() instanceof InsyteMediaText) {
                            InsyteMediaText mediaText = (InsyteMediaText) insyteData.getMedia();
                            cardContent.setText(mediaText.getContent());
                        } else if (insyteData.getMedia() instanceof InsyteMediaVideo) {
                            InsyteMediaVideo mediaVideo = (InsyteMediaVideo) insyteData.getMedia();
                            setupVideoContent(mediaVideo.getUrl());
                        } else if (insyteData.getMedia() instanceof InsyteMediaAudio) {
                            InsyteMediaAudio mAudio = (InsyteMediaAudio) insyteData.getMedia();
                            setupAudioContent(mAudio.getUrl());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

                toastAnError();
            }
        });

        // Add the request to the queue
        iVolley.add(stringRequest);
    }

    private void setupAudioContent(String url)
    {
        // TODO audio continues to play after leaving fragment...

        showBufferingDialog();

        final MediaPlayer mediaPlayer = new MediaPlayer();
        // Set type to streaming
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // Listen for if the audio file can't be prepared
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // ... react appropriately ...
                // The MediaPlayer has moved to the Error state, must be reset!
                return false;
            }
        });
        // Attach to when audio file is prepared for playing
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                pDialog.dismiss();
            }
        });
        // Set the data source to the remote URL
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Trigger an async preparation which will file listener when completed
        mediaPlayer.prepareAsync();
    }

    private void setupVideoContent(String url)
    {
        final VideoView videoview = (VideoView) rootView.findViewById(R.id.card_video);

        videoview.setVisibility(View.VISIBLE);
        // Execute StreamVideo AsyncTask

        showBufferingDialog();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    getActivity());
            mediacontroller.setAnchorView(videoview);

            // Get the URL from String VideoURL
            Uri video = Uri.parse(url);
            videoview.setMediaController(mediacontroller);
            videoview.requestFocus();

        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.requestFocus();
                videoview.start();
            }
        });
        videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showBufferingDialog()
    {
        pDialog = new ProgressDialog(getContext());
        // Set progressbar title
        pDialog.setTitle("Please wait");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        // Show progressbar
        pDialog.show();
    }

    private void toastAnError()
    {
        Toast.makeText(this.getContext(), "Error getting insyte", Toast.LENGTH_SHORT).show();
    }
}
