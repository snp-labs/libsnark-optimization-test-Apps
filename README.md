

## Downlaod and build libsnark for android
See : [Zkrypto libsnark repository](https://github.com/snp-labs/libsnark-optimization).


## Build shared library

Set enviroment variables :

```bash
export ANDROID_API_LEVEL=23
export ANDROID_NDK=20.0.5594570
export LIBSNARK_DIR=${PWD}/../libsnark-optimization
export BUILD_TYPE=debug
```

Extract object files and build shared library :

```bash
cp ~/Library/Android/sdk/ndk/${ANDROID_NDK}/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/lib/aarch64-linux-android/libc++_shared.so \
    app/src/main/jniLibs/arm64-v8a/libc++_shared.so

mkdir -p o-files 

cd o-files

~/Library/Android/sdk/ndk/${ANDROID_NDK}/toolchains/llvm/prebuilt/darwin-x86_64/bin/aarch64-linux-android-ar -x \
    ${LIBSNARK_DIR}/lib/android_${BUILD_TYPE}/lib/libSnark_arm64.a 

~/Library/Android/sdk/ndk/${ANDROID_NDK}/toolchains/llvm/prebuilt/darwin-x86_64/bin/aarch64-linux-android${ANDROID_API_LEVEL}-clang++ \
    -flto -fuse-ld=gold -shared *.o \
    -L../app/src/main/jniLibs/arm64-v8a \
    -lssl -lcrypto -lgmp -lomp -lc++_shared -ldl -llog  \
    -o ../app/src/main/jniLibs/arm64-v8a/libSnark.so

cd ..   

```


Extract object files and build x86_64 shared library if your host (android simulator) cpu is Intel :

```bash
cp ~/Library/Android/sdk/ndk/${ANDROID_NDK}/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/lib/x86_64-linux-android/libc++_shared.so \
    app/src/main/jniLibs/x86_64/libc++_shared.so

mkdir -p o-files-sim 

cd o-files-sim

~/Library/Android/sdk/ndk/${ANDROID_NDK}/toolchains/llvm/prebuilt/darwin-x86_64/bin/x86_64-linux-android-ar -x \
    ${LIBSNARK_DIR}/lib/android_${BUILD_TYPE}/lib/libSnark_x86_64.a 

~/Library/Android/sdk/ndk/${ANDROID_NDK}/toolchains/llvm/prebuilt/darwin-x86_64/bin/x86_64-linux-android${ANDROID_API_LEVEL}-clang++ \
    -flto -fuse-ld=gold -shared *.o \
    -L../app/src/main/jniLibs/x86_64 \
    -lssl -lcrypto -lgmp -lomp -lc++_shared -ldl -llog  \
    -o ../app/src/main/jniLibs/x86_64/libSnark.so

cd ..

```


### Open this folder with Android Studio
