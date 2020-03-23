set -x
project=javautil-7.4
current_dir=`basename pwd`
if [ ! $project = $current_dir ] ; then
   echo project $project is not current_dir $current_dir  >&2
fi 
 
#git init
cp ~/templates/.gitignore .gigignore
git config --global user.email "james.joseph.schmidt@gmail.com"
git remote add origin ssh://git@github.com/techwanker/$project
git remote set-url origin ssh://git@github.com/techwanker/$project
git remote -v
git add -A .
git commit
git push -u origin master
