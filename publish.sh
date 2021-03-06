set -x
rm -rf /common/scratch/javautil-6
rsync -tav ~/java-projects/diamond-19/javautil-6/ /common/scratch/javautil-6
cd /common/scratch/javautil-6 && rm -rf .git
cd /common/scratch/javautil-6 && rm -rf .gitignore

git init
git config --global user.name "Jim Schmidt"
cp ~/templates/.gitignore .
pwd
echo .gitignore is
cat .gitignore
sleep 3
#git config --global user.email james.joseph.schmidt@gmail.com
git remote add origin git@github.com/techwanker/javautil-6.git
git remote set-url origin ssh://git@github.com/techwanker/javautil-6
git add -A .
git pull 
git commit -m "snapshot"
git push -u origin master

