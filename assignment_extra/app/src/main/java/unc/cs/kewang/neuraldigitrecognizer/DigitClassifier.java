package unc.cs.kewang.neuraldigitrecognizer;

import android.content.res.AssetManager;
import android.os.Trace;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;

/**
 * Created by kewang on 4/17/17.
 */

public class DigitClassifier {
    private String inputName;
    private String outputName;
    private String[] outputNames;
    private float[] outputs;
    private int inputSize;
    private static final String TAG = DigitClassifier.class.getSimpleName();
    private TensorFlowInferenceInterface mTFInference;

    public static DigitClassifier create(AssetManager assertManager, String modelFilename, int inputSize, String inputName, String outputName) throws IOException {
        DigitClassifier digitClassifier = new DigitClassifier();
        digitClassifier.inputSize = inputSize;
        digitClassifier.inputName = inputName;
        digitClassifier.outputName = outputName;
        try {

            digitClassifier.mTFInference = new TensorFlowInferenceInterface(assertManager, modelFilename);
        } catch (final Exception e) {
            throw new RuntimeException("Error loading tensorflow library.", e);
        }

        int numClasses = (int)digitClassifier.mTFInference.graph().operation(outputName).output(0).shape().size(1);

        digitClassifier.outputNames = new String[]{outputName};
        digitClassifier.outputs = new float[numClasses];

        return digitClassifier;
    }

    public float[] classifyImage(final float[] pixels) {
        mTFInference.feed(inputName, pixels, 1, inputSize * inputSize);
        mTFInference.run(outputNames);
        mTFInference.fetch(outputName,outputs);

        return outputs;
    }
}
