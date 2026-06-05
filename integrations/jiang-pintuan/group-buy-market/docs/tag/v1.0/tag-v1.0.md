## 1. 妫€鍑轰唬鐮?

```java
mkdir group-buy-market
cd group-buy-market        
git clone -b tag-v1.0 https://gitcode.net/KnowledgePlanet/group-buy-market.git
```

## 2. 鎵撳寘浠ｇ爜

```java
mvn clean install
```

## 3. 鏋勫缓闀滃儚

```java
cd group-buy-market-app
chmod +x build.sh
```

## 4. 閮ㄧ讲椤圭洰

```java
cd /dev-ops/group-buy-market/group-buy-market/docs/tag/v1.0
docker-compose -f docker-compose-app-v1.0.yml up -d
```

