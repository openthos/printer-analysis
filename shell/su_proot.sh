###
#run programs as root
#author:bboxhe@gmail.com
###
ARGS="$@"
su -c "cd `pwd` && sh proot.sh $ARGS"
