# s-pay-mall-ddd-market锛圖DD 鏀粯 + 鎷煎洟钀ラ攢绀轰緥锛夐」鐩鏄庢枃妗?

> 鐩綍

- [1. 椤圭洰鏁翠綋浠嬬粛](#1-椤圭洰鏁翠綋浠嬬粛)
  - [1.1 杩欐槸涓粈涔堥」鐩甝(#11-杩欐槸涓粈涔堥」鐩?
  - [1.2 鎶€鏈爤](#12-鎶€鏈爤)
  - [1.3 DDD 鍒嗗眰锛堜綘鍙璁颁綇涓€鍙ヨ瘽锛塢(#13-ddd-鍒嗗眰浣犲彧瑕佽浣忎竴鍙ヨ瘽)
- [2. 妯″潡缁撴瀯涓庤亴璐(#2-妯″潡缁撴瀯涓庤亴璐?
  - [2.1 妯″潡渚濊禆鍏崇郴锛堣皝渚濊禆璋侊級](#21-妯″潡渚濊禆鍏崇郴璋佷緷璧栬皝)
  - [2.2 鍚勬ā鍧楄鏄嶿(#22-鍚勬ā鍧楄鏄?
- [3. 椤圭洰鍚姩涓庤繍琛岋紙灏忕櫧鐗堬級](#3-椤圭洰鍚姩涓庤繍琛屽皬鐧界増)
  - [3.1 鍚姩绫籡(#31-鍚姩绫?
  - [3.2 閰嶇疆鏂囦欢涓庣幆澧僝(#32-閰嶇疆鏂囦欢涓庣幆澧?
  - [3.3 闇€瑕佺殑澶栭儴鐜](#33-闇€瑕佺殑澶栭儴鐜)
- [4. 閰嶇疆鏂囦欢璇存槑锛坅pplication*.yml锛塢(#4-閰嶇疆鏂囦欢璇存槑applicationyml)
  - [4.1 application.yml](#41-applicationyml)
  - [4.2 application-dev.yml锛堥噸鐐癸級](#42-application-devyml閲嶇偣)
  - [4.3 鐢熶骇/娴嬭瘯閰嶇疆宸紓](#43-鐢熶骇娴嬭瘯閰嶇疆宸紓)
- [5. 鏁版嵁搴撹璁★紙MySQL锛塢(#5-鏁版嵁搴撹璁ysql)
  - [5.1 pay_order 琛ㄧ粨鏋刔(#51-pay_order-琛ㄧ粨鏋?
  - [5.2 瀛楁涓氬姟鍚箟瑙ｉ噴锛堝皬鐧界増锛塢(#52-瀛楁涓氬姟鍚箟瑙ｉ噴灏忕櫧鐗?
  - [5.3 DAO 涓?MyBatis SQL 瀵圭収](#53-dao-涓?mybatis-sql-瀵圭収)
- [6. 鏍稿績涓氬姟娴佺▼锛堣鍗?鏀粯/鎷煎洟/閫€娆撅級](#6-鏍稿績涓氬姟娴佺▼璁㈠崟鏀粯鎷煎洟閫€娆?
  - [6.1 涓嬪崟骞剁敓鎴愭敮浠樺崟锛坈reate_pay_order锛塢(#61-涓嬪崟骞剁敓鎴愭敮浠樺崟create_pay_order)
  - [6.2 鏀粯瀹濆洖璋冿紙alipay_notify_url锛塢(#62-鏀粯瀹濆洖璋僡lipay_notify_url)
  - [6.3 鎷煎洟缁勯槦鎴愬姛鍥炶皟锛坓roup_buy_notify锛塢(#63-鎷煎洟缁勯槦鎴愬姛鍥炶皟group_buy_notify)
  - [6.4 鏌ヨ鐢ㄦ埛璁㈠崟鍒楄〃锛坬uery_user_order_list锛塢(#64-鏌ヨ鐢ㄦ埛璁㈠崟鍒楄〃query_user_order_list)
  - [6.5 鐢ㄦ埛鍙戣捣閫€鍗曪紙refund_order锛塢(#65-鐢ㄦ埛鍙戣捣閫€鍗時efund_order)
  - [6.6 瓒呮椂鍏冲崟 Job锛圱imeoutCloseOrderJob锛塢(#66-瓒呮椂鍏冲崟-jobtimeoutcloseorderjob)
  - [6.7 鏈敮浠樻彁閱?Job锛圢oPayNotifyOrderJob锛塢(#67-鏈敮浠樻彁閱?jobnopaynotifyorderjob)
- [7. HTTP 鎺ュ彛娓呭崟锛圱rigger 灞傦級](#7-http-鎺ュ彛娓呭崟trigger-灞?
- [8. 浠ｇ爜鍒嗗眰鈥滄€庝箞璇烩€濇寚鍗楋紙浠庡鍒板唴锛塢(#8-浠ｇ爜鍒嗗眰鎬庝箞璇绘寚鍗椾粠澶栧埌鍐?
- [9. 绫讳笌鏂规硶璇存槑锛堟寜妯″潡褰掓。锛屽厛鏍稿績鍚庢瑕侊級](#9-绫讳笌鏂规硶璇存槑鎸夋ā鍧楀綊妗ｅ厛鏍稿績鍚庢瑕?
  - [9.1 s-pay-mall-ddd-app锛堝惎鍔ㄤ笌閰嶇疆锛塢(#91-s-pay-mall-ddd-app鍚姩涓庨厤缃?
  - [9.2 s-pay-mall-ddd-trigger锛圚TTP/Job/Listener 鍏ュ彛锛塢(#92-s-pay-mall-ddd-triggerhttpjoblistener-鍏ュ彛)
  - [9.3 s-pay-mall-ddd-domain锛堥鍩熸ā鍨嬩笌鏍稿績閫昏緫锛塢(#93-s-pay-mall-ddd-domain棰嗗煙妯″瀷涓庢牳蹇冮€昏緫)
  - [9.4 s-pay-mall-ddd-infrastructure锛堣惤鍦板疄鐜帮細DAO/Repository/Gateway锛塢(#94-s-pay-mall-ddd-infrastructure钀藉湴瀹炵幇daorepositorygateway)
  - [9.5 s-pay-mall-ddd-api锛堝澶栨帴鍙ｄ笌 DTO锛塢(#95-s-pay-mall-ddd-api瀵瑰鎺ュ彛涓?dto)
  - [9.6 s-pay-mall-ddd-types锛堥€氱敤鏋氫妇/甯搁噺/寮傚父/宸ュ叿锛塢(#96-s-pay-mall-ddd-types閫氱敤鏋氫妇甯搁噺寮傚父宸ュ叿)
- [10. 闄勫綍锛氬父瑙侀棶棰橈紙灏忕櫧鑷晳锛塢(#10-闄勫綍甯歌闂灏忕櫧鑷晳)

---

## 1. 椤圭洰鏁翠綋浠嬬粛

### 1.1 杩欐槸涓粈涔堥」鐩?

杩欐槸涓€涓?*鏀粯鍟嗗煄锛堜笅鍗?+ 鏀粯 + 鏌ヨ璁㈠崟 + 閫€娆撅級**锛屽苟涓旀敮鎸佷竴绉嶈惀閿€绫诲瀷锛?*鎷煎洟锛坓roup-buy锛?*銆?

浣犲彲浠ユ妸瀹冪悊瑙ｄ负锛?

- 鐢ㄦ埛閫夋嫨鍟嗗搧 鈫?涓嬪崟锛堢敓鎴愯鍗曞彿锛?
- 绯荤粺璋冪敤鏀粯瀹濈敓鎴愭敮浠橀〉闈紙杩斿洖涓€娈?HTML form锛?
- 鐢ㄦ埛鏀粯鎴愬姛 鈫?鏀粯瀹濆洖璋冩垜浠殑鎺ュ彛 鈫?鎴戜滑鎶婅鍗曠姸鎬佹敼鎴愨€滄敮浠樻垚鍔熲€?
- 濡傛灉鏄嫾鍥㈣鍗?鈫?杩樿璋冪敤鈥滄嫾鍥㈢郴缁熲€濊繘琛岀粨绠?閫€娆惧鐞?

DDD锛堥鍩熼┍鍔ㄨ璁★級鍙槸鎶婁唬鐮佸垎浜嗗眰锛岃涓氬姟閫昏緫鏇存竻鏅帮細

- trigger锛氬叆鍙ｏ紙HTTP銆佸畾鏃朵换鍔°€佹秷鎭洃鍚級
- domain锛氫笟鍔℃牳蹇冿紙璁㈠崟鎬庝箞鍒涘缓銆佹€庝箞鍙樻洿鐘舵€侊級
- infrastructure锛氭妧鏈疄鐜帮紙鏁版嵁搴?澶栭儴鎺ュ彛锛?

### 1.2 鎶€鏈爤

浠?`pom.xml` 涓庝唬鐮佸彲瑙侊細

- JDK 8
- Spring Boot 2.7.12
- MyBatis锛圶ML Mapper锛?
- MySQL 8 椹卞姩
- RabbitMQ锛圓MQP锛?
- Lombok锛堢敤浜庣敓鎴?getter/setter銆乥uilder 绛夛級
- 鏀粯瀹?SDK锛坅lipay-sdk-java锛?
- Retrofit2锛堢敤浜庤皟鐢ㄥ閮?HTTP 鎺ュ彛锛屽鎷煎洟绯荤粺銆佸晢鍝佹湇鍔°€佸井淇℃帴鍙ｏ級

### 1.3 DDD 鍒嗗眰锛堜綘鍙璁颁綇涓€鍙ヨ瘽锛?

> **trigger 璐熻矗鈥滄帴璇锋眰鈥濓紝domain 璐熻矗鈥滃仛涓氬姟鈥濓紝infrastructure 璐熻矗鈥滃共鑴忔椿绱椿锛圖B/RPC/MQ锛夆€濄€?*

---

## 2. 妯″潡缁撴瀯涓庤亴璐?

椤圭洰鏄竴涓?**Maven 澶氭ā鍧楀伐绋?*锛屾牴 `pom.xml` 澹版槑妯″潡锛?

- `s-pay-mall-ddd-api`
- `s-pay-mall-ddd-app`
- `s-pay-mall-ddd-domain`
- `s-pay-mall-ddd-trigger`
- `s-pay-mall-ddd-infrastructure`
- `s-pay-mall-ddd-types`

### 2.1 妯″潡渚濊禆鍏崇郴锛堣皝渚濊禆璋侊級

浠庡悇妯″潡 `pom.xml` 鍙帹鏂緷璧栨柟鍚戯細

```mermaid
graph TD
  app[s-pay-mall-ddd-app 鍚姩妯″潡] --> trigger[s-pay-mall-ddd-trigger 鍏ュ彛灞俔
  app --> infra[s-pay-mall-ddd-infrastructure 鍩虹璁炬柦]

  trigger --> api[s-pay-mall-ddd-api 鎺ュ彛/DTO]
  trigger --> domain[s-pay-mall-ddd-domain 棰嗗煙閫昏緫]
  trigger --> types[s-pay-mall-ddd-types 閫氱敤绫诲瀷]

  domain --> types
  infra --> domain
  infra --> types
```

瑙ｉ噴锛?

- **鍚姩鍙仛鏁村悎**锛歚app` 寮曠敤 `trigger + infra`锛岃 Spring 涓€娆℃壂鎻忓埌鎵€鏈?Bean銆?
- **trigger 涓嶇洿鎺ョ鏁版嵁搴?*锛歵rigger 璋?domain 鐨?service銆?
- **domain 涓嶅叧蹇冩暟鎹簱鎬庝箞鍐?*锛氶€氳繃 repository/port 鎺ュ彛闅旂銆?
- **infrastructure 鎵嶆槸鐪熸瀹炵幇**锛歊epository 瀹炵幇銆丏AO銆丮apper XML銆佸閮ㄦ帴鍙ｇ綉鍏崇瓑銆?

### 2.2 鍚勬ā鍧楄鏄?

#### s-pay-mall-ddd-app

- 鍚姩鍏ュ彛锛歚cn.bugstack.Application`
- 鏀惧悇绉?Spring 閰嶇疆锛氱嚎绋嬫睜銆丷abbitMQ銆丷etrofit銆佹敮浠樺疂閰嶇疆绛夈€?

#### s-pay-mall-ddd-trigger

- HTTP Controller锛堝澶栨帴鍙ｏ級
- Job锛堝畾鏃朵换鍔★細瓒呮椂鍏冲崟銆佹湭鏀粯鎻愰啋绛夛級
- MQ Listener锛堟秷鎭洃鍚細鏀粯鎴愬姛銆佹嫾鍥㈡垚鍔熴€侀€€娆炬垚鍔熺瓑锛?

鍖呬笂鏈夎鏄庯細`cn.bugstack.trigger.http` 鐨?`package-info.java` 鍐欎簡鈥淗TTP 鎺ュ彛鏈嶅姟鈥濄€?

#### s-pay-mall-ddd-domain

棰嗗煙鏍稿績锛?

- 璁㈠崟妯″瀷锛堝疄浣?鍊煎璞?鑱氬悎锛?
- 璁㈠崟鏈嶅姟锛堝垱寤鸿鍗曘€佹敮浠樻垚鍔熷彉鏇淬€侀€€娆剧瓑锛?
- 绔彛锛坧ort锛夛細瀵瑰閮ㄧ郴缁熺殑鎶借薄锛堝鍟嗗搧鏌ヨ銆佹嫾鍥㈤攣鍗?缁撶畻/閫€娆撅級
- 浠撳偍锛坮epository锛夛細瀵规暟鎹簱鐨勬娊璞?

#### s-pay-mall-ddd-infrastructure

鍩虹璁炬柦瀹炵幇锛?

- DAO锛坄IOrderDao`锛?
- MyBatis Mapper XML锛坄pay_order_mapper.xml`锛?
- Repository 瀹炵幇锛堟妸 domain 鐨勪粨鍌ㄦ帴鍙ｈ惤鍒?DAO锛?
- Gateway/Port 閫傞厤鍣細璋冪敤鎷煎洟绯荤粺銆佸井淇℃帴鍙ｇ瓑
- EventPublisher锛氬彂甯冩秷鎭紙渚嬪鏀粯鎴愬姛娑堟伅锛?

#### s-pay-mall-ddd-api

瀵瑰鈥滄帴鍙ｅ畾涔夊眰鈥濓細

- `IPayService`銆乣IAuthService`锛氶潰鍚?Controller 鐨勬帴鍙ｆ娊璞?
- DTO锛氭帴鍙ｅ叆鍙?鍑哄弬缁撴瀯锛堝 `CreatePayRequestDTO`銆乣QueryOrderListResponseDTO`锛?
- 閫氱敤鍝嶅簲 `Response<T>`

#### s-pay-mall-ddd-types

閫氱敤鑳藉姏锛?

- 甯搁噺 `Constants`
- 鏋氫妇/鍝嶅簲鐮佺瓑
- 寮傚父 `AppException`
- 寰俊 XML/绛惧悕宸ュ叿锛坄SignatureUtil`銆乣XmlUtil`銆乣MessageTextEntity`锛?

---

## 3. 椤圭洰鍚姩涓庤繍琛岋紙灏忕櫧鐗堬級

### 3.1 鍚姩绫?

鍚姩绫诲湪锛歚s-pay-mall-ddd-app/src/main/java/cn/bugstack/Application.java`

鏍稿績鐐癸細

- `@SpringBootApplication`锛歋pring Boot 鍚姩
- `@EnableScheduling`锛氬紑鍚畾鏃朵换鍔★紙椤圭洰閲屾湁 Job锛?

```java
@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }
}
```

### 3.2 閰嶇疆鏂囦欢涓庣幆澧?

`application.yml` 鎸囧畾锛氶粯璁ゆ縺娲?`dev` 鐜銆?

```yaml
spring:
  profiles:
    active: dev
```

鎵€浠ラ粯璁よ鍙栵細`application-dev.yml`銆?

### 3.3 闇€瑕佺殑澶栭儴鐜

浠?`application-dev.yml` 鑳界湅鍑烘潵锛屼綘鑷冲皯闇€瑕侊細

- MySQL锛堥粯璁よ繛鍒?`127.0.0.1:13306`锛?
- RabbitMQ锛堥粯璁?`127.0.0.1:5672`锛?

SQL 鍒濆鍖栬剼鏈湪锛?

- `docs/dev-ops/mysql/sql/s-pay-mall-ddd-market.sql`

---

## 4. 閰嶇疆鏂囦欢璇存槑锛坅pplication*.yml锛?

### 4.1 application.yml

浣嶇疆锛歚s-pay-mall-ddd-app/src/main/resources/application.yml`

| 閰嶇疆椤?| 鍚箟 |
|---|---|
| `spring.config.name` | 搴旂敤鍚嶏紙褰卞搷鏃ュ織/鏌愪簺閰嶇疆鍔犺浇锛?|
| `spring.profiles.active` | 婵€娲诲摢涓幆澧冮厤缃紙dev/test/prod锛?|

### 4.2 application-dev.yml锛堥噸鐐癸級

浣嶇疆锛歚s-pay-mall-ddd-app/src/main/resources/application-dev.yml`

#### 4.2.1 鏈嶅姟绔彛

| key | 绀轰緥 | 鍚箟 |
|---|---:|---|
| `server.port` | 8070 | 搴旂敤鍚姩绔彛 |

#### 4.2.2 搴旂敤鑷畾涔夐厤缃?app.config

```yaml
app:
  config:
    api-version: v1
    cross-origin: '*'
    group-buy-market:
      api-url: http://127.0.0.1:8091
      notify-url: http://127.0.0.1:8080/api/v1/alipay/group_buy_notify
      source: s01
      chanel: c01
```

瑙ｉ噴锛堝皬鐧界増锛夛細

- `api-version`锛氭帴鍙ｇ増鏈紙Controller 鐨勮矾寰勯噷鐢?`/api/v1/...`锛?
- `cross-origin`锛氳法鍩熼厤缃紙鍓嶇寮€鍙戦樁娈靛父鐢?`*`锛?
- `group-buy-market.api-url`锛氭嫾鍥㈢郴缁熷湴鍧€
- `group-buy-market.notify-url`锛氭嫾鍥㈢郴缁熷洖璋冩湰绯荤粺鐨勫湴鍧€
- `source/chanel`锛氭笭閬撴爣璇嗭紙涓氬姟涓婂尯鍒嗘潵婧愭笭閬擄級

#### 4.2.3 绾跨▼姹?thread.pool.executor.config

鐢ㄤ簬寮傛浠诲姟/骞跺彂鎵ц銆?

鍏抽敭椤癸細

- `core-pool-size`锛氭牳蹇冪嚎绋嬫暟
- `max-pool-size`锛氭渶澶х嚎绋嬫暟
- `block-queue-size`锛氶槦鍒楅暱搴?
- `policy`锛氭嫆缁濈瓥鐣ワ紙绀轰緥涓?`CallerRunsPolicy`锛氫换鍔″お澶氭椂鐢辫皟鐢ㄧ嚎绋嬭嚜宸辨墽琛岋級

#### 4.2.4 鏁版嵁搴?spring.datasource + hikari

鍏抽敭椤癸細

- `spring.datasource.url/username/password`锛歁ySQL 杩炴帴淇℃伅
- `driver-class-name`锛歁ySQL 椹卞姩
- `spring.hikari.*`锛氳繛鎺ユ睜鍙傛暟锛堟€ц兘鐩稿叧锛?

#### 4.2.5 RabbitMQ spring.rabbitmq

闄や簡鍩虹杩炴帴淇℃伅锛岄」鐩繕鎶婁氦鎹㈡満/璺敱/闃熷垪杩涜浜嗏€滈厤缃寲鈥濓紝鏀惧湪锛?

- `spring.rabbitmq.config.producer.*`
- `spring.rabbitmq.config.consumer.*`

杩欐剰鍛崇潃锛氫唬鐮侀噷鍙互閫氳繃閰嶇疆鎷垮埌闃熷垪/浜ゆ崲鏈哄悕绉帮紝鑰屼笉鏄啓姝汇€?

#### 4.2.6 MyBatis

| key | 鍚箟 |
|---|---|
| `mybatis.mapper-locations` | Mapper XML 鎵€鍦ㄤ綅缃?|
| `mybatis.config-location` | MyBatis 鍏ㄥ眬閰嶇疆 |

Mapper 鐩綍锛歚classpath:/mybatis/mapper/*.xml`锛堟垜浠凡缁忕湅鍒?`pay_order_mapper.xml`锛?

#### 4.2.7 寰俊 weixin.config

鐢ㄤ簬鍏紬鍙峰鎺ワ紙鎵爜鐧诲綍銆佹秷鎭敹鍙戯級锛?

- `originalid`锛氬叕浼楀彿鍘熷 ID
- `token`锛氶獙绛?token
- `app-id/app-secret`锛氬叕浼楀彿 app 鍑瘉

瀵瑰簲 Controller锛歚WeixinPortalController`

#### 4.2.8 鏀粯瀹?alipay

鐢ㄤ簬鐢熸垚鏀粯椤甸潰銆侀獙绛惧洖璋冦€侀€€娆剧瓑锛?

- `alipay_public_key`锛氭敮浠樺疂鍏挜锛堥獙绛剧敤锛?
- `merchant_private_key`锛氬晢鎴风閽ワ紙绛惧悕鐢級
- `notify_url`锛氭敮浠樺疂鎴愬姛鍚庡洖璋冨湴鍧€锛堥潪甯稿叧閿級
- `return_url`锛氭敮浠樺悗娴忚鍣ㄨ烦杞〉闈?
- `gatewayUrl`锛氭矙绠辩綉鍏?

瀵瑰簲 Controller锛歚AliPayController#payNotify`
瀵瑰簲 Domain Service锛歚OrderService#doPrepayOrder`銆乣refundPayOrder`

### 4.3 鐢熶骇/娴嬭瘯閰嶇疆宸紓

`application-prod.yml` / `application-test.yml` 涓?dev 鐩告瘮涓昏宸紓锛?

- `server.port`
- DB/RabbitMQ 鐨勮繛鎺ュ湴鍧€
- 鎷煎洟绯荤粺鍦板潃 `group-buy-market.api-url`

---

## 5. 鏁版嵁搴撹璁★紙MySQL锛?

SQL 鏂囦欢锛歚docs/dev-ops/mysql/sql/s-pay-mall-ddd-market.sql`

褰撳墠鏍稿績琛細`pay_order`

### 5.1 pay_order 琛ㄧ粨鏋?

> 琛ㄧ敤閫旓細淇濆瓨鐢ㄦ埛涓嬪崟涓庢敮浠樿繃绋嬬殑璁㈠崟淇℃伅銆?

| 瀛楁鍚?| 绫诲瀷 | 涓婚敭 | 鍙┖ | 涓氬姟鍚箟 |
|---|---|---:|---:|---|
| `id` | int unsigned | 鉁?| 鉂?| 鑷涓婚敭锛堢敤浜庡垎椤?鎺掑簭锛?|
| `user_id` | varchar(32) |  | 鉂?| 鐢ㄦ埛 ID |
| `product_id` | varchar(16) |  | 鉂?| 鍟嗗搧 ID |
| `product_name` | varchar(64) |  | 鉂?| 鍟嗗搧鍚嶇О |
| `order_id` | varchar(16) | 鍞竴 | 鉂?| 涓氬姟璁㈠崟鍙凤紙瀵瑰灞曠ず/鏀粯鐢級 |
| `order_time` | datetime |  | 鉂?| 涓嬪崟鏃堕棿 |
| `total_amount` | decimal(8,2) unsigned |  | 鉁?| 璁㈠崟鍘熶环閲戦 |
| `status` | varchar(32) |  | 鉂?| 璁㈠崟鐘舵€侊紙瑙佷笅鏂囷級 |
| `pay_url` | varchar(2014) |  | 鉁?| 鏀粯椤甸潰淇℃伅锛堟敮浠樺疂杩斿洖鐨?form锛?|
| `pay_time` | datetime |  | 鉁?| 鏀粯鏃堕棿 |
| `market_type` | tinyint(1) |  | 鉁?| 钀ラ攢绫诲瀷锛? 鏃犺惀閿€銆? 鎷煎洟钀ラ攢 |
| `market_deduction_amount` | decimal(8,2) |  | 鉁?| 钀ラ攢浼樻儬閲戦 |
| `pay_amount` | decimal(8,2) |  | 鉂?| 瀹為檯鏀粯閲戦 |
| `create_time` | datetime |  | 鉂?| 鍒涘缓鏃堕棿 |
| `update_time` | datetime |  | 鉂?| 鏇存柊鏃堕棿 |

绱㈠紩锛?

- `PRIMARY KEY (id)`
- `UNIQUE uq_order_id (order_id)`锛氫繚璇佽鍗曞彿鍞竴
- `INDEX idx_user_id_product_id (user_id, product_id)`锛氱敤浜庢煡鏌愮敤鎴锋煇鍟嗗搧鏈€杩戜竴绗旇鍗?

### 5.2 瀛楁涓氬姟鍚箟瑙ｉ噴锛堝皬鐧界増锛?

#### status锛堣鍗曠姸鎬侊級

浣犲彲浠ユ妸璁㈠崟鎯宠薄鎴愪細鈥滄垚闀库€濈殑锛?

- `CREATE`锛氳鍗曞垱寤哄畬鎴愶紝浣嗗彲鑳借繕娌＄敓鎴愭敮浠樺崟锛堟垨鑰呮敮浠樺崟娌″啓鍏ワ級
- `PAY_WAIT`锛氱瓑寰呮敮浠橈紙宸茬粡鐢熸垚鏀粯椤甸潰 pay_url锛?
- `PAY_SUCCESS`锛氭敮浠樻垚鍔燂紙鏀跺埌鏀粯瀹濆洖璋冿級
- `DEAL_DONE`锛氫氦鏄撳畬鎴愶紙鍙戣揣/鏍搁攢瀹屾垚锛?
- `CLOSE`锛氬叧闂紙瓒呮椂鏈粯銆佸彇娑堛€侀€€娆惧叧闂瓑锛?
- `MARKET`锛氳惀閿€缁撶畻鐘舵€侊紙鎷煎洟瀹屾垚鍚庯級
- `WAIT_REFUND`锛氱瓑寰呴€€娆撅紙鎷煎洟閫€娆惧満鏅級

> 娉ㄦ剰锛歋QL 娉ㄩ噴閲屼笌 Mapper/浠ｇ爜閲岀殑瀛楃涓插ぇ灏忓啓鍙兘涓嶅悓锛屼絾涓氬姟鍚箟涓€鑷淬€?

### 5.3 DAO 涓?MyBatis SQL 瀵圭収

DTO = Data Transfer Object锛堟暟鎹紶杈撳璞★級

DAO 鎺ュ彛锛歚s-pay-mall-ddd-infrastructure/.../IOrderDao.java`
Mapper锛歚s-pay-mall-ddd-app/src/main/resources/mybatis/mapper/pay_order_mapper.xml`
PO = Persistent Object锛堟寔涔呭寲瀵硅薄锛?
浣犲彲浠ョ悊瑙ｄ负锛氣€滀笓闂ㄧ敤鏉ュ拰鏁版嵁搴撹〃鎵撲氦閬撶殑 Java 瀵硅薄鈥濄€?

PO锛歚s-pay-mall-ddd-infrastructure/.../po/PayOrder.java`

| DAO 鏂规硶 | 瀵瑰簲 XML id | SQL 鍋氫粈涔?|
|---|---|---|
| `insert(PayOrder)` | `insert` | 鎻掑叆鏂拌鍗?|
| `queryUnPayOrder(PayOrder)` | `queryUnPayOrder` | 鏌ョ敤鎴锋煇鍟嗗搧鏈€杩戜竴绗旇鍗曪紙鐢ㄤ簬闃查噸/澶嶇敤鏈敮浠橈級 |
| `updateOrderPayInfo(PayOrder)` | `updateOrderPayInfo` | 鍐欏叆 pay_url銆佺姸鎬併€佽惀閿€閲戦銆佹敮浠橀噾棰?|
| `changeOrderPaySuccess(PayOrder)` | `changeOrderPaySuccess` | 鏇存柊涓烘敮浠樻垚鍔?+ pay_time |
| `queryTimeoutCloseOrderList()` | `queryTimeoutCloseOrderList` | 鏌ヨ秴鏃?30 鍒嗛挓鏈敮浠樿鍗?|
| `queryNoPayNotifyOrder()` | `queryNoPayNotifyOrder` | 鏌?1 鍒嗛挓鏈敮浠樿鍗曪紙鐢ㄤ簬鎻愰啋锛?|
| `changeOrderClose(String)` | `changeOrderClose` | 鍏冲崟锛堟敼 CLOSE锛?|
| `changeOrderMarketSettlement(List)` | `changeOrderMarketSettlement` | 鎵归噺鏀逛负 MARKETING 缁撶畻鎬?|
| `queryUserOrderList(userId,lastId,pageSize)` | `queryUserOrderList` | 鐢ㄦ埛璁㈠崟鍒嗛〉 |
| `queryOrderByUserIdAndOrderId(userId,orderId)` | `queryOrderByUserIdAndOrderId` | 鏌ユ煇鐢ㄦ埛鏌愯鍗?|
| `refundOrder(userId,orderId)` | `refundOrder` | 閫€娆?閫€鍗曟敼 CLOSE |
| `refundMarketOrder(userId,orderId)` | `refundMarketOrder` | 钀ラ攢閫€鍗曟敼 WAIT_REFUND |

---

## 6. 鏍稿績涓氬姟娴佺▼锛堣鍗?鏀粯/鎷煎洟/閫€娆撅級

杩欎竴绔犳槸鍏ㄩ」鐩渶閲嶈鐨勶細浣犵悊瑙ｄ簡娴佺▼锛屼唬鐮佸氨鈥滄湁鐢婚潰鈥濄€?

### 6.1 涓嬪崟骞剁敓鎴愭敮浠樺崟锛坈reate_pay_order锛?

鍏ュ彛锛歚AliPayController#createPayOrder(CreatePayRequestDTO)`

#### 鍏ュ弬锛圕reatePayRequestDTO锛?

鏉ヨ嚜 `s-pay-mall-ddd-api` 鐨?DTO锛堝瓧娈典互浠ｇ爜涓哄噯锛夛細

- `userId: String` 鐢ㄦ埛 ID
- `productId: String` 鍟嗗搧 ID
- `teamId: String` 鎷煎洟闃熶紞 ID锛堝彲绌猴紝鎷煎洟鏃朵娇鐢級
- `marketType: Integer` 钀ラ攢绫诲瀷锛堜緥濡?0 鏃犺惀閿€銆? 鎷煎洟锛?
- `activityId: Long` 娲诲姩 ID锛堟嫾鍥㈡椿鍔級

#### 鍑哄弬

- `Response<String>`锛歚data` 鏄敮浠樺疂鏀粯椤甸潰锛圚TML form 瀛楃涓诧級

#### 鏍稿績閫昏緫锛堟枃瀛楃増锛?

1. Controller 鎺ユ敹璇锋眰
2. 缁勮 `ShopCartEntity`锛堣喘鐗╄溅瀹炰綋锛?
3. 璋冪敤棰嗗煙鏈嶅姟锛歚orderService.createOrder(shopCartEntity)`
4. 杩斿洖 `PayOrderEntity.payUrl`

#### 鍏抽敭浠ｇ爜璋冪敤閾撅紙浼唬鐮侊級

```text
AliPayController.createPayOrder(dto)
  -> ShopCartEntity.builder(...)
  -> IOrderService.createOrder(shopCart)
      -> AbstractOrderService.createOrder(shopCart)
          -> repository.queryUnPayOrder(鏌ラ噸澶嶈鍗?
          -> port.queryProductByProductId(鏌ュ晢鍝?
          -> doSaveOrder(钀藉簱)
          -> 濡傛灉鏄嫾鍥? port.lockMarketPayOrder(钀ラ攢閿佸崟)
          -> doPrepayOrder(璋冪敤鏀粯瀹濈敓鎴愭敮浠樺崟)
              -> repository.updateOrderPayInfo(淇濆瓨 pay_url 绛?
  -> 杩斿洖 payUrl
```

#### 娴佺▼鍥撅紙Mermaid锛?

```mermaid
flowchart TD
  A[瀹㈡埛绔?璇锋眰 create_pay_order] --> B[AliPayController]
  B --> C[IOrderService.createOrder]
  C --> D{鏄惁瀛樺湪鏈敮浠樿鍗?}
  D -- 鏄? PAY_WAIT --> E[鐩存帴杩斿洖宸叉湁 payUrl]
  D -- 鏄? CREATE --> F[琛ュ垱寤烘敮浠樺崟]
  D -- 鍚?--> G[鏌ヨ鍟嗗搧淇℃伅]
  G --> H[鍒涘缓璁㈠崟骞惰惤搴揮
  H --> I{鏄惁鎷煎洟钀ラ攢?}
  I -- 鏄?--> J[璋冪敤鎷煎洟绯荤粺閿佸崟/浼樻儬]
  I -- 鍚?--> K[鐩存帴鎸夊師浠峰彂璧锋敮浠樺疂棰勬敮浠榏
  J --> K
  K --> L[淇濆瓨 pay_url/鏀粯閲戦/鐘舵€乚
  L --> M[杩斿洖 payUrl]
```

### 6.2 鏀粯瀹濆洖璋冿紙alipay_notify_url锛?

鍏ュ彛锛歚AliPayController#payNotify(HttpServletRequest)`

#### 鍏抽敭鍙傛暟

鏀粯瀹濆洖璋冧細甯﹀緢澶氬弬鏁帮紝杩欓噷椤圭洰涓昏鐢ㄥ埌锛?

- `trade_status`锛氫氦鏄撶姸鎬侊紙椤圭洰鍒ゆ柇鏄惁涓?`TRADE_SUCCESS`锛?
- `out_trade_no`锛氬晢鎴疯鍗曞彿锛堜篃灏辨槸鎴戜滑绯荤粺鐨?`orderId`锛?
- `gmt_payment`锛氭敮浠樻椂闂?
- `trade_no`锛氭敮浠樺疂浜ゆ槗鍙?
- `sign`锛氱鍚?

#### 鏍稿績閫昏緫

1. 濡傛灉 `trade_status != TRADE_SUCCESS`锛岃繑鍥?`false`
2. 浠?request 鎷垮埌鍙傛暟 map
3. 鐢?`AlipaySignature` + 閰嶇疆鐨?`alipay_public_key` 楠岀
4. 楠岀閫氳繃锛氳皟鐢?`orderService.changeOrderPaySuccess(orderId, payTime)`

### 6.3 鎷煎洟缁勯槦鎴愬姛鍥炶皟锛坓roup_buy_notify锛?

鍏ュ彛锛歚AliPayController#groupBuyNotify(NotifyRequestDTO)`

#### 鍏ュ弬锛圢otifyRequestDTO锛?

- `outTradeNoList: List<String>`锛氭嫾鍥㈢郴缁熼€氱煡锛氬摢浜涜鍗曞凡缁忕粍闃熸垚鍔燂紝闇€瑕佺粨绠?

#### 鏍稿績閫昏緫

- `orderService.changeOrderMarketSettlement(outTradeNoList)`
  - repository 鎵归噺鎶婅鍗曠姸鎬佹敼鎴?`MARKET`

### 6.4 鏌ヨ鐢ㄦ埛璁㈠崟鍒楄〃锛坬uery_user_order_list锛?

鍏ュ彛锛歚AliPayController#queryUserOrderList(QueryOrderListRequestDTO)`

#### 鍏ュ弬

- `userId: String`
- `lastId: Long`锛氫笂涓€椤垫渶鍚庝竴鏉¤褰曠殑 `id`锛堢敤浜庘€滄父鏍囧垎椤碘€濓級
- `pageSize: Integer`

#### 鍑哄弬

`QueryOrderListResponseDTO`锛?

- `orderList: List<OrderInfo>`锛氳鍗曚俊鎭垪琛?
- `hasMore: boolean`锛氭槸鍚﹁繕鏈変笅涓€椤?
- `lastId: Long`锛氭湰椤垫渶鍚庝竴鏉?id锛堜笅涓€椤电户缁紶锛?

#### 鏍稿績閫昏緫

- 澶氭煡 1 鏉★細`pageSize + 1`
- 濡傛灉瓒呰繃 pageSize锛岃鏄?hasMore=true锛岀劧鍚庢埅鏂?

### 6.5 鐢ㄦ埛鍙戣捣閫€鍗曪紙refund_order锛?

鍏ュ彛锛歚AliPayController#refundOrder(RefundOrderRequestDTO)`

#### 鍏ュ弬

- `userId: String`
- `orderId: String`

#### 鍑哄弬

`RefundOrderResponseDTO`锛?

- `success: boolean`
- `orderId: String`
- `message: String`

#### 棰嗗煙閫昏緫锛圤rderService#refundMarketOrder锛?

1. 鏍￠獙璁㈠崟瀛樺湪涓斿睘浜庣敤鎴凤細`repository.queryOrderByUserIdAndOrderId`
2. 濡傛灉璁㈠崟宸?CLOSE锛氱洿鎺ュけ璐?
3. 濡傛灉鏄嫾鍥?钀ラ攢璁㈠崟锛氬厛璋冪敤鎷煎洟绯荤粺鎵ц閫€鍗曪細`port.refundMarketPayOrder`
4. 鏍规嵁璁㈠崟鐘舵€侀€夋嫨锛?
   - `CREATE` / `PAY_WAIT`锛氱洿鎺?`repository.refundOrder`锛堜笉璧版敮浠樺疂閫€娆撅級
   - 鍏朵粬锛歚repository.refundMarketOrder` 鏀逛负 `WAIT_REFUND`

> 娉ㄦ剰锛氶」鐩噷杩樻湁 `refundPayOrder`锛屽畠浼氱湡鐨勮皟鐢ㄦ敮浠樺疂閫€娆炬帴鍙ｏ細`AlipayTradeRefundRequest`銆?

### 6.6 瓒呮椂鍏冲崟 Job锛圱imeoutCloseOrderJob锛?

浣嶇疆锛歚s-pay-mall-ddd-trigger/src/main/java/cn/bugstack/trigger/job/TimeoutCloseOrderJob.java`

瀹冧細锛?

- 鏌ヨ秴鏃舵湭鏀粯璁㈠崟锛歚orderService.queryTimeoutCloseOrderList()`
- 瀵规瘡涓鍗曟墽琛屽叧鍗曪細`orderService.changeOrderClose(orderId)`

Mapper 鐨勮秴鏃惰鍒欙細

- `status = 'PAY_WAIT' AND NOW() >= order_time + INTERVAL 30 MINUTE`

### 6.7 鏈敮浠樻彁閱?Job锛圢oPayNotifyOrderJob锛?

浣嶇疆锛歚s-pay-mall-ddd-trigger/src/main/java/cn/bugstack/trigger/job/NoPayNotifyOrderJob.java`

瀹冧細锛?

- 鏌ヤ笅鍗?1 鍒嗛挓浠嶆湭鏀粯鐨勮鍗曪紙鏈€澶?10 鏉★級
- 鍏蜂綋鈥滄€庝箞閫氱煡鐢ㄦ埛鈥濊鐪嬮」鐩疄鐜帮紙鍙兘鍙?MQ/寰俊妯℃澘娑堟伅绛夛級

---

## 7. HTTP 鎺ュ彛娓呭崟锛圱rigger 灞傦級

### 7.1 鏀粯鐩稿叧锛圓liPayController锛?

鍩虹璺緞锛歚/api/v1/alipay/`

| 鏂规硶 | 璺緞 | 鍏ュ弬 | 鍑哄弬 | 璇存槑 |
|---|---|---|---|---|
| POST | `create_pay_order` | `CreatePayRequestDTO` | `Response<String>` | 涓嬪崟骞惰繑鍥?payUrl锛堟敮浠橀〉闈?form锛?|
| POST | `group_buy_notify` | `NotifyRequestDTO` | `String` | 鎷煎洟绯荤粺鍥炶皟缁撶畻锛坰uccess/error锛?|
| POST | `alipay_notify_url` | form 鍥炶皟鍙傛暟 | `String` | 鏀粯瀹濆洖璋冿紙success/false锛?|
| POST | `query_user_order_list` | `QueryOrderListRequestDTO` | `Response<QueryOrderListResponseDTO>` | 鏌ヨ鐢ㄦ埛璁㈠崟鍒楄〃 |
| POST | `refund_order` | `RefundOrderRequestDTO` | `Response<RefundOrderResponseDTO>` | 鐢ㄦ埛閫€鍗?|

> `AliPayController` 瀹炵幇浜?`IPayService`锛屾墍浠ヨ繖浜涙柟娉曚篃绠楁槸瀵?`api` 妯″潡鎺ュ彛鐨勮惤鍦般€?

### 7.2 鐧诲綍鐩稿叧锛圠oginController锛?

鍩虹璺緞锛歚/api/v1/login/`

| 鏂规硶 | 璺緞 | 鍙傛暟 | 杩斿洖 | 璇存槑 |
|---|---|---|---|---|
| GET | `weixin_qrcode_ticket` | 鏃?| `Response<String>` | 鐢熸垚鎵爜鐧诲綍 ticket |
| GET | `weixin_qrcode_ticket_scene` | `sceneStr` | `Response<String>` | 甯﹀満鏅€肩殑 ticket |
| GET | `check_login` | `ticket` | `Response<String>` | 杞妫€鏌ユ槸鍚︾櫥褰曟垚鍔?|
| GET | `check_login_scene` | `ticket, sceneStr` | `Response<String>` | 甯﹀満鏅€肩殑鐧诲綍妫€鏌?|

### 7.3 寰俊娑堟伅鍏ュ彛锛圵eixinPortalController锛?

鍩虹璺緞锛歚/api/v1/weixin/portal/`

| 鏂规硶 | 璺緞 | 璇存槑 |
|---|---|---|
| GET | `receive` | 寰俊鏈嶅姟鍣ㄦ帴鍏ユ椂鐨勯獙绛?|
| POST | `receive` | 鎺ユ敹寰俊浜嬩欢/鏂囨湰娑堟伅锛堟壂鐮佺櫥褰曚簨浠朵細钀界櫥褰曠姸鎬侊級 |

---

## 8. 浠ｇ爜鍒嗗眰鈥滄€庝箞璇烩€濇寚鍗楋紙浠庡鍒板唴锛?

濡傛灉浣犳槸灏忕櫧锛屽缓璁寜杩欎釜椤哄簭鐪嬶細

1. **HTTP 鍏ュ彛**锛歚s-pay-mall-ddd-trigger/trigger/http/*Controller`
2. **棰嗗煙鏈嶅姟**锛歚s-pay-mall-ddd-domain/domain/*/service/*Service`
3. **棰嗗煙妯″瀷**锛歚domain/order/model/*`锛堝疄浣?鍊煎璞?鑱氬悎锛?
4. **浠撳偍鎺ュ彛锟斤拷锟界鍙ｆ帴鍙?*锛歚domain/*/adapter/repository`銆乣domain/*/adapter/port`
5. **鍩虹璁炬柦瀹炵幇**锛歚infrastructure/adapter/repository`銆乣infrastructure/adapter/port`
6. **DAO + Mapper XML + 琛ㄧ粨鏋?*锛歚infrastructure/dao` + `mybatis/mapper/*.xml` + SQL

---

## 9. 绫讳笌鏂规硶璇存槑锛堟寜妯″潡褰掓。锛屽厛鏍稿績鍚庢瑕侊級

> 璇存槑锛氳繖涓€绔犱互鈥滀綘鐪嬩唬鐮佹椂鏈€甯搁棶鐨勯棶棰樷€濅负瀵煎悜锛岃В閲婃瘡涓被鍦ㄥ共鍢涖€佸叧閿柟娉曠洰鐨勬槸浠€涔堛€?

### 9.1 s-pay-mall-ddd-app锛堝惎鍔ㄤ笌閰嶇疆锛?

#### 9.1.1 `cn.bugstack.Application`

- **鐢ㄩ€?*锛歋pring Boot 搴旂敤鍚姩鍏ュ彛銆?
- **鍏抽敭鐐?*锛歚@EnableScheduling` 璁?`trigger/job` 涓嬬殑瀹氭椂浠诲姟鐢熸晥銆?
- **main 鏂规硶**锛氬惎鍔?Spring 瀹瑰櫒銆?

### 9.2 s-pay-mall-ddd-trigger锛圚TTP/Job/Listener 鍏ュ彛锛?

#### 9.2.1 `AliPayController`

- **鐢ㄩ€?*锛氭敮浠樼浉鍏崇殑 HTTP 鎺ュ彛銆?
- **涓昏渚濊禆**锛?
  - `IOrderService orderService`锛氶鍩熸湇鍔★紙鐪熸鐨勪笟鍔￠€昏緫鍦?domain锛?
  - `AlipayClient alipayClient`锛氭敮浠樺疂 SDK 瀹㈡埛绔?
  - `alipayPublicKey`锛氶獙绛剧敤

鍏抽敭鏂规硶锛?

- `createPayOrder(CreatePayRequestDTO)`
  - **鐩殑**锛氬垱寤鸿鍗?+ 鐢熸垚鏀粯鍗曪紙payUrl锛?
  - **杩斿洖鍊?*锛歚Response<String>`锛宒ata 鏄?HTML form
- `payNotify(HttpServletRequest)`
  - **鐩殑**锛氭敮浠樺疂鍥炶皟锛岄獙绛惧苟鎶婅鍗曟敼鎴愭敮浠樻垚鍔?
  - **杩斿洖鍊?*锛氬瓧绗︿覆 `success` 琛ㄧず鏀粯瀹濆彲浠ュ仠姝㈤噸璇?
- `queryUserOrderList(QueryOrderListRequestDTO)`
  - **鐩殑**锛氬垎椤垫媺鍙栫敤鎴疯鍗?
- `refundOrder(RefundOrderRequestDTO)`
  - **鐩殑**锛氱敤鎴峰彂璧烽€€鍗曪紙钀ラ攢/闈炶惀閿€鍒嗘敮锛?

#### 9.2.2 `LoginController`

- **鐢ㄩ€?*锛氭壂鐮佺櫥褰曟祦绋嬫帶鍒躲€?
- **渚濊禆**锛歚ILoginService loginService`

鍏抽敭鏂规硶锛?

- `weixinQrCodeTicket()` / `weixinQrCodeTicket(sceneStr)`
  - **鐩殑**锛氱敓鎴愬井淇′簩缁寸爜 ticket
- `checkLogin(ticket)` / `checkLogin(ticket, sceneStr)`
  - **鐩殑**锛氳疆璇㈡鏌ョ櫥褰曠姸鎬侊紱杩斿洖 openidToken 琛ㄧず鐧诲綍鎴愬姛

#### 9.2.3 `WeixinPortalController`

- **鐢ㄩ€?*锛氬鎺ュ井淇″叕浼楀彿娑堟伅鍏ュ彛銆?
- `validate(...)`锛氬井淇℃湇鍔″櫒鎺ュ叆楠岀
- `post(...)`锛氭帴鏀舵秷鎭紱鎵爜浜嬩欢锛圫CAN锛変細鎶婄櫥褰曠姸鎬佸啓鍏ワ紙`loginService.saveLoginState(ticket, openid)`锛?

### 9.3 s-pay-mall-ddd-domain锛堥鍩熸ā鍨嬩笌鏍稿績閫昏緫锛?

#### 9.3.1 `IOrderService`

- **鐢ㄩ€?*锛氶鍩熷眰瀵光€滆鍗曡兘鍔涒€濈殑鎶借薄鎺ュ彛銆?

鏂规硶璇存槑锛?

- `PayOrderEntity createOrder(ShopCartEntity shopCartEntity)`
  - **鍏ュ弬**锛氳喘鐗╄溅瀹炰綋锛堢敤鎴枫€佸晢鍝併€佽惀閿€绫诲瀷绛夛級
  - **杩斿洖**锛氭敮浠樺崟瀹炰綋锛堝寘鍚?orderId銆乸ayUrl锛?
  - **鐩殑**锛氱粺涓€灏佽鈥滄牎楠岄噸澶嶈鍗?鈫?鍒涘缓璁㈠崟 鈫?閿佽惀閿€ 鈫?鐢熸垚鏀粯瀹濇敮浠樺崟鈥濈殑瀹屾暣杩囩▼銆?

- `void changeOrderPaySuccess(String orderId, Date orderTime)`
  - **鐩殑**锛氬鐞嗘敮浠樻垚鍔熷悗鐨勮鍗曠姸鎬佸彉鏇淬€?

- `List<String> queryNoPayNotifyOrder()`
  - **鐩殑**锛氭煡鏈敮浠樻彁閱掕鍗曪紙缁?Job 鐢級銆?

- `List<String> queryTimeoutCloseOrderList()`
  - **鐩殑**锛氭煡瓒呮椂鏈敮浠樿鍗曪紙缁欏叧鍗?Job 鐢級銆?

- `boolean changeOrderClose(String orderId)`
  - **鐩殑**锛氭妸璁㈠崟鍏虫帀銆?

- `void changeOrderMarketSettlement(List<String> outTradeNoList)`
  - **鐩殑**锛氭嫾鍥㈢郴缁熼€氱煡缁撶畻鍚庯紝鎵归噺鏀硅鍗曠姸鎬併€?

- `List<OrderEntity> queryUserOrderList(String userId, Long lastId, Integer pageSize)`
  - **鐩殑**锛氱敤鎴疯鍗曞垎椤垫煡璇€?

- `boolean refundMarketOrder(String userId, String orderId)`
  - **鐩殑**锛氳惀閿€閫€鍗曪紙鍏堟嫾鍥㈤€€闃?閫€鍗曪紝鐒跺悗鏀规湰鍦扮姸鎬侊級銆?

- `boolean refundPayOrder(String userId, String orderId)`
  - **鐩殑**锛氱湡姝ｈ皟鐢ㄦ敮浠樺疂閫€娆撅紙闇€瑕佹敮浠樺疂 SDK锛夈€?

#### 9.3.2 `AbstractOrderService`

- **鐢ㄩ€?*锛氳鍗曢鍩熸湇鍔＄殑鈥滄祦绋嬫ā鏉库€濓紙鎶婇€氱敤娴佺▼鍐欏ソ锛岀粏鑺備氦缁欏瓙绫诲疄鐜帮級銆?
- **浣犲彲浠ョ悊瑙ｄ负**锛?
  - 杩欏氨鏄€滀笅鍗曟祦绋嬬殑鎬诲婕斺€濄€?
  - 瀛愮被 `OrderService` 璐熻矗鈥滄紨鍛樷€濓紙钀藉簱銆佽皟鐢ㄥ閮ㄣ€佺敓鎴愭敮浠樺崟锛夈€?

鏍稿績鏂规硶锛?

- `createOrder(ShopCartEntity shopCartEntity)`
  - **鐩殑**锛氬疄鐜板垱寤鸿鍗曠殑瀹屾暣缂栨帓娴佺▼锛堥槻閲嶅銆佹煡鍟嗗搧銆佸垱寤鸿鍗曘€佽惀閿€閿佸崟銆侀鏀粯锛夈€?
  - **鍏抽敭鍒嗘敮**锛?
    - 濡傛灉瀛樺湪鏈敮浠樿鍗曪細鐩存帴澶嶇敤 payUrl
    - 濡傛灉璁㈠崟瀛樺湪浣嗘病 payUrl锛氳ˉ鐢熸垚鏀粯鍗?
    - 濡傛灉娌℃湁璁㈠崟锛氬垱寤烘柊璁㈠崟骞剁敓鎴愭敮浠樺崟

鎶借薄鏂规硶锛堝瓙绫诲繀椤诲疄鐜帮級锛?

- `doSaveOrder(CreateOrderAggregate orderAggregate)`锛氫繚瀛樿鍗曪紙repository/DAO锛?
- `lockMarketPayOrder(...)`锛氳惀閿€閿佸崟锛堣皟鐢ㄦ嫾鍥㈢郴缁燂級
- `doPrepayOrder(...)`锛氶鏀粯锛堣皟鐢ㄦ敮浠樺疂骞朵繚瀛?pay_url锛?

#### 9.3.3 `OrderService`

- **鐢ㄩ€?*锛歚AbstractOrderService` 鐨勫叿浣撳疄鐜般€?

鍏抽敭鏂规硶锛?

- `doSaveOrder(...)`
  - **瀹炵幇鐩殑**锛氳皟鐢?repository 钀藉簱銆?

- `lockMarketPayOrder(...)`
  - **瀹炵幇鐩殑**锛氳皟鐢?`IProductPort` 鐨勮惀閿€閿佸崟鑳藉姏锛堟嫾鍥㈢郴缁燂級銆?

- `doPrepayOrder(...)`
  - **瀹炵幇鐩殑**锛氳皟鐢ㄦ敮浠樺疂 SDK 鐢熸垚鏀粯椤甸潰 form锛屽苟鍐欏叆璁㈠崟琛ㄣ€?
  - **鍏ュ弬鍏抽敭鐐?*锛?
    - `totalAmount`锛氬師浠?
    - `marketPayDiscountEntity`锛氳惀閿€浼樻儬淇℃伅锛堟嫾鍥㈡椂鍙兘鏈変紭鎯狅級
  - **杈撳嚭**锛歚PayOrderEntity`锛坥rderId/payUrl/status/钀ラ攢閲戦/鏀粯閲戦锛?

- `changeOrderPaySuccess(orderId, payTime)`
  - **鎷煎洟鍒嗘敮**锛?
    - 鏈湴璁㈠崟鏍囪鎷煎洟鏀粯鎴愬姛
    - 璋冪敤鎷煎洟绯荤粺缁撶畻锛堝疄闄呴」鐩噷鍙兘浼氱敤 MQ + 琛ュ伩锛?
  - **闈炴嫾鍥?*锛氱洿鎺ユ敼璁㈠崟鏀粯鎴愬姛

- `refundMarketOrder(userId, orderId)`
  - **鐩殑**锛氱敤鎴蜂富鍔ㄩ€€鍗曪紙钀ラ攢鍗曞厛閫氱煡鎷煎洟绯荤粺锛?

- `refundPayOrder(userId, orderId)`
  - **鐩殑**锛氳皟鐢ㄦ敮浠樺疂閫€娆炬帴鍙ｏ紝鎴愬姛鍚庢敼鏈湴鐘舵€?

### 9.4 s-pay-mall-ddd-infrastructure锛堣惤鍦板疄鐜帮細DAO/Repository/Gateway锛?

#### 9.4.1 `IOrderDao`

- **鐢ㄩ€?*锛歁yBatis DAO 鎺ュ彛銆?
- **瀵瑰簲 XML**锛歚pay_order_mapper.xml`

鏂规硶鍙傛暟璇存槑锛堜妇渚嬶級锛?

- `queryUserOrderList(@Param("userId") String userId, @Param("lastId") Long lastId, @Param("pageSize") Integer pageSize)`
  - `userId`锛氱敤鎴?
  - `lastId`锛氭父鏍囧垎椤电殑璧风偣锛坣ull 琛ㄧず浠庡ご锛?
  - `pageSize`锛氳繑鍥炴潯鏁?
  - 杩斿洖锛歚List<PayOrder>`锛圥O 瀵硅薄锛?

#### 9.4.2 `PayOrder`锛圥O 瀵硅薄锛?

浣嶇疆锛歚infrastructure/dao/po/PayOrder.java`

- **鐢ㄩ€?*锛氭暟鎹簱琛?`pay_order` 鐨?Java 鏄犲皠瀵硅薄銆?
- **瀛楁**锛氫笌琛ㄥ瓧娈典竴涓€瀵瑰簲锛坲serId/productId/orderId/status/...锛夈€?

### 9.5 s-pay-mall-ddd-api锛堝澶栨帴鍙ｄ笌 DTO锛?

#### 9.5.1 `IPayService` / `IAuthService`

- **鐢ㄩ€?*锛氬畾涔?Controller 搴斿疄鐜扮殑鎺ュ彛锛堢粺涓€鎶娾€滃澶栬兘鍔涒€濇娊璞℃垚 API锛夈€?

#### 9.5.2 DTO 涓?Response

- `CreatePayRequestDTO`锛氬垱寤烘敮浠樿鍗曡姹?
- `QueryOrderListRequestDTO` / `QueryOrderListResponseDTO`锛氭煡璇㈣鍗曞垪琛?
- `RefundOrderRequestDTO` / `RefundOrderResponseDTO`锛氶€€鍗?
- `NotifyRequestDTO`锛氭嫾鍥㈠洖璋?
- `Response<T>`锛氱粺涓€杩斿洖缁撴瀯锛坈ode/info/data锛?

### 9.6 s-pay-mall-ddd-types锛堥€氱敤鏋氫妇/甯搁噺/寮傚父/宸ュ叿锛?

#### 9.6.1 `Constants`

- `ResponseCode`锛氭帴鍙ｅ搷搴旂爜
- `OrderStatusEnum`锛氳鍗曠姸鎬佹灇涓撅紙CREATE/PAY_WAIT/...锛?

#### 9.6.2 寰俊宸ュ叿

- `SignatureUtil.check(token, signature, timestamp, nonce)`锛氬井淇￠獙绛?
- `XmlUtil.xmlToBean(...)` / `beanToXml(...)`锛歑ML 涓?Java 瀵硅薄浜掕浆
- `MessageTextEntity`锛氬井淇℃枃鏈秷鎭ā鍨?

---

## 10. 闄勫綍锛氬父瑙侀棶棰橈紙灏忕櫧鑷晳锛?

### Q1锛氭垜鐪嬪埌 payUrl 鏄竴澶ф HTML锛岃繖鏄甯哥殑鍚楋紵

姝ｅ父銆?

鏀粯瀹濃€滅綉椤垫敮浠樷€濇帴鍙ｈ繑鍥炵殑鏄竴涓?form锛堥噷闈㈠寘鍚鍚嶅弬鏁帮級锛屽墠绔?娴忚鍣ㄦ墽琛屽悗灏变細璺宠浆鍒版敮浠樺疂鏀粯椤甸潰銆?

### Q2锛氫负浠€涔堣鏈?domain / infrastructure 杩欎箞澶氬眰锛?

浣犲彲浠ュ厛涓嶅叧蹇冣€淒DD 鍚嶈瘝鈥濓紝鍙浣忥細

- 涓氬姟閫昏緫闆嗕腑鍦?domain锛屼究浜庣淮鎶?
- 鏁版嵁搴?澶栭儴鎺ュ彛闆嗕腑鍦?infrastructure锛屼究浜庢浛鎹?
- Controller 鍙仛鍙傛暟鎺ユ敹涓庤繑鍥烇紝涓嶅啓澶嶆潅涓氬姟

### Q3锛氭垜搴旇浠庡摢閲屽紑濮嬭浠ｇ爜锛?

浠?**Controller 寮€濮?*锛?

- `AliPayController#createPayOrder`锛堜笅鍗曪級
- `AliPayController#payNotify`锛堟敮浠樺洖璋冿級
- 鍐嶈拷鍒?`OrderService` / `AbstractOrderService`

---

## 鏂囨。璇存槑涓庡悗缁彲瀹屽杽鐐?

杩欎唤鏂囨。鍩轰簬浣犲綋鍓嶄粨搴撲腑鍙洿鎺ヨ鍙栧埌鐨勶細

- Controller/Service/DAO/Mapper/SQL/閰嶇疆鏂囦欢

鍚庣画濡傛灉浣犲笇鏈涒€滄洿缁嗗埌姣忎釜 Java 绫绘瘡涓柟娉曢兘閫愪竴瑙ｉ噴鈥濓紝涔熷彲浠ョ户缁墿灞曪細

- 鎶?`s-pay-mall-ddd-domain/domain/goods`銆乣auth`銆乣infrastructure/gateway` 绛夌被琛ヤ笂
- 鎶?`trigger/listener`锛圡Q 鐩戝惉锛夊拰 `trigger/job`锛堝畾鏃朵换鍔★級閫愪釜鎸夋秷鎭?浠诲姟瑙ｉ噴

> 浣犲彧瑕佸憡璇夋垜锛氫綘鎯充紭鍏堢湅鈥滄敮浠橀摼璺€濊繕鏄€滃井淇℃壂鐮佺櫥褰曢摼璺€濓紝鎴戝彲浠ユ妸閭ｆ潯閾捐矾鐨勭被涓庢柟娉曡鏄庡啓鍒扳€滈€愯璁茶В鈥濈骇鍒€?

