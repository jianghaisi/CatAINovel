# 鍩虹闀滃儚
FROM openjdk:8-jre-slim

# 浣滆€?
MAINTAINER jiang

# 閰嶇疆
ENV PARAMS=""

# 鏃跺尯
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 娣诲姞搴旂敤
ADD target/s-pay-mall-ddd-app.jar /s-pay-mall-ddd-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /s-pay-mall-ddd-app.jar $PARAMS"]