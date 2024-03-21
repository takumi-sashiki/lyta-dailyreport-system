package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.techacademy.entity.Reports;
import com.techacademy.service.ReportsService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportsService reportsService;

    @Autowired
    public ReportController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", reportsService.findAll().size());
        model.addAttribute("reportsList", reportsService.findAll());

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable String code, Model model) {

        model.addAttribute("reports", reportsService.findByCode(code));
        return "reports/detail";
    }

    // 日報更新画面
    @GetMapping(value = "/{code}/update")
    public String edit(@ModelAttribute Reports reports, @PathVariable("code") String code, Model model) {

        if (code != null) {
            model.addAttribute("reports", reportsService.findByCode(code));
        } else {
            model.addAttribute("reports", reports);
        }
        return "reports/update";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Reports reports) {

        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Reports reports, BindingResult res, Model model) {



        // 入力チェック
        if (res.hasErrors()) {
            return create(reports);
        }

        return "redirect:/reports";
    }

    // 日報更新処理
    @PostMapping(value = "/{code}/update")
    public String update(@Validated Reports reports, @PathVariable("code") String code, BindingResult res,
            Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return edit(reports, null, model);
        }
        reportsService.update(reports);

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportsService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("reports", reportsService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/reports";
    }
}
