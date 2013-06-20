for x in *.cpp; do
    echo $x
    cat $x | sed -e 's/notify/event/g'\
           | sed -e 's/becomeInactive/exitProgram/g' > docasny
    mv docasny $x
done
    
