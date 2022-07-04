

## Downlaod and build libsnark for iOS
See : [Zkrypto libsnark repository](https://github.com/snp-labs/libsnark-optimization).


## Copy or link libSnark library

```bash
export LIBSNARK_DIR=${PWD}/../libsnark-optimization 

ln -s ${LIBSNARK_DIR}/lib/ios_release/lib/libSnark_iphoneos.a native_libs/iphoneos_release/libSnark.a 

ln -s ${LIBSNARK_DIR}/lib/ios_release/lib/libSnark_iphonesimulator.a native_libs/iphonesimulator_release/libSnark.a


ln -s ${LIBSNARK_DIR}/lib/ios_debug/lib/libSnark_iphoneos.a native_libs/iphoneos_debug/libSnark.a 

ln -s ${LIBSNARK_DIR}/lib/ios_debug/lib/libSnark_iphonesimulator.a native_libs/iphonesimulator_debug/libSnark.a 

```

### Open this folder with Xcode
