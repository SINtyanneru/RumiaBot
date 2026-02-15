#!/bin/sh
BASE_DIR=$(pwd)
BUILD_DIR="$BASE_DIR/build"

mkdir -p "$BUILD_DIR"

#るみさんBOT
echo 'るみさんBOTをビルドします...'
cd $BASE_DIR/Bot/
mvn clean package
mv $BASE_DIR/Bot/target/rumisanbot-1.0-full.jar $BUILD_DIR/bot.jar

#BaseSystem
echo "ﾍﾞｰｽｼｽﾃﾑをビルドします..."
cd $BASE_DIR/BaseSystem/
mvn clean package
mv $BASE_DIR/BaseSystem/target/base_system-1.0-full.jar $BUILD_DIR/os.jar

cd $BASE_DIR/