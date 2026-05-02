package com.n11bootcamp.product_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProductRequest {

    @NotBlank
    @Schema(example = "Spring Boot & Java Desk Mat")
    private String title;

    @Schema(example = "Mikroservis mimarisi cizenler icin tasarlanmis calisma alani.")
    private String description;

    @Positive
    @Schema(example = "550")
    private long price;

    @Schema(example = "https://example.com/images/dev-deskmat.jpg")
    private String img;

    @Schema(example = "TechStyle")
    private String brand;

    @Schema(example = "Antrasit")
    private String color;

    @NotBlank
    @Schema(example = "Aksesuar")
    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
