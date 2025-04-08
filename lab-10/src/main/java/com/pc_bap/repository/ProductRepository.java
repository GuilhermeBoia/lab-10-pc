package com.pc_bap.repository;

import com.pc_bap.dto.ProductPostDTO;
import com.pc_bap.model.Product;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepository {

    private final Map<Long, Product> productMap = new ConcurrentHashMap<>();

    public Collection<Product> findAll() {
        return productMap.values();
    }

    public Product findById(long id) {
        return productMap.get(id);
    }

    public boolean existsById(long id) {
        return productMap.containsKey(id);
    }

    public void save(ProductPostDTO product) {
        Product p = new Product(product.getId(), product.getName(), product.getPrice(), product.getQuantity(), 0);
        productMap.put(p.getId(), p);
    }

    public void updateStock(long id, int quantity) {
        Product product = productMap.get(id);
        if (product != null) {
            synchronized (product) {
                product.setQuantityStock(quantity);
            }
        }
    }
}

