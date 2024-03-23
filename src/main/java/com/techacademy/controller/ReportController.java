package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        List<Report> reportList = null;
        Employee employee = userDetail.getEmployee();
        if (Employee.Role.ADMIN.equals(employee.getRole())) {
            reportList = reportService.findAll();
        } else {
            reportList = reportService.findByEmployee(employee);
        }
        model.addAttribute("listSize", reportList.size());
        model.addAttribute("reportList", reportList);
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute Report report, Model model) {

        Employee employee = userDetail.getEmployee();
        model.addAttribute("name", employee.getName());
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@AuthenticationPrincipal UserDetail userDetail, @Validated Report report, BindingResult res,
            Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(userDetail, report, model);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            Employee employee = userDetail.getEmployee();
            report.setEmployee(employee);
            ErrorKinds result = reportService.save(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(userDetail, report, model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(userDetail, report, model);
        }

        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String edit(@AuthenticationPrincipal UserDetail userDetail,@ModelAttribute Report report, @PathVariable("id") Integer id, Model model) {

        if (id != null) {
            model.addAttribute("report", reportService.findById(id));
        } else {
            model.addAttribute("report", report);
        }
        Employee employee = userDetail.getEmployee();
        model.addAttribute("name", employee.getName());
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@AuthenticationPrincipal UserDetail userDetail,@Validated Report report, BindingResult res,@PathVariable("id") Integer id, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return edit(userDetail,report, null, model);
        }
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);
        ErrorKinds result = reportService.update(report);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return edit(userDetail,report, null, model);
        }

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportService.delete(id, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }
}