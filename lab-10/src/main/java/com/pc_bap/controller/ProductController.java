package com.pc_bap.controller;

import com.pc_bap.dto.ProductPostDTO;
import com.pc_bap.dto.ProductPutDTO;
import com.pc_bap.dto.PurchasePostDTO;
import com.pc_bap.model.Product;
import com.pc_bap.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getById(@PathVariable long id) {
        return service.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Produto não encontrado.")));
    }

    @PostMapping("/products")
    public ResponseEntity<?> create(@RequestBody ProductPostDTO p) {
        if (service.exists(p.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Produto com ID já existente."));
        }
        service.create(p);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Produto cadastrado com sucesso.",
                             "product", Map.of("id", p.getId(), "name", p.getName())));
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@RequestBody PurchasePostDTO purchase) {
        long id = purchase.getId();
        int quantity = purchase.getQuantity();

        Map<String, Object> result = service.purchase(id, quantity);

        int status = (int) result.get("status");
        if (status == 200) {
            return ResponseEntity.ok(Map.of("message", "Compra realizada com sucesso.", "product", result.get("product")));
        }
        else if (status == 400){
            return ResponseEntity.badRequest().body(Map.of("message", result.get("message")));
        } 
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", result.get("message")));
    }

    @PutMapping("/products/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable long id, @RequestBody ProductPutDTO req) {
        Map<Boolean, Integer> result = service.updateStock(id, req.getQuantity());
    
        if (!result.containsKey(true)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Produto não encontrado."));
        }
    
        return ResponseEntity.ok(Map.of(
                "message", "Estoque atualizado.",
                "remainingStock", result.get(true)
        ));
    }
    

    @GetMapping("/sales/report")
    public ResponseEntity<?> report() {
        return ResponseEntity.ok(service.salesReport());
    }
}

