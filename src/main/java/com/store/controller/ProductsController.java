package com.store.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.store.model.Product;
import com.store.model.ProductDto;
import com.store.repository.ProductsRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping({ "", "/" })
    public String showProductList(Model model) {
        var products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";

    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/create";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        if (productDto.getImageFileName().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "Image is required"));
            return "products/create";
        }

        if (result.hasErrors()) {
            return "products/create";
        }
        // save image
        MultipartFile imageFile = productDto.getImageFileName();
        Date createdAt = new Date(System.currentTimeMillis());
        String fileName = createdAt.getTime() + "-" + imageFile.getOriginalFilename();

        try {
            String uploadDirectory = "public/images/";
            Path uploadPath = Paths.get(uploadDirectory + fileName);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageFileName(fileName);
        productsRepository.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditForm(Model model,
            @RequestParam int id) {
        // check if product exists
        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            // product dto
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
            model.addAttribute("productDto", productDto);
        } catch (Exception e) {
            return "redirect:/products";
        }

        return "products/edit";
    }

    @PostMapping("/edit")
    public String editProduct(@Valid @ModelAttribute ProductDto productDto,
            Model model,
            @RequestParam int id,
            BindingResult result) {
        try {

            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/edit";
            }

            if (!productDto.getImageFileName().isEmpty()) {
                // delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // save new image
                MultipartFile imageFile = productDto.getImageFileName();
                Date createdAt = new Date(System.currentTimeMillis());
                String fileName = createdAt.getTime() + "-" + imageFile.getOriginalFilename();

                try (InputStream inputStream = imageFile.getInputStream()) {
                    Path uploadPath = Paths.get(uploadDir + fileName);
                    Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                product.setImageFileName(fileName);
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            productsRepository.save(product);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(
        @RequestParam int id
        ) {
        try {
            Product product = productsRepository.findById(id).get();

            // delete image
            String uploadDir = "public/images/";
            Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

            try {
                Files.delete(oldImagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            productsRepository.delete(product);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";
    }

}