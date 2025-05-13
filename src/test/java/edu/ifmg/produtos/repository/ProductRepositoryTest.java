package edu.ifmg.produtos.repository;

import edu.ifmg.produtos.entities.Product;
import edu.ifmg.produtos.util.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;

    @BeforeEach
    public void setUp()  throws Exception {
        existingId = 1L;
    }

    @Test
    @DisplayName(value = "Verificando se o objeto não existe no " +
            "BD depois de deletado.")
    public void deleteShoudDeleteObjectWhenIdExists() {

        productRepository.deleteById(existingId);
        Optional<Product> obj = productRepository.findById(existingId);

        Assertions.assertFalse(obj.isPresent());

    }
    @Test
    @DisplayName(value = "Verificando o auto incremento da chave primaria")
    public void insertShoudPersistWithAutoIncrementIdWhenIdNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        Product p = productRepository.save(product);
        Optional<Product> obj = productRepository.findById(p.getId());
        Assertions.assertTrue(obj.isPresent());
        Assertions.assertNotNull(obj.get().getId());
        Assertions.assertEquals(26, obj.get().getId());
    }
}