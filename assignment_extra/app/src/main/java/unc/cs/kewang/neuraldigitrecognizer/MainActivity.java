package unc.cs.kewang.neuraldigitrecognizer;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import unc.cs.kewang.neuraldigitrecognizer.view.DrawTrajectory;
import unc.cs.kewang.neuraldigitrecognizer.view.DrawView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawTrajectory mTrajectories;
    private DrawView mDrawView;
    private static final int MNIST_PIXEL_WIDTH = 28;
    private static final int MNIST_PIXEL_HEIGHT = 28;
    private SeekBar mRedSeekBar;
    private SeekBar mGreenSeekBar;
    private SeekBar mBlueSeekBar;
    private Button mClearButton;
    private Button mDetectButton;
    private Button mColorButton;
    private TextView mDigitTextView;
    private TextView mProbTextView;
    private static final String MODEL_FILE = "file:///android_asset/mnist0.pb";
    private static final String TF_INPUT_NAME = "input";
    private static final String TF_OUTPUT_NAME = "output";
    private Executor executor = Executors.newSingleThreadExecutor();
    private DigitClassifier mDigitClassifier;

    private int mRed = 0;
    private int mGreen = 0;
    private int mBlue = 0;
    private int mDrawingColor = 0;
    private float[] mDigitPixels = new float[MNIST_PIXEL_HEIGHT * MNIST_PIXEL_WIDTH];
    private float[] mDigitProbs;

    @Override
    public void onClick(View v) {
        if(v == mClearButton) {
            mTrajectories.clear();
            mDrawView.reset();
            mDrawView.invalidate();

            mDigitTextView.setText("");
            mProbTextView.setText("");
        } else if(v == mDetectButton) {
            mDigitPixels = mDrawView.getImagePixels();
            mDigitProbs = mDigitClassifier.classifyImage(mDigitPixels);

            int digitClass = 0;
            float digitProb = Float.MIN_VALUE;
            for(int i = 0; i < mDigitProbs.length; ++i) {
                if(mDigitProbs[i] > digitProb) {
                    digitProb = mDigitProbs[i];
                    digitClass = i;
                }
                Log.d(TAG, String.format("Val %d Prob %f", i, digitProb));
            }

            mDigitTextView.setText("Digit:" + Integer.toString(digitClass));
            mProbTextView.setText("Prob:" + Float.toString(digitProb));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float mLastX = event.getX();
                float mLastY = event.getY();
                PointF mTmpPoint = mDrawView.projectToCanvas(mLastX, mLastY);
                float lastConvX = mTmpPoint.x;
                float lastConvY = mTmpPoint.y;
                mTrajectories.startTrajectory(lastConvX, lastConvY);
                mTrajectories.setTrajectoryColor(mRed, mGreen, mBlue);
                return true;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                mTmpPoint = mDrawView.projectToCanvas(x, y);
                float newConvX = mTmpPoint.x;
                float newConvY = mTmpPoint.y;
                mTrajectories.addPoint(newConvX, newConvY);
                mDrawView.invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                mTrajectories.endTrajectory();
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTrajectories = new DrawTrajectory();
        mDrawView = (DrawView) findViewById(R.id.draw_view);
        mDrawView.setTrajectories(mTrajectories);
        mDrawView.setOnTouchListener(this);
        if(!mDrawView.init(MNIST_PIXEL_WIDTH, MNIST_PIXEL_HEIGHT)){
            throw new RuntimeException("Cannot create custom drawing view");
        }

        mRedSeekBar = (SeekBar) findViewById(R.id.r_slider);
        mGreenSeekBar = (SeekBar) findViewById(R.id.g_slider);
        mBlueSeekBar = (SeekBar) findViewById(R.id.b_slider);
        mRedSeekBar.setOnSeekBarChangeListener(this);
        mGreenSeekBar.setOnSeekBarChangeListener(this);
        mBlueSeekBar.setOnSeekBarChangeListener(this);

        mClearButton = (Button) findViewById(R.id.clear_canvas_button);
        mDetectButton = (Button) findViewById(R.id.recognize_digit_button);
        mColorButton = (Button) findViewById(R.id.color_button);
        mDrawingColor = getCurrentColor();
        mColorButton.setBackgroundColor(mDrawingColor);
        mClearButton.setOnClickListener(this);
        mDetectButton.setOnClickListener(this);

        mDigitTextView = (TextView) findViewById(R.id.tv_digit);
        mProbTextView = (TextView) findViewById(R.id.tv_prob);

        // load Tensorflow libraries
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mDigitClassifier = DigitClassifier.create(getAssets(), MODEL_FILE, MNIST_PIXEL_WIDTH, TF_INPUT_NAME, TF_OUTPUT_NAME);
                    Log.d(TAG, "Loaded libraries");
                } catch (final Exception e) {
                    throw new RuntimeException("Error loading tensorflow library.", e);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        mDrawView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mDrawView.onPause();
        super.onPause();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == mRedSeekBar) {
            mRed = progress;
        } else if (seekBar == mGreenSeekBar) {
            mGreen = progress;
        } else if (seekBar == mBlueSeekBar) {
            mBlue = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, String.format("Color (%d, %d, %d)", mRed, mGreen, mBlue));
        mDrawingColor = getCurrentColor();
        mTrajectories.setTrajectoryColor(mDrawingColor);
        mColorButton.setBackgroundColor(mDrawingColor);
    }

    private int getCurrentColor(int red, int green, int blue) {
        int color = 0xFF000000;
        color |= (red << 16);
        color |= (green << 8);
        color |= (blue);
        return color;
    }

    private int getCurrentColor() {
        return getCurrentColor(mRed, mGreen, mBlue);
    }

}
