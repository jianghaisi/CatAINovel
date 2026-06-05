# 鏅€氶暅鍍忔瀯寤猴紝闅忕郴缁熺増鏈瀯寤?amd/arm
docker build -t /group-buy-market-app:3.0 -f ./Dockerfile .

# 鍏煎 amd銆乤rm 鏋勫缓闀滃儚
# docker buildx build --load --platform linux/amd64,linux/arm64 -t /group-buy-market-app:1.2 -f ./Dockerfile . --push