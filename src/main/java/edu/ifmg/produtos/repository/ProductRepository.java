package edu.ifmg.produtos.repository;

import edu.ifmg.produtos.entities.Product;
import edu.ifmg.produtos.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
            nativeQuery = true,
            value = """
            SELECT * FROM (
                SELECT DISTINCT p.id, p.name, p.image_url, p.price
                FROM tb_product p
                INNER JOIN tb_product_category pc ON pc.product_id = p.id
                WHERE pc.category_id IN :categoriesID
                  AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ) AS tb_result
            """,
            countQuery = """
            SELECT COUNT(*) FROM (
                SELECT DISTINCT p.id, p.name, p.image_url, p.price
                FROM tb_product p
                INNER JOIN tb_product_category pc ON pc.product_id = p.id
                WHERE pc.category_id IN :categoriesID
                  AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ) AS tb_result
            """
    )
    Page<ProductProjection> searchProductsWithCategories(List<Long> categoriesID, String name, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = """
            SELECT * FROM (
                SELECT DISTINCT p.id, p.name, p.image_url, p.price
                FROM tb_product p
                INNER JOIN tb_product_category pc ON pc.product_id = p.id
                WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ) AS tb_result
            """,
            countQuery = """
            SELECT COUNT(*) FROM (
                SELECT DISTINCT p.id, p.name, p.image_url, p.price
                FROM tb_product p
                INNER JOIN tb_product_category pc ON pc.product_id = p.id
                WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ) AS tb_result
            """
    )
    Page<ProductProjection> searchProductsWithoutCategories(String name, Pageable pageable);
}