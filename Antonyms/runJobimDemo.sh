
export CLASSPATH=$CLASSPATH:./lib/RiTaWN.jar
export CLASSPATH=$CLASSPATH:./lib/RiTaWN.zip
export CLASSPATH=$CLASSPATH:./lib/core.jar
export CLASSPATH=$CLASSPATH../lib/java-json.jar
export CLASSPATH=$CLASSPATH:./lib/jaws-bin.jar
export CLASSPATH=$CLASSPATH:./lib/supportWN.jar
export CLASSPATH=$CLASSPATH:./lib/ejml-0.24.jar

export CLASSPATH=.:$CLASSPATH
export CLASSPATH=$CLASSPATH:./src/watson.jar

echo java -Xmx10G -cp "$CLASSPATH" AntonymChecker 
java -Xmx16G -cp "$CLASSPATH" MatrixAntonymCheckerForJobimDemo 

