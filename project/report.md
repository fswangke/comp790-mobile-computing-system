# DanceDanceConvolution

## TensorFlow on Mobile Architecture

build inference engine in native code, and have a convenient java binding to do inference only
you feed in the input data, you run the inference/forward-feed, and then fetch the result back to your app

## Pose Estimation using Convolutional Neural Networks

## Customizing CNN on Mobile Platforms

1. Trim architecture, use only one scale
1. Use small scale image resolutions
1. discarding the part affinity field, we're only assuming only one player in the game
1. Another option is transffering data back to server and send back the result


## Improving performances

1. YUV420 to RGB conversion in native code
1. quantize floating number computations into fixed-point computations
1. freeze computation graph into fixed graph, throw away operations not needed at inference time
