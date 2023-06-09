#include "faceRecognize.hpp"

FaceRecognizer *gFace;
int gArithmetic = 2; // Default use LBPHFaceRecognizer
cv::Size gNewSize = cv::Size(92, 112);
std::string gDirectory;
std::string gWorkPath;
std::string gCascadeFile;

FaceRecognizer::FaceRecognizer(string workPath, string folderPath,
                               cv::Size newSize, const string &cascadeFile,
                               int arithmetic)
        : _workPath(std::move(workPath)), _folderPath(std::move(folderPath)),
          _newSize(newSize), _cascadeFile(cascadeFile), _arithmetic(arithmetic) {
    _faceCascade.load(cascadeFile);
}

int FaceRecognizer::FindImages(const string &folderPath,
                               cv::CascadeClassifier &faceCascade,
                               const cv::Size &newSize, vector<cv::Mat> &images,
                               vector<int> &labels, vector<string> &labelsName,
                               int &index) {
    DIR *dir;
    struct dirent *ent;
    if ((dir = opendir(folderPath.c_str())) != nullptr) {
        while ((ent = readdir(dir)) != nullptr) {
            if (ent->d_type == DT_REG) { // 判断是否是普通文件
                string filename = ent->d_name;
                string filepath = folderPath + "/" + filename;
                cv::Mat img = imread(filepath, cv::IMREAD_GRAYSCALE);
                if (img.empty())
                    continue;
                vector<cv::Rect> faces;
                faceCascade.detectMultiScale(img, faces, 1.1, 5);
                for (const auto &face : faces) {
                    cv::Mat face_roi = img(face);
                    if (face_roi.empty())
                        continue;
                    resize(face_roi, face_roi, newSize);
                    images.push_back(face_roi);
                    labels.push_back(index++);
                    labelsName.push_back(filename);
                }
            } else if (ent->d_type == DT_DIR && strcmp(ent->d_name, ".") != 0 &&
                       strcmp(ent->d_name, "..") != 0) { // 判断是否是文件夹
                string subFolderPath = folderPath + "/" + ent->d_name;

                if (FindImages(subFolderPath, faceCascade, newSize, images, labels,
                               labelsName, index) == -1) {
                    LOGE("FindImages Failed");
                    return -1;
                }
            }
        }
        closedir(dir);
    } else {
        LOGE("Open directory failed");
        return -1;
    }
    return 0;
}

int FaceRecognizer::FillData(vector<cv::Mat> &images, vector<int> &labels) {
    int index = 0;
    int status;
    status = FindImages(_folderPath, _faceCascade, _newSize, images, labels,
                        labelsName, index);
    if (status == -1) {
        LOGE("FindImages Failed");
        return -1;
    }
    LOGD("images count -> %ld", images.size());
    LOGD("labels count -> %ld", labels.size());
    LOGD("labelsName count -> %ld", labelsName.size());
    LOGD("index -> %d", index);

    std::ofstream ofs(labelFile);
    for (const auto &lab : labelsName)
        ofs << lab << std::endl;

    LOGD("Into -> %s()", __FUNCTION__);
    return 0;
}

int FaceRecognizer::FetchModel() {
    switch (_arithmetic) {
        case 1:
            recognizer = cv::face::EigenFaceRecognizer::create();
            localModelFile = _workPath + "/model/Eigen_model.yml";
            labelFile = _workPath + "/model/Eigen_labels.npy";
            keys = EIGEN_FACE_KEYS;
            break;
        case 2:
            recognizer = cv::face::LBPHFaceRecognizer::create();
            localModelFile = _workPath + "/model/LBPH_model.yml";
            labelFile = _workPath + "/model/LBPH_labels.npy";
            keys = LBPH_FACE_KEYS;
            break;
        case 3:
            recognizer = cv::face::FisherFaceRecognizer::create();
            localModelFile = _workPath + "/model/Fisher_model.yml";
            labelFile = _workPath + "/model/Fisher_labels.npy";
            keys = FISHER_FACE_KEYS;
            break;
    }

    if (access(localModelFile.c_str(), F_OK) != 0) {
        vector<cv::Mat> images;
        vector<int> labels;
        LOGD("Train done out model -> %s", localModelFile.c_str());
        struct timespec start_time{
        }, end_time{};

        LOGD("Filling image ...");
        clock_gettime(CLOCK_REALTIME, &start_time);
        if (FillData(images, labels) == -1) {
            LOGE("FillData Failed");
            return -1;
        }
        clock_gettime(CLOCK_REALTIME, &end_time);
        LOGD("Fill done");
        LOGD("Number of seconds -> %ld", end_time.tv_sec - start_time.tv_sec);

        LOGD("Training ...");
        clock_gettime(CLOCK_REALTIME, &start_time);
        recognizer->train(images, labels);
        clock_gettime(CLOCK_REALTIME, &end_time);
        LOGD("Number of seconds -> %ld", end_time.tv_sec - start_time.tv_sec);
        LOGD("Train done out model -> %s", localModelFile.c_str());

        recognizer->save(localModelFile);
    } else {
        LOGD("Reading local mode ...");
        recognizer->read(localModelFile);
    }

    if (labelsName.size() == 0) {
        std::ifstream f(labelFile);
        if (f.is_open()) {
            std::string line;
            while (std::getline(f, line)) {
                labelsName.push_back(line);
            }
            f.close();
            LOGD("Load labelsName size-> %d, last labelName-> %s", labelsName.size(),
                 labelsName[labelsName.size() - 1].c_str());
        }
    }
    LOGD("Into -> %s()", __FUNCTION__);
    return 0;
}

int CheckFilePath() {
    int err;
    string modeDir = gWorkPath + "/" + "model";
    err = access(modeDir.c_str(), F_OK);
    if (err == -1)
        err = mkdir(modeDir.c_str(), 0777);

    if (err == -1) {
        LOGE("Create directory failed");
        return -1;
    }

    err = access(gDirectory.c_str(), F_OK);
    if (err == -1) {
        LOGE("Don't find %s", gDirectory.c_str());
        return -1;
    }

    err = access(gCascadeFile.c_str(), F_OK);
    if (err == -1) {
        LOGE("Don't find %s", gCascadeFile.c_str());
        return -1;
    }

    //    if(gArithmetic != 1 && gArithmetic != 2 && gArithmetic != 3){
    if (gArithmetic != 2) {
        // TODO : Just support LBPHFaceRecognizer arithmetic at now, because depend
        // the fast update model,unless spend abundance time retrain model
        LOGE("Arithmetic format error");
        return -1;
    }
    return 0;
}

// 获取不包括路径和后缀的文件名
std::string inline getFileName(const std::string &filePath) {
    // 查找最后一个目录分隔符的位置
    size_t lastSlashPos = filePath.find_last_of("/\\");
    if (lastSlashPos == std::string::npos) {
        lastSlashPos = 0; // 如果找不到目录分隔符，则从头开始截取
    } else {
        ++lastSlashPos; // 如果找到目录分隔符，则从下一个位置开始截取
    }
    // 查找最后一个点号的位置，即文件后缀的位置
    size_t lastDotPos = filePath.find_last_of(".");
    if (lastDotPos == std::string::npos || lastDotPos < lastSlashPos) {
        // 如果找不到点号或点号在目录分隔符之前，则截取到字符串末尾
        lastDotPos = filePath.length();
    }
    // 截取文件名部分
    return filePath.substr(lastSlashPos, lastDotPos - lastSlashPos);
}

JNIEXPORT int JNICALL JNI_Initialization(JNIEnv *env, __unused jclass thi,
                                         jstring workPath,
                                         jstring dataDirectory,
                                         jstring cascadeFile,
                                         jint useArithmetic) {
    gWorkPath = env->GetStringUTFChars(workPath, nullptr);
    gDirectory = gWorkPath + "/" + env->GetStringUTFChars(dataDirectory, nullptr);
    gCascadeFile = gWorkPath + "/" + env->GetStringUTFChars(cascadeFile, nullptr);
    gArithmetic = useArithmetic;

    if (CheckFilePath()) {
        return -1;
    }
    gFace = new FaceRecognizer(gWorkPath, gDirectory, gNewSize, gCascadeFile,
                               gArithmetic);
    if (gFace->FetchModel() == -1) {
        LOGE("FetchModel failed");
        return -1;
    }
    return 0;
}

JNIEXPORT int JNICALL JNI_JustSaveFaceImage(JNIEnv *env, __unused jobject thi,
                                            jstring oldFaceImagePath) {
    // Load the cascade classifier
    cv::CascadeClassifier faceCascade;
    faceCascade.load(gCascadeFile);

    string filepath = env->GetStringUTFChars(oldFaceImagePath, nullptr);
    string userName = getFileName(filepath);
    LOGD("fileName -> %s", userName.c_str());

    cv::Mat grayImg = imread(filepath, cv::IMREAD_GRAYSCALE);
    cv::Mat img = imread(filepath, cv::IMREAD_COLOR);
    if (grayImg.empty())
        return -1;
    vector<cv::Rect> faces;
    faceCascade.detectMultiScale(grayImg, faces, 1.1, 5);

    long labelCount = gFace->labelsName.size();
    LOGD("labelCount -> %ld", labelCount);

    vector<cv::Mat> images;
    vector<int> labels;
    vector<string> newLabelsName;
    // string newLabelsName;

    cv::Mat face_roi;
    for (const auto &face : faces) {
        face_roi = grayImg(face); // save gray image
        // face_roi = img(face); //save color image
        if (face_roi.empty())
            continue;
        resize(face_roi, face_roi, gNewSize);
        images.push_back(face_roi);
        labels.push_back(labelCount++); // addition index "0"
        newLabelsName.emplace_back(userName);
        // newLabelsName = "User-Just-Now-Add";
        gFace->labelsName.emplace_back(userName);
    }

    if (!face_roi.empty()) {
        if (!cv::imwrite(filepath, face_roi)) {
            std::cerr << "Failed to save image." << std::endl;
            return -1;
        }
        if (gFace->recognizer.empty()) {
            LOGE("Pointer recognizer is empty");
            return -1;
        }

        struct timespec start_time{
        }, end_time{};
        LOGD("Model Updating ...");
        clock_gettime(CLOCK_REALTIME, &start_time);
        gFace->recognizer->update(images, labels);
        LOGD("Model Updating images:%d,labels:%d,labels_index:%d", images.size(),
             labels.size(), labelCount);
        clock_gettime(CLOCK_REALTIME, &end_time);
        LOGD("Number of seconds -> %ld", end_time.tv_sec - start_time.tv_sec);
        gFace->recognizer->write(gFace->localModelFile);
        LOGD("Update done out model -> %s", gFace->localModelFile.c_str());

        FILE *f = fopen(gFace->labelFile.c_str(), "a+");
        if (!f) {
            LOGE("Don't find the %s file", gFace->labelFile.c_str());
            return -1;
        }
        for (const auto &it : newLabelsName) {
            fprintf(f, "%s", (it + "\n").c_str());
        }
        fclose(f);
    } else {
        LOGE("empty");
        return -1;
    }

    return 0;
}

int faultToleranceNum = 0;

JNIEXPORT int JNICALL JNI_FaceDetection(JNIEnv *env, jobject thi,
                                        jlong matAddressGray,
                                        jlong matAddressRgba) {
    cv::Mat &mGr = *(cv::Mat *) matAddressGray;
    cv::Mat &mRgb = *(cv::Mat *) matAddressRgba;

    // Load the cascade classifier
    cv::CascadeClassifier faceCascade;
    faceCascade.load(gCascadeFile);

    // Detect faces
    std::vector<cv::Rect> faces;
    faceCascade.detectMultiScale(mGr, faces, 1.1, 2,
                                 (unsigned int) 0 | cv::CASCADE_SCALE_IMAGE,
                                 cv::Size(30, 30));

    // Draw rectangles around detected faces
    for (auto &face : faces) {
        rectangle(mRgb, face, cv::Scalar(255, 0, 0), 2, cv::LINE_AA);
        // resize(face_roi, face_roi, _newSize);
        int label = -1;
        double confidence = 0.0;
        gFace->recognizer->predict(mGr(face), label, confidence);
        // cv::putText(frame, std::to_string(confidence), cv::Point(face.x, face.y -
        // 10), cv::FONT_HERSHEY_SIMPLEX, 0.9, cv::Scalar(0, 255, 0), 2);;
        if (confidence < gFace->keys) {
            string name = gFace->labelsName[label];
            LOGD("index -> %d", label);
            LOGD("name -> %s", name.c_str());
            LOGD("predict -> %f", confidence);
            // To Java->String username
            jstring faceRecognizeUserName =
                    env->NewStringUTF(name.c_str());
            jfieldID fieldId =
                    env->GetFieldID(env->GetObjectClass(thi),
                                    "faceRecognizeUserName", "Ljava/lang/String;");
            env->SetObjectField(thi, fieldId, faceRecognizeUserName);
            return 0;

            //            jmethodID method =
            //            env->GetMethodID(env->GetObjectClass(thi), "showToast",
            //                                                "(Ljava/lang/String;)V");
            //            if (method == nullptr) {
            //                env->ThrowNew(env->FindClass("java/lang/NoSuchMethodError"),
            //                              "myMethod not found");
            //                return;
            //            }
            //
            //            //调用myMethod方法
            //            jstring str = env->NewStringUTF(name.c_str());
            //            env->CallVoidMethod(thi, method, str);

            // 显示绿色框
            // cv::rectangle(mRgb, face, cv::Scalar(0, 255, 0), 2);
            // cv::putText(mRgb, name, cv::Point(face.x, face.y - 10),
            // cv::FONT_HERSHEY_SIMPLEX, 0.9, cv::Scalar(0, 255, 0), 2);
        } else if (confidence > gFace->keys && confidence < gFace->keys + 8) {
            if (faultToleranceNum >= 10) {
                faultToleranceNum = 0;
                return -1;
            }
            faultToleranceNum++;
            LOGD("faultToleranceNum:%d predict:%f , keys:%f", faultToleranceNum,
                 confidence, gFace->keys);

        } else {
            // 显示红色框
            // cv::rectangle(mRgb, face, cv::Scalar(0, 0, 255), 2);
            // cv::putText(mRgb, "unknown", cv::Point(face.x, face.y - 10),
            // cv::FONT_HERSHEY_SIMPLEX, 0.9, cv::Scalar(0, 0, 255), 2);
            LOGD("Unknown predict:%f , keys:%f", confidence, gFace->keys);
        }
    }
    return 1;
}

// JNIEXPORT void JNICALL JNI_EyeDetection(JNIEnv *env, jobject thi,
//                                         jlong matAddressGray,
//                                         jlong matAddressRgba) {
//     cv::Mat &mGr = *(cv::Mat *) matAddressGray;
//     cv::Mat &mRgb = *(cv::Mat *) matAddressRgba;
//  Load the cascade classifier
//  cv::CascadeClassifier eyeCascade;
//  eyeCascade.load("/haarcascades/haarcascade_eye.xml");
//
//// Detect eyes
// std::vector<cv::Rect> eyes;
// eyeCascade.detectMultiScale(mGr, eyes, 1.1, 2, 0 | cv::CASCADE_SCALE_IMAGE,
// cv::Size(30, 30));
//
//// Draw rectangles around detected eyes
// for (auto & eye : eyes) {
//   rectangle(mRgb, eye, cv::Scalar(0, 255, 0), 2, cv::LINE_AA);
// }
//}

JNIEXPORT void JNICALL JNI_Close(__unused JNIEnv *env, __unused jclass thi) {
    if (gFace) {
        delete gFace;
        LOGD("gFace Closed");
    }
}

static JNINativeMethod nativeMethods[] = {
        {"JNI_FaceDetection",     "(JJ)I", (void *) JNI_FaceDetection},
        //        {"JNI_EyeDetection",      "(JJ)V", (void *) JNI_EyeDetection},
        {"JNI_Initialization",
                                  "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I",
                                           (void *) JNI_Initialization},
        {"JNI_JustSaveFaceImage", "(Ljava/lang/String;)I",
                                           (void *) JNI_JustSaveFaceImage},
        {"JNI_Close",             "()V",   (void *) JNI_Close}};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, __unused void *reserved) {
    JNIEnv *env = nullptr;
    jint result;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    jclass clazz = env->FindClass(
            "com/rl/ff_face_detection_terload/faceRecognize/FaceRecognize");
    if (clazz == nullptr) {
        return -1;
    }

    if (env->RegisterNatives(clazz, nativeMethods,
                             sizeof(nativeMethods) / sizeof(nativeMethods[0])) !=
        JNI_OK) {
        return -1;
    }

    result = JNI_VERSION_1_6;

    return result;
}
