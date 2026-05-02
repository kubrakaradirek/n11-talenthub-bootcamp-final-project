package com.n11bootcamp.order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "coupons")
@Schema(description = "Kullanıcının kazandığı tek kullanımlık yüzde 20 indirim kuponu.")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "1")
    private Long id;

    @Schema(example = "KUBA20-ABC123")
    private String code;
    @Schema(example = "203")
    private Long userId;
    @Schema(example = "false")
    private Boolean isUsed = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Boolean getIsUsed() { return isUsed; }
    public void setIsUsed(Boolean used) { isUsed = used; }
}
