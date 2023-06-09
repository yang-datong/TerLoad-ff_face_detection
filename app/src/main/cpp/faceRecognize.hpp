#ifndef MAIN_HPP_VDJI1OUJ
#define MAIN_HPP_VDJI1OUJ

#include "opencv2/face/facerec.hpp"
#include "opencv2/opencv.hpp"
#include <fstream>
#include <iostream>
#include <locale>
#include <vector>

#include <android/log.h>
#include <dirent.h>
#include <jni.h>
#include <sys/stat.h>
#include <sys/unistd.h>
#include <unistd.h>
#include <utility>

#define TAG "FaceRecognize"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

using std::string;
using std::vector;

class FaceRecognizer {
public:
  double keys;
  string labelFile;
  string localModelFile;
  std::vector<string> labelsName;
  cv::Ptr<cv::face::FaceRecognizer> recognizer = nullptr;

  FaceRecognizer(string workPath, string folderPath, cv::Size newSize,
                 const string &cascadeFile, int arithmetic);

  int FillData(vector<cv::Mat> &images, vector<int> &labels);

  int FetchModel();

  int FindImages(const string &folderPath, cv::CascadeClassifier &faceCascade,
                 const cv::Size &newSize, vector<cv::Mat> &images,
                 vector<int> &labels, vector<string> &labelsName, int &index);

private:
  string _workPath;
  string _folderPath;
  cv::Size _newSize;
  string _cascadeFile;
  int _arithmetic;
  cv::CascadeClassifier _faceCascade;

  const double EIGEN_FACE_KEYS = 4500.0;
  const double LBPH_FACE_KEYS = 83.0;
  const double FISHER_FACE_KEYS = 4500.0;
};

#endif /* end of include guard: MAIN_HPP_VDJI1OUJ */
