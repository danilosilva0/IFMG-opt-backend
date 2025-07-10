package edu.ifmg.produtos.services;

import edu.ifmg.produtos.dtos.ProductDTO;
import edu.ifmg.produtos.dtos.ProductListDTO;
import edu.ifmg.produtos.entities.Product;
import edu.ifmg.produtos.repository.ProductRepository;
import edu.ifmg.produtos.resources.ProductResource;
import edu.ifmg.produtos.services.exceptions.DatabaseException;
import edu.ifmg.produtos.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired //Pra não precisar instanciar o repositório manualmente
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {

        Page<Product> list = productRepository.findAll(pageable);
        return list.map(product -> new ProductDTO(product)
                .add(linkTo(methodOn(ProductResource.class).findAll(null)).withSelfRel())
                .add(linkTo(methodOn(ProductResource.class).findById(product.getId())).withRel("Get a product"))
        );

    }

    public Page<ProductListDTO> findAllPaged (String name, String categoryId, Pageable pageable) {
        return null;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {

        Optional<Product> obj = productRepository.findById(id);
        Product product = obj.orElseThrow(() -> new ResourceNotFound("Product not found" + id));
        return new ProductDTO(product)
                .add(linkTo(methodOn(ProductResource.class).findById(product.getId())).withSelfRel())
                .add(linkTo(methodOn(ProductResource.class).findAll(null)).withRel("All products"))
                .add(linkTo(methodOn(ProductResource.class).update(product.getId(),
                        null /*Pode passar new ProductDTO(product)*/)).withRel("Update product"))
                .add(linkTo(methodOn(ProductResource.class).delete(product.getId())).withRel("Delete product"))
                ;

    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){

        Product entity = new Product();
        copyDtoToEntity(dto, entity);

        entity = productRepository.save(entity);

        return new ProductDTO(entity)
                .add(linkTo(methodOn(ProductResource.class).findById(entity.getId())).withRel("Get a product"))
                .add(linkTo(methodOn(ProductResource.class).findAll(null)).withRel("All products"))
                .add(linkTo(methodOn(ProductResource.class).update(entity.getId(),
                        null /*Pode passar new ProductDTO(product)*/)).withRel("Update product"))
                .add(linkTo(methodOn(ProductResource.class).delete(entity.getId())).withRel("Delete product"))

                ;
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){

        try{
            Product entity = productRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = productRepository.save(entity);

            return new ProductDTO(entity)
                    .add(linkTo(methodOn(ProductResource.class).findById(entity.getId())).withRel("Find a product"))
                    .add(linkTo(methodOn(ProductResource.class).findAll(null)).withRel("All products"))
                    .add(linkTo(methodOn(ProductResource.class).delete(entity.getId())).withRel("Delete product"))
                    ;
        }catch (EntityNotFoundException e){
            throw new ResourceNotFound("Product not found" + id);
        }

    }

    @Transactional
    public void delete(Long id){

        if (!productRepository.existsById(id)){
            throw new ResourceNotFound("Product not found" + id);
        }
        try{
            productRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
    }



    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
    }

}