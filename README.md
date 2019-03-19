This assignment will increase your understanding of image compression. We have studied JPEG
compression in class, which works by transforming the image to the frequency domain and quantizing the frequency coefficients in that domain. Here you will implement a common but contrasting method using "vector quantization". Quantization or more formally scalar quantization, as you know, is a way to represent (or code) one sample of a continuous signal with a discrete value. Vector quantization on the contrary codes a group or block of samples (or a vector of samples) using a single discrete value or index.
Why does this work, or why should this work? Most natural images are not a random collection of pixels but have very smooth varying areas – where pixels are not changing rapidly. Consequently, we could pre-decide a codebook of vectors, each vector represented by a block of two pixels (or four pixels etc.) and then replace all similar looking blocks in the image with one of the code vectors. The number of vectors, or the length of the code book used, will depend on how much error you are willing to tolerate in your compression. More vectors will result in larger coding indexes (and hence less compression) but results are perceptually better and vice versa. Thus, vector quantization may be described as a lossy compression technique where groups of samples are given one index that represents a code word. In general, this can work in n dimensions, we will start your implementation to two dimensions to perform vector quantization on an image and later extend it to higher dimensions. Your program will be invoked as follows
MyCompression.exe myImage.ext N mode
Your result should present images side by side – original and output. The output would be computed depending on three parameters:
• An input image, all images are standard CIF size 352x288 and supplied in two formats – myImage.raw (if it is a single channel 8 bit per pixel image) or myImage.rgb (if it is a color three channel 24 bits per pixel image). Please refer to the readme with the images for the format. Your code in assignment 1 can be used to display these images.
• N which gives you several vectors for quantization, so that each vector in the input can ultimately use log2N bits. Expect this input to be a power of 2.
• Mode, which suggests how pixels should be grouped to form vectors. For this assignment, these are the following values the variable can take
o 1 – suggesting two side by side pixels (whether gray or color) form a vector o 2 – suggesting a 2x2 block of pixels (whether gray or color) form a vector o 3 – suggesting a 4x4 block of pixels (whether gray or color) form a vector
For the sake of understanding, the following description outlines in detail the working for a 2- dimensional vector where each vector is composed of two side by side pixels for a gray image. i.e. for the case:
MyCompression.exe myImage.raw 16 1
Here are the steps that you need to implement to compress an image for such a condition.
1. Understanding your two pixel vector space to see what vectors your image contains
2. Initialization of codewords - select N (16 in this case) initial codewords
3. Clustering vectors around each code word
4. Refine and Update your code words depending on outcome of step 3.
Repeat steps 3 and 4 until code words don’t change or the change is very minimal.
5. Quantize input vectors to produce output image
