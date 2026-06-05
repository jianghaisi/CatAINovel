package cn.bugstack.infrastructure.gateway;

import cn.bugstack.infrastructure.gateway.dto.ProductDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductRPC {

    private static final Map<String, ProductDTO> PRODUCTS = new HashMap<String, ProductDTO>();

    static {
        addProduct("member-pro", "灏忕尗鍐欎綔 Pro 浼氬憳", "閫傚悎鏃ュ父绔犺妭鐢熸垚", "19.00");
        addProduct("member-plus", "灏忕尗鍐欎綔 Plus 浼氬憳", "閫傚悎闀跨瘒鑷姩浠诲姟", "49.00");
        addProduct("member-max", "灏忕尗鍐欎綔 Max 浼氬憳", "閫傚悎鐧句竾瀛楅暱绡囨寔缁垱浣?, "99.00");
        addProduct("9890001", "MyBatisBook", "鎷煎洟椤圭洰绀轰緥鍟嗗搧", "100.00");
    }

    private static void addProduct(String productId, String name, String desc, String price) {
        ProductDTO product = new ProductDTO();
        product.setProductId(productId);
        product.setProductName(name);
        product.setProductDesc(desc);
        product.setPrice(new BigDecimal(price));
        PRODUCTS.put(productId, product);
    }

    public ProductDTO queryProductByProductId(String productId) {
        ProductDTO product = PRODUCTS.get(productId);
        if (product != null) {
            return product;
        }
        ProductDTO fallback = new ProductDTO();
        fallback.setProductId(productId);
        fallback.setProductName("灏忕尗鍐欎綔浼氬憳");
        fallback.setProductDesc("AI 灏忚鍒涗綔骞冲彴浼氬憳");
        fallback.setPrice(new BigDecimal("19.00"));
        return fallback;
    }
}
