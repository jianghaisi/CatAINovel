# 鏅€氶暅鍍忔瀯寤猴紝闅忕郴缁熺増鏈瀯寤?amd/arm
docker build -t /s-pay-mall-ddd-app:2.0 -f ./Dockerfile .

# 鍏煎 amd銆乤rm 鏋勫缓闀滃儚
# docker buildx build --load --platform liunx/amd64,linux/arm64 -t jiang/xfg-frame-archetype-app:1.0 -f ./Dockerfile . --push