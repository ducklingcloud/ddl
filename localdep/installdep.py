#!/usr/bin/python

import os
from subprocess import call

def install_local(gid,aid,ver):
    goal="install:install-file"
    file="-Dfile=./"+aid+'-'+ver+'.jar'
    cmd=["mvn",goal,file,"-DgroupId="+gid,"-DartifactId="+aid,
         "-Dversion="+ver, "-Dpackaging=jar"]
    print cmd
    call(cmd)
    
def cp_from_localrepo(gid,aid,ver):
    gidpath=gid.replace('.','/')
    jarfile=os.path.expanduser(
        "~/.m2/repository/"+gidpath+'/'+aid+'/'+ver+'/'
        +aid+'-'+ver+'.jar')
    cmd=["cp",jarfile,"."]
    print cmd
    call(cmd)
        
def get_jars(gid,aid,ver):
    depget="org.apache.maven.plugins:maven-dependency-plugin:2.10:get"
    repo="-DremoteRepositories="\
        "http://nexus.cerc.cnic.cn/nexus/content/groups/cerc"
    artifact="-Dartifact="+gid+':'+aid+':'+ver
    cmd=["mvn",depget,repo,artifact]
    print cmd
    call(cmd)


with open("./dep-wanted") as fn:
    lines = fn.readlines()

count=0
for line in lines:
    p = line.strip().split(':')
    if len(p) >= 4:
        gid=p[0]; aid=p[1]; ver=p[3]
        install_local(gid,aid,ver)
        count += 1

print len(lines), count

