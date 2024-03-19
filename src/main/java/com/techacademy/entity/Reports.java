package com.techacademy.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
@Table(name = "employees")
@SQLRestriction("delete_flg = false")
public class Reports {

    // ID
    @Id
    @Column(length = 10)
    @NotEmpty
    @Length(max = 10)
    private String code;

    // 名前
    @Column(length = 20, nullable = false)
    @NotEmpty
    @Length(max = 20)
    private String name;

    //タイトル
    @Column(length = 20, nullable = false)
    @NotEmpty
    @Length(max = 100)
    private String title;

    //内容
    @Column(length = 20, nullable = false)
    @NotEmpty
    @Length(max = 100)
    private String content;

    //社員番号
    @Column(length = 10, nullable = false)
    @NotEmpty
    @Length(max = 10)
    private String employee_code;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
