package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Reports;
import com.techacademy.repository.ReportsRepository;

@Service
public class ReportsService {

    private final ReportsRepository reportsRepository;

    @Autowired
    public ReportsService(ReportsRepository reportsRepository) {
        this.reportsRepository = reportsRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Reports reports) {


        reports.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        reports.setCreatedAt(now);
        reports.setUpdatedAt(now);

        reportsRepository.save(reports);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Reports reports) {

        Reports emp = findByCode(reports.getCode());


        LocalDateTime now = LocalDateTime.now();
        reports.setCreatedAt(emp.getCreatedAt());
        reports.setUpdatedAt(now);
        reports.setDeleteFlg(false);

        reportsRepository.save(reports);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {

        Reports reports = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        reports.setUpdatedAt(now);
        reports.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報一覧表示処理
    public List<Reports> findAll() {
        return reportsRepository.findAll();
    }

    // 1件を検索
    public Reports findByCode(String code) {
        // findByIdで検索
        Optional<Reports> option = reportsRepository.findById(code);
        // 取得できなかった場合はnullを返す
        Reports reports = option.orElse(null);
        return reports;
    }
}
