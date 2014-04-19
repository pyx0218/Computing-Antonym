
export CLASSPATH=$CLASSPATH:../lib/RiTaWN.jar
export CLASSPATH=$CLASSPATH:../lib/RiTaWN.zip
export CLASSPATH=$CLASSPATH:../lib/core.jar
export CLASSPATH=$CLASSPATH:../lib/java-json.jar
export CLASSPATH=$CLASSPATH:../lib/jaws-bin.jar
export CLASSPATH=$CLASSPATH:../lib/supportWN.jar

export CLASSPATH=.:$CLASSPATH

cd src 
rm -rf *.class */*.class
javac *.java -cp "$CLASSPATH" 

STATUS=$?

if [ $STATUS -eq 0 ];then

	jar cvf watson.jar *
	rm -rf *.class */*.class
	cd ..
echo "Build Completed"

else 
	echo "Fail"
fi
