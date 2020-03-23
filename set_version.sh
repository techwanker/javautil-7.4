#set -e -x
for file in $(find . -name "pom.xml") 
do
    from_text="VER-7.3.0"
    to_text="VER-7.4.0"
    echo examining $file
    if grep $from_text $file ; then  
        echo modifying $file
        sed -e "s/${from_text}/${to_text}/" -i $file
    fi
done
