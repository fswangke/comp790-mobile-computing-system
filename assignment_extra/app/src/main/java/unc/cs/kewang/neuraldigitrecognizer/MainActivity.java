package unc.cs.kewang.neuraldigitrecognizer;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import unc.cs.kewang.neuraldigitrecognizer.view.DrawTrajectory;
import unc.cs.kewang.neuraldigitrecognizer.view.DrawView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    static {
        System.loadLibrary("tensorflow_inference");
    }

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
    private Button mFastDetectButton;
    private Button mColorButton;
    private TextView mDigitTextView;
    private TextView mProbTextView;
    private TextView mTfRunStats;
    private static final String FULL_MODEL_FILE = "file:///android_asset/mnist_frozen.pb";
    private static final String QUANTIZED_MODEL_FILE = "file:///android_asset/mnist_quantized.pb";
    private static final String TF_INPUT_NAME = "pixels";
    private static final String TF_OUTPUT_NAME = "output";
    private Executor executor = Executors.newSingleThreadExecutor();
    private DigitClassifier mDigitClassifier;
    private DigitClassifier mQuantizedDigitClassifier;

    private int mRed = 0;
    private int mGreen = 0;
    private int mBlue = 0;
    private int mDrawingColor = 0;
    private float[] mDigitPixels = new float[MNIST_PIXEL_HEIGHT * MNIST_PIXEL_WIDTH];
    private float[] mDigitProbs;

    private int softmax(float[] logits) {
        int max_index = 0;
        float max_logit = Float.MIN_VALUE;
        for (int i = 0; i < logits.length; ++i) {
            if (logits[i] > max_logit) {
                max_logit = logits[i];
                max_index = i;
            }
        }

        float softmax_sum = 0;
        for (int i = 0; i < logits.length; ++i) {
            logits[i] -= max_logit;
            logits[i] = (float) Math.exp(logits[i]);
            softmax_sum += logits[i];
        }

        for (int i = 0; i < logits.length; ++i) {
            logits[i] /= softmax_sum;
        }

        return max_index;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int digitClass = 0;
        float digitProb = 0.0f;
        switch (id) {
            case R.id.clear_canvas_button:
                mTrajectories.clear();
                mDrawView.reset();
                mDrawView.invalidate();

                mDigitTextView.setText("");
                mProbTextView.setText("");
                break;
            case R.id.fast_recognize_digit_button:
                mDigitPixels = mDrawView.getImagePixels();
                mDigitProbs = mQuantizedDigitClassifier.classifyImage(mDigitPixels);

                digitClass = softmax(mDigitProbs);
                digitProb = mDigitProbs[digitClass];

                mDigitTextView.setText(String.format("Digit: %d", digitClass));
                mProbTextView.setText(String.format("Probab: %.5f", digitProb));
                mTfRunStats.setText(mQuantizedDigitClassifier.getRuntimeStats());
                break;
            case R.id.recognize_digit_button:
                mDigitPixels = mDrawView.getImagePixels();
                mDigitProbs = mDigitClassifier.classifyImage(mDigitPixels);

                digitClass = softmax(mDigitProbs);
                digitProb = mDigitProbs[digitClass];

                mDigitTextView.setText(String.format("Digit: %d", digitClass));
                mProbTextView.setText(String.format("Probab: %.5f", digitProb));
                mTfRunStats.setText(mDigitClassifier.getRuntimeStats());
                break;
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
        if (!mDrawView.init(MNIST_PIXEL_WIDTH, MNIST_PIXEL_HEIGHT)) {
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
        mFastDetectButton = (Button) findViewById(R.id.fast_recognize_digit_button);
        mColorButton = (Button) findViewById(R.id.color_button);
        mDrawingColor = getCurrentColor();
        mColorButton.setBackgroundColor(mDrawingColor);
        mClearButton.setOnClickListener(this);
        mDetectButton.setOnClickListener(this);
        mFastDetectButton.setOnClickListener(this);

        mDigitTextView = (TextView) findViewById(R.id.tv_digit);
        mProbTextView = (TextView) findViewById(R.id.tv_prob);
        mTfRunStats = (TextView) findViewById(R.id.tf_runtime);

        // load Tensorflow libraries
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mDigitClassifier = DigitClassifier.create(getAssets(), FULL_MODEL_FILE, MNIST_PIXEL_WIDTH, TF_INPUT_NAME, TF_OUTPUT_NAME);
                    mQuantizedDigitClassifier = DigitClassifier.create(getAssets(), QUANTIZED_MODEL_FILE, MNIST_PIXEL_WIDTH, TF_INPUT_NAME, TF_OUTPUT_NAME);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (mTfRunStats.getVisibility() == View.VISIBLE) {
                mTfRunStats.setVisibility(View.INVISIBLE);
            } else {
                mTfRunStats.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
