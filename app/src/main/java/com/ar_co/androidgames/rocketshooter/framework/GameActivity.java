package com.ar_co.androidgames.rocketshooter.framework;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ar_co.androidgames.rocketshooter.R;
import com.ar_co.androidgames.rocketshooter.interfaces.FileIO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
/*import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;*/
import com.google.example.games.basegameutils.BaseGameUtils;

public abstract class GameActivity extends AppCompatActivity{ //implements
        //GoogleApiClient.ConnectionCallbacks,
        ///GoogleApiClient.OnConnectionFailedListener{

    private static final String WAKE_LOCK = "WakeLock";
    private static final int REQUEST_LEADERBOARD = 0;
    private static final int RC_SIGN_IN = 1;

    private static final int CHANGE_AD_STATE = 0;
    private static final int SHOW_INTERSTITIAL = 1;
    private static final int SHOW_LEADERBOARD = 2;
    private static final int RATE = 3;

    //private GoogleApiClient mClient;
    //private AdView mAdView;
    //private InterstitialAd mInterstitialAd;
    private RelativeLayout mMain;
    private Handler mHandler;
    private PowerManager.WakeLock mWakeLock;
    private boolean mResolvingConnectionFailure = false;

    private long hiscore;

    private FileIO fileIO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics resolution = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(resolution);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        fileIO = new AndroidFileIO(this);

        mMain = (RelativeLayout) findViewById(R.id.main);
       // mAdView = (AdView) findViewById(R.id.adView);

        setupGame();

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                Log.i("GameActivity", "Handling message");
                if(msg.what == CHANGE_AD_STATE){
                    Log.i("GameActivity", "Ad state change received");
                    //mAdView.setVisibility((int)msg.obj);
                }else if(msg.what == SHOW_INTERSTITIAL){
                    //if(mInterstitialAd.isLoaded()){
                      //      mInterstitialAd.show();
                   // }
                }/*else if(msg.what == SHOW_LEADERBOARD){
                    hiscore = (long)((float)msg.obj * 100f);
                    Log.i("GameActivity", "Hiscore = " + hiscore);
                    if(!mClient.isConnected() && mClient != null){
                            mClient.connect();
                    }else {
                        showLeaderBoard();
                    }
                }*/else if(msg.what == RATE) {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://draw.google.com/store/apps/details?id=" + getPackageName())));
                    }
                }
            }
        };

        /*mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();*/

        AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        //mAdView.bringToFront();

       // mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-7100768117749835/1390051308"); //ca-app-pub-7100768117749835/1390051308

        AdRequest interstitialRequest = new AdRequest.Builder()
                .build();

       // mInterstitialAd.loadAd(interstitialRequest);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, WAKE_LOCK);
    }

    @Override
    public void onResume(){
        super.onResume();
        mWakeLock.acquire();
       // mAdView.resume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mWakeLock.release();
       // mAdView.pause();
    }

    @Override
    public void onStop(){
        super.onStop();
        /*if(mClient.isConnected()) {
            Games.signOut(mClient);
            mClient.disconnect();
        }*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //if(mAdView != null){
         //   mAdView.destroy();
     //   }
        /*if(mClient != null){
            mClient = null;
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        /*if(requestCode == REQUEST_LEADERBOARD){

        }else if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                mClient.connect();
            }else{
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.sign_in_failure);
            }
        }*/
    }

    /*@Override
    public void onConnected(Bundle connectionHint){
        showLeaderBoard();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        if(mResolvingConnectionFailure){
            return;
        }
            if(!BaseGameUtils.resolveConnectionFailure(this, mClient, connectionResult, RC_SIGN_IN, getString(R.string.signin_error))){
                mResolvingConnectionFailure = false;
            }else{
                mResolvingConnectionFailure = true;
            }
    }

    @Override
    public void onConnectionSuspended(int i){
        mClient.connect();
    }

    public void requestLeaderboard(float score){
        mHandler.obtainMessage(SHOW_LEADERBOARD, score).sendToTarget();
    }*/

    public void requestReview(){
        mHandler.sendEmptyMessage(RATE);
    }

    /*public void showLeaderBoard(){

        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mClient, getString(R.string.leaderboard_id),
                LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC)
                .setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                    long currentScore;

                    @Override
                    public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                        if (scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null) {
                            currentScore = scoreResult.getScore().getRawScore();
                        } else {
                            currentScore = 0;
                        }
                        if (hiscore > currentScore) {
                            Games.Leaderboards.submitScore(mClient, getString(R.string.leaderboard_id), (long)hiscore);
                        } else if (hiscore < currentScore) {
                            Settings settings = Settings.getSettings(fileIO);

                            settings.pushHighScore(((float)currentScore) / 100f);
                            settings.save(fileIO);
                        }
                        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mClient, getString(R.string.leaderboard_id)), REQUEST_LEADERBOARD);
                    }
                });
    }*/

    public void displayBanner(boolean visible){
        final boolean v = visible;
        mHandler.post(new Runnable(){
            @Override
            public void run() {
                Message msg;
                if (v) {
                    Log.i("GameActivity", "Obtained message");
                    msg = mHandler.obtainMessage(CHANGE_AD_STATE, AdView.VISIBLE);
                } else {
                    msg = mHandler.obtainMessage(CHANGE_AD_STATE, AdView.GONE);
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    public void displayInterstitial(){
        mHandler.sendEmptyMessage(SHOW_INTERSTITIAL);
    }

    protected void addView(View view){
        mMain.addView(view);
    }

    protected abstract void setupGame();

}
