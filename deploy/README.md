# 灏忕尗鍐欎綔閮ㄧ讲璇存槑

鏈洰褰曟彁渚涗袱绉嶉儴缃叉柟寮忥細

- `docker-compose.yml`锛氬彧閮ㄧ讲灏忕尗鍐欎綔銆?- `docker-compose.full.yml` + `full-deploy.sh`锛氳仈鍚堥儴缃插皬鐚啓浣溿€丮ySQL銆丷edis銆丷abbitMQ銆佹嫾鍥㈡敮浠樻湇鍔°€?
鎺ㄨ崘浣跨敤鑱斿悎閮ㄧ讲銆傚畠浼氳锛?
- 灏忕尗鍐欎綔鍓嶇杩愯鍦?`8080`銆?- 灏忕尗鍐欎綔鍚庣杩愯鍦ㄥ鍣ㄥ唴 `8001`銆?- 鎷煎洟鏀粯鏈嶅姟杩愯鍦?`8070`锛岀敤浜庢敮浠樺疂鍥炶皟銆?- MySQL 瀛樿处鍙峰拰璁㈠崟鏁版嵁銆?- Redis 瀛橀獙璇佺爜銆佺櫥褰曟€佸拰涓存椂鐘舵€併€?- RabbitMQ 渚涙嫾鍥㈡敮浠橀」鐩唴閮ㄤ簨浠朵娇鐢ㄣ€?
## 涓€閿仈鍚堥儴缃?
鏈」鐩凡缁忓唴缃嫾鍥㈡簮鐮侊細

```text
/root/opt/langgraph-novel-agent/integrations/jiang-pintuan/group-buy-market
/root/opt/langgraph-novel-agent/integrations/jiang-pintuan/s-pay-mall-ddd-market
```

鏈嶅姟鍣ㄥ彧闇€瑕佷笂浼犲畬鏁寸殑 `langgraph-novel-agent` 鏂囦欢澶癸紝鐒跺悗鎵ц锛?
```bash
cd /root/opt/langgraph-novel-agent
chmod +x deploy/full-deploy.sh
sudo SERVER_NAME=198.144.177.171 \
  HTTP_PORT=8080 \
  PAY_MALL_PORT=8070 \
  GROUP_BUY_PORT=8091 \
  ./deploy/full-deploy.sh
```

閮ㄧ讲瀹屾垚鍚庤闂細

```text
http://198.144.177.171:8080
```

鏀粯鏈嶅姟鍥炶皟鍦板潃鏄細

```text
http://198.144.177.171:8070/api/v1/alipay/alipay_notify_url
```

濡傛灉鏈嶅姟鍣ㄩ槻鐏寮€鍚簡锛岄渶瑕佹斁琛岋細

```bash
sudo ufw allow 8080/tcp
sudo ufw allow 8070/tcp
```

## 閰嶇疆鏂囦欢

鑱斿悎閮ㄧ讲浼氳嚜鍔ㄦ洿鏂?`deploy/docker.env` 閲岀殑鍏抽敭閰嶇疆锛?
```env
FRONTEND_URL=http://198.144.177.171:8080/
PAY_MALL_PUBLIC_URL=http://198.144.177.171:8070
PAY_MALL_RETURN_URL=http://198.144.177.171:8080/membership
PAY_MALL_BASE_URL=http://pay-mall:8070
MEMBERSHIP_PRO_PRODUCT_ID=member-pro
MEMBERSHIP_PLUS_PRODUCT_ID=member-plus
MEMBERSHIP_MAX_PRODUCT_ID=member-max
MEMBERSHIP_PRO_MARKET_TYPE=1
MEMBERSHIP_PLUS_MARKET_TYPE=1
MEMBERSHIP_MAX_MARKET_TYPE=1
MEMBERSHIP_PRO_ACTIVITY_ID=100123
MEMBERSHIP_PLUS_ACTIVITY_ID=100123
MEMBERSHIP_MAX_ACTIVITY_ID=100123
```

鍚箟锛?
- `PAY_MALL_PUBLIC_URL`锛氭敮浠樺疂浠庡叕缃戝洖璋冩嫾鍥㈡敮浠樻湇鍔＄敤銆?- `PAY_MALL_RETURN_URL`锛氱敤鎴锋敮浠樺畬鎴愬悗璺冲洖灏忕尗鍐欎綔浼氬憳椤点€?- `PAY_MALL_BASE_URL`锛氬皬鐚啓浣滃悗绔湪 Docker 缃戠粶閲岃皟鐢ㄦ嫾鍥㈡敮浠樻湇鍔＄敤銆?- `MEMBERSHIP_*_PRODUCT_ID`锛氬皬鐚啓浣滀細鍛樺椁愬搴旂殑鎷煎洟鏀粯鍟嗗搧 ID銆?- `MEMBERSHIP_*_MARKET_TYPE=1`锛氳〃绀轰細鍛樿喘涔颁細杩涘叆 `group-buy-market` 钀ラ攢閿佸崟娴佺▼銆?- `MEMBERSHIP_*_ACTIVITY_ID=100123`锛氬搴旀嫾鍥㈣惀閿€椤圭洰鍒濆鍖栨椿鍔ㄣ€?
## 鏌ョ湅鐘舵€佸拰鏃ュ織

```bash
sudo docker compose --env-file deploy/docker.env -f deploy/docker-compose.full.yml ps
sudo docker compose --env-file deploy/docker.env -f deploy/docker-compose.full.yml logs -f backend
sudo docker compose --env-file deploy/docker.env -f deploy/docker-compose.full.yml logs -f pay-mall
```

## 閲嶆柊閮ㄧ讲

淇敼浠ｇ爜鍚庨噸鏂版墽琛岋細

```bash
sudo SERVER_NAME=198.144.177.171 ./deploy/full-deploy.sh
```

## 娉ㄦ剰

- 涓嶈浣跨敤 `3000` 绔彛锛岄」鐩細閬垮紑瀹冦€?- 濡傛灉鏀逛簡 MySQL 瀵嗙爜锛岃鍚屾淇敼 `deploy/docker.env`銆?- 鎷煎洟鏀粯椤圭洰閲岀殑鏀粯瀹濇矙绠卞瘑閽ラ渶瑕佷綘鑷繁鎹㈡垚鐪熷疄鍙敤閰嶇疆銆?- 褰撳墠閮ㄧ讲瀹屾暣鍐呯疆骞跺鐢ㄤ簡 `group-buy-market` 鍜?`s-pay-mall-ddd-market` 涓や釜 DDD 椤圭洰锛涘皬鐚啓浣滃彧璐熻矗浼氬憳鏉冪泭鍜?AI 鍐欎綔鍑嗗叆銆?