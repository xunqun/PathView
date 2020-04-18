## Publish navigation module to jCenter

Firstly change the version name. Then, at command line, use the
following command

This library is upload to JCenter based on the following instructions:


- [Novoda's Github](https://github.com/novoda/bintray-release)
- [6 easy steps to upload your Android library to Bintray/JCenter](https://medium.com/@anitaa_1990/6-easy-steps-to-upload-your-android-library-to-bintray-jcenter-59e6030c8890)

Uses this command to publish this library to the JCenter

    ./gradlew bintrayUpload -PbintrayUser={userid_bintray} -PbintrayKey={apikey_bintray} -PdryRun=false
