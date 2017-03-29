package unc.edu.kewang.opencv4androidexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private final static String TAG = MainActivity.class.getSimpleName();
    private CameraBridgeViewBase mCameraView;
    private Mat mEdge;

    private BaseLoaderCallback mOpenCVLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mCameraView.enableView();
                    mCameraView.enableFpsMeter();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_java_camera_view);
        mCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraView != null) {
            mCameraView.disableView();
            mCameraView.disableFpsMeter();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraView != null) {
            mCameraView.disableView();
            mCameraView.disableFpsMeter();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Loading OpenV");

        if (!OpenCVLoader.initDebug()) {
            Log.i(TAG, "Using OpenCV Manager for initialization.");
            if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mOpenCVLoaderCallback)) {
                Log.e(TAG, "Cannot load OpenCV library.");
            }
        } else {
            Log.i(TAG, "Using internal OpenCV libraries.");
            mOpenCVLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mEdge = new Mat(width, height, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Imgproc.Canny(inputFrame.gray(), mEdge, 80, 100);
        return mEdge;
    }
}
