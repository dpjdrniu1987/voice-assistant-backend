# Dockerfile说明

在项目根目录下，执行下列命令，即可编译并上传镜像

> docker build -t voiceassistant:latest -f docker-file/Dockerfile .

> docker tag voiceassistant:latest 119.3.4.205/mingdu/voiceassistant:latest

> docker push 119.3.4.205/mingdu/voiceassistant:latest