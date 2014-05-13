#include <jni.h>
#include <android/log.h>

#include <opencv2/opencv.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <cmath>
#include <string>

#define DEBUG_TAG "Foo"

std::string getImgType(int imgTypeInt)
{
    int numImgTypes = 35; // 7 base types, with five channel options each (none or C1, ..., C4)

    int enum_ints[] =       {CV_8U,  CV_8UC1,  CV_8UC2,  CV_8UC3,  CV_8UC4,
                             CV_8S,  CV_8SC1,  CV_8SC2,  CV_8SC3,  CV_8SC4,
                             CV_16U, CV_16UC1, CV_16UC2, CV_16UC3, CV_16UC4,
                             CV_16S, CV_16SC1, CV_16SC2, CV_16SC3, CV_16SC4,
                             CV_32S, CV_32SC1, CV_32SC2, CV_32SC3, CV_32SC4,
                             CV_32F, CV_32FC1, CV_32FC2, CV_32FC3, CV_32FC4,
                             CV_64F, CV_64FC1, CV_64FC2, CV_64FC3, CV_64FC4};

    std::string enum_strings[] = {"CV_8U",  "CV_8UC1",  "CV_8UC2",  "CV_8UC3",  "CV_8UC4",
                             "CV_8S",  "CV_8SC1",  "CV_8SC2",  "CV_8SC3",  "CV_8SC4",
                             "CV_16U", "CV_16UC1", "CV_16UC2", "CV_16UC3", "CV_16UC4",
                             "CV_16S", "CV_16SC1", "CV_16SC2", "CV_16SC3", "CV_16SC4",
                             "CV_32S", "CV_32SC1", "CV_32SC2", "CV_32SC3", "CV_32SC4",
                             "CV_32F", "CV_32FC1", "CV_32FC2", "CV_32FC3", "CV_32FC4",
                             "CV_64F", "CV_64FC1", "CV_64FC2", "CV_64FC3", "CV_64FC4"};

    for(int i=0; i<numImgTypes; i++)
    {
        if(imgTypeInt == enum_ints[i]) return enum_strings[i];
    }
    return "unknown image type";
}

cv::Mat elbp(cv::Mat image, int radius, int neighbors) {
	cv::Mat dst(image.rows - 2 * radius, image.cols - 2 * radius, CV_32SC1);

	// allocate memory for result
	// zero
	dst.setTo(0);
	for (int n = 0; n < neighbors; n++) {
		// sample points
		float x = static_cast<float>(radius
				* cos(2.0 * CV_PI * n / static_cast<float>(neighbors)));
		float y = static_cast<float>(-radius
				* sin(2.0 * CV_PI * n / static_cast<float>(neighbors)));
		// relative indices
		int fx = static_cast<int>(floor(x));
		int fy = static_cast<int>(floor(y));
		int cx = static_cast<int>(ceil(x));
		int cy = static_cast<int>(ceil(y));
		// fractional part
		float ty = y - fy;
		float tx = x - fx;
		// set interpolation weights
		float w1 = (1 - tx) * (1 - ty);
		float w2 = tx * (1 - ty);
		float w3 = (1 - tx) * ty;
		float w4 = tx * ty;
		// iterate through your data
		for (int i = radius; i < image.rows - radius; i++) {
			for (int j = radius; j < image.cols - radius; j++) {
				// calculate interpolated value
				float t = static_cast<float>(w1
						* image.at<unsigned char>(i + fy, j + fx)
						+ w2 * image.at<unsigned char>(i + fy, j + cx)
						+ w3 * image.at<unsigned char>(i + cy, j + fx)
						+ w4 * image.at<unsigned char>(i + cy, j + cx));
				// floating point precision, so check some machine-dependent epsilon
				dst.at<int>(i - radius, j - radius) += ((t
						> image.at<unsigned char>(i, j))
						|| (std::abs(t - image.at<unsigned char>(i, j))
								< std::numeric_limits<float>::epsilon())) << n;
			}
		}
	}

	return dst;
}

static cv::Mat histc(const cv::Mat& src, int minVal, int maxVal, bool normed)
{
    cv::Mat result;
    // Establish the number of bins.
    int histSize = maxVal-minVal+1;
    // Set the ranges.
    float range[] = { static_cast<float>(minVal), static_cast<float>(maxVal+1) };
    const float* histRange = { range };
    // calc histogram

    cv::calcHist(&src, 1, 0, cv::Mat(), result, 1, &histSize, &histRange, true, false);
    // normalize
    if(normed) {
        result /= (int)src.total();
    }
    return result.reshape(1,1);
}

static cv::Mat spatial_histogram(cv::Mat src, int numPatterns,
                             int grid_x, int grid_y, bool normed)
{
    // calculate LBP patch size
    int width = src.cols/grid_x;
    int height = src.rows/grid_y;
    // allocate memory for the spatial histogram
    cv::Mat result = cv::Mat::zeros(grid_x * grid_y, numPatterns, CV_32FC1);
    // return matrix with zeros if no data was given
    if(src.empty())
        return result.reshape(1,1);
    // initial result_row
    int resultRowIdx = 0;
    // iterate through grid
    for(int i = 0; i < grid_y; i++) {
        for(int j = 0; j < grid_x; j++) {
        	cv::Mat src_cell = cv::Mat(src, cv::Range(i*height,(i+1)*height), cv::Range(j*width,(j+1)*width));
        	cv::Mat cell_hist = histc(cv::Mat_<float>(src_cell), 0, (numPatterns-1), true);
            // copy to the result matrix
        	cv::Mat result_row = result.row(resultRowIdx);
            cell_hist.reshape(1,1).convertTo(result_row, CV_32FC1);
            // increase row count in result matrix
            resultRowIdx++;
        }
    }
    // return result as reshaped feature vector
    return result.reshape(1,1);
}


extern "C" {
JNIEXPORT void JNICALL Java_uk_co_gpigc_androidapp_FaceSystemActivity_computeFaceData(
		JNIEnv * jniEnv, jclass jniClass, jobjectArray jniImage, jint jniWidth,
		jint jniHeight, jlong outputMatAddress) {
	int numPixels = jniEnv->GetArrayLength(jniImage);
	jbyteArray dim = (jbyteArray) jniEnv->GetObjectArrayElement(jniImage, 0);
	int numChannels = jniEnv->GetArrayLength(dim);

	unsigned char* imageDataFlat = new unsigned char[numPixels * numChannels];
	for (int i = 0; i < numPixels; i++) {
		jbyteArray jniRow = (jbyteArray) jniEnv->GetObjectArrayElement(jniImage,
				i);
		jniEnv->GetByteArrayRegion(jniRow, 0, numChannels,
				reinterpret_cast<signed char*>(&imageDataFlat[i * 3]));
		jniEnv->DeleteLocalRef(jniRow);
	}

	cv::Mat image(jniWidth, jniHeight, CV_8UC3, imageDataFlat);

	cv::Mat imageGray;
	cv::cvtColor(image, imageGray, CV_RGB2GRAY);

	const int gridX = 8;
	const int gridY = 8;

	cv::Mat patternImage = elbp(imageGray, 1, 8);
	cv::Mat query = spatial_histogram(patternImage,
			static_cast<int>(std::pow(2.0, static_cast<double>(8))),
			gridX, gridY, true);

	cv::Mat* mat = reinterpret_cast<cv::Mat*>(outputMatAddress);
	query.copyTo(*mat);

	__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%f]",
			query.at<float>(0));
	__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%f]",
			query.at<float>(1));
}
}
