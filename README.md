

## Downlaod and build libsnark for android
See : [Zkrypto libsnark repository](https://github.com/snp-labs/libsnark-optimization).


## Build shared library

Set enviroment variables :

```bash
export ANDROID_API_LEVEL=23
export ANDROID_NDK=20.0.5594570
export LIBSNARK_DIR=${PWD}../libsnark-optimization 
```

Extract object files and build shared library :

```bash
mkdir -p o-files 

cd o-files

~/Library/Android/sdk/ndk/${ANDROID_NDK}/toolchains/llvm/prebuilt/darwin-x86_64/bin/aarch64-linux-android-ar -x \
    ${LIBSNARK_DIR}/lib/android_release/lib/libSnark_aarch64.a 

~/Library/Android/sdk/ndk/${ANDROID_NDK}/toolchains/llvm/prebuilt/darwin-x86_64/bin/aarch64-linux-android${ANDROID_API_LEVEL}-clang++ \
    -flto -fuse-ld=gold -shared *.o \
    -L../app/src/main/jniLibs/arm64-v8a \
    -lssl -lcrypto -lgmp  -lomp -ldl -llog  \
    -o ../app/src/main/jniLibs/arm64-v8a/libSnark.so

cd ..

```


### Open this folder with Android Studio

