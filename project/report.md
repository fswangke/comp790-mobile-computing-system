# DanceDanceConvolution
In this project, we developed a mobile game ``DanceDanceConvolution'', which
mimics the famous music video game ``Dance Dance Revolution''. Traditional DDR
game requires special hardware, for example, an Arcade machine, or a Kinect for
home playing. Instead, our mobile app doesn't require any dedicated hardware.
By utilizing state-of-the-art artificial intelligence approach, we infer the
player's movement from the live camera stream.

Different from traditional DDR-like games, our app shines with its minimal
requirement on hardware. In addition to the game logic and the user interface,
the core of our app is to infer the human player pose from live camera stream.
Our solution is to use deep learning methods (mostly convolutional neural
networks, thus the name DanceDanceConvolution) to directly infer the human
player movement from the video stream.

Deep learning as a technique, gained its popularity in the recent few years,
partly because of the increased computation powers (especially GPUs) and the
availability of much bigger datasets. Our project, although heavily relies on
CNNs, only does run-time inference on the mobile platform. Training of the
original network on large-scale datasets is done with desktop GPUs.

## Roles
The project is developed by me and Zhen Wei. My roles included:

1. Camera preview and camera preprocessing (for example YUV->RGB)
1. Building the TensorFlow runtime inference engine for the arm-architecture platform.
1. CNN architecture design and optimization.
1. Training/Converting the pose estimation model on desktop GPUs.
1. Preparing TensorFlow model for the Android mobile platform. For example, freezing/trimming/quantizing the model weights.

## TensorFlow on Mobile Architecture
We choose the TensorFlow framework as the mobile platform runtime inference
engine. TensorFlow supports Android by providing a minimal inference engine.
The core TensorFlow engine is implemented in native C++, thus the mobile
android inference engine has a native dynamic library which provides all the
essential functionality for runtime inference. A Java binding package is
provided as a separate JAR file. To deploy the TensorFlow runtime engine in
Android project, we need to first build the native inference engine (.so native
dynamic library) and the Java binding interface (.jar package).

To use the TensorFlow inference engine on Android, we need a pre-trained model
in TensorFlow format. Once the TensorFlow inference interface is initialized
with a pre-trained neural network model, player poses can be inferred
relatively easily:

1. Feed data to the convolutional neural network (CNN) input
1. Run input data through CNN via forward-propagation 
1. Fetch result from the CNN output nodes

The inferred pose is then received by the app game logic. The game would then
score the player, and moves on to the next frame.

## Customizing CNN on Mobile Platforms

We based our pose estimation on a state-of-the-art network method [Cite rtpose
paper]. Please refer to the paper for more technical details.

However, although the original method achieved near real-time performance on a
high-end desktop GPU on video/camera input, the proposed network is too heavy
for mobile platforms. Notice that on Android, the inference engine actually
runs on mobile CPUs rather than GPUs. iOS platforms support limited GPU
functionality via metal. But there is no simple and unified GPU programming
interface on Android because of the vast differences among Android hardware
platforms. Thus, on Android mobile platform, we need to trim the CNN model to
make it more efficient on ARM-based CPU architectures.

We made the following changes to the original network architectures to make the
inference applicable on Android:
1. Discard the image pyramid of the input image. Use only one input scale of the input image.
1. Downsample the input image to 224x224 instead of the original 368x368 input image resolutions.
1. Discarding the part affinity field computation and association stages. We assume there's only one player in the game, thus simply performing non-maximum suppression in the output is sufficient to get the single human player pose from the CNN output.
1. Compression of the neural network. We quantize the network weights from 16-bit floating-point numbers to 8-bit fixed-point numbers. 
1. Android camera preview provides pixel data in YUV420 format. But the neural network model requires RGB format pixel. To improve the performance, we perform the YUV420 to RGB888 conversion via native C++ code.

## Prepare the Pre-Trained Model
TensorFlow models the neural network computations on a graph. During training,
the TensorFlow engine saves checkpoint containing all the variables.

The first step of preparing the TensorFlow model is to merge the checkpoint
variable weights and the graph definition into a single binary file (in
protobuf format). At inference time, many operations in the computation graph
can be thrown away, to optimize the model file size and model load time. After
the merge and optimization, we also tried to quantize/compress the model file
to 8-bit representation.

## Issues
Although we tried many engineering tricks to make the inference as efficient as
possible on the mobile platform, on a Qualcomm Snapdragon 820 processor, the
inference can only run at ~ 2 seconds/frame. This is enough for a
proof-of-concept app, but still not enough for interactive playing. Instead of
running inference on mobile platform, we can try to use a client-server mode,
doing the inference on the cloud.
