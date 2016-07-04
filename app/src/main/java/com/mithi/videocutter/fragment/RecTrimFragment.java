package com.mithi.videocutter.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;

import com.mithi.videocutter.R;
import com.mithi.videocutter.model.VideoSliceSeekBar;
import com.mithi.videocutter.util.AppUtils;
import com.mithi.videocutter.util.DialogUtils;
import com.mithi.videocutter.util.FFmpegUtils;
import com.mithi.videocutter.util.FragmentUtils;

public class RecTrimFragment extends Fragment {
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final String TAG = "VideoCutterLastVideo";
    private static final int VIDEO_DURATION = 60;
    TextView textViewLeft, textViewRight;
    VideoSliceSeekBar videoSliceSeekBar;
    VideoView videoView;
    View videoControlBtn;
    private Uri mPath;
    private ProgressDialog mProgressDialog;
    private boolean isTriming;
    Button mVideoControlBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rec_trim, container, false);
        textViewLeft = (TextView) view.findViewById(R.id.left_pointer);
        textViewRight = (TextView) view.findViewById(R.id.right_pointer);
        videoSliceSeekBar = (VideoSliceSeekBar) view.findViewById(R.id.seek_bar);
        videoView = (VideoView) view.findViewById(R.id.video);
        videoControlBtn = view.findViewById(R.id.video_control_btn);
        mVideoControlBtn = (Button)view.findViewById(R.id.video_control_btn);
        mVideoControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performVideoViewClick();
            }
        });
        mVideoControlBtn.setEnabled(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);
        if (isTriming) {
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.show();
        }
        View view = getView();

        view.findViewById(R.id.cut_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimVideo(mPath);
            }
        });

        view.findViewById(R.id.rec_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

        view.findViewById(R.id.log_last_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.logLastVideo();
            }
        });
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void trimVideo(Uri path) {
        if (path == null) {
            DialogUtils.showAlert(getContext(), R.string.error, R.string.video_is_not_recorded);
            return;
        }
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.show();
        isTriming = true;
        FFmpegUtils.trimVideo(getContext(), path, VIDEO_DURATION, new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String s) {
                mProgressDialog.dismiss();
                DialogUtils.showAlert(getContext(), R.string.error, R.string.trim_error);
            }

            @Override
            public void onSuccess(String s) {
                mProgressDialog.dismiss();
                DialogUtils.showAlert(getContext(), R.string.app_name, R.string.trim_success);
                FragmentUtils.addFragment(getFragmentManager(), VideoListFragment.class);
            }

            @Override
            public void onFinish() {
                isTriming = false;
                mProgressDialog.dismiss();
                AppUtils.logLastVideo();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            mPath = data.getData();
            initVideoView(mPath);
            mVideoControlBtn.setEnabled(true);
        }
    }


    public static String getTimeForTrackFormat(int timeInMills, boolean display2DigitsInMinsSection) {
        int minutes = (timeInMills / (60 * 1000));
        int seconds = (timeInMills - minutes * 60 * 1000) / 1000;
        String result = display2DigitsInMinsSection && minutes < 10 ? "0" : "";
        result += minutes + ":";
        if (seconds < 10) {
            result += "0" + seconds;
        } else {
            result += seconds;
        }
        return result;
    }



    private void initVideoView(Uri path) {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoSliceSeekBar.setSeekBarChangeListener(new VideoSliceSeekBar.SeekBarChangeListener() {
                    @Override
                    public void SeekBarValueChanged(int leftThumb, int rightThumb) {
                        textViewLeft.setText(getTimeForTrackFormat(leftThumb, true));
                        textViewRight.setText(getTimeForTrackFormat(rightThumb, true));
                    }
                });
                videoSliceSeekBar.setMaxValue(mp.getDuration());
                videoSliceSeekBar.setLeftProgress(0);
                videoSliceSeekBar.setRightProgress(mp.getDuration());
                videoSliceSeekBar.setProgressMinDiff(mp.getDuration() / 10);
            }
        });
        videoView.setVideoURI(path);
        //videoView.setVideoURI(Uri.parse("android.resource://com.example.TwoThumbsSeekBarActivity/" + R.raw.video));
    }

    private void performVideoViewClick() {
        if (videoView.isPlaying()) {
            videoView.pause();
            videoSliceSeekBar.setSliceBlocked(false);
            videoSliceSeekBar.removeVideoStatusThumb();
        } else {
            videoView.seekTo(videoSliceSeekBar.getLeftProgress());
            videoView.start();
            videoSliceSeekBar.setSliceBlocked(true);
            videoSliceSeekBar.videoPlayingProgress(videoSliceSeekBar.getLeftProgress());
            videoStateObserver.startVideoProgressObserving();
        }
    }

    private StateObserver videoStateObserver = new StateObserver();

    private class StateObserver extends Handler {

        private boolean alreadyStarted = false;

        private void startVideoProgressObserving() {
            if (!alreadyStarted) {
                alreadyStarted = true;
                sendEmptyMessage(0);
            }
        }

        private Runnable observerWork = new Runnable() {
            @Override
            public void run() {
                startVideoProgressObserving();
            }
        };

        @Override
        public void handleMessage(Message msg) {
            alreadyStarted = false;
            videoSliceSeekBar.videoPlayingProgress(videoView.getCurrentPosition());
            if (videoView.isPlaying() && videoView.getCurrentPosition() < videoSliceSeekBar.getRightProgress()) {
                postDelayed(observerWork, 50);
            } else {

                if (videoView.isPlaying()) videoView.pause();

                videoSliceSeekBar.setSliceBlocked(false);
                videoSliceSeekBar.removeVideoStatusThumb();
            }
        }
    }
}
