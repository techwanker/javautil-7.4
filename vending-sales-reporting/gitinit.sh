set -x
 
git init
cp ~/templates/.gitignore .gigignore
git add -A .
git commit -m "first commit"
git config --global user.email "james.joseph.schmidt@gmail.com"
git remote add origin ssh://git@github.com/techwanker/vending-sales-reporting.git
git remote set-url origin ssh://git@github.com/techwanker/vending-sales-reporting.git
git remote -v
git push -u origin master
