# README
1. Download the icons by running `./scripts/get-icons.sh` from the root directory of the repository
2. On OSX (probably works on windows too..) run `./gradlew <something>` to create the APK
3. Make sure your module strips unused resources from your final APK by adding the following to your build file. Read about it more [here](http://tools.android.com/tech-docs/new-build-system/resource-shrinking)
```
android {
    ...

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
```