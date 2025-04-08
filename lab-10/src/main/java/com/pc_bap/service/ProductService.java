package com.pc_bap.service;

import com.pc_bap.dto.ProductGetDTO;
import com.pc_bap.dto.ProductPostDTO;
import com.pc_bap.dto.ProductSalesGetDTO;
import com.pc_bap.model.Product;
import com.pc_bap.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<ProductGetDTO> listAll() {
        List<ProductGetDTO> result = new ArrayList<>();

        for (Product p : repository.findAll()) {
            result.add(toDTO(p));
        }

        return result;
    }

    public Optional<ProductGetDTO> findById(long id) {
        Product product = repository.findById(id);

        if (product == null) {
            return Optional.empty();
        }

        return Optional.of(toDTO(product));
    }

    public Optional<Product> rawFindById(long id) {
        return Optional.ofNullable(repository.findById(id));
    }

    public boolean exists(long id) {
        return repository.existsById(id);
    }

    public void create(ProductPostDTO p) {
        repository.save(p);
    }

    public synchronized Map<String, Object> purchase(long id, int quantity) {
        Product product = repository.findById(id);

        if (product == null) {
            return Map.of("status", 404, "message", "Produto não encontrado.");
        }

        synchronized (product) {
            if (product.getQuantityStock() < quantity) {
                return Map.of("status", 400, "message", "Estoque insuficiente. Quantidade disponível: " + product.getQuantityStock());
            }
            product.setQuantityStock(product.getQuantityStock() - quantity);
            product.setQuantitySold(product.getQuantitySold() + quantity);
            return Map.of(
                "status", 200,
                "product", new ProductSalesGetDTO(product.getId(), product.getName(), product.getQuantityStock())
            );
        }
    }

    public Map<Boolean, Integer> updateStock(long id, int quantity) {
        Product product = repository.findById(id);
        if (product == null) return Map.of(false, 0);
        repository.updateStock(id, quantity);
        return Map.of(true, product.getQuantityStock());
    }

    public Map<String, Object> salesReport() {
        List<Map<String, Object>> soldProducts = new ArrayList<>();
        int totalSales = 0;

        for (Product product : repository.findAll()) {
            if (product.getQuantitySold() > 0) {
                totalSales += product.getQuantitySold();
                soldProducts.add(Map.of(
                    "id", product.getId(),
                    "name", product.getName(),
                    "quantitySold", product.getQuantitySold()
                ));
            }
        }

        return Map.of(
            "totalSales", totalSales,
            "products", soldProducts
        );
    }

    private ProductGetDTO toDTO(Product p) {
        ProductGetDTO dto = new ProductGetDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantityStock());
        return dto;
    }
}
